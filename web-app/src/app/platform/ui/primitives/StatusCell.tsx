import StatusIcon, { type StatusTone } from './StatusIcon'
import './StatusCell.css'

interface StatusCellProps {
    tone: StatusTone
    label: string
    className?: string
}

export default function StatusCell({ tone, label, className }: StatusCellProps) {
    const classes = ['status-cell', className].filter(Boolean).join(' ')

    return (
        <span className={classes} title={label}>
            <StatusIcon tone={tone} label={label} />
        </span>
    )
}
