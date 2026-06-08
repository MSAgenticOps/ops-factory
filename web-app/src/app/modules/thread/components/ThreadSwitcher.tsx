import { useEffect, useRef, useState } from 'react'
import { useTranslation } from 'react-i18next'
import type { ThreadDescriptor } from '../../../platform/providers/ThreadUnreadContext'
import '../../../platform/chat/AgentSelector.css'

interface ThreadSwitcherProps {
    threads: ThreadDescriptor[]
    selectedKey: string | undefined
    onSelect: (key: string) => void
}

/**
 * Header pill dropdown to switch assistant/copilot — reuses the platform `AgentSelector` pill + menu styling
 * (so it matches the chat-input agent picker) but lists IM threads (label = agent name · channel), opening
 * downward from the page header. Single-select, unbounded item count.
 */
export default function ThreadSwitcher({ threads, selectedKey, onSelect }: ThreadSwitcherProps) {
    const { t } = useTranslation()
    const [isOpen, setIsOpen] = useState(false)
    const ref = useRef<HTMLDivElement>(null)

    useEffect(() => {
        const onClickOutside = (event: MouseEvent) => {
            if (ref.current && !ref.current.contains(event.target as Node)) setIsOpen(false)
        }
        document.addEventListener('mousedown', onClickOutside)
        return () => document.removeEventListener('mousedown', onClickOutside)
    }, [])

    const selected = threads.find(thread => thread.key === selectedKey) ?? threads[0]
    const labelOf = (thread: ThreadDescriptor) => `${thread.agentName} · ${channelLabel(thread.channelType, t)}`

    return (
        <div className="agent-selector" ref={ref}>
            <button
                type="button"
                className="agent-selector-trigger"
                onClick={() => setIsOpen(open => !open)}
                aria-label={t('thread.switcherLabel')}
            >
                <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth="1.9" width="14" height="14"
                    className="agent-icon">
                    <path d="M4 7a2 2 0 0 1 2-2h8a2 2 0 0 1 2 2v4a2 2 0 0 1-2 2H8l-3 3v-3H6a2 2 0 0 1-2-2z" />
                </svg>
                <span className="agent-name">{selected ? labelOf(selected) : ''}</span>
                <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth="2" width="12" height="12"
                    className={`chevron ${isOpen ? 'open' : ''}`}>
                    <polyline points="6 9 12 15 18 9" />
                </svg>
            </button>

            {isOpen && (
                <div className="agent-dropdown below">
                    {threads.map(thread => (
                        <button
                            key={thread.key}
                            type="button"
                            className={`agent-option ${thread.key === selected?.key ? 'selected' : ''}`}
                            onClick={() => {
                                onSelect(thread.key)
                                setIsOpen(false)
                            }}
                        >
                            {labelOf(thread)}
                            {thread.key === selected?.key && (
                                <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth="3"
                                    width="14" height="14" className="check-icon">
                                    <polyline points="20 6 9 17 4 12" />
                                </svg>
                            )}
                        </button>
                    ))}
                </div>
            )}
        </div>
    )
}

function channelLabel(type: string, t: (key: string) => string): string {
    if (type === 'wechat') return t('channels.type_wechat')
    if (type === 'whatsapp') return t('channels.type_whatsapp')
    return type
}
