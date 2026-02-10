#!/usr/bin/env python3
"""
Contract Fulfillment Review Tool
合同履约审核工具

Compare contract KPIs against actual operational performance data.
Generates an interactive HTML report with compliance analysis.
"""
from __future__ import annotations

import argparse
import hashlib
import json
import logging
import os
import re
import threading
from concurrent.futures import ThreadPoolExecutor, as_completed
from dataclasses import dataclass, field
from datetime import datetime, timezone
from pathlib import Path
from typing import Any, List, Optional

from openai import OpenAI

# ============================================================================
# Configuration
# ============================================================================

SCRIPT_DIR = Path(__file__).parent
DEFAULT_DATA_DIR = SCRIPT_DIR.parent / "data"
DEFAULT_OUTPUT_DIR = SCRIPT_DIR.parent / "output"
DEFAULT_KPI = DEFAULT_DATA_DIR / "kpi.md"
DEFAULT_PERFORMANCE = DEFAULT_DATA_DIR / "operation_performance.md"

LOGGER = logging.getLogger(__name__)

LABELS = {
    "zh": {
        "report_title": "合同履约审核报告",
        "summary_title": "履约概览",
        "total_items": "考核项总数",
        "compliant": "达标",
        "non_compliant": "未达标",
        "borderline": "临界",
        "not_applicable": "不适用",
        "needs_review": "需复核",
        "compliance_rate": "综合达标率",
        "kpi_requirement": "合同KPI要求",
        "actual_performance": "实际表现",
        "deviation": "偏差",
        "risk": "风险",
        "recommendation": "改进建议",
        "severity_high": "高",
        "severity_medium": "中",
        "severity_low": "低",
        "filter_all": "全部",
        "executive_summary": "高管摘要",
        "detail_title": "逐项分析",
        "kpi_panel_title": "合同KPI条款",
        "performance_panel_title": "运维实际表现",
        "print_btn": "打印报告",
        "search_placeholder": "搜索KPI条目...",
    },
    "en": {
        "report_title": "Contract Fulfillment Review Report",
        "summary_title": "Compliance Overview",
        "total_items": "Total KPI Items",
        "compliant": "Compliant",
        "non_compliant": "Non-Compliant",
        "borderline": "Borderline",
        "not_applicable": "N/A",
        "needs_review": "Needs Review",
        "compliance_rate": "Overall Compliance Rate",
        "kpi_requirement": "Contract KPI Requirement",
        "actual_performance": "Actual Performance",
        "deviation": "Deviation",
        "risk": "Risk",
        "recommendation": "Recommendation",
        "severity_high": "High",
        "severity_medium": "Medium",
        "severity_low": "Low",
        "filter_all": "All",
        "executive_summary": "Executive Summary",
        "detail_title": "Detailed Analysis",
        "kpi_panel_title": "Contract KPI Terms",
        "performance_panel_title": "Operational Performance",
        "print_btn": "Print Report",
        "search_placeholder": "Search KPI items...",
    },
}

STATUS_COLORS = {
    "达标": "#16a34a",
    "未达标": "#dc2626",
    "临界": "#ea580c",
    "不适用": "#6b7280",
    "Compliant": "#16a34a",
    "Non-Compliant": "#dc2626",
    "Borderline": "#ea580c",
    "N/A": "#6b7280",
}

# ============================================================================
# Data Models
# ============================================================================


@dataclass(slots=True)
class KPISection:
    """A parsed KPI section from the contract."""
    section_id: str
    heading_path: List[str]
    text: str


@dataclass(slots=True)
class FulfillmentJudgment:
    """LLM evaluation result for a single KPI item."""
    section_id: str
    heading_path: List[str]
    kpi_item: str
    status: str           # 达标 | 未达标 | 临界 | 不适用
    severity: str         # 高 | 中 | 低
    deviation: str
    risk_description: str
    rationale: str
    evidence_kpi: str
    evidence_performance: str
    recommendation: str
    confidence: str       # 高 | 中 | 低
    needs_review: bool

    def to_dict(self) -> dict:
        return {
            "section_id": self.section_id,
            "heading_path": self.heading_path,
            "kpi_item": self.kpi_item,
            "status": self.status,
            "severity": self.severity,
            "deviation": self.deviation,
            "risk_description": self.risk_description,
            "rationale": self.rationale,
            "evidence_kpi": self.evidence_kpi,
            "evidence_performance": self.evidence_performance,
            "recommendation": self.recommendation,
            "confidence": self.confidence,
            "needs_review": self.needs_review,
        }


# ============================================================================
# Markdown KPI Parser
# ============================================================================

HEADING_RE = re.compile(r'^(#{1,6})\s+(.*)', re.MULTILINE)


def parse_kpi_sections(text: str) -> List[KPISection]:
    """Split KPI markdown into sections by heading structure."""
    lines = text.split('\n')
    sections: List[KPISection] = []
    heading_stack: List[str] = []
    buffer: List[str] = []
    section_counter = 1

    def flush():
        nonlocal section_counter
        content = '\n'.join(buffer).strip()
        if not content or len(content) < 20:
            buffer.clear()
            return
        sections.append(KPISection(
            section_id=f"K{section_counter:03d}",
            heading_path=heading_stack.copy(),
            text=content,
        ))
        section_counter += 1
        buffer.clear()

    for line in lines:
        m = HEADING_RE.match(line)
        if m:
            flush()
            level = len(m.group(1))
            title = m.group(2).strip()
            while len(heading_stack) >= level:
                heading_stack.pop()
            heading_stack.append(title)
            continue
        buffer.append(line)

    flush()
    return sections


# ============================================================================
# LLM Cache
# ============================================================================

class LLMCache:
    """Persistent SHA256-based cache to avoid redundant LLM calls."""

    def __init__(self, cache_dir: Path | str | None = None, flush_interval: int = 20):
        if cache_dir is None:
            cache_dir = DEFAULT_OUTPUT_DIR / ".cache"
        self.cache_dir = Path(cache_dir)
        self.cache_dir.mkdir(parents=True, exist_ok=True)
        self.cache_file = self.cache_dir / "llm_cache.json"
        self._cache: dict[str, dict] = self._load()
        self._dirty = 0
        self._flush_interval = flush_interval
        self._lock = threading.Lock()

    def _load(self) -> dict[str, dict]:
        if self.cache_file.exists():
            try:
                return json.loads(self.cache_file.read_text(encoding="utf-8"))
            except (json.JSONDecodeError, IOError):
                return {}
        return {}

    def _save(self) -> None:
        self.cache_file.write_text(
            json.dumps(self._cache, ensure_ascii=False, indent=2),
            encoding="utf-8",
        )

    @staticmethod
    def _key(section_text: str, perf_text: str, model: str) -> str:
        content = f"{model}::{section_text}::{perf_text}"
        return hashlib.sha256(content.encode("utf-8")).hexdigest()[:16]

    def get(self, section_text: str, perf_text: str, model: str) -> dict | None:
        return self._cache.get(self._key(section_text, perf_text, model))

    def set(self, section_text: str, perf_text: str, model: str, data: dict) -> None:
        key = self._key(section_text, perf_text, model)
        with self._lock:
            self._cache[key] = data
            self._dirty += 1
            if self._dirty >= self._flush_interval:
                self._save()
                self._dirty = 0

    def flush(self) -> None:
        with self._lock:
            if self._dirty > 0:
                self._save()
                self._dirty = 0


# ============================================================================
# LLM Client
# ============================================================================

ALLOWED_STATUS = {"达标", "未达标", "临界", "不适用"}

PROMPT_TEMPLATE = '''你是合同履约审核专家。请对比合同中的KPI要求与运维团队的实际表现数据，判断该KPI是否达标。

分析要求：
1. 逐条核对KPI目标值与实际数值
2. 量化偏差（如有）
3. 评估未达标项的合同风险（违约金、服务信用扣款等）
4. 给出具体可执行的改进建议

## 合同KPI要求
标题链：{heading}
内容：
"""{kpi_text}"""

## 运维实际表现数据（全文）
"""{performance_text}"""

请严格输出以下JSON格式（不要输出其他内容）：
{{
  "status": "达标|未达标|临界|不适用",
  "severity": "高|中|低",
  "deviation": "量化偏差描述，达标填'无'",
  "risk_description": "未达标可能触发的合同条款后果",
  "rationale": "分析推理过程",
  "evidence_kpi": "引用合同KPI原文",
  "evidence_performance": "引用实际表现数据",
  "recommendation": "具体改进建议",
  "confidence": "高|中|低",
  "needs_review": false
}}

status 判断标准：
- 达标：实际表现达到或超过KPI目标值
- 未达标：实际表现明显低于KPI目标值
- 临界：实际表现接近但略低于KPI目标值（偏差在目标的5%以内），或数据不够充分
- 不适用：运维数据中无对应考核项

severity 判断标准（仅用于未达标/临界项）：
- 高：可能触发重大违约条款、高额罚款或合同终止
- 中：可能触发服务信用扣款或需提交整改方案
- 低：轻微偏差，提醒关注即可
- 达标项 severity 统一填"低"

仅输出JSON：'''


class LLMClient:
    """OpenAI-compatible LLM client with caching and retry."""

    def __init__(
        self,
        model: str | None = None,
        base_url: str | None = None,
        api_key: str | None = None,
        max_retries: int = 2,
        cache: LLMCache | None = None,
    ) -> None:
        self.model = model or os.environ.get("OPENAI_MODEL")
        if not self.model:
            raise ValueError("OPENAI_MODEL is required")
        base_url = base_url or os.environ.get("OPENAI_BASE_URL")
        api_key = api_key or os.environ.get("OPENAI_API_KEY")
        if not api_key:
            raise ValueError("OPENAI_API_KEY is required")
        self.client = OpenAI(api_key=api_key, base_url=base_url)
        self.max_retries = max_retries
        self.cache = cache
        self._hits = 0
        self._misses = 0

    def evaluate(self, section: KPISection, perf_text: str) -> FulfillmentJudgment:
        """Evaluate a KPI section against performance data."""
        if self.cache:
            cached = self.cache.get(section.text, perf_text, self.model)
            if cached:
                self._hits += 1
                LOGGER.debug("Cache hit for %s", section.section_id)
                return self._build(section, cached)
            self._misses += 1

        heading = " > ".join(section.heading_path) or "(未命名)"
        prompt = PROMPT_TEMPLATE.format(
            heading=heading,
            kpi_text=section.text,
            performance_text=perf_text,
        )

        last_err: Exception | None = None
        for attempt in range(1, self.max_retries + 1):
            try:
                resp = self.client.chat.completions.create(
                    model=self.model,
                    messages=[{"role": "user", "content": prompt}],
                    temperature=0.0,
                )
                raw = resp.choices[0].message.content.strip()
                data = self._parse_json(raw)
                self._validate(data)
                if self.cache:
                    self.cache.set(section.text, perf_text, self.model, data)
                return self._build(section, data)
            except Exception as e:
                last_err = e
                LOGGER.warning("Attempt %d failed for %s: %s", attempt, section.section_id, e)

        LOGGER.error("All attempts failed for %s: %s", section.section_id, last_err)
        return self._fallback(section, str(last_err))

    @staticmethod
    def _parse_json(raw: str) -> dict:
        raw = raw.strip()
        if raw.startswith("```"):
            raw = re.sub(r'^```(?:json)?\s*', '', raw)
            raw = re.sub(r'\s*```$', '', raw)
        return json.loads(raw)

    @staticmethod
    def _validate(data: dict) -> None:
        status = data.get("status", "")
        if status not in ALLOWED_STATUS:
            # Try normalization
            norm_map = {
                "合规": "达标", "符合": "达标", "通过": "达标",
                "不合规": "未达标", "不符合": "未达标", "未通过": "未达标",
                "边界": "临界", "接近": "临界",
            }
            for k, v in norm_map.items():
                if k in status:
                    data["status"] = v
                    break
            else:
                data["status"] = "不适用"

    @staticmethod
    def _build(section: KPISection, data: dict) -> FulfillmentJudgment:
        return FulfillmentJudgment(
            section_id=section.section_id,
            heading_path=section.heading_path,
            kpi_item=" > ".join(section.heading_path),
            status=data.get("status", "不适用"),
            severity=data.get("severity", "低"),
            deviation=data.get("deviation", ""),
            risk_description=data.get("risk_description", ""),
            rationale=data.get("rationale", ""),
            evidence_kpi=data.get("evidence_kpi", ""),
            evidence_performance=data.get("evidence_performance", ""),
            recommendation=data.get("recommendation", ""),
            confidence=data.get("confidence", "中"),
            needs_review=bool(data.get("needs_review", False)),
        )

    @staticmethod
    def _fallback(section: KPISection, error: str) -> FulfillmentJudgment:
        return FulfillmentJudgment(
            section_id=section.section_id,
            heading_path=section.heading_path,
            kpi_item=" > ".join(section.heading_path),
            status="不适用",
            severity="低",
            deviation="",
            risk_description="",
            rationale=f"LLM分析失败: {error}",
            evidence_kpi="",
            evidence_performance="",
            recommendation="需人工审核",
            confidence="低",
            needs_review=True,
        )


# ============================================================================
# HTML Report Generator
# ============================================================================

def _esc(text: str) -> str:
    """Escape HTML special characters."""
    return (text
            .replace("&", "&amp;")
            .replace("<", "&lt;")
            .replace(">", "&gt;")
            .replace('"', "&quot;"))


def generate_report_html(
    judgments: List[FulfillmentJudgment],
    kpi_text: str,
    perf_text: str,
    lang: str = "zh",
    timestamp: str = "",
) -> str:
    """Generate an interactive HTML fulfillment review report."""
    L = LABELS.get(lang, LABELS["zh"])

    total = len(judgments)
    compliant = sum(1 for j in judgments if j.status == "达标")
    non_compliant = sum(1 for j in judgments if j.status == "未达标")
    borderline = sum(1 for j in judgments if j.status == "临界")
    na = sum(1 for j in judgments if j.status == "不适用")
    review = sum(1 for j in judgments if j.needs_review)
    applicable = total - na
    rate = f"{compliant / applicable * 100:.1f}%" if applicable > 0 else "N/A"

    # Build executive summary rows (only non-compliant and borderline)
    exec_rows = []
    for j in judgments:
        if j.status in ("未达标", "临界"):
            sev_class = {"高": "high", "中": "med", "低": "low"}.get(j.severity, "low")
            status_cls = "non-compliant" if j.status == "未达标" else "borderline"
            exec_rows.append(f'''<tr class="clickable-row" data-target="{j.section_id}">
  <td>{_esc(j.section_id)}</td>
  <td>{_esc(j.kpi_item)}</td>
  <td><span class="badge {status_cls}">{_esc(j.status)}</span></td>
  <td><span class="sev-dot {sev_class}"></span>{_esc(j.severity)}</td>
  <td>{_esc(j.deviation)}</td>
  <td>{_esc(j.risk_description[:80])}</td>
</tr>''')

    exec_table = "\n".join(exec_rows) if exec_rows else f'<tr><td colspan="6" style="text-align:center;color:#16a34a;">All KPIs compliant</td></tr>'

    # Build detail cards
    detail_cards = []
    for j in judgments:
        status_cls = {
            "达标": "compliant", "未达标": "non-compliant",
            "临界": "borderline", "不适用": "na",
        }.get(j.status, "na")
        sev_class = {"高": "high", "中": "med", "低": "low"}.get(j.severity, "low")

        card = f'''<div class="card {status_cls}" id="{j.section_id}"
     data-status="{j.status}" data-severity="{j.severity}" data-review="{str(j.needs_review).lower()}">
  <div class="card-header">
    <span class="card-id">{_esc(j.section_id)}</span>
    <span class="card-title">{_esc(j.kpi_item)}</span>
    <span class="badge {status_cls}">{_esc(j.status)}</span>
    <span class="sev-dot {sev_class}" title="severity: {_esc(j.severity)}"></span>
    {f'<span class="review-flag">&#9873;</span>' if j.needs_review else ''}
  </div>
  <div class="card-body">
    <div class="analysis-row">
      <div class="analysis-block">
        <h4>{L["deviation"]}</h4>
        <p>{_esc(j.deviation) or '—'}</p>
      </div>
      <div class="analysis-block">
        <h4>{L["risk"]}</h4>
        <p>{_esc(j.risk_description) or '—'}</p>
      </div>
    </div>
    <div class="rationale">
      <h4>&#128269; 分析</h4>
      <p>{_esc(j.rationale)}</p>
    </div>
    <div class="evidence-row">
      <div class="evidence kpi-ev">
        <h4>&#128196; {L["kpi_requirement"]}</h4>
        <p>{_esc(j.evidence_kpi)}</p>
      </div>
      <div class="evidence perf-ev">
        <h4>&#128200; {L["actual_performance"]}</h4>
        <p>{_esc(j.evidence_performance)}</p>
      </div>
    </div>
    <div class="recommendation">
      <h4>&#128161; {L["recommendation"]}</h4>
      <p>{_esc(j.recommendation) or '—'}</p>
    </div>
    <div class="meta">
      置信度: {_esc(j.confidence)} &nbsp;|&nbsp; {L["needs_review"]}: {'是' if j.needs_review else '否'}
    </div>
  </div>
</div>'''
        detail_cards.append(card)

    cards_html = "\n".join(detail_cards)

    # Simple markdown → HTML for sidebar panels
    def md_to_html(md: str) -> str:
        html_lines = []
        for line in md.split('\n'):
            stripped = line.strip()
            if not stripped:
                html_lines.append('')
                continue
            hm = re.match(r'^(#{1,6})\s+(.*)', stripped)
            if hm:
                lvl = len(hm.group(1))
                html_lines.append(f'<h{lvl}>{_esc(hm.group(2))}</h{lvl}>')
                continue
            tm = re.match(r'^\|(.+)\|$', stripped)
            if tm:
                cells = [c.strip() for c in tm.group(1).split('|')]
                if all(re.match(r'^[-:]+$', c) for c in cells):
                    continue
                tag = 'td'
                html_lines.append('<tr>' + ''.join(f'<{tag}>{_esc(c)}</{tag}>' for c in cells) + '</tr>')
                continue
            if stripped.startswith('- ') or stripped.startswith('* '):
                html_lines.append(f'<li>{_esc(stripped[2:])}</li>')
                continue
            if stripped.startswith('> '):
                html_lines.append(f'<blockquote>{_esc(stripped[2:])}</blockquote>')
                continue
            # Bold
            processed = re.sub(r'\*\*(.+?)\*\*', r'<strong>\1</strong>', stripped)
            html_lines.append(f'<p>{processed}</p>')
        # Wrap table rows
        result = '\n'.join(html_lines)
        result = re.sub(r'((?:<tr>.*?</tr>\s*)+)', r'<table class="md-table">\1</table>', result, flags=re.DOTALL)
        # Wrap list items
        result = re.sub(r'((?:<li>.*?</li>\s*)+)', r'<ul>\1</ul>', result, flags=re.DOTALL)
        return result

    kpi_html = md_to_html(kpi_text)
    perf_html = md_to_html(perf_text)

    # Compliance rate for bar chart
    rates = {}
    if applicable > 0:
        rates["compliant"] = compliant / applicable * 100
        rates["non_compliant"] = non_compliant / applicable * 100
        rates["borderline"] = borderline / applicable * 100

    bar_segments = ""
    if applicable > 0:
        bar_segments = f'''
<div class="bar-seg compliant" style="width:{rates['compliant']:.1f}%">{rates['compliant']:.0f}%</div>
<div class="bar-seg borderline" style="width:{rates['borderline']:.1f}%">{rates['borderline']:.0f}%</div>
<div class="bar-seg non-compliant" style="width:{rates['non_compliant']:.1f}%">{rates['non_compliant']:.0f}%</div>'''

    html = f'''<!DOCTYPE html>
<html lang="{lang}">
<head>
<meta charset="UTF-8">
<meta name="viewport" content="width=device-width,initial-scale=1">
<title>{L["report_title"]}</title>
<style>
*{{margin:0;padding:0;box-sizing:border-box}}
body{{font-family:-apple-system,BlinkMacSystemFont,"Segoe UI",Roboto,"Helvetica Neue",Arial,sans-serif;
  background:#f0f2f5;color:#1a1a2e;line-height:1.6;font-size:14px}}

.header{{background:linear-gradient(135deg,#1e3a5f 0%,#2d5a87 100%);color:#fff;
  padding:28px 32px;display:flex;justify-content:space-between;align-items:center}}
.header h1{{font-size:22px;font-weight:700}}
.header .meta{{font-size:13px;opacity:.85}}
.header button{{background:rgba(255,255,255,.2);color:#fff;border:none;
  padding:8px 18px;border-radius:6px;cursor:pointer;font-size:13px}}
.header button:hover{{background:rgba(255,255,255,.35)}}

.stats{{display:flex;gap:16px;padding:24px 32px;flex-wrap:wrap}}
.stat-card{{flex:1;min-width:130px;background:#fff;border-radius:10px;padding:18px;
  box-shadow:0 1px 4px rgba(0,0,0,.08);text-align:center}}
.stat-card .num{{font-size:28px;font-weight:700}}
.stat-card .label{{font-size:12px;color:#666;margin-top:4px}}
.stat-card.compliant .num{{color:#16a34a}}
.stat-card.non-compliant .num{{color:#dc2626}}
.stat-card.borderline .num{{color:#ea580c}}
.stat-card.rate .num{{color:#1e3a5f}}

.bar-chart{{margin:0 32px 20px;background:#e5e7eb;border-radius:8px;height:32px;display:flex;overflow:hidden}}
.bar-seg{{display:flex;align-items:center;justify-content:center;color:#fff;font-size:12px;font-weight:600;
  min-width:28px;transition:width .4s}}
.bar-seg.compliant{{background:#16a34a}}
.bar-seg.borderline{{background:#ea580c}}
.bar-seg.non-compliant{{background:#dc2626}}

.section-title{{font-size:17px;font-weight:700;padding:18px 32px 8px;color:#1e3a5f}}

.exec-table-wrapper{{padding:0 32px 20px}}
.exec-table{{width:100%;border-collapse:collapse;background:#fff;border-radius:10px;overflow:hidden;
  box-shadow:0 1px 4px rgba(0,0,0,.08)}}
.exec-table th{{background:#f8fafc;font-weight:600;font-size:12px;text-transform:uppercase;
  padding:10px 14px;text-align:left;border-bottom:2px solid #e5e7eb;color:#475569}}
.exec-table td{{padding:10px 14px;border-bottom:1px solid #f1f5f9;font-size:13px}}
.clickable-row{{cursor:pointer}}
.clickable-row:hover{{background:#f0f7ff}}

.toolbar{{display:flex;gap:10px;padding:10px 32px;flex-wrap:wrap;align-items:center}}
.toolbar .filter-btn{{padding:6px 16px;border:1px solid #d1d5db;border-radius:20px;
  background:#fff;cursor:pointer;font-size:13px;transition:all .2s}}
.toolbar .filter-btn:hover,.toolbar .filter-btn.active{{background:#1e3a5f;color:#fff;border-color:#1e3a5f}}
.toolbar input[type=text]{{flex:1;min-width:200px;padding:8px 14px;border:1px solid #d1d5db;
  border-radius:8px;font-size:13px}}

.main-layout{{display:flex;gap:0;padding:0 32px 40px}}
.detail-panel{{flex:3;min-width:0}}
.side-panel{{flex:2;min-width:0;max-height:calc(100vh - 100px);overflow-y:auto;position:sticky;top:20px;
  background:#fff;border-radius:10px;padding:20px;box-shadow:0 1px 4px rgba(0,0,0,.08);
  margin-left:20px;font-size:13px}}
.side-panel h1,.side-panel h2,.side-panel h3{{color:#1e3a5f;margin:16px 0 8px}}
.side-panel h1{{font-size:18px}} .side-panel h2{{font-size:15px}} .side-panel h3{{font-size:14px}}
.side-panel .md-table{{width:100%;border-collapse:collapse;margin:8px 0;font-size:12px}}
.side-panel .md-table td,.side-panel .md-table th{{border:1px solid #e5e7eb;padding:5px 8px}}
.side-panel blockquote{{border-left:3px solid #cbd5e1;padding-left:12px;color:#64748b;margin:8px 0}}
.side-panel ul{{padding-left:20px;margin:4px 0}}
.side-panel p{{margin:4px 0}}
.side-panel strong{{color:#334155}}
.panel-tabs{{display:flex;border-bottom:2px solid #e5e7eb;margin-bottom:16px}}
.panel-tab{{padding:8px 18px;cursor:pointer;font-size:13px;font-weight:600;color:#64748b;border-bottom:2px solid transparent;margin-bottom:-2px}}
.panel-tab.active{{color:#1e3a5f;border-bottom-color:#1e3a5f}}
.panel-content{{display:none}}
.panel-content.active{{display:block}}

.card{{background:#fff;border-radius:10px;margin-bottom:14px;overflow:hidden;
  box-shadow:0 1px 4px rgba(0,0,0,.08);border-left:4px solid #d1d5db}}
.card.compliant{{border-left-color:#16a34a}}
.card.non-compliant{{border-left-color:#dc2626}}
.card.borderline{{border-left-color:#ea580c}}
.card.na{{border-left-color:#6b7280}}
.card-header{{display:flex;align-items:center;gap:10px;padding:14px 18px;background:#f8fafc;
  cursor:pointer;user-select:none}}
.card-header:hover{{background:#f0f4f8}}
.card-id{{font-weight:700;color:#475569;font-size:12px;min-width:40px}}
.card-title{{flex:1;font-weight:600;font-size:14px}}
.badge{{padding:3px 10px;border-radius:12px;font-size:11px;font-weight:700;color:#fff}}
.badge.compliant{{background:#16a34a}}
.badge.non-compliant{{background:#dc2626}}
.badge.borderline{{background:#ea580c}}
.badge.na{{background:#6b7280}}
.sev-dot{{display:inline-block;width:10px;height:10px;border-radius:50%;margin-right:4px}}
.sev-dot.high{{background:#dc2626}}
.sev-dot.med{{background:#ea580c}}
.sev-dot.low{{background:#3b82f6}}
.review-flag{{color:#dc2626;font-size:14px}}

.card-body{{padding:0 18px 16px;display:none}}
.card.open .card-body{{display:block}}
.card-body h4{{font-size:12px;font-weight:700;color:#475569;margin:12px 0 4px;text-transform:uppercase}}
.card-body p{{font-size:13px;color:#334155}}
.analysis-row,.evidence-row{{display:flex;gap:16px}}
.analysis-row>div,.evidence-row>div{{flex:1}}
.evidence{{background:#f8fafc;border-radius:6px;padding:10px 14px;margin-top:4px}}
.evidence.kpi-ev{{border-left:3px solid #1e3a5f}}
.evidence.perf-ev{{border-left:3px solid #16a34a}}
.recommendation{{background:#fffbeb;border-radius:6px;padding:10px 14px;margin-top:8px}}
.meta{{font-size:11px;color:#94a3b8;margin-top:10px;padding-top:8px;border-top:1px solid #f1f5f9}}

@media print{{
  .toolbar,.header button{{display:none}}
  .main-layout{{display:block}}
  .side-panel{{position:static;max-height:none;margin:20px 0;page-break-before:always}}
  .card-body{{display:block!important}}
  .card{{page-break-inside:avoid}}
}}
@media(max-width:900px){{
  .main-layout{{flex-direction:column}}
  .side-panel{{margin-left:0;position:static;max-height:none}}
}}
</style>
</head>
<body>

<div class="header">
  <div>
    <h1>{L["report_title"]}</h1>
    <div class="meta">{timestamp}</div>
  </div>
  <button onclick="window.print()">{L["print_btn"]}</button>
</div>

<div class="stats">
  <div class="stat-card"><div class="num">{total}</div><div class="label">{L["total_items"]}</div></div>
  <div class="stat-card compliant"><div class="num">{compliant}</div><div class="label">{L["compliant"]}</div></div>
  <div class="stat-card non-compliant"><div class="num">{non_compliant}</div><div class="label">{L["non_compliant"]}</div></div>
  <div class="stat-card borderline"><div class="num">{borderline}</div><div class="label">{L["borderline"]}</div></div>
  <div class="stat-card rate"><div class="num">{rate}</div><div class="label">{L["compliance_rate"]}</div></div>
</div>

<div class="bar-chart">{bar_segments}</div>

<div class="section-title">{L["executive_summary"]}</div>
<div class="exec-table-wrapper">
<table class="exec-table">
<thead><tr>
  <th>ID</th><th>KPI</th><th>状态</th><th>严重度</th><th>{L["deviation"]}</th><th>{L["risk"]}</th>
</tr></thead>
<tbody>{exec_table}</tbody>
</table>
</div>

<div class="toolbar">
  <button class="filter-btn active" data-filter="all">{L["filter_all"]}</button>
  <button class="filter-btn" data-filter="未达标" style="color:#dc2626;border-color:#dc2626">{L["non_compliant"]}</button>
  <button class="filter-btn" data-filter="临界" style="color:#ea580c;border-color:#ea580c">{L["borderline"]}</button>
  <button class="filter-btn" data-filter="达标" style="color:#16a34a;border-color:#16a34a">{L["compliant"]}</button>
  <button class="filter-btn" data-filter="review">{L["needs_review"]}</button>
  <input type="text" id="searchBox" placeholder="{L["search_placeholder"]}">
</div>

<div class="main-layout">
  <div class="detail-panel">
    <div class="section-title">{L["detail_title"]}</div>
    {cards_html}
  </div>
  <div class="side-panel">
    <div class="panel-tabs">
      <div class="panel-tab active" data-panel="kpi">{L["kpi_panel_title"]}</div>
      <div class="panel-tab" data-panel="perf">{L["performance_panel_title"]}</div>
    </div>
    <div class="panel-content active" id="panel-kpi">{kpi_html}</div>
    <div class="panel-content" id="panel-perf">{perf_html}</div>
  </div>
</div>

<script>
// Card toggle
document.querySelectorAll('.card-header').forEach(h=>{{
  h.addEventListener('click',()=>h.parentElement.classList.toggle('open'))
}});
// Filter
document.querySelectorAll('.filter-btn').forEach(btn=>{{
  btn.addEventListener('click',()=>{{
    document.querySelectorAll('.filter-btn').forEach(b=>b.classList.remove('active'));
    btn.classList.add('active');
    const f=btn.dataset.filter;
    document.querySelectorAll('.card').forEach(c=>{{
      if(f==='all') c.style.display='';
      else if(f==='review') c.style.display=c.dataset.review==='true'?'':'none';
      else c.style.display=c.dataset.status===f?'':'none';
    }});
  }})
}});
// Search
document.getElementById('searchBox').addEventListener('input',e=>{{
  const q=e.target.value.toLowerCase();
  document.querySelectorAll('.card').forEach(c=>{{
    c.style.display=c.textContent.toLowerCase().includes(q)?'':'none';
  }});
}});
// Exec table click → scroll to card
document.querySelectorAll('.clickable-row').forEach(r=>{{
  r.addEventListener('click',()=>{{
    const el=document.getElementById(r.dataset.target);
    if(el){{el.classList.add('open');el.scrollIntoView({{behavior:'smooth',block:'center'}})}}
  }})
}});
// Side panel tabs
document.querySelectorAll('.panel-tab').forEach(tab=>{{
  tab.addEventListener('click',()=>{{
    document.querySelectorAll('.panel-tab').forEach(t=>t.classList.remove('active'));
    document.querySelectorAll('.panel-content').forEach(p=>p.classList.remove('active'));
    tab.classList.add('active');
    document.getElementById('panel-'+tab.dataset.panel).classList.add('active');
  }})
}});
</script>
</body>
</html>'''
    return html


# ============================================================================
# CLI & Main
# ============================================================================

def _load_env(env_path: Path) -> None:
    """Load .env file if it exists."""
    if not env_path.exists():
        return
    for line in env_path.read_text(encoding="utf-8").splitlines():
        line = line.strip()
        if not line or line.startswith("#"):
            continue
        if "=" in line:
            k, v = line.split("=", 1)
            k, v = k.strip(), v.strip().strip('"').strip("'")
            if k and k not in os.environ:
                os.environ[k] = v


def main() -> None:
    parser = argparse.ArgumentParser(
        description="合同履约审核工具 - Compare contract KPIs against operational performance"
    )
    parser.add_argument("kpi", nargs="?", default=str(DEFAULT_KPI),
                        help="KPI markdown file (default: data/kpi.md)")
    parser.add_argument("performance", nargs="?", default=str(DEFAULT_PERFORMANCE),
                        help="Performance data markdown file (default: data/operation_performance.md)")
    parser.add_argument("-o", "--output", default=str(DEFAULT_OUTPUT_DIR),
                        help="Output directory (default: output/)")
    parser.add_argument("-l", "--lang", choices=["zh", "en"], default="zh",
                        help="Output language (default: zh)")
    parser.add_argument("-w", "--workers", type=int, default=8,
                        help="Parallel worker threads (default: 8)")
    parser.add_argument("--no-cache", action="store_true",
                        help="Disable LLM response caching")
    args = parser.parse_args()

    logging.basicConfig(level=logging.INFO, format="%(levelname)s %(message)s")

    # Load .env
    _load_env(SCRIPT_DIR.parent / ".env")

    kpi_path = Path(args.kpi)
    perf_path = Path(args.performance)
    output_dir = Path(args.output)
    output_dir.mkdir(parents=True, exist_ok=True)

    LOGGER.info("KPI file:         %s", kpi_path)
    LOGGER.info("Performance file: %s", perf_path)

    kpi_text = kpi_path.read_text(encoding="utf-8")
    perf_text = perf_path.read_text(encoding="utf-8")

    # Parse KPI sections
    sections = parse_kpi_sections(kpi_text)
    LOGGER.info("Parsed %d KPI sections", len(sections))

    # Init cache & client
    cache = None if args.no_cache else LLMCache(output_dir / ".cache")
    client = LLMClient(cache=cache)

    # Parallel evaluation
    judgments: List[FulfillmentJudgment] = [None] * len(sections)  # type: ignore
    with ThreadPoolExecutor(max_workers=args.workers) as pool:
        future_map = {
            pool.submit(client.evaluate, sec, perf_text): idx
            for idx, sec in enumerate(sections)
        }
        done = 0
        for future in as_completed(future_map):
            idx = future_map[future]
            try:
                judgments[idx] = future.result()
            except Exception as e:
                LOGGER.error("Section %s failed: %s", sections[idx].section_id, e)
                judgments[idx] = client._fallback(sections[idx], str(e))
            done += 1
            if done % 5 == 0 or done == len(sections):
                LOGGER.info("Progress: %d/%d", done, len(sections))

    if cache:
        cache.flush()
        LOGGER.info("Cache: %d hits, %d misses", client._hits, client._misses)

    # Output timestamp
    ts = datetime.now().strftime("%Y%m%d_%H%M%S")
    stem = kpi_path.stem

    # 1. JSON results
    json_path = output_dir / f"{stem}_{ts}_fulfillment.json"
    json_data = [j.to_dict() for j in judgments if j]
    json_path.write_text(json.dumps(json_data, ensure_ascii=False, indent=2), encoding="utf-8")
    LOGGER.info("JSON: %s", json_path)

    # 2. HTML report
    html_path = output_dir / f"{stem}_{ts}_report.html"
    html_content = generate_report_html(
        [j for j in judgments if j],
        kpi_text, perf_text,
        lang=args.lang,
        timestamp=ts,
    )
    html_path.write_text(html_content, encoding="utf-8")
    LOGGER.info("HTML: %s", html_path)

    # Summary
    real_judgments = [j for j in judgments if j]
    total = len(real_judgments)
    compliant = sum(1 for j in real_judgments if j.status == "达标")
    non_compliant = sum(1 for j in real_judgments if j.status == "未达标")
    borderline = sum(1 for j in real_judgments if j.status == "临界")
    na_count = sum(1 for j in real_judgments if j.status == "不适用")
    applicable = total - na_count
    rate = f"{compliant / applicable * 100:.1f}%" if applicable > 0 else "N/A"

    print(f"\n{'='*50}")
    print(f" 合同履约审核完成")
    print(f"{'='*50}")
    print(f" 考核项: {total}  (适用: {applicable})")
    print(f" 达标: {compliant}  未达标: {non_compliant}  临界: {borderline}  不适用: {na_count}")
    print(f" 综合达标率: {rate}")
    print(f" JSON: {json_path}")
    print(f" HTML: {html_path}")
    print(f"{'='*50}\n")


if __name__ == "__main__":
    main()
