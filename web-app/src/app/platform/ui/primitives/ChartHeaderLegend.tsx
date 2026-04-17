import './ChartHeaderLegend.css'

export interface ChartHeaderLegendItem {
    label: string
    color: string
    dashed?: boolean
}

interface ChartHeaderLegendProps {
    items: ChartHeaderLegendItem[]
    className?: string
}

export default function ChartHeaderLegend({ items, className }: ChartHeaderLegendProps) {
    if (items.length === 0) {
        return null
    }

    const classes = ['chart-header-legend', className].filter(Boolean).join(' ')

    return (
        <div className={classes} aria-hidden="true">
            {items.map(item => (
                <span key={item.label} className="chart-header-legend-item">
                    <span
                        className={`chart-header-legend-swatch${item.dashed ? ' chart-header-legend-swatch-dashed' : ''}`}
                        style={{ color: item.color }}
                    />
                    <span>{item.label}</span>
                </span>
            ))}
        </div>
    )
}
