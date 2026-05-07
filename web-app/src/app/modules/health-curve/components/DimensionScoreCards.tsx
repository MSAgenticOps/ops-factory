import { useState, useEffect } from 'react'
import { useTranslation } from 'react-i18next'
import ReactECharts from 'echarts-for-react'
import type { EChartsOption } from 'echarts'
import { useUser } from '../../../platform/providers/UserContext'
import { getIndicatorDetail, getResourceIndicatorDetail } from '../../../../services/healthCurveAPI'
import { CHART_COLORS } from '../styles/chart-colors'

interface DimensionScoreCardsProps {
    envCode: string
    startTime: number
    endTime: number
}

interface TimeSeriesPoint {
    timestamp: number
    value: number
}

export default function DimensionScoreCards({ envCode, startTime, endTime }: DimensionScoreCardsProps) {
    const { t } = useTranslation()
    const { userId } = useUser()
    const [availabilityData, setAvailabilityData] = useState<TimeSeriesPoint[]>([])
    const [performanceData, setPerformanceData] = useState<TimeSeriesPoint[]>([])
    const [resourceData, setResourceData] = useState<TimeSeriesPoint[]>([])

    const fetchDetailSeries = (endpoint: string, setter: (d: TimeSeriesPoint[]) => void) => {
        if (!envCode) { setter([]); return }
        getIndicatorDetail(endpoint, envCode, startTime, endTime, 1, 9999, userId)
            .then(res => {
                const results = res.results || []
                const grouped = new Map<number, { sum: number; count: number }>()
                for (const row of results) {
                    const ts = Number(row.timestamp)
                    const val = Number(row.dnIndicatorValue)
                    if (!grouped.has(ts)) grouped.set(ts, { sum: 0, count: 0 })
                    const entry = grouped.get(ts)!
                    entry.sum += val
                    entry.count++
                }
                const series: TimeSeriesPoint[] = []
                grouped.forEach((v, ts) => series.push({ timestamp: ts, value: Math.round((v.sum / v.count) * 100) / 100 }))
                series.sort((a, b) => a.timestamp - b.timestamp)
                setter(series)
            })
            .catch(() => setter([]))
    }

    useEffect(() => { fetchDetailSeries('/qos/getAvailableIndicatorDetail', setAvailabilityData) }, [envCode, startTime, endTime, userId])
    useEffect(() => { fetchDetailSeries('/qos/getPerformanceIndicatorDetail', setPerformanceData) }, [envCode, startTime, endTime, userId])
    useEffect(() => {
        if (!envCode) { setResourceData([]); return }
        getResourceIndicatorDetail(envCode, startTime, endTime, userId)
            .then(res => {
                const results = res.results || []
                const grouped = new Map<number, { sum: number; count: number }>()
                for (const row of results) {
                    const ts = Number(row.timestamp)
                    const val = Number(row.indicatorValue)
                    if (!grouped.has(ts)) grouped.set(ts, { sum: 0, count: 0 })
                    const entry = grouped.get(ts)!
                    entry.sum += val
                    entry.count++
                }
                const series: TimeSeriesPoint[] = []
                grouped.forEach((v, ts) => series.push({ timestamp: ts, value: Math.round((v.sum / v.count) * 100) / 100 }))
                series.sort((a, b) => a.timestamp - b.timestamp)
                setResourceData(series)
            })
            .catch(() => setResourceData([]))
    }, [envCode, startTime, endTime, userId])

    const buildChartOption = (data: TimeSeriesPoint[], color: string): EChartsOption => ({
        grid: { top: 8, right: 8, bottom: 8, left: 8 },
        xAxis: { type: 'time', show: false },
        yAxis: { type: 'value', show: false },
        tooltip: { trigger: 'axis' },
        series: [{
            type: 'line',
            smooth: true,
            symbol: 'none',
            lineStyle: { width: 2, color },
            data: data.map(d => [d.timestamp, d.value]),
        }],
    })

    return (
        <div className="dimension-score-cards">
            <div className="score-card score-card-chart">
                <span className="score-label">{t('healthCurve.availability')}</span>
                {availabilityData.length > 0 ? (
                    <ReactECharts option={buildChartOption(availabilityData, CHART_COLORS.availability)} style={{ height: 120 }} opts={{ renderer: 'svg' }} />
                ) : (
                    <div className="score-chart-empty">{t('healthCurve.noData')}</div>
                )}
            </div>
            <div className="score-card score-card-chart">
                <span className="score-label">{t('healthCurve.performance')}</span>
                {performanceData.length > 0 ? (
                    <ReactECharts option={buildChartOption(performanceData, CHART_COLORS.performance)} style={{ height: 120 }} opts={{ renderer: 'svg' }} />
                ) : (
                    <div className="score-chart-empty">{t('healthCurve.noData')}</div>
                )}
            </div>
            <div className="score-card score-card-chart">
                <span className="score-label">{t('healthCurve.resource')}</span>
                {resourceData.length > 0 ? (
                    <ReactECharts option={buildChartOption(resourceData, CHART_COLORS.resource)} style={{ height: 120 }} opts={{ renderer: 'svg' }} />
                ) : (
                    <div className="score-chart-empty">{t('healthCurve.noData')}</div>
                )}
            </div>
        </div>
    )
}
