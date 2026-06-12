import { Routes, Route } from 'react-router-dom'
import Sidebar from './app/platform/navigation/Sidebar'
import { PreviewProvider, usePreview } from './app/platform/providers/PreviewContext'
import { InboxProvider } from './app/platform/providers/InboxContext'
import { ThreadUnreadProvider } from './app/platform/providers/ThreadUnreadContext'
import { SidebarProvider, useSidebar } from './app/platform/providers/SidebarContext'
import { RightPanelProvider, useRightPanel } from './app/platform/providers/RightPanelContext'
import { PagePanelProvider, usePagePanel } from './app/platform/providers/PagePanelContext'
import { isEmbedMode } from './utils/urlParams'
import { buildRoutes } from './app/platform/RouteBuilder'
import { AppShell } from './app/platform/AppShell'
import { RightPanelHost } from './app/platform/RightPanelHost'
import { useEnabledModules } from './app/platform/useEnabledModules'
import { useRouteDiagnostics } from './app/platform/logging/useRouteDiagnostics'

const IS_EMBED = isEmbedMode()

function AppContent() {
    const { previewFile, isPreviewFullscreen } = usePreview()
    const { isCollapsed } = useSidebar()
    const { isMarketOpen } = useRightPanel()
    const { panel } = usePagePanel()
    const isPreviewOpen = !!previewFile
    const isPagePanelOpen = !!panel && !isMarketOpen && !isPreviewOpen
    const isRightPanelOpen = isMarketOpen || isPreviewOpen || isPagePanelOpen
    const rightPanelMode = (() => {
        if (isMarketOpen) return 'panel-drawer'
        if (isPreviewOpen) return `panel-preview${isPreviewFullscreen ? ' panel-preview-fullscreen' : ''}`
        if (isPagePanelOpen) return `panel-${panel.mode}`
        return ''
    })()
    const isEmbed = IS_EMBED
    const enabledModules = useEnabledModules()
    const routes = buildRoutes(enabledModules)

    useRouteDiagnostics(enabledModules)

    return (
        <AppShell
            isEmbed={isEmbed}
            isCollapsed={isCollapsed}
            isRightPanelOpen={isRightPanelOpen}
            rightPanelMode={rightPanelMode}
            sidebar={<Sidebar />}
            rightPanel={<RightPanelHost />}
        >
            <Routes>{routes}</Routes>
        </AppShell>
    )
}

export default function App() {
    return (
        <Routes>
            <Route path="/*" element={
                <SidebarProvider>
                    <InboxProvider>
                        <ThreadUnreadProvider>
                            <PreviewProvider>
                                <RightPanelProvider>
                                    <PagePanelProvider>
                                        <AppContent />
                                    </PagePanelProvider>
                                </RightPanelProvider>
                            </PreviewProvider>
                        </ThreadUnreadProvider>
                    </InboxProvider>
                </SidebarProvider>
            } />
        </Routes>
    )
}
