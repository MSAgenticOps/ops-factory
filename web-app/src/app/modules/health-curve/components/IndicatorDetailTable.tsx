import { useState, useEffect } from 'react'
import { useTranslation } from 'react-i18next'
import { useUser } from '../../../platform/providers/UserContext'
import { getIndicatorDetail } from '../../../../services/healthCurveAPI'
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

    useEffect(() => {
        if (!envCode) return
        const endpoint = type === 'A'
            ? '/qos/getAvailableIndicatorDetail'
            : '/qos/getPerformanceIndicatorDetail'
        getIndicatorDetail(endpoint, envCode, startTime, endTime, page, 10, userId)
            .then((res: { results?: Record<string, unknown>[]; total?: number }) => { setData(res.results || []); setTotal(res.total || 0) })
            .catch(() => { setData([]); setTotal(0) })
    }, [envCode, startTime, endTime, page, type, userId])

    return (
        <div className="indicator-detail-table">
            <table>
                <thead>
                    <tr>
                        <th>{t('healthCurve.timestamp')}</th>
                        <th>{t('healthCurve.indicatorName')}</th>
                        <th>{t('healthCurve.dn')}</th>
                        <th>{t('healthCurve.score')}</th>
                        {type === 'A' ? (
                            <>
                                <th>{t('healthCurve.successRatio')}</th>
                                <th>{t('healthCurve.successCount')}</th>
                                <th>{t('healthCurve.totalCount')}</th>
                            </>
                        ) : (
                            <>
                                <th>{t('healthCurve.avgResTime')}</th>
                                <th>{t('healthCurve.minResTime')}</th>
                                <th>{t('healthCurve.maxResTime')}</th>
                            </>
                        )}
                    </tr>
                </thead>
                <tbody>
                    {data.length === 0 ? (
                        <tr><td colSpan={7}>{t('healthCurve.noData')}</td></tr>
                    ) : data.map((row, i) => {
                        const values = row.values as Record<string, string> | null
                        return (
                            <tr key={row.indicatorName ? `${row.indicatorName}-${row.dn}-${row.timestamp}` : i}>
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
