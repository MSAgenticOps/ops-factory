import { useState, useEffect } from 'react'
import { useTranslation } from 'react-i18next'
import { useUser } from '../../../platform/providers/UserContext'
import { getIndicatorDetail } from '../../../../services/operationIntelligenceAPI'
import Pagination from '../../../platform/ui/primitives/Pagination'

interface AlarmDetailTableProps {
    envCode: string
    startTime: number
    endTime: number
}

export default function AlarmDetailTable({ envCode, startTime, endTime }: AlarmDetailTableProps) {
    const { t } = useTranslation()
    const { userId } = useUser()
    const [data, setData] = useState<Record<string, unknown>[]>([])
    const [page, setPage] = useState(1)
    const [total, setTotal] = useState(0)

    useEffect(() => {
        if (!envCode) return
        getIndicatorDetail('/qos/getAlarmIndicatorDetail', envCode, startTime, endTime, page, 10, userId)
            .then((res: { results?: Record<string, unknown>[]; total?: number }) => { setData(res.results || []); setTotal(res.total || 0) })
            .catch(() => { setData([]); setTotal(0) })
    }, [envCode, startTime, endTime, page, userId])

    return (
        <div className="alarm-detail-table">
            <table>
                <thead>
                    <tr>
                        <th>{t('operationIntelligence.timestamp')}</th>
                        <th>{t('operationIntelligence.alarmName')}</th>
                        <th>{t('operationIntelligence.severity')}</th>
                        <th>{t('operationIntelligence.dn')}</th>
                        <th>{t('operationIntelligence.count')}</th>
                        <th>{t('operationIntelligence.alarmDesc')}</th>
                        <th>{t('operationIntelligence.alarmDetail')}</th>
                    </tr>
                </thead>
                <tbody>
                    {data.length === 0 ? (
                        <tr><td colSpan={7}>{t('operationIntelligence.noData')}</td></tr>
                    ) : data.map((row, i) => (
                        <tr key={row.alarmName ? `${row.alarmName}-${row.occurUtc}-${i}` : i}>
                            <td>{row.occurUtc ? new Date(Number(row.occurUtc)).toLocaleString() : ''}</td>
                            <td>{String(row.alarmName ?? '')}</td>
                            <td>{String(row.severity ?? '')}</td>
                            <td>{String(row.dn ?? '')}</td>
                            <td>{String(row.count ?? '')}</td>
                            <td>{String(row.moi ?? '')}</td>
                            <td>{String(row.additionalInformation ?? '')}</td>
                        </tr>
                    ))}
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
