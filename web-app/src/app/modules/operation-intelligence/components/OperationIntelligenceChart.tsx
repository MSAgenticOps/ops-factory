import ReactECharts from 'echarts-for-react'
import type { EChartsOption } from 'echarts'
import { useTranslation } from 'react-i18next'
import type { HealthIndicatorPoint } from '../../../../types/operationIntelligence'
import { CHART_COLORS_LIGHT } from '../styles/chart-colors'

interface OperationIntelligenceChartProps {
    points: HealthIndicatorPoint[]
    loading?: boolean
}

export default function OperationIntelligenceChart({ points, loading = false }: OperationIntelligenceChartProps) {
    const { t } = useTranslation()

    const option: EChartsOption = {
        xAxis: { type: 'time' },
        yAxis: {
            type: 'value',
            min: 0,
            max: 1,
            interval: 0.1,
            axisLabel: { formatter: '{value}' },
        },
        tooltip: {
            trigger: 'axis',
            formatter: (params: unknown) => {
                const p = Array.isArray(params) ? params[0] : params
                if (p && typeof p === 'object' && 'value' in p) {
                    const val = Array.isArray(p.value) ? p.value[1] : p.value
                    return `${Number(val).toFixed(2)}`
                }
                return ''
            },
        },
        series: [{
            type: 'line',
            smooth: true,
            data: points.map(p => [p.timestamp, parseFloat(p.value)]),
            markArea: {
                silent: true,
                data: [
                    [{ yAxis: 0.9, itemStyle: { color: CHART_COLORS_LIGHT.good } }, { yAxis: 1 }],
                    [{ yAxis: 0.7, itemStyle: { color: CHART_COLORS_LIGHT.warning } }, { yAxis: 0.9 }],
                    [{ yAxis: 0.5, itemStyle: { color: CHART_COLORS_LIGHT.orange } }, { yAxis: 0.7 }],
                    [{ yAxis: 0, itemStyle: { color: CHART_COLORS_LIGHT.critical } }, { yAxis: 0.5 }],
                ],
            },
        }],
    }

    if (loading) {
        return <div className="operation-intelligence-chart-loading">{t('operationIntelligence.loading')}</div>
    }

    if (points.length === 0) {
        return <div className="operation-intelligence-chart-empty">{t('operationIntelligence.noData')}</div>
    }

    return <ReactECharts option={option} style={{ height: 300 }} opts={{ renderer: 'svg' }} />
}
