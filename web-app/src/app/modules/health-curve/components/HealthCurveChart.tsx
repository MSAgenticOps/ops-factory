import ReactECharts from 'echarts-for-react'
import type { EChartsOption } from 'echarts'
import { useTranslation } from 'react-i18next'
import type { HealthIndicatorPoint } from '../../../../types/health'

interface HealthCurveChartProps {
    points: HealthIndicatorPoint[]
    loading?: boolean
}

export default function HealthCurveChart({ points, loading = false }: HealthCurveChartProps) {
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
                    [{ yAxis: 0.9, itemStyle: { color: 'rgba(76,175,80,0.08)' } }, { yAxis: 1 }],
                    [{ yAxis: 0.7, itemStyle: { color: 'rgba(255,193,7,0.08)' } }, { yAxis: 0.9 }],
                    [{ yAxis: 0.5, itemStyle: { color: 'rgba(255,152,0,0.08)' } }, { yAxis: 0.7 }],
                    [{ yAxis: 0, itemStyle: { color: 'rgba(244,67,54,0.08)' } }, { yAxis: 0.5 }],
                ],
            },
        }],
    }

    if (loading) {
        return <div className="health-curve-chart-loading">{t('healthCurve.loading')}</div>
    }

    if (points.length === 0) {
        return <div className="health-curve-chart-empty">{t('healthCurve.noData')}</div>
    }

    return <ReactECharts option={option} style={{ height: 300 }} opts={{ renderer: 'svg' }} />
}
