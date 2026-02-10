---
name: contract-fulfillment-review
description: |
  Contract fulfillment review tool. Compares contract KPI requirements against actual operational performance data to assess compliance.

  Use cases:
  - Assess whether vendor is meeting contractual SLA and KPI obligations
  - Identify non-compliant items and quantify deviations
  - Generate compliance reports for management review
  - Calculate potential service credit deductions or penalty exposure

  Trigger phrases: fulfillment review, KPI compliance, SLA compliance, contract performance, 履约审核, 履约分析, KPI达标

  Outputs (named as {kpi}_{timestamp}_{type}.{ext}):
  - {kpi}_{timestamp}_fulfillment.json: Structured compliance judgment results
  - {kpi}_{timestamp}_report.html: Interactive HTML report (executive summary + detailed KPI analysis + side-by-side reference)
---

# Contract Fulfillment Review

Compare contract KPI requirements against actual operational performance data to determine compliance status.

## Features

- **Three-level compliance status**: Compliant (达标), Non-Compliant (未达标), Borderline (临界)
- **Severity assessment**: High/Medium/Low risk for non-compliant items
- **Quantified deviation**: Precise gap analysis between target and actual
- **Contract risk mapping**: Links non-compliance to specific penalty clauses
- **Actionable recommendations**: Concrete improvement suggestions per KPI item
- **Interactive HTML report**: Filtering, search, expandable cards, side panel reference
- **Response caching**: SHA256-based LLM cache for cost efficiency
- **Bilingual support**: Chinese/English output

## Quick Start

```bash
cd contract-fulfillment-review

# Use defaults (data/kpi.md + data/operation_performance.md)
python scripts/run_review.py

# Specify files
python scripts/run_review.py path/to/kpi.md path/to/performance.md

# Options
python scripts/run_review.py -o results/ -l en -w 4 --no-cache
```

## Directory Structure

```
contract-fulfillment-review/
├── SKILL.md
├── scripts/
│   └── run_review.py          # Main script
├── data/
│   ├── kpi.md                 # Contract KPI requirements
│   └── operation_performance.md  # Actual operational data
├── output/
│   ├── kpi_{timestamp}_fulfillment.json  # JSON results
│   ├── kpi_{timestamp}_report.html       # HTML report
│   └── .cache/                           # LLM response cache
└── .env                       # Environment config
```

## Environment Configuration

Create `.env` file:

```env
OPENAI_API_KEY=your-api-key
OPENAI_BASE_URL=https://api.deepseek.com/v1
OPENAI_MODEL=deepseek-chat
```

Supports OpenAI-compatible APIs (DeepSeek, Azure OpenAI, GLM, etc.).

## Command Parameters

```bash
python scripts/run_review.py [KPI] [PERFORMANCE] [OPTIONS]

Positional arguments:
  KPI                   KPI markdown file (default: data/kpi.md)
  PERFORMANCE           Performance markdown file (default: data/operation_performance.md)

Options:
  -o, --output DIR      Output directory (default: output/)
  -l, --lang {zh,en}    Output language (default: zh)
  -w, --workers N       Parallel threads (default: 8)
  --no-cache            Disable LLM caching
```

## Output Description

### 1. {kpi}\_{timestamp}\_fulfillment.json

Structured compliance results per KPI section:
- `status`: 达标 | 未达标 | 临界 | 不适用
- `severity`: 高 | 中 | 低
- `deviation`: Quantified gap (e.g., "可用性差距0.04%")
- `risk_description`: Contract penalty exposure
- `rationale`: Analysis reasoning
- `evidence_kpi`: Quoted KPI requirement
- `evidence_performance`: Quoted performance data
- `recommendation`: Improvement action
- `confidence`: 高 | 中 | 低
- `needs_review`: true/false

### 2. {kpi}\_{timestamp}\_report.html

Interactive HTML compliance report:
- Statistics cards with compliance rate
- Compliance distribution bar chart
- Executive summary table (non-compliant + borderline items only)
- Filter buttons (All / Non-Compliant / Borderline / Compliant / Needs Review) + search
- Expandable detail cards with evidence and recommendations
- Side panel with tabbed KPI terms and performance data reference
- Print-friendly, responsive design

## Data Format

### KPI File (kpi.md)

Markdown with heading-based structure. Each heading section becomes a review item:

```markdown
# Contract KPIs

## 1. System Availability
### 1.1 Core System Availability
- **Target**: Monthly availability >= 99.9%
- **Penalty**: 2% deduction per 0.1% below target

## 2. Incident Response
### 2.1 Response Time
| Level | Target |
|-------|--------|
| P1    | <= 15 min |
```

### Performance File (operation_performance.md)

Free-form markdown monthly report with actual metrics and data tables.
