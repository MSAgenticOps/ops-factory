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
} as const
