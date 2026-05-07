import { useState, useEffect } from 'react'
import { useTranslation } from 'react-i18next'
import { useUser } from '../../../platform/providers/UserContext'
import { getIndicatorDetail } from '../../../../services/healthCurveAPI'

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
                        <th>{t('healthCurve.timestamp')}</th>
                        <th>{t('healthCurve.alarmName')}</th>
                        <th>{t('healthCurve.severity')}</th>
                        <th>{t('healthCurve.dn')}</th>
                        <th>{t('healthCurve.count')}</th>
                        <th>{t('healthCurve.alarmDesc')}</th>
                        <th>{t('healthCurve.alarmDetail')}</th>
                    </tr>
                </thead>
                <tbody>
                    {data.length === 0 ? (
                        <tr><td colSpan={7}>{t('healthCurve.noData')}</td></tr>
                    ) : data.map((row, i) => (
                        <tr key={i}>
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
                <div className="pagination">
                    <button disabled={page <= 1} onClick={() => setPage(p => p - 1)}>&lt;</button>
                    <span>{page} / {Math.ceil(total / 10)}</span>
                    <button disabled={page >= Math.ceil(total / 10)} onClick={() => setPage(p => p + 1)}>&gt;</button>
                    <input
                        type="number"
                        min={1}
                        max={Math.ceil(total / 10)}
                        placeholder="页码"
                        onKeyDown={e => {
                            if (e.key === 'Enter') {
                                const v = parseInt((e.target as HTMLInputElement).value, 10)
                                if (v >= 1 && v <= Math.ceil(total / 10)) setPage(v)
                            }
                        }}
                    />
                    <button onClick={e => {
                        const input = (e.currentTarget as HTMLElement).previousElementSibling as HTMLInputElement
                        const v = parseInt(input.value, 10)
                        if (v >= 1 && v <= Math.ceil(total / 10)) setPage(v)
                    }}>跳转</button>
                </div>
            )}
        </div>
    )
}
