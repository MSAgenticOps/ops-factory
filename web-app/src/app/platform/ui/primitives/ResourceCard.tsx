import type { ReactElement, ReactNode } from 'react'
import Button from './Button'
import './ResourceCard.css'

export type ResourceStatusTone = 'neutral' | 'configured' | 'success' | 'warning' | 'danger'

export interface ResourceCardMetric {
    label: string
    value: ReactNode
    valueClassName?: string
}

interface ResourceCardProps {
    className?: string
    title: string
    statusLabel?: string
    statusTone?: ResourceStatusTone
    tags?: ReactNode
    summary?: ReactNode
    metrics: ResourceCardMetric[]
    footer?: ReactNode
}

interface ResourceCardActionProps {
    danger?: boolean
    onClick?: () => void
    children: ReactNode
}

function EditIcon(): ReactElement {
    return (
        <svg viewBox="0 0 20 20" fill="none" aria-hidden="true">
            <path
                d="M4.75 13.95 4 16l2.05-.75 8.5-8.5-1.3-1.3-8.5 8.5Z"
                stroke="currentColor"
                strokeWidth="1.7"
                strokeLinecap="round"
                strokeLinejoin="round"
            />
            <path
                d="m11.95 6.05 1.3 1.3m.65-.65 1.05-1.05a1.15 1.15 0 0 0 0-1.6l-.5-.5a1.15 1.15 0 0 0-1.6 0L11.8 4.6"
                stroke="currentColor"
                strokeWidth="1.7"
                strokeLinecap="round"
                strokeLinejoin="round"
            />
            <path
                d="M4 16h12"
                stroke="currentColor"
                strokeWidth="1.7"
                strokeLinecap="round"
            />
        </svg>
    )
}

function TrashIcon(): ReactElement {
    return (
        <svg viewBox="0 0 20 20" fill="none" aria-hidden="true">
            <path
                d="M6.5 5.5h7m-6 0V4.75A1.75 1.75 0 0 1 9.25 3h1.5A1.75 1.75 0 0 1 12.5 4.75v.75m-8 0h11m-1 0-.6 8.39a1.75 1.75 0 0 1-1.75 1.61H7.85A1.75 1.75 0 0 1 6.1 13.89L5.5 5.5m2.75 2.5v4m4-4v4"
                stroke="currentColor"
                strokeWidth="1.8"
                strokeLinecap="round"
                strokeLinejoin="round"
            />
        </svg>
    )
}

export function ResourceCardPrimaryAction({ onClick, children }: ResourceCardActionProps): ReactElement {
    return (
        <Button variant="primary" size="sm" className="resource-card-primary-button" onClick={onClick}>
            {children}
        </Button>
    )
}

export function ResourceCardDangerAction({ onClick, children }: ResourceCardActionProps): ReactElement {
    return (
        <button type="button" className="resource-card-danger-action" onClick={onClick}>
            {children}
        </button>
    )
}

interface ResourceCardIconActionProps {
    onClick?: () => void
    label: string
}

export function ResourceCardEditAction({ onClick, label }: ResourceCardIconActionProps): ReactElement {
    return (
        <button type="button" className="card-icon-action" onClick={onClick} title={label} aria-label={label}>
            <EditIcon />
        </button>
    )
}

export function ResourceCardDeleteAction({ onClick, label }: ResourceCardIconActionProps): ReactElement {
    return (
        <button
            type="button"
            className="card-icon-action card-icon-action-danger"
            onClick={onClick}
            title={label}
            aria-label={label}
        >
            <TrashIcon />
        </button>
    )
}

function getMetricColumnClass(count: number): string {
    if (count <= 1) return 'columns-1'
    if (count === 2) return 'columns-2'
    return 'columns-3'
}

export default function ResourceCard({
    className,
    title,
    statusLabel,
    statusTone = 'neutral',
    tags,
    summary,
    metrics,
    footer,
}: ResourceCardProps) {
    const cardClassName = ['resource-card', className].filter(Boolean).join(' ')
    const metricClassName = ['resource-card-metrics', getMetricColumnClass(metrics.length)].join(' ')

    return (
        <article className={cardClassName}>
            <div className="resource-card-header">
                <h3 className="resource-card-title" title={title}>
                    {title}
                </h3>
                {statusLabel && (
                    <span className={`resource-status resource-status-${statusTone}`}>
                        {statusLabel}
                    </span>
                )}
            </div>

            {(tags || summary) && (
                <div className="resource-card-summary">
                    {tags && <div className="resource-card-tags-slot">{tags}</div>}
                    {summary}
                </div>
            )}

            <div className={metricClassName}>
                {metrics.map(metric => (
                    <div key={metric.label} className="resource-card-metric">
                        <span className="resource-card-metric-label">{metric.label}</span>
                        <span className={['resource-card-metric-value', metric.valueClassName].filter(Boolean).join(' ')}>
                            {metric.value}
                        </span>
                    </div>
                ))}
            </div>

            {footer && (
                <div className="resource-card-footer">
                    {footer}
                </div>
            )}
        </article>
    )
}
