import { useMemo } from 'react'
import { useTranslation } from 'react-i18next'
import type { Cluster, Host } from '../../../../types/host'
import TopologyNodeIcon from './TopologyNodeIcon'

type Props = {
    cluster: Cluster | null
    hosts: Host[]
    selectedHostId?: string | null
    onSelectHost?: (host: Host) => void
}

function getRoleLabel(role: Host['role'], t: (key: string, options?: Record<string, unknown>) => string): string {
    if (role === 'primary') return t('hostResource.hostRolePrimary')
    if (role === 'backup') return t('hostResource.hostRoleBackup')
    return t('hostResource.hostRoleNone')
}

export default function ClusterHostPanel({ cluster, hosts, selectedHostId, onSelectHost }: Props) {
    const { t } = useTranslation()

    const sortedHosts = useMemo(
        () => [...hosts].sort((a, b) => a.name.localeCompare(b.name)),
        [hosts],
    )

    if (!cluster) {
        return (
            <aside className="hr-cluster-host-panel hr-cluster-host-panel-empty">
                <div className="hr-cluster-host-panel-placeholder">
                    <TopologyNodeIcon kind="cluster" size={28} />
                    <h3>{t('hostResource.clusterHostsTitle')}</h3>
                    <p>{t('hostResource.selectClusterToViewHosts')}</p>
                </div>
            </aside>
        )
    }

    return (
        <aside className="hr-cluster-host-panel">
            <div className="hr-cluster-host-panel-header">
                <div className="hr-cluster-host-panel-title-wrap">
                    <TopologyNodeIcon kind="cluster" size={20} />
                    <div>
                        <div className="hr-cluster-host-panel-eyebrow">{t('hostResource.clusterHostsTitle')}</div>
                        <h3 className="hr-cluster-host-panel-title">{cluster.name}</h3>
                    </div>
                </div>
                <div className="hr-cluster-host-panel-meta">
                    <span className="hr-cluster-host-panel-tag">{cluster.type || t('hostResource.createCluster')}</span>
                    <span className="hr-cluster-host-panel-tag hr-cluster-host-panel-tag-count">
                        {t('hostResource.clusterHostCount', { count: sortedHosts.length })}
                    </span>
                </div>
            </div>

            <div className="hr-cluster-host-panel-summary">
                <div className="hr-cluster-host-panel-field">
                    <span>{t('hostResource.clusterName')}</span>
                    <strong>{cluster.name}</strong>
                </div>
                <div className="hr-cluster-host-panel-field">
                    <span>{t('hostResource.clusterType')}</span>
                    <strong>{cluster.type || '-'}</strong>
                </div>
                <div className="hr-cluster-host-panel-field">
                    <span>{t('hostResource.hostCount')}</span>
                    <strong>{sortedHosts.length}</strong>
                </div>
            </div>

            <div className="hr-cluster-host-panel-list">
                {sortedHosts.length === 0 ? (
                    <div className="hr-cluster-host-panel-empty-state">
                        {t('hostResource.noHostsInCluster')}
                    </div>
                ) : (
                    sortedHosts.map(host => (
                        <button
                            key={host.id}
                            type="button"
                            className={`hr-cluster-host-row ${selectedHostId === host.id ? 'hr-cluster-host-row-selected' : ''}`}
                            onClick={() => onSelectHost?.(host)}
                        >
                            <div className="hr-cluster-host-row-main">
                                <span className="hr-cluster-host-row-name">{host.name}</span>
                                <span className="hr-cluster-host-row-ip">{host.ip}</span>
                            </div>
                            <div className="hr-cluster-host-row-meta">
                                <span className="hr-cluster-host-row-role">
                                    {t('hostResource.hostRole')}: {getRoleLabel(host.role, t)}
                                </span>
                                {host.purpose && (
                                    <span className="hr-cluster-host-row-purpose">
                                        {t('hostResource.purpose')}: {host.purpose}
                                    </span>
                                )}
                            </div>
                        </button>
                    ))
                )}
            </div>
        </aside>
    )
}
