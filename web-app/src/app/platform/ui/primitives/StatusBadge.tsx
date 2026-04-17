import type { ReactNode } from 'react'
import StatusIcon, { type StatusTone } from './StatusIcon'
import './StatusBadge.css'

interface StatusBadgeProps {
    tone: StatusTone
    label: ReactNode
    icon?: boolean
    className?: string
}

export default function StatusBadge({
    tone,
    label,
    icon = true,
    className,
}: StatusBadgeProps) {
    const classes = ['status-badge', `status-badge-${tone}`, className].filter(Boolean).join(' ')

    return (
        <span className={classes}>
            {icon ? <StatusIcon tone={tone} size={14} className="status-badge-icon" /> : null}
            <span>{label}</span>
        </span>
    )
}
