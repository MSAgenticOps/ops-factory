import SectionCard from './SectionCard'
import './PieDistributionCard.css'

export interface PieDistributionItem {
    label: string
    value: number
}

interface PieDistributionCardProps {
    title?: string
    items: PieDistributionItem[]
    colors: string[]
    otherLabel: string
    maxVisibleItems?: number
    className?: string
    maxHeight?: number
    embedded?: boolean
}

export default function PieDistributionCard({
    title,
    items,
    colors,
    otherLabel,
    maxVisibleItems = 6,
    className,
    maxHeight = 430,
    embedded = false,
}: PieDistributionCardProps) {
    const sortedItems = [...items].sort((a, b) => b.value - a.value)
    const visibleItems = sortedItems.slice(0, maxVisibleItems)
    const remainingItems = sortedItems.slice(maxVisibleItems)
    const otherValue = remainingItems.reduce((sum, item) => sum + item.value, 0)
    const displayItems = otherValue > 0
        ? [...visibleItems, { label: otherLabel, value: otherValue }]
        : visibleItems

    const total = displayItems.reduce((sum, item) => sum + item.value, 0)
    const displayColors = displayItems.map((_, idx) => (
        idx === displayItems.length - 1 && otherValue > 0
            ? 'var(--chart-8)'
            : colors[idx % colors.length]
    ))

    const cx = 100
    const cy = 100
    const r = 80
    let currentAngle = -90

    const slices = displayItems.map((item, idx) => {
        const percentage = total > 0 ? item.value / total : 0
        const angle = percentage * 360
        const startAngle = currentAngle
        const endAngle = currentAngle + angle
        currentAngle = endAngle

        const startRad = (startAngle * Math.PI) / 180
        const endRad = (endAngle * Math.PI) / 180

        const x1 = cx + r * Math.cos(startRad)
        const y1 = cy + r * Math.sin(startRad)
        const x2 = cx + r * Math.cos(endRad)
        const y2 = cy + r * Math.sin(endRad)
        const largeArc = angle > 180 ? 1 : 0

        const pathD = percentage > 0
            ? `M ${cx} ${cy} L ${x1} ${y1} A ${r} ${r} 0 ${largeArc} 1 ${x2} ${y2} Z`
            : ''

        return {
            pathD,
            color: displayColors[idx],
            label: item.label,
            value: item.value,
            percent: total > 0 ? ((item.value / total) * 100).toFixed(1) : '0.0',
        }
    })

    const content = (
        <div className="pie-distribution-card-content" style={{ maxHeight }}>
            <div className="pie-distribution-chart">
                <svg viewBox="0 0 200 200" className="pie-distribution-svg">
                    {slices.map((slice, idx) => (
                        slice.pathD ? (
                            <path
                                key={`${slice.label}-${idx}`}
                                d={slice.pathD}
                                fill={slice.color}
                                stroke="white"
                                strokeWidth="2"
                            />
                        ) : null
                    ))}
                </svg>
            </div>
            <div className="pie-distribution-legend">
                {slices.map(slice => (
                    <div key={slice.label} className="pie-distribution-row">
                        <span className="pie-distribution-main">
                            <span className="pie-distribution-dot" style={{ background: slice.color }} />
                            <span className="pie-distribution-label" title={slice.label}>{slice.label}</span>
                        </span>
                        <span className="pie-distribution-value">{slice.value.toLocaleString()}</span>
                        <span className="pie-distribution-percent">{slice.percent}%</span>
                    </div>
                ))}
            </div>
        </div>
    )

    if (embedded) {
        return content
    }

    return (
        <SectionCard
            title={title}
            className={['pie-distribution-card', className].filter(Boolean).join(' ')}
            bodyClassName="pie-distribution-card-body"
        >
            {content}
        </SectionCard>
    )
}
