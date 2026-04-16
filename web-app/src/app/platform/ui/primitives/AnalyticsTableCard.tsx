import type { ReactNode } from 'react'
import SectionCard from './SectionCard'
import './AnalyticsTableCard.css'

interface AnalyticsTableCardProps {
    title: ReactNode
    subtitle?: ReactNode
    children: ReactNode
    className?: string
}

export default function AnalyticsTableCard({
    title,
    subtitle,
    children,
    className,
}: AnalyticsTableCardProps) {
    return (
        <SectionCard
            title={title}
            subtitle={subtitle}
            className={['analytics-table-card', className].filter(Boolean).join(' ')}
            bodyClassName="analytics-table-card-body"
        >
            <div className="analytics-table-card-shell">
                {children}
            </div>
        </SectionCard>
    )
}
