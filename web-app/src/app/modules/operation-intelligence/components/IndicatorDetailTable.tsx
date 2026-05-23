import { useMemo } from 'react'
import IndicatorDataTable from './IndicatorDataTable'

interface IndicatorDetailTableProps {
    envCode: string
    startTime: number
    endTime: number
    type: string
}

export default function IndicatorDetailTable({ envCode, startTime, endTime, type }: IndicatorDetailTableProps) {
    const columns = useMemo(() => {
        const base = [
            { headerKey: 'timestamp', render: (row: Record<string, unknown>) => row.timestamp ? new Date(Number(row.timestamp)).toLocaleString() : '' },
            { headerKey: 'indicatorName', render: (row: Record<string, unknown>) => String(row.indicatorName ?? '') },
            { headerKey: 'dn', render: (row: Record<string, unknown>) => String(row.dn ?? '') },
            { headerKey: 'score', render: (row: Record<string, unknown>) => String(row.dnIndicatorValue ?? '') },
        ]
        if (type === 'A') {
            return [...base,
                { headerKey: 'successRatio', render: (row: Record<string, unknown>) => (row.values as Record<string, string>)?.urlCluster_successRatio ?? '' },
                { headerKey: 'successCount', render: (row: Record<string, unknown>) => (row.values as Record<string, string>)?.urlCluster_Success ?? '' },
                { headerKey: 'totalCount', render: (row: Record<string, unknown>) => (row.values as Record<string, string>)?.urlCluster_TotalCount ?? '' },
            ]
        }
        return [...base,
            { headerKey: 'avgResTime', render: (row: Record<string, unknown>) => (row.values as Record<string, string>)?.urlCluster_averageResTime ?? '' },
            { headerKey: 'minResTime', render: (row: Record<string, unknown>) => (row.values as Record<string, string>)?.urlCluster_MinResTime ?? '' },
            { headerKey: 'maxResTime', render: (row: Record<string, unknown>) => (row.values as Record<string, string>)?.urlCluster_MaxResTime ?? '' },
        ]
    }, [type])

    const endpoint = type === 'A' ? '/qos/getAvailableIndicatorDetail' : '/qos/getPerformanceIndicatorDetail'

    return (
        <IndicatorDataTable
            className="indicator-detail-table"
            endpoint={endpoint}
            envCode={envCode}
            startTime={startTime}
            endTime={endTime}
            rowKey={(row, i) => row.indicatorName ? `${row.indicatorName}-${row.dn}-${row.timestamp}-${i}` : i}
            columns={columns}
        />
    )
}
