import { useState, useEffect } from 'react'
import { useTranslation } from 'react-i18next'
import ReactECharts from 'echarts-for-react'
import { useUser } from '../../../platform/providers/UserContext'
import { getContributionData } from '../../../../services/healthCurveAPI'
import { CHART_COLORS } from '../styles/chart-colors'

interface ContributionAnalysisProps {
    envCode: string
    startTime: number
    endTime: number
}

interface ContributionItem {
    type: string
    contribution: number
}

export default function ContributionAnalysis({ envCode, startTime, endTime }: ContributionAnalysisProps) {
    const { t } = useTranslation()
    const { userId } = useUser()
    const [data, setData] = useState<ContributionItem[]>([])

    useEffect(() => {
        if (!envCode) { setData([]); return }
        getContributionData(envCode, startTime, endTime, userId)
            .then(res => setData(res.results || []))
            .catch(() => setData([]))
    }, [envCode, startTime, endTime, userId])

    const typeLabel = (type: string) => {
        if (type === 'A') return t('healthCurve.availability')
        if (type === 'P') return t('healthCurve.performance')
        return t('healthCurve.resource')
    }

    const typeColor = (type: string) => {
        if (type === 'A') return CHART_COLORS.availability
        if (type === 'P') return CHART_COLORS.performance
        return CHART_COLORS.resource
    }

    const option = data.length > 0 ? {
        grid: { left: 80, right: 40, top: 8, bottom: 8 },
        xAxis: { type: 'value' as const, show: false },
        yAxis: {
            type: 'category' as const,
            show: true,
            data: data.map(d => typeLabel(d.type)),
            axisLine: { show: false },
            axisTick: { show: false },
            axisLabel: { fontSize: 13, color: '#666' }
        },
        series: [{
            type: 'bar' as const,
            data: data.map(d => ({
                value: d.contribution,
                itemStyle: { color: typeColor(d.type) }
            })),
            barWidth: '40%',
            label: {
                show: true,
                position: 'right' as const,
                formatter: (p: { value: number }) => `${(p.value * 100).toFixed(2)}%`
            }
        }]
    } : null

    if (data.length === 0) {
        return <div className="contribution-analysis contribution-card">{t('healthCurve.noData')}</div>
    }

    return (
        <div className="contribution-analysis contribution-card">
            <span className="score-label">{t('healthCurve.contributionDetail')}</span>
            <ReactECharts option={option} style={{ height: 120 }} opts={{ renderer: 'svg' }} />
        </div>
    )
}
