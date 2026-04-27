import { useMemo } from 'react'
import { useTranslation } from 'react-i18next'
import type { Cluster, ClusterGraphData, Host } from '../../../../types/host'
import TopologyNodeIcon from './TopologyNodeIcon'

type Props = {
    cluster: Cluster | null
    hosts: Host[]
    graphData: ClusterGraphData
    viewingClusterHosts: boolean
    onViewClusterHosts?: (clusterId: string) => void
}

type RelatedItem = {
    id: string
    label: string
}

export default function ClusterInsightPanel({
    cluster,
    hosts,
    graphData,
    viewingClusterHosts,
    onViewClusterHosts,
}: Props) {
    const { t } = useTranslation()

    const insights = useMemo(() => {
        if (!cluster) {
            return {
                upstreamClusters: [] as RelatedItem[],
                downstreamClusters: [] as RelatedItem[],
                businessServices: [] as RelatedItem[],
                primaryHosts: 0,
                backupHosts: 0,
                peerHosts: 0,
            }
        }

        const nodeMap = new Map(graphData.nodes.map(node => [node.id, node]))
        const upstreamMap = new Map<string, RelatedItem>()
        const downstreamMap = new Map<string, RelatedItem>()
        const businessMap = new Map<string, RelatedItem>()

        for (const edge of graphData.edges) {
            if (edge.target === cluster.id) {
                const sourceNode = nodeMap.get(edge.source)
                if (sourceNode?.nodeType === 'cluster') {
                    upstreamMap.set(sourceNode.id, { id: sourceNode.id, label: sourceNode.name })
                }
                if (sourceNode?.nodeType === 'business-service') {
                    businessMap.set(sourceNode.id, { id: sourceNode.id, label: sourceNode.name })
                }
            }

            if (edge.source === cluster.id) {
                const targetNode = nodeMap.get(edge.target)
                if (targetNode?.nodeType === 'cluster') {
                    downstreamMap.set(targetNode.id, { id: targetNode.id, label: targetNode.name })
                }
            }
        }

        let primaryHosts = 0
        let backupHosts = 0
        let peerHosts = 0

        for (const host of hosts) {
            if (host.role === 'primary') {
                primaryHosts += 1
            } else if (host.role === 'backup') {
                backupHosts += 1
            } else {
                peerHosts += 1
            }
        }

        return {
            upstreamClusters: [...upstreamMap.values()],
            downstreamClusters: [...downstreamMap.values()],
            businessServices: [...businessMap.values()],
            primaryHosts,
            backupHosts,
            peerHosts,
        }
    }, [cluster, graphData, hosts])

    if (!cluster) {
        return (
            <aside className="hr-cluster-insight-panel hr-cluster-insight-panel-empty">
                <div className="hr-cluster-insight-placeholder">
                    <TopologyNodeIcon kind="cluster" size={28} />
                    <h3>{t('hostResource.clusterInsightTitle')}</h3>
                    <p>{t('hostResource.selectClusterToInspect')}</p>
                </div>
            </aside>
        )
    }

    return (
        <aside className="hr-cluster-insight-panel">
            <div className="hr-cluster-insight-header">
                <div className="hr-cluster-insight-title-wrap">
                    <TopologyNodeIcon kind="cluster" size={20} />
                    <div className="hr-cluster-insight-title-block">
                        <div className="hr-cluster-insight-eyebrow">{t('hostResource.clusterInsightTitle')}</div>
                        <button
                            type="button"
                            className="hr-cluster-insight-title-button"
                            disabled={viewingClusterHosts}
                            onClick={() => onViewClusterHosts?.(cluster.id)}
                        >
                            <h3 className="hr-cluster-insight-title">{cluster.name}</h3>
                        </button>
                    </div>
                </div>
            </div>

            <div className="hr-cluster-insight-kpis">
                <div className="hr-cluster-insight-kpi">
                    <span>{t('hostResource.hostCount')}</span>
                    <strong>{hosts.length}</strong>
                </div>
                <div className="hr-cluster-insight-kpi">
                    <span>{t('hostResource.upstreamClusterCount')}</span>
                    <strong>{insights.upstreamClusters.length}</strong>
                </div>
                <div className="hr-cluster-insight-kpi">
                    <span>{t('hostResource.downstreamClusterCount')}</span>
                    <strong>{insights.downstreamClusters.length}</strong>
                </div>
                <div className="hr-cluster-insight-kpi">
                    <span>{t('hostResource.relatedBusinessCount')}</span>
                    <strong>{insights.businessServices.length}</strong>
                </div>
            </div>

            <div className="hr-cluster-insight-section">
                <div className="hr-cluster-insight-section-title">{t('hostResource.clusterSummaryTitle')}</div>
                <div className="hr-cluster-insight-field-list">
                    <div className="hr-cluster-insight-field">
                        <span>{t('hostResource.clusterType')}</span>
                        <strong>{cluster.type || '-'}</strong>
                    </div>
                    <div className="hr-cluster-insight-field">
                        <span>{t('hostResource.purpose')}</span>
                        <strong>{cluster.purpose || '-'}</strong>
                    </div>
                </div>
            </div>

            <div className="hr-cluster-insight-section">
                <div className="hr-cluster-insight-section-title">{t('hostResource.hostRoleDistributionTitle')}</div>
                <div className="hr-cluster-insight-role-grid">
                    <div className="hr-cluster-insight-role-card">
                        <span>{t('hostResource.hostRolePrimary')}</span>
                        <strong>{insights.primaryHosts}</strong>
                    </div>
                    <div className="hr-cluster-insight-role-card">
                        <span>{t('hostResource.hostRoleBackup')}</span>
                        <strong>{insights.backupHosts}</strong>
                    </div>
                    <div className="hr-cluster-insight-role-card">
                        <span>{t('hostResource.hostRoleNone')}</span>
                        <strong>{insights.peerHosts}</strong>
                    </div>
                </div>
            </div>

            <div className="hr-cluster-insight-section">
                <div className="hr-cluster-insight-section-title">{t('hostResource.relatedBusinessTitle')}</div>
                <TagList items={insights.businessServices} emptyText={t('hostResource.noRelatedBusiness')} />
            </div>

            <div className="hr-cluster-insight-section">
                <div className="hr-cluster-insight-section-title">{t('hostResource.upstreamClustersTitle')}</div>
                <TagList items={insights.upstreamClusters} emptyText={t('hostResource.noUpstreamClusters')} />
            </div>

            <div className="hr-cluster-insight-section">
                <div className="hr-cluster-insight-section-title">{t('hostResource.downstreamClustersTitle')}</div>
                <TagList items={insights.downstreamClusters} emptyText={t('hostResource.noDownstreamClusters')} />
            </div>
        </aside>
    )
}

function TagList({ items, emptyText }: { items: RelatedItem[]; emptyText: string }) {
    if (items.length === 0) {
        return <div className="hr-cluster-insight-empty-inline">{emptyText}</div>
    }

    return (
        <div className="hr-cluster-insight-tag-list">
            {items.map(item => (
                <span key={item.id} className="hr-cluster-insight-tag">
                    {item.label}
                </span>
            ))}
        </div>
    )
}
