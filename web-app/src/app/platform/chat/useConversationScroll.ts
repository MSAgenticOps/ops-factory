import { useCallback, useEffect, useState, type RefObject } from 'react'

const BOTTOM_THRESHOLD_PX = 24
const USER_MESSAGE_TOP_ANCHOR_PX = 24
const USER_MESSAGE_TOP_TOLERANCE_PX = 12
const BOTTOM_CONTENT_GAP_PX = 24

function setScrollTop(element: HTMLElement, top: number, behavior: ScrollBehavior) {
    if (typeof element.scrollTo === 'function') {
        element.scrollTo({ top, behavior })
        return
    }
    element.scrollTop = top
}

interface ConversationScrollMessage {
    id?: string
    role: string
}

interface UseConversationScrollArgs {
    scrollContainerRef: RefObject<HTMLDivElement | null>
    messages: ReadonlyArray<ConversationScrollMessage>
    isLoading: boolean
    sessionId: string | null
}

interface UseConversationScrollResult {
    /** True when the latest message sits below the fold — drives the "jump to bottom" control. */
    showScrollToBottom: boolean
    /** Smoothly scrolls the conversation to the latest message. */
    handleJumpToBottom: () => void
    /** Call with a just-sent user message id to anchor it near the top so the streamed reply scrolls into view. */
    anchorSentMessage: (messageId: string) => void
    /** The message id currently being anchored (drives MessageList's anchor spacer); null when settled. */
    pendingAnchorId: string | null
}

/**
 * Conversation scroll affordances: a "jump to bottom" control that appears when the latest message is below the
 * fold, and "anchor a just-sent user message near the top" so the streamed reply scrolls into view.
 *
 * This MIRRORS the inline scroll logic in modules/chat/pages/ChatPage.tsx. It is kept as a standalone hook so the
 * Assistant (thread) page can reuse the identical behaviour WITHOUT modifying the chat page. If either copy
 * changes, keep both in sync (or migrate ChatPage onto this hook in a dedicated change).
 */
export function useConversationScroll({
    scrollContainerRef,
    messages,
    isLoading,
    sessionId,
}: UseConversationScrollArgs): UseConversationScrollResult {
    const [showScrollToBottom, setShowScrollToBottom] = useState(false)
    const [pendingAnchorId, setPendingAnchorId] = useState<string | null>(null)

    const resolveActiveScrollElement = useCallback((): HTMLElement => {
        const scrollContainer = scrollContainerRef.current
        if (scrollContainer && scrollContainer.scrollHeight - scrollContainer.clientHeight > BOTTOM_THRESHOLD_PX) {
            return scrollContainer
        }
        return document.scrollingElement instanceof HTMLElement ? document.scrollingElement : document.documentElement
    }, [scrollContainerRef])

    const getCurrentScrollTop = useCallback((element: HTMLElement): number => {
        const isDocumentScroll = element === document.scrollingElement
            || element === document.documentElement
            || element === document.body
        return isDocumentScroll ? window.scrollY : element.scrollTop
    }, [])

    const getMaxScrollTop = useCallback((element: HTMLElement): number => {
        return Math.max(element.scrollHeight - element.clientHeight, 0)
    }, [])

    const getBottomAnchorTop = useCallback((): number => {
        const composerSelector = '.chat-input-area-bottom .chat-input-area-inner'
        const inputInner = document.querySelector(composerSelector) as HTMLElement | null
        return inputInner ? inputInner.getBoundingClientRect().top - BOTTOM_CONTENT_GAP_PX : window.innerHeight - 180
    }, [])

    const getLastConversationElement = useCallback((): HTMLElement | null => {
        const messageRoot = document.querySelector('.chat-messages') as HTMLElement | null
        if (!messageRoot) return null
        const elements = Array.from(messageRoot.querySelectorAll('[data-message-id]'))
        return (elements[elements.length - 1] as HTMLElement | undefined) ?? null
    }, [])

    const getBottomScrollTarget = useCallback((): number => {
        const activeScrollElement = resolveActiveScrollElement()
        const lastElement = getLastConversationElement()
        if (!lastElement) return activeScrollElement.scrollHeight
        const currentTop = getCurrentScrollTop(activeScrollElement)
        const anchorBottomTop = getBottomAnchorTop()
        const delta = lastElement.getBoundingClientRect().bottom - anchorBottomTop
        return Math.max(currentTop + delta, 0)
    }, [getBottomAnchorTop, getCurrentScrollTop, getLastConversationElement, resolveActiveScrollElement])

    const getClampedBottomScrollTarget = useCallback((): number => {
        const activeScrollElement = resolveActiveScrollElement()
        return Math.min(getBottomScrollTarget(), getMaxScrollTop(activeScrollElement))
    }, [getBottomScrollTarget, getMaxScrollTop, resolveActiveScrollElement])

    const updateScrollToBottomVisibility = useCallback(() => {
        const activeScrollElement = resolveActiveScrollElement()
        const lastElement = getLastConversationElement()
        if (!lastElement) {
            setShowScrollToBottom(false)
            return
        }
        const currentTop = getCurrentScrollTop(activeScrollElement)
        const remainingScrollableDistance = Math.max(getMaxScrollTop(activeScrollElement) - currentTop, 0)
        const distancePastBottomAnchor = Math.max(lastElement.getBoundingClientRect().bottom - getBottomAnchorTop(), 0)
        setShowScrollToBottom(
            remainingScrollableDistance > BOTTOM_THRESHOLD_PX &&
            distancePastBottomAnchor > BOTTOM_THRESHOLD_PX,
        )
    }, [
        getBottomAnchorTop,
        getCurrentScrollTop,
        getLastConversationElement,
        getMaxScrollTop,
        resolveActiveScrollElement,
    ])

    useEffect(() => {
        updateScrollToBottomVisibility()
        const scrollContainer = scrollContainerRef.current
        if (scrollContainer) {
            scrollContainer.addEventListener('scroll', updateScrollToBottomVisibility, { passive: true })
        }
        window.addEventListener('scroll', updateScrollToBottomVisibility, { passive: true })
        window.addEventListener('resize', updateScrollToBottomVisibility)
        return () => {
            if (scrollContainer) {
                scrollContainer.removeEventListener('scroll', updateScrollToBottomVisibility)
            }
            window.removeEventListener('scroll', updateScrollToBottomVisibility)
            window.removeEventListener('resize', updateScrollToBottomVisibility)
        }
    }, [scrollContainerRef, updateScrollToBottomVisibility])

    useEffect(() => {
        const frame = window.requestAnimationFrame(() => {
            updateScrollToBottomVisibility()
        })
        return () => window.cancelAnimationFrame(frame)
    }, [messages, isLoading, sessionId, updateScrollToBottomVisibility])

    useEffect(() => {
        if (!pendingAnchorId) return
        if (!messages.some(message => message.id === pendingAnchorId && message.role === 'user')) return
        const frame = window.requestAnimationFrame(() => {
            const activeScrollElement = resolveActiveScrollElement()
            const messageSelector = `[data-message-id="${CSS.escape(pendingAnchorId)}"]`
            const targetElement = document.querySelector(messageSelector) as HTMLElement | null
            if (!targetElement) return
            const currentTop =
                activeScrollElement === document.scrollingElement ||
                activeScrollElement === document.documentElement ||
                activeScrollElement === document.body
                    ? window.scrollY
                    : activeScrollElement.scrollTop
            const delta = targetElement.getBoundingClientRect().top - USER_MESSAGE_TOP_ANCHOR_PX
            const isAnchored = Math.abs(delta) <= USER_MESSAGE_TOP_TOLERANCE_PX
            if (isAnchored) {
                setPendingAnchorId(null)
                return
            }
            setScrollTop(activeScrollElement, Math.max(currentTop + delta, 0), 'smooth')
        })
        return () => window.cancelAnimationFrame(frame)
    }, [messages, isLoading, pendingAnchorId, resolveActiveScrollElement])

    useEffect(() => {
        if (!pendingAnchorId) return
        const activeScrollElement = resolveActiveScrollElement()
        let frame: number | null = null
        const completeAnchorIfSettled = () => {
            frame = null
            const messageSelector = `[data-message-id="${CSS.escape(pendingAnchorId)}"]`
            const targetElement = document.querySelector(messageSelector) as HTMLElement | null
            if (!targetElement) return
            const currentTop = getCurrentScrollTop(activeScrollElement)
            const maxScrollTop = getMaxScrollTop(activeScrollElement)
            const delta = targetElement.getBoundingClientRect().top - USER_MESSAGE_TOP_ANCHOR_PX
            const isAnchored = Math.abs(delta) <= USER_MESSAGE_TOP_TOLERANCE_PX
            const isAtScrollLimit = Math.abs(maxScrollTop - currentTop) <= USER_MESSAGE_TOP_TOLERANCE_PX
            if (isAnchored || isAtScrollLimit) {
                setPendingAnchorId(null)
            }
        }
        const scheduleCheck = () => {
            if (frame !== null) {
                window.cancelAnimationFrame(frame)
            }
            frame = window.requestAnimationFrame(completeAnchorIfSettled)
        }
        activeScrollElement.addEventListener('scroll', scheduleCheck, { passive: true })
        scheduleCheck()
        return () => {
            activeScrollElement.removeEventListener('scroll', scheduleCheck)
            if (frame !== null) {
                window.cancelAnimationFrame(frame)
            }
        }
    }, [getCurrentScrollTop, getMaxScrollTop, pendingAnchorId, resolveActiveScrollElement])

    const handleJumpToBottom = useCallback(() => {
        const activeScrollElement = resolveActiveScrollElement()
        const bottomTarget = getClampedBottomScrollTarget()
        activeScrollElement.scrollTo({ top: bottomTarget, behavior: 'smooth' })
    }, [getClampedBottomScrollTarget, resolveActiveScrollElement])

    const anchorSentMessage = useCallback((messageId: string) => {
        setPendingAnchorId(messageId)
    }, [])

    return { showScrollToBottom, handleJumpToBottom, anchorSentMessage, pendingAnchorId }
}
