# UI Review Skill

Use this skill after completing UI/style changes to verify completeness and consistency.

## Checklist

1. **Inventory**: Grep the entire codebase for ALL instances of the changed pattern (CSS class names, component types, inline styles, style patterns). List every file and line number.
2. **Consistency check**: Verify every instance has been updated consistently. Flag any that were intentionally left unchanged with reasoning.
3. **Build verification**: Run `npm run build` in the affected package(s) and confirm zero errors.
4. **Responsive check**: Look for hardcoded widths/heights that should be responsive. Verify usage of existing CSS variables/design tokens.
5. **Summary**: List all files changed and the total count of instances found vs updated.

## Output Format

```
## Review Results

### Pattern searched: `<pattern>`
### Instances found: X
### Instances updated: Y

| File | Line | Status | Notes |
|------|------|--------|-------|
| ... | ... | Updated/Skipped | ... |

### Build: PASS/FAIL
### Issues found: <list or "None">
```
