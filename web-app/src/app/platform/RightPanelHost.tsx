import type { ReactNode } from 'react'
import FilePreview from './preview/FilePreview'
import CapabilityMarketPanel from './panels/CapabilityMarketPanel'
import { useRightPanel } from './providers/RightPanelContext'
import { usePreview } from './providers/PreviewContext'
import { usePagePanel } from './providers/PagePanelContext'

export function RightPanelHost() {
    const { previewFile, isPreviewFullscreen } = usePreview()
    const { isMarketOpen, marketActiveTab, closeMarket, setMarketActiveTab } = useRightPanel()
    const { panel } = usePagePanel()

    const isPreviewOpen = !!previewFile
    // Market and file preview are user-triggered and take precedence over a page-owned panel.
    const isPagePanelOpen = !!panel && !isMarketOpen && !isPreviewOpen
    const isRightPanelOpen = isMarketOpen || isPreviewOpen || isPagePanelOpen

    let panelMode = ''
    if (isMarketOpen) {
        panelMode = 'drawer'
    } else if (isPreviewOpen) {
        panelMode = `preview${isPreviewFullscreen ? ' fullscreen' : ''}`
    } else if (isPagePanelOpen) {
        panelMode = panel.mode
    }

    let content: ReactNode = null
    if (isMarketOpen) {
        content = (
            <CapabilityMarketPanel
                isOpen={isMarketOpen}
                activeTab={marketActiveTab}
                onClose={closeMarket}
                onTabChange={setMarketActiveTab}
            />
        )
    } else if (isPreviewOpen) {
        content = <FilePreview embedded />
    } else if (isPagePanelOpen) {
        content = (
            <>
                <div className="right-panel-header">
                    <span className="right-panel-title">{panel.title}</span>
                    {panel.onClose && (
                        <button
                            type="button"
                            className="right-panel-close"
                            onClick={panel.onClose}
                            aria-label="Close panel"
                        >
                            ×
                        </button>
                    )}
                </div>
                <div className="right-panel-body">{panel.content}</div>
            </>
        )
    }

    return (
        <div className={`right-panel-host ${isRightPanelOpen ? 'open' : ''} ${panelMode}`}>
            {content}
        </div>
    )
}
