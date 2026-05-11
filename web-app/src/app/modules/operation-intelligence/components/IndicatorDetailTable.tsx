import { useState, useEffect } from 'react'
import { useTranslation } from 'react-i18next'
import { useUser } from '../../../platform/providers/UserContext'
import { getIndicatorDetail } from '../../../../services/operationIntelligenceAPI'
import Pagination from '../../../platform/ui/primitives/Pagination'

interface IndicatorDetailTableProps {
    envCode: string
    startTime: number
    endTime: number
    type: string
}

export default function IndicatorDetailTable({ envCode, startTime, endTime, type }: IndicatorDetailTableProps) {
    const { t } = useTranslation()
    const { userId } = useUser()
    const [data, setData] = useState<Record<string, unknown>[]>([])
    const [page, setPage] = useState(1)
    const [total, setTotal] = useState(0)
    const [error, setError] = useState<string | null>(null)

    useEffect(() => {
        if (!envCode) {
            return
        }
        setError(null)
        const endpoint = type === 'A'
            ? '/qos/getAvailableIndicatorDetail'
            : '/qos/getPerformanceIndicatorDetail'
        getIndicatorDetail(endpoint, envCode, startTime, endTime, page, 10, userId)
            .then((res: { results?: Record<string, unknown>[]; total?: number }) => {
                setData(res.results || [])
                setTotal(res.total || 0)
            })
            .catch((err) => {
                setData([])
                setTotal(0)
                setError(err instanceof Error ? err.message : t('operationIntelligence.loadFailed'))
            })
    }, [envCode, startTime, endTime, page, type, userId, t])

    if (error) {
        return (
            <div className="conn-banner conn-banner-error">
                {t('operationIntelligence.loadFailedWithReason', { error })}
            </div>
        )
    }

    if (data.length === 0) {
        return (
            <div className="empty-state">
                <div className="empty-state-title">{t('operationIntelligence.noData')}</div>
            </div>
        )
    }

    return (
        <div className="indicator-detail-table">
            <table>
                <thead>
                    <tr>
                        <th>{t('operationIntelligence.timestamp')}</th>
                        <th>{t('operationIntelligence.indicatorName')}</th>
                        <th>{t('operationIntelligence.dn')}</th>
                        <th>{t('operationIntelligence.score')}</th>
                        {type === 'A' ? (
                            <>
                                <th>{t('operationIntelligence.successRatio')}</th>
                                <th>{t('operationIntelligence.successCount')}</th>
                                <th>{t('operationIntelligence.totalCount')}</th>
                            </>
                        ) : (
                            <>
                                <th>{t('operationIntelligence.avgResTime')}</th>
                                <th>{t('operationIntelligence.minResTime')}</th>
                                <th>{t('operationIntelligence.maxResTime')}</th>
                            </>
                        )}
                    </tr>
                </thead>
                <tbody>
                    {data.map((row, i) => {
                        const values = row.values as Record<string, string> | null
                        return (
                            <tr key={row.indicatorName ? `${row.indicatorName}-${row.dn}-${row.timestamp}-${i}` : i}>
                                <td>{row.timestamp ? new Date(Number(row.timestamp)).toLocaleString() : ''}</td>
                                <td>{String(row.indicatorName ?? '')}</td>
                                <td>{String(row.dn ?? '')}</td>
                                <td>{String(row.dnIndicatorValue ?? '')}</td>
                                {type === 'A' ? (
                                    <>
                                        <td>{values?.urlCluster_successRatio ?? ''}</td>
                                        <td>{values?.urlCluster_Success ?? ''}</td>
                                        <td>{values?.urlCluster_TotalCount ?? ''}</td>
                                    </>
                                ) : (
                                    <>
                                        <td>{values?.urlCluster_averageResTime ?? ''}</td>
                                        <td>{values?.urlCluster_MinResTime ?? ''}</td>
                                        <td>{values?.urlCluster_MaxResTime ?? ''}</td>
                                    </>
                                )}
                            </tr>
                        )
                    })}
                </tbody>
            </table>
            {total > 10 && (
                <Pagination
                    currentPage={page}
                    totalPages={Math.ceil(total / 10)}
                    pageSize={10}
                    totalItems={total}
                    onPageChange={setPage}
                />
            )}
        </div>
    )
}
