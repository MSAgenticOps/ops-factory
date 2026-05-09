import { useState, useEffect, useCallback } from 'react'
import { useTranslation } from 'react-i18next'
import OperationIntelligenceChart from '../components/OperationIntelligenceChart'
import DimensionScoreCards from '../components/DimensionScoreCards'
import IndicatorDetailTable from '../components/IndicatorDetailTable'
import AlarmDetailTable from '../components/AlarmDetailTable'
import OperationIntelligenceFilters from '../components/OperationIntelligenceFilters'
import ContributionAnalysis from '../components/ContributionAnalysis'
import TopologyView from '../components/TopologyView'
import { useUser } from '../../../platform/providers/UserContext'
import { getHealthIndicator } from '../../../../services/operationIntelligenceAPI'
import type { HealthIndicatorPoint } from '../../../../types/operationIntelligence'
import '../styles/operation-intelligence.css'

type DetailTab = 'availability' | 'performance' | 'alarm'

export default function OperationIntelligencePage() {
    const { t } = useTranslation()
    const { userId } = useUser()
    const [envCode, setEnvCode] = useState<string>('')
    const [startTime, setStartTime] = useState<number>(Date.now() - 3600000)
    const [endTime, setEndTime] = useState<number>(Date.now())
    const [points, setPoints] = useState<HealthIndicatorPoint[]>([])
    const [activeTab, setActiveTab] = useState<DetailTab>('availability')
    const [loading, setLoading] = useState(false)

    const fetchData = useCallback(async () => {
        if (!envCode) return
        setLoading(true)
        try {
            const res = await getHealthIndicator(envCode, startTime, endTime, userId)
            setPoints(res.results || [])
        } catch {
            setPoints([])
        } finally {
            setLoading(false)
        }
    }, [envCode, startTime, endTime, userId])

    useEffect(() => { fetchData() }, [fetchData])

    useEffect(() => {
        const timer = setInterval(fetchData, 60000)
        return () => clearInterval(timer)
    }, [fetchData])

    const tabs: { key: DetailTab; label: string }[] = [
        { key: 'availability', label: t('operationIntelligence.availabilityDetail') },
        { key: 'performance', label: t('operationIntelligence.performanceDetail') },
        { key: 'alarm', label: t('operationIntelligence.alarmDetail') },
    ]

    return (
        <div className="page-container page-shell-wide operation-intelligence-page">
            <div className="mon-page-header">
                <div className="mon-header-left">
                    <h1 className="page-title" style={{ marginBottom: 0 }}>{t('operationIntelligence.title')}</h1>
                </div>
                <OperationIntelligenceFilters
                    envCode={envCode}
                    onEnvCodeChange={setEnvCode}
                    onTimeRangeChange={(start, end) => { setStartTime(start); setEndTime(end) }}
                    onRefresh={fetchData}
                />
            </div>

            <div className="mon-section oi-cards-row">
                <DimensionScoreCards envCode={envCode} startTime={startTime} endTime={endTime} />
                <ContributionAnalysis envCode={envCode} startTime={startTime} endTime={endTime} />
            </div>

            <div className="mon-section">
                <h2 className="ui-section-title">{t('operationIntelligence.chart')}</h2>
                <OperationIntelligenceChart points={points} loading={loading} />
            </div>

            <div className="config-tabs oi-tabs">
                {tabs.map(tab => (
                    <button
                        key={tab.key}
                        type="button"
                        className={`config-tab ${activeTab === tab.key ? 'config-tab-active' : ''}`}
                        onClick={() => setActiveTab(tab.key)}
                    >
                        {tab.label}
                    </button>
                ))}
            </div>

            <div className="mon-section oi-detail-section">
                {activeTab === 'alarm' ? (
                    <AlarmDetailTable envCode={envCode} startTime={startTime} endTime={endTime} />
                ) : (
                    <IndicatorDetailTable
                        envCode={envCode}
                        startTime={startTime}
                        endTime={endTime}
                        type={activeTab === 'availability' ? 'A' : 'P'}
                    />
                )}
            </div>

            <div className="mon-section">
                <h2 className="ui-section-title">{t('operationIntelligence.topology')}</h2>
                <TopologyView points={points} envCode={envCode} />
            </div>
        </div>
    )
}
