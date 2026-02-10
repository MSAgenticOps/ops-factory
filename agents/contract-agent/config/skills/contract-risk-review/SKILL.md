---
name: contract-risk-review
description: |
  Contract risk review tool. Automatically parses DOCX and PDF contract documents, compares against baseline standards, and identifies risk clauses.

  Use cases:
  - Review vendor contracts, service agreements, outsourcing contracts, etc.
  - Identify conflicts between contract clauses and company standards
  - Generate risk assessment reports for legal/procurement/management reference

  Trigger phrases: review contract, contract risk, baseline comparison, contract audit, contract review

  Outputs (named as {contract}_{timestamp}_{type}.{ext}):
  - {contract}_{timestamp}_judgments.json: Structured judgment results
  - {contract}_{timestamp}_annotated.docx/pdf: Color-highlighted contract with comments (red=high risk, orange=medium risk, blue=low risk)
  - {contract}_{timestamp}_report.html: Interactive HTML report (executive summary + risk distribution + contract vs baseline comparison)
---

# Contract Risk Review

Automatically review contract clauses and identify conflicts and risks against baseline standards.

## Features

- **Three-tier risk classification**: Red line (never allowed), Orange (strategically negotiable), Blue (wording risk)
- **Smart chunking**: Automatically splits long clauses to avoid exceeding LLM context limits
- **Response caching**: Same content won't call LLM repeatedly, saving costs
- **PDF support**: Accepts both .docx and .pdf contracts with full annotation support
- **Color annotations**: Document highlighting + comments at a glance (Word comments for DOCX, highlight annotations for PDF)
- **Interactive comparison**: HTML comparison view with filtering and expandable details
- **Bilingual support**: Chinese/English output

## Quick Start

1. Set environment variables or `.env` file
2. Run review with contract and baseline files

```bash
cd contract-risk-review

# Specify contract and baseline files
python scripts/run_review.py path/to/contract.docx path/to/baseline.md

# Only specify contract, use default baseline (data/baseline.md)
python scripts/run_review.py path/to/contract.pdf

# Use all defaults (data/contract.docx, data/baseline.md)
python scripts/run_review.py
```

## Directory Structure

```
contract-risk-review/
├── scripts/
│   └── run_review.py      # Main script
├── data/
│   ├── contract.docx/pdf   # Contract to review (.docx or .pdf)
│   └── baseline.md        # Review baseline
├── output/                          # Output files named as {contract}_{timestamp}_{type}.{ext}
│   ├── contract_20260205_143000_judgments.json      # JSON results
│   ├── contract_20260205_143000_annotated.docx/pdf  # Annotated document
│   ├── contract_20260205_143000_report.html         # HTML report (summary + comparison)
│   └── .cache/                                      # LLM response cache
└── .env                   # Environment config
```

## Environment Configuration

Create `.env` file:

```env
OPENAI_API_KEY=your-api-key
OPENAI_BASE_URL=https://api.deepseek.com
OPENAI_MODEL=deepseek-chat
```

Supports OpenAI-compatible APIs (DeepSeek, Azure OpenAI, GLM, etc.).

## Command Parameters

```bash
python scripts/run_review.py [CONTRACT] [BASELINE] [OPTIONS]

Positional arguments:
  CONTRACT                Contract file path, .docx or .pdf (default: data/contract.docx)
  BASELINE                Baseline markdown file path (default: data/baseline.md)

Options:
  -o, --output DIR            Output directory (default: output/)
  -l, --lang {zh,en}          Output language (default: zh)
  -w, --workers N             Parallel threads (default: 8)
  --no-cache                  Disable LLM caching
  --max-section-chars N       Maximum characters per clause (default: 8000)
```

## Output Description

All output files are named as `{contract}_{timestamp}_{type}.{ext}`, e.g. `my_contract_20260205_143000_judgments.json`.

### 1. {contract}\_{timestamp}\_judgments.json

Structured judgment results containing:
- `verdict`: Conflict | Clear Violation | Possible Inconsistency | No Conflict/Equivalent | Not Applicable/No Match
- `severity`: High | Medium | Low
- `type`: Direct Contradiction | Condition Weakening | Unilateral Rights Expansion | Missing Key Restrictions | Unbounded Liability/Scope | Imbalanced Penalty/Liability | Term Definition Conflict | Ambiguous Wording
- `confidence`: High | Medium | Low
- `needs_review`: true/false (whether manual review is needed)
- `rationale`: Judgment reasoning
- `evidence_contract`: Contract text evidence
- `evidence_baseline`: Baseline text evidence

### 2. {contract}\_{timestamp}\_annotated.docx / .pdf

Annotated contract document (format matches input):
- Red highlight = High risk (red line clauses)
- Yellow highlight = Medium risk (orange clauses)
- Blue highlight = Low risk (wording risk)
- DOCX: Native Word comments with judgment details
- PDF: Highlight annotations with sticky-note comments

### 3. {contract}\_{timestamp}\_report.html

Interactive HTML report combining executive summary and clause comparison:
- Risk statistics cards and distribution bar chart
- High/medium risk items summary table with anchor navigation
- Filter buttons (All/High/Medium/Low/Needs Review) + text search
- Left panel: Contract clause cards with full rationale and dual evidence
- Right panel: Baseline document (full Markdown rendering)
- Print-friendly (browser print / save as PDF)
- Responsive design, mobile-friendly

## Baseline Template

Default baseline `data/baseline.md` uses three-tier risk structure:

```markdown
# IT Outsourcing Service Contract Review Baseline

## Red Line Clauses (Never Allowed)
These clauses touch core company interests and must be rejected.

### 1. Liability and Compensation
- **Unlimited liability**: Must not accept unlimited compensation liability
- **Indirect losses**: Must not commit to liability for indirect losses

## Orange Clauses (Strategically Negotiable)
May cause loss of interests, but can be negotiated based on strategic considerations.

### 1. SLA and Performance
- **Response time relaxation**: P1 can be relaxed to 30 minutes

## Blue Clauses (Wording Risk)
Ambiguous wording may cause disputes, recommend modification.

### 1. Definitions and Scope
- **"Reasonable"/"Appropriate"**: Avoid subjective expressions, should be quantified
```

## Caching Mechanism

The tool automatically caches LLM responses:
- Cache location: `output/.cache/llm_cache.json`
- Cache key: `SHA256(model + section_text + baseline_text)`
- When running the same contract repeatedly, 100% cache hit rate

Disable caching:
```bash
python scripts/run_review.py --no-cache
```

## Long Document Handling

Large contracts are automatically chunked:
- Default maximum 3000 characters per chunk (~1500 tokens)
- Smart splitting at sentence boundaries
- Avoids breaking in the middle of semantic units

Adjust chunk size:
```bash
python scripts/run_review.py --max-section-chars 2000
```

## Dependencies

```bash
pip install -r requirements.txt
```

## Bilingual Support

```bash
# Chinese output (default)
python scripts/run_review.py --lang zh

# English output
python scripts/run_review.py --lang en
```
