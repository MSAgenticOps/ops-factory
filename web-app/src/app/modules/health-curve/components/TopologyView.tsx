import { useMemo } from 'react'
import { useTranslation } from 'react-i18next'
import ReactECharts from 'echarts-for-react'
import type { HealthIndicatorPoint } from '../../../../types/health'

interface TopologyViewProps {
    points: HealthIndicatorPoint[]
    envCode: string
}

export default function TopologyView({ points, envCode }: TopologyViewProps) {
    const { t } = useTranslation()

    const latest = points.length > 0 ? parseFloat(points[points.length - 1].value) : null

    const statusColor = (v: number | null) => {
        if (v === null) return '#999'
        if (v >= 0.9) return '#4caf50'
        if (v >= 0.7) return '#ffc107'
        if (v >= 0.5) return '#ff9800'
        return '#f44336'
    }

    const statusLabel = (v: number | null) => {
        if (v === null) return '--'
        if (v >= 0.9) return t('healthCurve.good')
        if (v >= 0.7) return t('healthCurve.warning')
        if (v >= 0.5) return t('healthCurve.orange')
        return t('healthCurve.critical')
    }

    const option = useMemo(() => {
        const hs = latest !== null ? latest.toFixed(2) : '--'
        const color = statusColor(latest)

        const nodes = [
            { name: envCode || 'System', x: 300, y: 100, symbolSize: 50, itemStyle: { color } },
            { name: t('healthCurve.availability'), x: 150, y: 220, symbolSize: 35, itemStyle: { color: '#4caf50' } },
            { name: t('healthCurve.performance'), x: 300, y: 250, symbolSize: 35, itemStyle: { color: '#2196f3' } },
            { name: t('healthCurve.resource'), x: 450, y: 220, symbolSize: 35, itemStyle: { color: '#ff9800' } }
        ]

        const links = [
            { source: envCode || 'System', target: t('healthCurve.availability') },
            { source: envCode || 'System', target: t('healthCurve.performance') },
            { source: envCode || 'System', target: t('healthCurve.resource') }
        ]

        return {
            title: {
                text: `${t('healthCurve.topology')}: ${hs}  (${statusLabel(latest)})`,
                left: 'center',
                top: 5,
                textStyle: { fontSize: 14 }
            },
            tooltip: {},
            series: [{
                type: 'graph',
                layout: 'none',
                roam: false,
                label: { show: true, fontSize: 11 },
                edgeSymbol: ['none', 'arrow'],
                edgeSymbolSize: [4, 10],
                nodes,
                links,
                lineStyle: { color: '#aaa', width: 1.5, curveness: 0 }
            }]
        }
    }, [points, envCode, t])

    if (!envCode) {
        return <div className="topology-view">{t('healthCurve.noData')}</div>
    }

    return (
        <div className="topology-view">
            <ReactECharts option={option} style={{ height: 300 }} />
        </div>
    )
}
