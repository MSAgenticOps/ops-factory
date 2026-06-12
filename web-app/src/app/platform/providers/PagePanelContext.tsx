import { createContext, useCallback, useContext, useMemo, useState, type ReactNode } from 'react'

/**
 * A page-owned right-panel: a route can mount persistent content into the shared {@code RightPanelHost} slot
 * (the same one used by file preview / capability market) instead of hand-rolling its own side panel. The page
 * sets the panel on mount and clears it on unmount.
 */
export interface PagePanelState {
    /** Width-mode suffix → drives `.right-panel-host.open.<mode>` + `.main-wrapper…panel-<mode>` (e.g. `thread`). */
    mode: string
    /** 48px header title. */
    title: ReactNode
    /** Panel body content. */
    content: ReactNode
    /** Optional close handler; renders a close button in the panel header when provided. */
    onClose?: () => void
}

interface PagePanelContextValue {
    panel: PagePanelState | null
    setPagePanel: (panel: PagePanelState | null) => void
}

const PagePanelContext = createContext<PagePanelContextValue | null>(null)

export function PagePanelProvider({ children }: { children: ReactNode }) {
    const [panel, setPanel] = useState<PagePanelState | null>(null)
    const setPagePanel = useCallback((next: PagePanelState | null) => setPanel(next), [])
    const value = useMemo<PagePanelContextValue>(() => ({ panel, setPagePanel }), [panel, setPagePanel])
    return <PagePanelContext.Provider value={value}>{children}</PagePanelContext.Provider>
}

export function usePagePanel(): PagePanelContextValue {
    const ctx = useContext(PagePanelContext)
    if (!ctx) {
        throw new Error('usePagePanel must be used within a PagePanelProvider')
    }
    return ctx
}
