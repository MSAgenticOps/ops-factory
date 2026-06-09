import { useEffect, useRef, useState } from 'react'
import { useTranslation } from 'react-i18next'
import type { ThreadDescriptor } from '../../../platform/providers/ThreadUnreadContext'

interface ThreadSwitcherProps {
    threads: ThreadDescriptor[]
    selectedKey: string | undefined
    onSelect: (key: string) => void
}

/**
 * Header dropdown to switch assistant/copilot. Styled like the knowledge-base filter control (a compact
 * `radius-md` field, not a pill) with a custom menu (unbounded item count) opening downward. Each item is one
 * IM thread, labelled `agent · channel`; when two threads share the same agent + channel type (e.g. two WeChat
 * accounts) the channel *name* disambiguates them.
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
    const labelOf = (thread: ThreadDescriptor) => {
        const sameTypeCount = threads.filter(
            other => other.agentName === thread.agentName && other.channelType === thread.channelType,
        ).length
        const channel = sameTypeCount > 1 && thread.channelName
            ? thread.channelName
            : channelLabel(thread.channelType, t)
        return `${thread.agentName} · ${channel}`
    }

    return (
        <div className="thread-switcher" ref={ref}>
            <button
                type="button"
                className="thread-switcher-trigger"
                onClick={() => setIsOpen(open => !open)}
                aria-haspopup="listbox"
                aria-expanded={isOpen}
                aria-label={t('thread.switcherLabel')}
            >
                <span className="thread-switcher-label">{selected ? labelOf(selected) : ''}</span>
                <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth="2" width="14" height="14"
                    className={`thread-switcher-chevron ${isOpen ? 'open' : ''}`} aria-hidden="true">
                    <polyline points="6 9 12 15 18 9" />
                </svg>
            </button>

            {isOpen && (
                <div className="thread-switcher-menu" role="listbox">
                    {threads.map(thread => (
                        <button
                            key={thread.key}
                            type="button"
                            role="option"
                            aria-selected={thread.key === selected?.key}
                            className={`thread-switcher-option ${thread.key === selected?.key ? 'selected' : ''}`}
                            onClick={() => {
                                onSelect(thread.key)
                                setIsOpen(false)
                            }}
                        >
                            <span className="thread-switcher-option-label">{labelOf(thread)}</span>
                            {thread.key === selected?.key && (
                                <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth="3"
                                    width="14" height="14" className="thread-switcher-check" aria-hidden="true">
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
