import { useState } from 'react'
import { useTranslation } from 'react-i18next'
import PageHeader from '../../../platform/ui/primitives/PageHeader'
import KnowledgeGraphPage from './KnowledgeGraphPage'

type OperationWorkspaceTab = 'knowledge-graph'

export default function OperationIntelligenceWorkspacePage() {
    const { t } = useTranslation()
    const [activeTab, setActiveTab] = useState<OperationWorkspaceTab>('knowledge-graph')
    const tabs: Array<{ key: OperationWorkspaceTab; label: string }> = [
        { key: 'knowledge-graph', label: t('operationIntelligence.workspaceTabs.knowledgeGraph') },
    ]

    return (
        <div className="page-container sidebar-top-page page-shell-wide operation-intelligence-page">
            <PageHeader
                title={t('operationIntelligence.title')}
                subtitle={t('operationIntelligence.workspaceSubtitle')}
            />

            <div className="config-tabs operation-intelligence-main-tabs" role="tablist" aria-label={t('operationIntelligence.workspaceTabsLabel')}>
                {tabs.map(tab => (
                    <button
                        key={tab.key}
                        type="button"
                        role="tab"
                        aria-selected={activeTab === tab.key}
                        className={`config-tab ${activeTab === tab.key ? 'config-tab-active' : ''}`}
                        onClick={() => setActiveTab(tab.key)}
                    >
                        {tab.label}
                    </button>
                ))}
            </div>
            <KnowledgeGraphPage embedded />
        </div>
    )
}
