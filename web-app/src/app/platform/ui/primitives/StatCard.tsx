import type { ReactNode } from 'react'
import './StatCard.css'

type StatCardTone = 'neutral' | 'success' | 'warning' | 'danger'

interface StatCardProps {
    label: ReactNode
    value: ReactNode
    meta?: ReactNode
    tone?: StatCardTone
    icon?: ReactNode
    className?: string
}

export default function StatCard({
    label,
    value,
    meta,
    tone = 'neutral',
    icon,
    className,
}: StatCardProps) {
    const classes = ['stat-card', `stat-card-${tone}`, className].filter(Boolean).join(' ')

    return (
        <article className={classes}>
            <div className="stat-card-head">
                <div className="stat-card-label-row">
                    <span className="stat-card-label">{label}</span>
                    {icon ? <span className="stat-card-icon-slot">{icon}</span> : null}
                </div>
            </div>
            <strong className="stat-card-value">{value}</strong>
            {meta ? <div className="stat-card-meta">{meta}</div> : null}
        </article>
    )
}
