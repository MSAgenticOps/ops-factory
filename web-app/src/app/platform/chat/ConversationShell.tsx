import type { ReactNode, RefObject } from 'react'
import './conversation-layout.css'

interface ConversationShellProps {
    /** Content of the 48px aligned top header (e.g. a session title or a breadcrumb switcher). */
    header?: ReactNode
    /** Messages content, rendered inside the centered, scrollable area. */
    children: ReactNode
    /** Composer, docked full-width at the bottom (automatically right-panel-aware). */
    composer?: ReactNode
    /** Scroll container ref forwarded to the messages area (for MessageList scroll anchoring). */
    scrollRef?: RefObject<HTMLDivElement | null>
}

/**
 * The shared conversation shell used by the chat page layout and the Assistant (thread) page: a 100vh column
 * with a 48px header aligned to the app's top bar, a centered scrollable message area, and a fixed full-width
 * composer dock. Reuses the platform `conversation-layout.css` so both surfaces stay visually identical.
 */
export default function ConversationShell({ header, children, composer, scrollRef }: ConversationShellProps) {
    return (
        <div className="conversation-shell">
            {header != null && <div className="chat-session-header">{header}</div>}
            <div className="chat-messages-area" ref={scrollRef}>
                <div className="chat-messages-scroll">
                    {children}
                </div>
            </div>
            {composer != null && (
                <div className="chat-input-area-bottom">
                    <div className="chat-input-area-inner">
                        {composer}
                    </div>
                </div>
            )}
        </div>
    )
}
