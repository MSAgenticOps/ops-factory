import type { ReactNode } from 'react'
import './SectionCard.css'

interface SectionCardProps {
    title?: ReactNode
    subtitle?: ReactNode
    action?: ReactNode
    children: ReactNode
    className?: string
    bodyClassName?: string
}

export default function SectionCard({
    title,
    subtitle,
    action,
    children,
    className,
    bodyClassName,
}: SectionCardProps) {
    const classes = ['section-card', className].filter(Boolean).join(' ')
    const bodyClasses = ['section-card-body', bodyClassName].filter(Boolean).join(' ')

    return (
        <section className={classes}>
            {(title || subtitle || action) ? (
                <div className="section-card-header">
                    <div className="section-card-copy">
                        {title ? <h3 className="section-card-title">{title}</h3> : null}
                        {subtitle ? <p className="section-card-subtitle">{subtitle}</p> : null}
                    </div>
                    {action ? <div className="section-card-action">{action}</div> : null}
                </div>
            ) : null}
            <div className={bodyClasses}>
                {children}
            </div>
        </section>
    )
}
