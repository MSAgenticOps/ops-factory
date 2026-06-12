import { useMemo } from 'react'
import { useTranslation } from 'react-i18next'
import ReactECharts from 'echarts-for-react'
import type { HealthIndicatorPoint } from '../../../../types/operationIntelligence'
import { CHART_COLORS } from '../styles/chart-colors'

interface TopologyViewProps {
    points: HealthIndicatorPoint[]
    envCode: string
}

export default function TopologyView({ points, envCode }: TopologyViewProps) {
    const { t } = useTranslation()

    const latest = points.length > 0 ? parseFloat(points[points.length - 1].value) : null

    const statusColor = (v: number | null) => {
        if (v === null) {
            return CHART_COLORS.neutral
        }
        if (v >= 0.9) {
            return CHART_COLORS.good
        }
        if (v >= 0.7) {
            return CHART_COLORS.warning
        }
        if (v >= 0.5) {
            return CHART_COLORS.orange
        }
        return CHART_COLORS.critical
    }

    const statusLabel = (v: number | null) => {
        if (v === null) {
            return '--'
        }
        if (v >= 0.9) {
            return t('operationIntelligence.good')
        }
        if (v >= 0.7) {
            return t('operationIntelligence.warning')
        }
        if (v >= 0.5) {
            return t('operationIntelligence.orange')
        }
        return t('operationIntelligence.critical')
    }

    const option = useMemo(() => {
        const color = statusColor(latest)

        const nodes = [
            { name: envCode || t('operationIntelligence.title'), x: 300, y: 100, symbolSize: 50, itemStyle: { color } },
            { name: t('operationIntelligence.availability'), x: 150, y: 220, symbolSize: 35, itemStyle: { color: CHART_COLORS.availability } },
            { name: t('operationIntelligence.performance'), x: 300, y: 250, symbolSize: 35, itemStyle: { color: CHART_COLORS.performance } },
            { name: t('operationIntelligence.resource'), x: 450, y: 220, symbolSize: 35, itemStyle: { color: CHART_COLORS.resource } }
        ]

        const links = [
            { source: envCode || t('operationIntelligence.title'), target: t('operationIntelligence.availability') },
            { source: envCode || t('operationIntelligence.title'), target: t('operationIntelligence.performance') },
            { source: envCode || t('operationIntelligence.title'), target: t('operationIntelligence.resource') }
        ]

        return {
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
                lineStyle: { color: CHART_COLORS.neutral, width: 1.5, curveness: 0 }
            }]
        }
    }, [points, envCode, t])

    if (!envCode) {
        return (
            <div className="empty-state">
                <div className="empty-state-title">{t('operationIntelligence.noData')}</div>
            </div>
        )
    }

    return (
        <div className="topology-view">
            <div className="topology-summary">
                {t('operationIntelligence.healthScore')}: {latest !== null ? latest.toFixed(2) : '--'} ({statusLabel(latest)})
            </div>
            <ReactECharts option={option} className="topology-chart" opts={{ renderer: 'svg' }} />
        </div>
    )
}
