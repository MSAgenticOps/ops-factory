import { useEffect, useState } from 'react'
import { useTranslation } from 'react-i18next'
import { useA2AProgress } from './a2aProgress'
import './A2AStatusLine.css'

interface A2AStatusLineProps {
    /** The call_agent tool-call id (equals the Notification event request_id used for binding). */
    requestId?: string
    /** Whether the call_agent tool is still running (drives the live timer vs the collapsed summary). */
    isPending?: boolean
}

function formatElapsed(ms: number): string {
    const total = Math.max(0, Math.floor(ms / 1000))
    const minutes = Math.floor(total / 60)
    const seconds = total % 60
    return `${minutes}:${seconds.toString().padStart(2, '0')}`
}

/**
 * The one-line A2A activity row hung under a `call_agent` tool card. While running it shows the target agent, the
 * latest action label (last-wins), a step count, and a purely local elapsed timer (independent of notification
 * frequency). On completion it collapses to a summary. Renders nothing when there is no progress for the request.
 */
export default function A2AStatusLine({ requestId, isPending }: A2AStatusLineProps) {
    const progress = useA2AProgress(requestId)
    const { t } = useTranslation()
    const [now, setNow] = useState(() => Date.now())

    useEffect(() => {
        if (!progress || !isPending) return
        const timer = window.setInterval(() => setNow(Date.now()), 1000)
        return () => window.clearInterval(timer)
    }, [progress, isPending])

    if (!progress) return null

    const agent = progress.targetAgent || t('chat.a2a.agentFallback')
    const elapsed = formatElapsed((isPending ? now : progress.updatedAt) - progress.startedAt)

    if (isPending) {
        return (
            <div className="a2a-status-line a2a-status-running" title={agent}>
                <span className="a2a-spinner" aria-hidden="true" />
                <span className="a2a-agent">{agent}</span>
                {progress.label && <span className="a2a-label">{progress.label}</span>}
                <span className="a2a-elapsed">{elapsed}</span>
            </div>
        )
    }

    return (
        <div className="a2a-status-line a2a-status-done" title={agent}>
            <span className="a2a-arrow" aria-hidden="true">↳</span>
            <span className="a2a-agent">{agent}</span>
            {typeof progress.toolCalls === 'number' && progress.toolCalls > 0 && (
                <span className="a2a-tools">{t('chat.a2a.toolCalls', { count: progress.toolCalls })}</span>
            )}
            <span className="a2a-elapsed">{elapsed}</span>
        </div>
    )
}
