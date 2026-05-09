// Chart colors aligned with CSS variables defined in App.css
// --chart-1: Steel blue, --chart-2: Green (success), --chart-3: Orange (warning), --chart-4: Red (error)
export const CHART_COLORS = {
  availability: '#10b981',  // --chart-2 / --color-success
  performance: '#5b8db8',   // --chart-1
  resource: '#f59e0b',      // --chart-3 / --color-warning
  error: '#ef4444',         // --chart-4 / --color-error
  good: '#10b981',          // --chart-2
  warning: '#f59e0b',       // --chart-3
  orange: '#f59e0b',        // --chart-3
  critical: '#ef4444',      // --chart-4
  neutral: '#999',          // fallback / secondary
} as const

function hexToRgba(hex: string, alpha: number): string {
  const r = parseInt(hex.slice(1, 3), 16)
  const g = parseInt(hex.slice(3, 5), 16)
  const b = parseInt(hex.slice(5, 7), 16)
  return `rgba(${r},${g},${b},${alpha})`
}

export const CHART_COLORS_LIGHT = {
  good: hexToRgba(CHART_COLORS.good, 0.08),
  warning: hexToRgba(CHART_COLORS.warning, 0.08),
  orange: hexToRgba(CHART_COLORS.orange, 0.08),
  critical: hexToRgba(CHART_COLORS.critical, 0.08),
} as const
