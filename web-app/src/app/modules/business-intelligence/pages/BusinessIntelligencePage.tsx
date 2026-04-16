import { useCallback, useEffect, useMemo, useState } from 'react'
import { RefreshCw } from 'lucide-react'
import { useTranslation } from 'react-i18next'
import { BUSINESS_INTELLIGENCE_SERVICE_URL } from '../../../../config/runtime'
import { useToast } from '../../../platform/providers/ToastContext'
import FilterInlineGroup from '../../../platform/ui/filters/FilterInlineGroup'
import AnalyticsTableCard from '../../../platform/ui/primitives/AnalyticsTableCard'
import Button from '../../../platform/ui/primitives/Button'
import ChartHeaderLegend from '../../../platform/ui/primitives/ChartHeaderLegend'
import PageHeader from '../../../platform/ui/primitives/PageHeader'
import PieDistributionCard from '../../../platform/ui/primitives/PieDistributionCard'
import SectionCard from '../../../platform/ui/primitives/SectionCard'
import StatCard from '../../../platform/ui/primitives/StatCard'
import StatusIcon, { type StatusTone } from '../../../platform/ui/primitives/StatusIcon'
import StatusCell from '../../../platform/ui/primitives/StatusCell'
import '../styles/business-intelligence.css'

interface TabMeta {
    id: string
    label: string
}

interface ExecutiveHero {
    score: string
    grade: string
    summary: string
    changeHint: string
    periodLabel: string
}

interface ProcessHealth {
    id: string
    label: string
    score: string
    tone: string
    summary: string
}

interface ExecutiveRisk {
    id: string
    priority: string
    title: string
    impact: string
    process: string
    value: string
}

interface RiskSummary {
    critical: number
    warning: number
    attention: number
    topRisks: ExecutiveRisk[]
}

interface TrendPoint {
    label: string
    score: number
    signal: number
}

interface TrendSection {
    title: string
    subtitle: string
    points: TrendPoint[]
}

interface ExecutiveSummary {
    hero: ExecutiveHero
    processHealths: ProcessHealth[]
    riskSummary: RiskSummary
    trend: TrendSection
}

interface MetricCard {
    id: string
    label: string
    value: string
    tone: string
}

interface ChartDatum {
    label: string
    value: number
}

interface ChartConfig {
    series?: string[]
    colors?: string[]
    xAxisLabel?: string
    yAxisLabel?: string
}

interface ChartSection {
    id: string
    title: string
    type: string
    items: ChartDatum[]
    config?: ChartConfig
}

interface TableSection {
    id: string
    title: string
    columns: string[]
    rows: string[][]
}

interface TabContent {
    id: string
    label: string
    description: string
    executiveSummary: ExecutiveSummary | null
    slaAnalysis: unknown | null
    cards: MetricCard[]
    charts: ChartSection[]
    tables: TableSection[]
}

interface OverviewResponse {
    refreshedAt: string
    tabs: TabMeta[]
    tabContents: Record<string, TabContent>
}

type TranslateFn = (key: string, options?: Record<string, unknown>) => string

const BUSINESS_INTELLIGENCE_TAB_LABEL_KEYS: Record<string, string> = {
    'executive-summary': 'businessIntelligence.tabs.executiveSummary',
    'sla-analysis': 'businessIntelligence.tabs.slaAnalysis',
    'event-analysis': 'businessIntelligence.tabs.eventAnalysis',
    'incident-analysis': 'businessIntelligence.tabs.eventAnalysis',
    'change-analysis': 'businessIntelligence.tabs.changeAnalysis',
    'request-analysis': 'businessIntelligence.tabs.requestAnalysis',
    'problem-analysis': 'businessIntelligence.tabs.problemAnalysis',
    'cross-process-correlation': 'businessIntelligence.tabs.crossProcessCorrelation',
    'cross-process-analysis': 'businessIntelligence.tabs.crossProcessCorrelation',
    'personnel-efficiency': 'businessIntelligence.tabs.personnelEfficiency',
}

const BUSINESS_INTELLIGENCE_TAB_LABEL_FALLBACK_KEYS: Record<string, string> = {
    '执行摘要': 'businessIntelligence.tabs.executiveSummary',
    'sla分析': 'businessIntelligence.tabs.slaAnalysis',
    '事件分析': 'businessIntelligence.tabs.eventAnalysis',
    '变更分析': 'businessIntelligence.tabs.changeAnalysis',
    '请求分析': 'businessIntelligence.tabs.requestAnalysis',
    '问题分析': 'businessIntelligence.tabs.problemAnalysis',
    '跨流程关联': 'businessIntelligence.tabs.crossProcessCorrelation',
    '人员与效率': 'businessIntelligence.tabs.personnelEfficiency',
}

const INCIDENT_ANALYSIS_TAB_IDS = new Set(['event-analysis', 'incident-analysis'])
const INCIDENT_CARD_LABEL_KEYS: Record<string, string> = {
    'incident-total': 'businessIntelligence.incidents.cards.total',
    'incident-p1p2': 'businessIntelligence.incidents.cards.p1p2',
    'incident-open': 'businessIntelligence.incidents.cards.open',
    'incident-sla': 'businessIntelligence.incidents.cards.sla',
    'incident-p1p2-mttr': 'businessIntelligence.incidents.cards.p1p2Mttr',
    'incident-mttr': 'businessIntelligence.incidents.cards.mttr',
}

const INCIDENT_CHART_TITLE_KEYS: Record<string, string> = {
    'incident-volume-trend': 'businessIntelligence.incidents.charts.volumeTrend',
    'incident-mttr-trend': 'businessIntelligence.incidents.charts.mttrTrend',
    'incident-priority-pie': 'businessIntelligence.incidents.charts.priorityDistribution',
    'incident-category-pie': 'businessIntelligence.incidents.charts.typeDistribution',
}

const INCIDENT_CHART_SERIES_KEYS: Record<string, string[]> = {
    'incident-volume-trend': [
        'businessIntelligence.incidents.charts.volumeSeries',
        'businessIntelligence.incidents.charts.slaSeries',
    ],
    'incident-mttr-trend': [
        'businessIntelligence.incidents.charts.mttrSeries',
        'businessIntelligence.incidents.charts.p1p2MttrSeries',
    ],
}

const INCIDENT_TABLE_TITLE_KEYS: Record<string, string> = {
    'incident-resolver-table': 'businessIntelligence.incidents.tables.resolverTop10',
    'incident-recent-table': 'businessIntelligence.incidents.tables.samples',
}

const INCIDENT_TABLE_COLUMN_KEYS: Record<string, string> = {
    '处理人': 'businessIntelligence.incidents.columns.resolver',
    '事件数': 'businessIntelligence.incidents.columns.incidentCount',
    '编号': 'businessIntelligence.incidents.columns.id',
    '标题': 'businessIntelligence.incidents.columns.title',
    '优先级': 'businessIntelligence.incidents.columns.priority',
    '时长': 'businessIntelligence.incidents.columns.duration',
    'SLA': 'businessIntelligence.incidents.columns.sla',
}

function localizeIncidentCard(card: MetricCard, t: TranslateFn): MetricCard {
    const labelKey = INCIDENT_CARD_LABEL_KEYS[card.id]
    if (!labelKey) {
        return card
    }

    return {
        ...card,
        label: t(labelKey),
    }
}

function localizeIncidentChart(chart: ChartSection, t: TranslateFn): ChartSection {
    const titleKey = INCIDENT_CHART_TITLE_KEYS[chart.id]
    const seriesKeys = INCIDENT_CHART_SERIES_KEYS[chart.id]

    if (!titleKey && !seriesKeys) {
        return chart
    }

    return {
        ...chart,
        title: titleKey ? t(titleKey) : chart.title,
        config: {
            ...chart.config,
            series: seriesKeys ? seriesKeys.map(key => t(key)) : chart.config?.series,
        },
    }
}

function localizeIncidentTable(table: TableSection, t: TranslateFn): TableSection {
    const titleKey = INCIDENT_TABLE_TITLE_KEYS[table.id]

    return {
        ...table,
        title: titleKey ? t(titleKey) : table.title,
        columns: table.columns.map(column => {
            const key = INCIDENT_TABLE_COLUMN_KEYS[column]
            return key ? t(key) : column
        }),
    }
}

function localizeIncidentTab(tab: TabContent, t: TranslateFn): TabContent {
    return {
        ...tab,
        cards: tab.cards.map(card => localizeIncidentCard(card, t)),
        charts: tab.charts.map(chart => localizeIncidentChart(chart, t)),
        tables: tab.tables.map(table => localizeIncidentTable(table, t)),
    }
}

// Predefined period options
type PeriodPreset = 'last7days' | 'last30days' | 'last90days' | 'thisMonth' | 'lastMonth' | 'thisQuarter' | 'custom'

interface ReportingPeriod {
    preset: PeriodPreset
    startDate?: string
    endDate?: string
}

// Get default reporting period (last 30 days)
function getDefaultReportingPeriod(): ReportingPeriod {
    const today = new Date()
    const startDate = new Date(today.getTime() - 30 * 24 * 60 * 60 * 1000)
    return {
        preset: 'last30days',
        startDate: startDate.toISOString().split('T')[0],
        endDate: today.toISOString().split('T')[0],
    }
}

function ReportingPeriodSelector({
    value,
    onChange,
    disabled,
}: {
    value: ReportingPeriod
    onChange: (value: ReportingPeriod) => void
    disabled?: boolean
}) {
    const { t } = useTranslation()
    const presetLabels: Record<PeriodPreset, string> = {
        'last7days': t('businessIntelligence.reportingPeriods.last7days'),
        'last30days': t('businessIntelligence.reportingPeriods.last30days'),
        'last90days': t('businessIntelligence.reportingPeriods.last90days'),
        'thisMonth': t('businessIntelligence.reportingPeriods.thisMonth'),
        'lastMonth': t('businessIntelligence.reportingPeriods.lastMonth'),
        'thisQuarter': t('businessIntelligence.reportingPeriods.thisQuarter'),
        'custom': t('businessIntelligence.reportingPeriods.custom'),
    }

    const handlePresetChange = (preset: PeriodPreset) => {
        const today = new Date()
        let startDate: Date | undefined
        let endDate: Date | undefined

        switch (preset) {
            case 'last7days':
                startDate = new Date(today.getTime() - 7 * 24 * 60 * 60 * 1000)
                endDate = today
                break
            case 'last30days':
                startDate = new Date(today.getTime() - 30 * 24 * 60 * 60 * 1000)
                endDate = today
                break
            case 'last90days':
                startDate = new Date(today.getTime() - 90 * 24 * 60 * 60 * 1000)
                endDate = today
                break
            case 'thisMonth':
                startDate = new Date(today.getFullYear(), today.getMonth(), 1)
                endDate = new Date(today.getFullYear(), today.getMonth() + 1, 0)
                break
            case 'lastMonth':
                startDate = new Date(today.getFullYear(), today.getMonth() - 1, 1)
                endDate = new Date(today.getFullYear(), today.getMonth(), 0)
                break
            case 'thisQuarter':
                const quarterMonth = Math.floor(today.getMonth() / 3) * 3
                startDate = new Date(today.getFullYear(), quarterMonth, 1)
                endDate = new Date(today.getFullYear(), quarterMonth + 3, 0)
                break
            case 'custom':
                // Keep existing dates or leave undefined
                break
        }

        onChange({
            preset,
            startDate: startDate?.toISOString().split('T')[0],
            endDate: endDate?.toISOString().split('T')[0],
        })
    }

    const handleStartDateChange = (date: string) => {
        onChange({
            ...value,
            startDate: date,
        })
    }

    const handleEndDateChange = (date: string) => {
        onChange({
            ...value,
            endDate: date,
        })
    }

    return (
        <div className="reporting-period-selector">
            <div className="business-intelligence-period-field">
                <span className="reporting-period-selector-label">{t('businessIntelligence.reportingPeriod')}:</span>
                <select
                    className="filter-select reporting-period-select"
                    value={value.preset}
                    onChange={(e) => handlePresetChange(e.target.value as PeriodPreset)}
                    disabled={disabled}
                >
                    {Object.entries(presetLabels).map(([key, label]) => (
                        <option key={key} value={key}>{label}</option>
                    ))}
                </select>
            </div>
            {value.preset === 'custom' && (
                <div className="reporting-period-custom-dates">
                    <input
                        type="date"
                        className="reporting-period-date-input"
                        value={value.startDate || ''}
                        onChange={(e) => handleStartDateChange(e.target.value)}
                        disabled={disabled}
                        max={value.endDate || new Date().toISOString().split('T')[0]}
                    />
                    <span className="reporting-period-date-separator">{t('businessIntelligence.dateRangeSeparator')}</span>
                    <input
                        type="date"
                        className="reporting-period-date-input"
                        value={value.endDate || ''}
                        onChange={(e) => handleEndDateChange(e.target.value)}
                        disabled={disabled}
                        min={value.startDate}
                        max={new Date().toISOString().split('T')[0]}
                    />
                </div>
            )}
        </div>
    )
}

function getBusinessIntelligenceTabLabel(tab: TabMeta, t: (key: string) => string): string {
    const keyById = BUSINESS_INTELLIGENCE_TAB_LABEL_KEYS[tab.id]
    if (keyById) {
        return t(keyById)
    }

    const normalizedLabel = tab.label.trim().toLowerCase()
    const keyByLabel = BUSINESS_INTELLIGENCE_TAB_LABEL_FALLBACK_KEYS[normalizedLabel] ?? BUSINESS_INTELLIGENCE_TAB_LABEL_FALLBACK_KEYS[tab.label.trim()]
    if (keyByLabel) {
        return t(keyByLabel)
    }

    return tab.label
}

function ExecutiveSummaryPanel({
    summary,
    t,
}: {
    summary: ExecutiveSummary
    cards: MetricCard[]
    t: (key: string, options?: Record<string, unknown>) => string
}) {
    const totalRisks = summary.riskSummary.critical + summary.riskSummary.warning + summary.riskSummary.attention

    return (
        <div className="business-intelligence-dashboard-shell">
            <div className="business-intelligence-dashboard-stats">
                <section className="mon-kpi-card business-intelligence-dashboard-stat-card">
                    <span className="mon-kpi-label">{t('businessIntelligence.dashboard.healthScoreLabel')}</span>
                    <strong className="mon-kpi-value">{summary.hero.score}</strong>
                    <p className="mon-kpi-sub">{t('businessIntelligence.dashboard.healthScoreDescription')}</p>
                </section>
                <section className="mon-kpi-card business-intelligence-dashboard-stat-card">
                    <span className="mon-kpi-label">{t('businessIntelligence.dashboard.totalRisksLabel')}</span>
                    <strong className="mon-kpi-value">{t('businessIntelligence.dashboard.totalRisksValue', { count: totalRisks })}</strong>
                    <p className="mon-kpi-sub">{t('businessIntelligence.dashboard.totalRisksDescription')}</p>
                </section>
            </div>

            <div className="business-intelligence-dashboard-grid">
                <section className="mon-kpi-card business-intelligence-dashboard-panel business-intelligence-dashboard-panel-primary">
                    <div className="mon-chart-card-head business-intelligence-dashboard-panel-header">
                        <div className="mon-chart-card-meta">
                            <h3>{t('businessIntelligence.dashboard.overviewTitle')}</h3>
                            <p className="mon-chart-subtitle">{t('businessIntelligence.dashboard.overviewDescription')}</p>
                        </div>
                    </div>
                    <div className="business-intelligence-dashboard-panel-body business-intelligence-dashboard-panel-body-tall" />
                </section>

                <div className="business-intelligence-dashboard-side">
                    <section className="mon-kpi-card business-intelligence-dashboard-panel">
                        <div className="mon-chart-card-head business-intelligence-dashboard-panel-header">
                            <div className="mon-chart-card-meta">
                                <h3>{t('businessIntelligence.dashboard.evidenceTitle')}</h3>
                                <p className="mon-chart-subtitle">{t('businessIntelligence.dashboard.evidenceDescription')}</p>
                            </div>
                        </div>
                        <div className="business-intelligence-dashboard-panel-body" />
                    </section>

                    <section className="mon-kpi-card business-intelligence-dashboard-panel">
                        <div className="mon-chart-card-head business-intelligence-dashboard-panel-header">
                            <div className="mon-chart-card-meta">
                                <h3>{t('businessIntelligence.dashboard.risksTitle')}</h3>
                                <p className="mon-chart-subtitle">{t('businessIntelligence.dashboard.risksDescription')}</p>
                            </div>
                        </div>
                        <div className="business-intelligence-dashboard-panel-body" />
                    </section>
                </div>
            </div>

            <section className="mon-kpi-card business-intelligence-dashboard-panel business-intelligence-dashboard-panel-wide">
                <div className="mon-chart-card-head business-intelligence-dashboard-panel-header">
                    <div className="mon-chart-card-meta">
                        <h3>{t('businessIntelligence.dashboard.governanceTitle')}</h3>
                        <p className="mon-chart-subtitle">{t('businessIntelligence.dashboard.governanceDescription')}</p>
                    </div>
                </div>
                <div className="business-intelligence-dashboard-panel-body business-intelligence-dashboard-panel-body-wide" />
            </section>
        </div>
    )
}

function getMetricTone(tone: string): 'neutral' | 'success' | 'warning' | 'danger' {
    switch (tone) {
        case 'success':
            return 'success'
        case 'warning':
            return 'warning'
        case 'danger':
            return 'danger'
        default:
            return 'neutral'
    }
}

function getToneClass(tone: string): string {
    const mappedTone = getMetricTone(tone)
    return mappedTone === 'neutral' ? '' : `tone-${mappedTone}`
}

function isIncidentAnalysisTab(tab: TabContent): boolean {
    return INCIDENT_ANALYSIS_TAB_IDS.has(tab.id)
}

function getTableColumnClassName(column: string): string {
    const normalized = column.trim().toLowerCase()

    if (normalized.includes('title') || normalized.includes('标题')) {
        return 'business-intelligence-col-order-title'
    }

    if (normalized.includes('编号') || normalized.includes('id') || normalized.includes('单号')) {
        return 'business-intelligence-col-order-number'
    }

    if (normalized.includes('priority') || normalized.includes('优先级')) {
        return 'business-intelligence-col-priority'
    }

    if (normalized.includes('category') || normalized.includes('类型')) {
        return 'business-intelligence-col-category'
    }

    if (normalized.includes('resolver') || normalized.includes('处理人') || normalized.includes('assignee')) {
        return 'business-intelligence-col-resolver'
    }

    if (normalized.includes('duration') || normalized.includes('时长') || normalized.includes('mttr')) {
        return 'business-intelligence-col-duration'
    }

    if (normalized.includes('sla')) {
        return 'business-intelligence-col-violation-type'
    }

    return ''
}

function getSlaStatus(cell: string, t: TranslateFn): { tone: StatusTone; label: string } | null {
    const normalized = cell.trim().toLowerCase()

    if (!normalized) {
        return null
    }

    if (
        normalized === '×' ||
        normalized === 'x' ||
        normalized === '✕' ||
        normalized === '✖' ||
        normalized.includes('violat') ||
        normalized.includes('breach') ||
        normalized.includes('超时') ||
        normalized.includes('未达成')
    ) {
        return { tone: 'danger', label: t('businessIntelligence.slaStatus.breached') }
    }

    if (
        normalized === '√' ||
        normalized === '✓' ||
        normalized === '✔' ||
        normalized.includes('met') ||
        normalized.includes('达成') ||
        normalized.includes('满足') ||
        normalized.includes('通过')
    ) {
        return { tone: 'success', label: t('businessIntelligence.slaStatus.met') }
    }

    if (normalized.includes('risk') || normalized.includes('warning') || normalized.includes('临界') || normalized.includes('预警')) {
        return { tone: 'warning', label: t('businessIntelligence.slaStatus.atRisk') }
    }

    if (normalized === '-' || normalized === '--' || normalized === 'n/a' || normalized.includes('unknown') || normalized.includes('未知')) {
        return { tone: 'neutral', label: t('businessIntelligence.slaStatus.unknown') }
    }

    return null
}

function shouldRenderAxisLabel(index: number, total: number, maxVisible: number): boolean {
    if (total <= maxVisible) {
        return true
    }

    const interval = Math.ceil(total / maxVisible)
    return index === total - 1 || index % interval === 0
}

function splitAxisLabel(label: string, wrap: boolean): string[] {
    if (!wrap) {
        return [label]
    }

    const dashIdx = label.indexOf('-')
    if (dashIdx > 0) {
        return [label.slice(0, dashIdx), label.slice(dashIdx)]
    }

    return [label]
}

function containsCjk(value: string): boolean {
    return /[\u3400-\u9fff]/.test(value)
}

function getIncidentSectionTitle(
    key: 'incidentSnapshot' | 'incidentTrends' | 'distribution' | 'operationalTables',
    _tab: TabContent,
    t: TranslateFn,
): string {
    return t(`businessIntelligence.sections.${key}`)
}

function getChartLegendItems(chart: ChartSection, t: TranslateFn): Array<{ label: string; color: string; dashed?: boolean }> {
    if (chart.type === 'line') {
        const colors = chart.config?.colors || ['#5b8db8', '#10b981']
        const seriesNames = chart.config?.series || [t('businessIntelligence.incidents.charts.volumeSeries')]
        return seriesNames.map((label, idx) => ({
            label,
            color: colors[idx] || '#5b8db8',
        }))
    }

    if (chart.type === 'combo') {
        const colors = chart.config?.colors || ['#5b8db8', '#10b981']
        const seriesNames = chart.config?.series || [
            t('businessIntelligence.incidents.charts.volumeSeries'),
            t('businessIntelligence.incidents.charts.slaSeries'),
        ]
        return seriesNames.map((label, idx) => ({
            label,
            color: colors[idx] || (idx === 2 ? '#ef4444' : '#5b8db8'),
            dashed: idx === 2,
        }))
    }

    if (chart.type === 'grouped-bar' || chart.type === 'stacked-bar') {
        const colors = chart.config?.colors || ['#5b8db8', '#10b981']
        const seriesNames = chart.config?.series || ['Series 1', 'Series 2']
        return seriesNames.map((label, idx) => ({
            label,
            color: colors[idx] || '#5b8db8',
        }))
    }

    return []
}

function GenericTabPanel({
    tab,
    t: _t,
}: {
    tab: TabContent
    t: (key: string, options?: Record<string, unknown>) => string
}) {
    const localizedTab = isIncidentAnalysisTab(tab) ? localizeIncidentTab(tab, _t) : tab
    const maxValue = (items: ChartDatum[]) => Math.max(...items.map(item => item.value), 1)

    const renderLineChart = (chart: ChartSection, options?: { hideLegend?: boolean }) => {
        const colors = chart.config?.colors || ['#5b8db8', '#10b981']
        const seriesNames = chart.config?.series || [_t('businessIntelligence.incidents.charts.volumeSeries')]

        // Parse multi-series data from compound labels (format: "period|value1|value2|...")
        const dataPoints = chart.items.map(item => {
            const parts = item.label.split('|')
            return {
                period: parts[0] || item.label,
                values: parts.slice(1).map(v => parseFloat(v) || 0)
            }
        })

        // Get all values for scaling, filter out zeros for better visualization
        const allValues = dataPoints.flatMap(dp => dp.values).filter(v => v > 0)
        const maxVal = Math.max(...allValues, 1)

        // Use a very wide viewBox for horizontal stretching
        const vbWidth = 1000
        const vbHeight = 360
        const padding = { top: 24, right: 30, bottom: 72, left: 60 }
        const innerWidth = vbWidth - padding.left - padding.right
        const innerHeight = vbHeight - padding.top - padding.bottom

        const getY = (value: number) => padding.top + innerHeight - (value / maxVal) * innerHeight
        const getX = (index: number) => {
            if (dataPoints.length <= 1) return padding.left + innerWidth / 2
            return padding.left + (index / (dataPoints.length - 1)) * innerWidth
        }

        // Generate Y-axis labels
        const yAxisLabels = [0, 0.25, 0.5, 0.75, 1].map(ratio => ({
            value: Math.round(maxVal * ratio),
            y: getY(maxVal * ratio)
        }))

        return (
            <div style={{ width: '100%' }}>
                <svg
                    viewBox={`0 0 ${vbWidth} ${vbHeight}`}
                    preserveAspectRatio="none"
                    style={{ width: '100%', height: '360px', display: 'block' }}
                >
                    {/* Y-axis grid lines and labels */}
                    {yAxisLabels.map((label, idx) => (
                        <g key={idx}>
                            <line
                                x1={padding.left}
                                y1={label.y}
                                x2={vbWidth - padding.right}
                                y2={label.y}
                                stroke="var(--color-border)"
                                strokeDasharray="4 4"
                            />
                            <text
                                x={padding.left - 10}
                                y={label.y + 4}
                                fill="var(--color-text-secondary)"
                                fontSize="12"
                                textAnchor="end"
                            >
                                {label.value}
                            </text>
                        </g>
                    ))}

                    {/* Data lines for each series */}
                    {seriesNames.map((_, seriesIdx) => {
                        const points = dataPoints
                            .map((dp, idx) => {
                                const val = dp.values[seriesIdx]
                                if (val === undefined || val === 0) return null
                                return `${getX(idx)},${getY(val)}`
                            })
                            .filter(Boolean)
                            .join(' ')

                        return (
                            <g key={seriesIdx}>
                                <polyline
                                    points={points}
                                    fill="none"
                                    stroke={colors[seriesIdx] || '#5b8db8'}
                                    strokeWidth="3"
                                    strokeLinecap="round"
                                    strokeLinejoin="round"
                                />
                                {/* Data points */}
                                {dataPoints.map((dp, idx) => {
                                    const val = dp.values[seriesIdx]
                                    if (val === undefined || val === 0) return null
                                    return (
                                        <circle
                                            key={idx}
                                            cx={getX(idx)}
                                            cy={getY(val)}
                                            r="4"
                                            fill={colors[seriesIdx] || '#5b8db8'}
                                        />
                                    )
                                })}
                            </g>
                        )
                    })}

                    {/* X-axis labels with auto-wrapping */}
                    {(() => {
                        const colWidth = dataPoints.length > 1
                            ? innerWidth / (dataPoints.length - 1)
                            : innerWidth
                        const maxCharsPerLine = Math.max(4, Math.floor(colWidth / 7))
                        const needsWrap = dataPoints.some(dp => dp.period.length > maxCharsPerLine)
                        const splitPeriod = (label: string): string[] => {
                            return splitAxisLabel(label, needsWrap)
                        }
                        return dataPoints.map((dp, idx) => {
                            if (!shouldRenderAxisLabel(idx, dataPoints.length, 12)) {
                                return null
                            }
                            const lines = splitPeriod(dp.period)
                            return lines.map((line, lineIdx) => (
                                <text
                                    key={`${idx}-${lineIdx}`}
                                    x={getX(idx)}
                                    y={vbHeight - padding.bottom + 14 + lineIdx * 14}
                                    fill="var(--color-text-secondary)"
                                    fontSize="11"
                                    textAnchor="middle"
                                >
                                    {line}
                                </text>
                            ))
                        })
                    })()}
                </svg>
                {!options?.hideLegend ? (
                    <div className="line-chart-legend">
                        {seriesNames.map((name, idx) => (
                            <span key={name} className="line-chart-legend-item">
                                <span className="line-chart-legend-line" style={{ background: colors[idx] }} />
                                {name}
                            </span>
                        ))}
                    </div>
                ) : null}
            </div>
        )
    }

    const renderStackedBarChart = (chart: ChartSection, options?: { hideLegend?: boolean }) => {
        const colors = chart.config?.colors || ['#5b8db8', '#ef4444']
        const seriesNames = chart.config?.series || ['Series 1', 'Series 2']

        // Parse compound labels: "category|val1|val2|..."
        const dataPoints = chart.items.map(item => {
            const parts = item.label.split('|')
            return {
                label: parts[0] || item.label,
                values: parts.slice(1).map(v => parseFloat(v) || 0),
            }
        })

        const maxVal = Math.max(...dataPoints.map(dp => dp.values.reduce((a: number, b: number) => a + b, 0)), 1)
        const vbWidth = 500
        const vbHeight = 280
        const padding = { top: 20, right: 20, bottom: 30, left: 50 }
        const innerWidth = vbWidth - padding.left - padding.right
        const innerHeight = vbHeight - padding.top - padding.bottom

        const getY = (value: number) => padding.top + innerHeight - (value / maxVal) * innerHeight
        const groupWidth = innerWidth / dataPoints.length
        const barWidth = Math.min(70, groupWidth * 0.65)

        // Y-axis labels
        const yTicks = 5
        const yAxisLabels = Array.from({ length: yTicks + 1 }, (_, i) => {
            const value = (maxVal / yTicks) * i
            return { value: Math.round(value), y: getY(value) }
        })

        return (
            <div style={{ width: '100%', minWidth: 0 }}>
                <svg
                    viewBox={`0 0 ${vbWidth} ${vbHeight}`}
                    preserveAspectRatio="xMidYMid meet"
                    style={{ width: '100%', display: 'block' }}
                >
                    {/* Y-axis grid lines and labels */}
                    {yAxisLabels.map((label, idx) => (
                        <g key={idx}>
                            <line
                                x1={padding.left} y1={label.y}
                                x2={vbWidth - padding.right} y2={label.y}
                                stroke="var(--color-border)" strokeDasharray="4 4"
                            />
                            <text
                                x={padding.left - 8} y={label.y + 5}
                                fill="var(--color-text-secondary)" fontSize="12" textAnchor="end"
                            >
                                {label.value}
                            </text>
                        </g>
                    ))}

                    {/* Stacked bars */}
                    {dataPoints.map((dp, groupIdx) => {
                        const groupX = padding.left + groupIdx * groupWidth + groupWidth / 2
                        const barX = groupX - barWidth / 2
                        let cumulative = 0

                        return (
                            <g key={groupIdx}>
                                {dp.values.map((val, seriesIdx) => {
                                    const segHeight = (val / maxVal) * innerHeight
                                    const segY = getY(cumulative + val)
                                    cumulative += val
                                    return (
                                        <g key={seriesIdx}>
                                            <rect
                                                x={barX} y={segY}
                                                width={barWidth} height={segHeight}
                                                fill={colors[seriesIdx]} rx={seriesIdx === dp.values.length - 1 ? 3 : 0}
                                                opacity="0.85"
                                            />
                                            {val > 0 && (
                                                <text
                                                    x={barX + barWidth / 2} y={segY + segHeight / 2 + 5}
                                                    fill="white" fontSize="12"
                                                    textAnchor="middle" fontWeight="600"
                                                >
                                                    {Math.round(val)}
                                                </text>
                                            )}
                                        </g>
                                    )
                                })}
                                <text
                                    x={groupX} y={vbHeight - 6}
                                    fill="var(--color-text-secondary)" fontSize="12"
                                    textAnchor="middle" fontWeight="500"
                                >
                                    {dp.label}
                                </text>
                            </g>
                        )
                    })}
                </svg>
                {!options?.hideLegend ? (
                    <div className="line-chart-legend">
                        {seriesNames.map((name, idx) => (
                            <span key={name} className="line-chart-legend-item">
                                <span
                                    className="line-chart-legend-line"
                                    style={{ background: colors[idx], borderRadius: '2px', height: '12px' }}
                                />
                                {name}
                            </span>
                        ))}
                    </div>
                ) : null}
            </div>
        )
    }

    const renderColumnChart = (chart: ChartSection) => {
        const dataPoints = chart.items
        const maxVal = Math.max(...dataPoints.map(dp => dp.value), 1)
        const vbWidth = 500
        const vbHeight = 300
        const padding = { top: 20, right: 20, bottom: 60, left: 45 }
        const innerWidth = vbWidth - padding.left - padding.right
        const innerHeight = vbHeight - padding.top - padding.bottom

        const getY = (value: number) => padding.top + innerHeight - (value / maxVal) * innerHeight
        const barWidth = Math.min(50, (innerWidth / dataPoints.length) * 0.6)

        // Split label into lines (max 2 lines, ~8 chars each)
        const splitLabel = (label: string): string[] => {
            if (label.length <= 8) return [label]
            const mid = Math.ceil(label.length / 2)
            // Try to split at space
            const spaceIdx = label.lastIndexOf(' ', mid + 2)
            if (spaceIdx > 0 && spaceIdx < label.length - 1) {
                return [label.slice(0, spaceIdx), label.slice(spaceIdx + 1)]
            }
            return [label.slice(0, mid), label.slice(mid)]
        }

        // Y-axis ticks
        const yTicks = 5
        const yAxisLabels = Array.from({ length: yTicks + 1 }, (_, i) => {
            const value = (maxVal / yTicks) * i
            return { value: Math.round(value), y: getY(value) }
        })

        return (
            <div style={{ width: '100%', minWidth: 0 }}>
                <svg
                    viewBox={`0 0 ${vbWidth} ${vbHeight}`}
                    preserveAspectRatio="xMidYMid meet"
                    style={{ width: '100%', display: 'block' }}
                >
                    {/* Y-axis grid and labels */}
                    {yAxisLabels.map((label, idx) => (
                        <g key={idx}>
                            <line
                                x1={padding.left} y1={label.y}
                                x2={vbWidth - padding.right} y2={label.y}
                                stroke="var(--color-border)" strokeDasharray="4 4"
                            />
                            <text
                                x={padding.left - 8} y={label.y + 4}
                                fill="var(--color-text-secondary)" fontSize="12" textAnchor="end"
                            >
                                {label.value}
                            </text>
                        </g>
                    ))}

                    {/* Bars */}
                    {dataPoints.map((dp, idx) => {
                        const groupWidth = innerWidth / dataPoints.length
                        const centerX = padding.left + idx * groupWidth + groupWidth / 2
                        const barX = centerX - barWidth / 2
                        const barY = getY(dp.value)
                        const barHeight = innerHeight - (barY - padding.top)
                        const lines = splitLabel(dp.label)
                        return (
                            <g key={idx}>
                                <rect
                                    x={barX} y={barY}
                                    width={barWidth} height={barHeight}
                                    fill="#5b8db8" rx="3" opacity="0.85"
                                />
                                <text
                                    x={centerX} y={barY - 5}
                                    fill="var(--color-text-primary)" fontSize="12"
                                    textAnchor="middle" fontWeight="600"
                                >
                                    {Math.round(dp.value)}
                                </text>
                                {lines.map((line, lineIdx) => (
                                    <text
                                        key={lineIdx}
                                        x={centerX} y={vbHeight - 40 + lineIdx * 14}
                                        fill="var(--color-text-secondary)" fontSize="11"
                                        textAnchor="middle" fontWeight="500"
                                    >
                                        {line}
                                    </text>
                                ))}
                            </g>
                        )
                    })}
                </svg>
            </div>
        )
    }

    const renderBarChart = (chart: ChartSection) => {
        return (
            <div className="business-intelligence-chart-list">
                {chart.items.map((item, index) => (
                    <div key={`${item.label}-${index}`} className="business-intelligence-chart-row">
                        <div className="business-intelligence-chart-meta">
                            <span className="business-intelligence-chart-label">{item.label}</span>
                            <span className="business-intelligence-chart-value">{item.value.toLocaleString()}</span>
                        </div>
                        <div className="business-intelligence-chart-track">
                            <div
                                className="business-intelligence-chart-fill"
                                style={{ width: `${(item.value / maxValue(chart.items)) * 100}%` }}
                            />
                        </div>
                    </div>
                ))}
            </div>
        )
    }

    const renderGroupedBarChart = (chart: ChartSection, options?: { hideLegend?: boolean }) => {
        const colors = chart.config?.colors || ['#5b8db8', '#10b981']
        const seriesNames = chart.config?.series || ['Series 1', 'Series 2']

        // Parse compound labels: "label|val1|val2|..."
        const dataPoints = chart.items.map(item => {
            const parts = item.label.split('|')
            return {
                label: parts[0] || item.label,
                values: parts.slice(1).map(v => parseFloat(v) || 0),
            }
        })

        const maxVal = Math.max(...dataPoints.flatMap(dp => dp.values), 1)
        const yAxisLabel = chart.config?.yAxisLabel || ''
        const isPercentage = !yAxisLabel ? (maxVal <= 100 && dataPoints.some(dp => dp.values.some(v => v > 5))) : yAxisLabel.includes('%') || yAxisLabel.includes('率')
        const vbWidth = 800
        const vbHeight = 280
        const padding = { top: 30, right: 30, bottom: 40, left: 60 }
        const innerWidth = vbWidth - padding.left - padding.right
        const innerHeight = vbHeight - padding.top - padding.bottom

        const getY = (value: number) => padding.top + innerHeight - (value / maxVal) * innerHeight
        const groupWidth = innerWidth / dataPoints.length
        const barWidth = Math.min(36, (groupWidth * 0.6) / seriesNames.length)
        const barGap = 4

        // Y-axis labels
        const yAxisLabels = [0, 0.25, 0.5, 0.75, 1].map(ratio => ({
            value: Math.round(maxVal * ratio),
            y: getY(maxVal * ratio),
        }))

        return (
            <div style={{ width: '100%' }}>
                <svg
                    viewBox={`0 0 ${vbWidth} ${vbHeight}`}
                    preserveAspectRatio="xMidYMid meet"
                    style={{ width: '100%', height: '280px', display: 'block' }}
                >
                    {/* Y-axis grid lines and labels */}
                    {yAxisLabels.map((label, idx) => (
                        <g key={idx}>
                            <line
                                x1={padding.left} y1={label.y}
                                x2={vbWidth - padding.right} y2={label.y}
                                stroke="var(--color-border)" strokeDasharray="4 4"
                            />
                            <text
                                x={padding.left - 10} y={label.y + 4}
                                fill="var(--color-text-secondary)" fontSize="12" textAnchor="end"
                            >
                                {isPercentage ? `${label.value}%` : label.value}
                            </text>
                        </g>
                    ))}

                    {/* Grouped bars */}
                    {dataPoints.map((dp, groupIdx) => {
                        const groupX = padding.left + groupIdx * groupWidth + groupWidth / 2
                        const totalBarsWidth = seriesNames.length * barWidth + (seriesNames.length - 1) * barGap
                        const startX = groupX - totalBarsWidth / 2

                        return (
                            <g key={groupIdx}>
                                {seriesNames.map((_, seriesIdx) => {
                                    const val = dp.values[seriesIdx] || 0
                                    const barX = startX + seriesIdx * (barWidth + barGap)
                                    const barY = getY(val)
                                    const barHeight = innerHeight - (barY - padding.top)
                                    return (
                                        <g key={seriesIdx}>
                                            <rect
                                                x={barX} y={barY}
                                                width={barWidth} height={barHeight}
                                                fill={colors[seriesIdx]} rx="3" opacity="0.85"
                                            />
                                            <text
                                                x={barX + barWidth / 2} y={barY - 5}
                                                fill={colors[seriesIdx]} fontSize="11"
                                                textAnchor="middle" fontWeight="600"
                                            >
                                                {isPercentage ? `${val.toFixed(1)}%` : (val % 1 === 0 ? val : val.toFixed(1))}
                                            </text>
                                        </g>
                                    )
                                })}
                                <text
                                    x={groupX} y={vbHeight - 15}
                                    fill="var(--color-text-secondary)" fontSize="13"
                                    textAnchor="middle" fontWeight="500"
                                >
                                    {dp.label}
                                </text>
                            </g>
                        )
                    })}
                </svg>
                {!options?.hideLegend ? (
                    <div className="line-chart-legend">
                        {seriesNames.map((name, idx) => (
                            <span key={name} className="line-chart-legend-item">
                                <span
                                    className="line-chart-legend-line"
                                    style={{ background: colors[idx], borderRadius: '2px', height: '12px' }}
                                />
                                {name}
                            </span>
                        ))}
                    </div>
                ) : null}
            </div>
        )
    }

    const renderComboChart = (chart: ChartSection, options?: { hideLegend?: boolean }) => {
        const colors = chart.config?.colors || ['#5b8db8', '#10b981']
        const seriesNames = chart.config?.series || [
            _t('businessIntelligence.incidents.charts.volumeSeries'),
            _t('businessIntelligence.incidents.charts.slaSeries'),
        ]

        // Parse combo data: format "period|volume|completionRate" or "period|volume|completionRate|causedCount"
        const dataPoints = chart.items.map(item => {
            const parts = item.label.split('|')
            return {
                period: parts[0] || item.label,
                volume: parseFloat(parts[1]) || item.value,
                completionRate: parseFloat(parts[2]) || 0,
                causedCount: parseFloat(parts[3]) || 0,
            }
        })

        // Calculate max values for each axis
        const hasCausedLine = seriesNames.length >= 3 && dataPoints.some(dp => dp.causedCount > 0)
        const maxVolume = Math.max(...dataPoints.map(dp => dp.volume), ...dataPoints.map(dp => dp.causedCount), 1)
        const maxLineValue = Math.max(...dataPoints.map(dp => dp.completionRate), 0)
        // Auto-detect right Y-axis mode:
        // 1. Score scale: values ≤ 5 (satisfaction scores 1-5)
        // 2. Count scale: values > 5 and series name doesn't contain percentage indicators
        // 3. Percentage scale: series name contains "率" or values clearly %
        const secondSeries = seriesNames.length > 1 ? seriesNames[1] : ''
        const isScoreScale = maxLineValue <= 5 && maxLineValue > 0
        const isPercentageScale = !isScoreScale && (secondSeries.includes('率') || secondSeries.includes('%'))
        const isCountScale = !isScoreScale && !isPercentageScale
        const maxRate = isScoreScale ? 5 : isCountScale ? Math.ceil(maxLineValue / 5) * 5 || 5 : 100

        const vbWidth = 1000
        const vbHeight = 360
        const padding = { top: 28, right: 60, bottom: 72, left: 60 }
        const innerWidth = vbWidth - padding.left - padding.right
        const innerHeight = vbHeight - padding.top - padding.bottom

        // Bar chart Y-axis (left) - volume
        const getBarY = (value: number) => padding.top + innerHeight - (value / maxVolume) * innerHeight
        // Line chart Y-axis (right) - percentage or score
        const getLineY = (value: number) => padding.top + innerHeight - (value / maxRate) * innerHeight

        const getBarX = (index: number) => {
            const barWidth = innerWidth / dataPoints.length
            return padding.left + index * barWidth + barWidth / 2
        }

        const barWidth = Math.min(60, (innerWidth / dataPoints.length) * 0.6)

        // Generate Y-axis labels for bar (left)
        const barYAxisLabels = [0, 0.25, 0.5, 0.75, 1].map(ratio => ({
            value: Math.round(maxVolume * ratio),
            y: getBarY(maxVolume * ratio),
        }))

        // Generate Y-axis labels for line (right) - score, count, or percentage
        const lineYAxisLabels = isScoreScale
            ? [0, 1, 2, 3, 4, 5].map(value => ({ value, y: getLineY(value) }))
            : isPercentageScale
            ? [0, 25, 50, 75, 100].map(value => ({ value, y: getLineY(value) }))
            : [0, 0.25, 0.5, 0.75, 1].map(ratio => ({ value: Math.round(maxRate * ratio), y: getLineY(maxRate * ratio) }))

        // Build line path
        const linePoints = dataPoints
            .map((dp, idx) => `${getBarX(idx)},${getLineY(dp.completionRate)}`)
            .join(' ')

        return (
            <div style={{ width: '100%' }}>
                <svg
                    viewBox={`0 0 ${vbWidth} ${vbHeight}`}
                    preserveAspectRatio="xMidYMid meet"
                    style={{ width: '100%', height: '360px', display: 'block' }}
                >
                    {/* Y-axis grid lines */}
                    {barYAxisLabels.map((label, idx) => (
                        <line
                            key={idx}
                            x1={padding.left}
                            y1={label.y}
                            x2={vbWidth - padding.right}
                            y2={label.y}
                            stroke="var(--color-border)"
                            strokeDasharray="4 4"
                        />
                    ))}

                    {/* Left Y-axis labels (volume) */}
                    {barYAxisLabels.map((label, idx) => (
                        <text
                            key={idx}
                            x={padding.left - 10}
                            y={label.y + 4}
                            fill="var(--color-text-secondary)"
                            fontSize="12"
                            textAnchor="end"
                        >
                            {label.value}
                        </text>
                    ))}

                    {/* Right Y-axis labels (score or percentage) */}
                    {lineYAxisLabels.map((label, idx) => (
                        <text
                            key={idx}
                            x={vbWidth - padding.right + 10}
                            y={label.y + 4}
                            fill={colors[1]}
                            fontSize="12"
                            textAnchor="start"
                        >
                            {isScoreScale ? label.value : isPercentageScale ? `${label.value}%` : label.value}
                        </text>
                    ))}

                    {/* Bars */}
                    {dataPoints.map((dp, idx) => (
                        <rect
                            key={idx}
                            x={getBarX(idx) - barWidth / 2}
                            y={getBarY(dp.volume)}
                            width={barWidth}
                            height={innerHeight - (getBarY(dp.volume) - padding.top)}
                            fill={colors[0]}
                            rx="4"
                            opacity="0.85"
                        />
                    ))}

                    {/* Line overlay */}
                    <polyline
                        points={linePoints}
                        fill="none"
                        stroke={colors[1]}
                        strokeWidth="3"
                        strokeLinecap="round"
                        strokeLinejoin="round"
                    />

                    {/* Line data points */}
                    {dataPoints.map((dp, idx) => (
                        <circle
                            key={idx}
                            cx={getBarX(idx)}
                            cy={getLineY(dp.completionRate)}
                            r="4"
                            fill={colors[1]}
                            stroke="white"
                            strokeWidth="2"
                        />
                    ))}

                    {/* Second line overlay (causedCount, left Y-axis scale) */}
                    {hasCausedLine && (() => {
                        const causedPoints = dataPoints
                            .map((dp, idx) => `${getBarX(idx)},${getBarY(dp.causedCount)}`)
                            .join(' ')
                        return (
                            <g>
                                <polyline
                                    points={causedPoints}
                                    fill="none"
                                    stroke={colors[2] || '#ef4444'}
                                    strokeWidth="2.5"
                                    strokeLinecap="round"
                                    strokeLinejoin="round"
                                    strokeDasharray="6 3"
                                />
                                {dataPoints.map((dp, idx) => dp.causedCount > 0 ? (
                                    <circle
                                        key={`c${idx}`}
                                        cx={getBarX(idx)}
                                        cy={getBarY(dp.causedCount)}
                                        r="3.5"
                                        fill={colors[2] || '#ef4444'}
                                        stroke="white"
                                        strokeWidth="2"
                                    />
                                ) : null)}
                            </g>
                        )
                    })()}

                    {/* X-axis labels with auto-wrapping */}
                    {(() => {
                        const colWidth = innerWidth / dataPoints.length
                        const maxCharsPerLine = Math.max(4, Math.floor(colWidth / 7))
                        const needsWrap = dataPoints.some(dp => dp.period.length > maxCharsPerLine)
                        const splitPeriod = (label: string): string[] => {
                            return splitAxisLabel(label, needsWrap)
                        }
                        return dataPoints.map((dp, idx) => {
                            if (!shouldRenderAxisLabel(idx, dataPoints.length, 12)) {
                                return null
                            }
                            const lines = splitPeriod(dp.period)
                            return lines.map((line, lineIdx) => (
                                <text
                                    key={`${idx}-${lineIdx}`}
                                    x={getBarX(idx)}
                                    y={vbHeight - padding.bottom + 14 + lineIdx * 14}
                                    fill="var(--color-text-secondary)"
                                    fontSize="12"
                                    textAnchor="middle"
                                >
                                    {line}
                                </text>
                            ))
                        })
                    })()}
                </svg>
                {!options?.hideLegend ? (
                    <div className="line-chart-legend">
                        {seriesNames.map((name, idx) => (
                            <span key={name} className="line-chart-legend-item">
                                <span
                                    className="line-chart-legend-line"
                                    style={{
                                        background: colors[idx],
                                        borderRadius: idx === 0 ? '2px' : '0',
                                        height: idx === 0 ? '12px' : '3px',
                                    }}
                                />
                                {name}
                            </span>
                        ))}
                    </div>
                ) : null}
            </div>
        )
    }

    const renderHeatmapChart = (chart: ChartSection) => {
        const colors = chart.config?.colors || ['#5b8db8', '#ef4444']
        const dayLabels = containsCjk(chart.title)
            ? ['周一', '周二', '周三', '周四', '周五', '周六', '周日']
            : ['Mon', 'Tue', 'Wed', 'Thu', 'Fri', 'Sat', 'Sun']

        // Parse heatmap data: "dow|hour|changeCount|incidentCount"
        const cellMap = new Map<string, { changeCount: number; incidentCount: number }>()
        let maxChange = 0
        for (const item of chart.items) {
            const parts = item.label.split('|')
            const dow = parts[0]
            const hour = parts[1]
            const changeCount = parseFloat(parts[2]) || 0
            const incidentCount = parseFloat(parts[3]) || 0
            cellMap.set(dow + '-' + hour, { changeCount, incidentCount })
            if (changeCount > maxChange) maxChange = changeCount
        }

        const vbWidth = 900
        const vbHeight = 280
        const padding = { top: 10, right: 20, bottom: 30, left: 50 }
        const innerWidth = vbWidth - padding.left - padding.right
        const innerHeight = vbHeight - padding.top - padding.bottom
        const cellW = innerWidth / 24
        const cellH = innerHeight / 7

        return (
            <div style={{ width: '100%' }}>
                <svg
                    viewBox={`0 0 ${vbWidth} ${vbHeight}`}
                    preserveAspectRatio="xMidYMid meet"
                    style={{ width: '100%', height: '280px', display: 'block' }}
                >
                    {/* Y-axis labels (days) */}
                    {dayLabels.map((label, idx) => (
                        <text
                            key={label}
                            x={padding.left - 8}
                            y={padding.top + idx * cellH + cellH / 2 + 4}
                            fill="var(--color-text-secondary)"
                            fontSize="11"
                            textAnchor="end"
                        >
                            {label}
                        </text>
                    ))}
                    {/* X-axis labels (hours) */}
                    {[0, 3, 6, 9, 12, 15, 18, 21].map(hour => (
                        <text
                            key={hour}
                            x={padding.left + hour * cellW + cellW / 2}
                            y={vbHeight - 8}
                            fill="var(--color-text-secondary)"
                            fontSize="11"
                            textAnchor="middle"
                        >
                            {hour}h
                        </text>
                    ))}
                    {/* Cells */}
                    {Array.from({ length: 7 }, (_, dow) =>
                        Array.from({ length: 24 }, (_, hour) => {
                            const cell = cellMap.get((dow + 1) + '-' + hour)
                            const changeCount = cell?.changeCount || 0
                            const incidentCount = cell?.incidentCount || 0
                            const opacity = maxChange > 0 ? 0.06 + 0.84 * (changeCount / maxChange) : 0.06
                            const x = padding.left + hour * cellW
                            const y = padding.top + dow * cellH
                            return (
                                <g key={`${dow}-${hour}`}>
                                    <rect
                                        x={x + 1} y={y + 1}
                                        width={cellW - 2} height={cellH - 2}
                                        rx="3"
                                        fill={`rgba(91, 141, 184, ${opacity})`}
                                    />
                                    {changeCount > 0 && (
                                        <text
                                            x={x + cellW / 2} y={y + cellH / 2 + 3}
                                            fill={opacity > 0.5 ? 'white' : 'var(--color-text-secondary)'}
                                            fontSize="9"
                                            textAnchor="middle"
                                        >
                                            {changeCount}
                                        </text>
                                    )}
                                    {incidentCount > 0 && (
                                        <circle
                                            cx={x + cellW - 6} cy={y + 6}
                                            r="4"
                                            fill={colors[1]}
                                            opacity="0.85"
                                        />
                                    )}
                                </g>
                            )
                        })
                    )}
                </svg>
                <div className="line-chart-legend">
                    <span className="line-chart-legend-item">
                        <span className="line-chart-legend-line" style={{ background: 'rgba(91, 141, 184, 0.6)', height: '12px', width: '20px', borderRadius: '3px' }} />
                        {_t('businessIntelligence.incidents.charts.changeDensity')}
                    </span>
                    <span className="line-chart-legend-item">
                        <span style={{ display: 'inline-block', width: '8px', height: '8px', borderRadius: '50%', background: colors[1] }} />
                        {_t('businessIntelligence.incidents.charts.incidentHotspots')}
                    </span>
                </div>
            </div>
        )
    }

    const renderBubbleChart = (chart: ChartSection) => {
        const seriesColors = chart.config?.colors || ['#5b8db8', '#10b981', '#f59e0b', '#ef4444', '#8b7fc7', '#c97082']
        const xAxisLabel = chart.config?.xAxisLabel || _t('businessIntelligence.incidents.charts.averageBacklogDays')
        const yAxisLabel = chart.config?.yAxisLabel || _t('businessIntelligence.incidents.charts.openProblemCount')
        // Parse: "ciName|rootCauseCategory|avgAging|openProblemCount|totalIncidents"
        const points = chart.items
            .map(item => {
                const parts = item.label.split('|')
                return {
                    ci: parts[0] || _t('businessIntelligence.incidents.fallbacks.unlabeled'),
                    category: parts[1] || _t('businessIntelligence.incidents.fallbacks.unlabeled'),
                    avgAging: parseFloat(parts[2]) || 0,
                    openCount: parseFloat(parts[3]) || 0,
                    totalIncidents: parseFloat(parts[4]) || 0,
                }
            })
            .filter(p => p.avgAging > 0 || p.openCount > 0 || p.totalIncidents > 0)
        const uniqueCategories = [...new Set(points.map(p => p.category))]
        const colorMap = new Map(uniqueCategories.map((cat, idx) => [cat, seriesColors[idx % seriesColors.length]]))
        const maxAging = Math.max(...points.map(p => p.avgAging), 1)
        const maxOpen = Math.max(...points.map(p => p.openCount), 1)
        const vbWidth = 900
        const vbHeight = 400
        const padding = { top: 20, right: 30, bottom: 60, left: 70 }
        const innerWidth = vbWidth - padding.left - padding.right
        const innerHeight = vbHeight - padding.top - padding.bottom
        const getX = (aging: number) => padding.left + (aging / maxAging) * innerWidth
        const getY = (open: number) => padding.top + innerHeight - (open / maxOpen) * innerHeight
        const getR = (incidents: number) => Math.max(10, Math.min(50, 10 + incidents * 2))
        const sorted = [...points].sort((a, b) => b.totalIncidents - a.totalIncidents)
        const trunc = (s: string, max: number) => s.length > max ? s.slice(0, max) + '…' : s
        return (
            <div style={{ width: '100%' }}>
                <svg
                    viewBox={`0 0 ${vbWidth} ${vbHeight}`}
                    preserveAspectRatio="xMidYMid meet"
                    style={{ width: '100%', height: '400px', display: 'block' }}
                >
                    {/* Y-axis grid + labels */}
                    {[0, 0.25, 0.5, 0.75, 1].map(ratio => {
                        const val = Math.round(maxOpen * ratio)
                        const y = getY(val)
                        return (
                            <g key={`y${ratio}`}>
                                <line x1={padding.left} y1={y} x2={vbWidth - padding.right} y2={y}
                                    stroke="var(--color-border)" strokeDasharray="4 4" />
                                <text x={padding.left - 10} y={y + 4}
                                    fill="var(--color-text-secondary)" fontSize="12" textAnchor="end">
                                    {val}
                                </text>
                            </g>
                        )
                    })}
                    {/* X-axis labels */}
                    {[0, 0.25, 0.5, 0.75, 1].map(ratio => {
                        const val = Math.round(maxAging * ratio)
                        return (
                            <text key={`x${ratio}`}
                                x={getX(val)} y={vbHeight - 30}
                                fill="var(--color-text-secondary)" fontSize="11" textAnchor="middle">
                                {val}{_t('businessIntelligence.incidents.units.daysShort')}
                            </text>
                        )
                    })}
                    {/* X-axis label */}
                    <text x={padding.left + innerWidth / 2} y={vbHeight - 8}
                        fill="var(--color-text-secondary)" fontSize="12" textAnchor="middle" fontWeight="500">
                        {xAxisLabel}
                    </text>
                    {/* Y-axis label (rotated) */}
                    <text x={14} y={padding.top + innerHeight / 2}
                        fill="var(--color-text-secondary)" fontSize="12" textAnchor="middle" fontWeight="500"
                        transform={`rotate(-90, 14, ${padding.top + innerHeight / 2})`}>
                        {yAxisLabel}
                    </text>
                    {/* Bubbles */}
                    {sorted.map((p, idx) => (
                        <g key={idx}>
                            <circle
                                cx={getX(p.avgAging)} cy={getY(p.openCount)}
                                r={getR(p.totalIncidents)}
                                fill={colorMap.get(p.category) || seriesColors[0]}
                                opacity="0.6"
                            />
                            <text
                                x={getX(p.avgAging)} y={getY(p.openCount) + 3}
                                fill="var(--color-text-primary)"
                                fontSize="10" textAnchor="middle" fontWeight="600"
                            >
                                {trunc(p.ci, 12)}
                            </text>
                        </g>
                    ))}
                </svg>
                <div className="line-chart-legend">
                    {uniqueCategories.slice(0, 6).map((cat, idx) => (
                        <span key={cat} className="line-chart-legend-item">
                            <span style={{ display: 'inline-block', width: '12px', height: '12px', borderRadius: '50%', background: seriesColors[idx % seriesColors.length] }} />
                            {cat}
                        </span>
                    ))}
                </div>
            </div>
        )
    }
    const renderChartContent = (chart: ChartSection, options?: { hideLegend?: boolean }) => {
        return (
            <>
                {chart.type === 'pie' && (
                    <PieDistributionCard
                        embedded
                        items={chart.items}
                        colors={chart.config?.colors || ['#5b8db8', '#10b981', '#f59e0b', '#ef4444', '#8b7fc7', '#c97082']}
                        otherLabel={_t('common.other')}
                    />
                )}
                {chart.type === 'line' && renderLineChart(chart, options)}
                {chart.type === 'combo' && renderComboChart(chart, options)}
                {chart.type === 'grouped-bar' && renderGroupedBarChart(chart, options)}
                {chart.type === 'stacked-bar' && renderStackedBarChart(chart, options)}
                {chart.type === 'column' && renderColumnChart(chart)}
                {chart.type === 'heatmap' && renderHeatmapChart(chart)}
                {chart.type === 'bubble' && renderBubbleChart(chart)}
                {(chart.type === 'bar' || !chart.type) && renderBarChart(chart)}
            </>
        )
    }

    const renderChart = (chart: ChartSection) => {
        const isWideChart = chart.type === 'line' || chart.type === 'combo' || chart.type === 'grouped-bar' || chart.type === 'heatmap' || chart.type === 'bubble'
        return (
            <section
                key={chart.id}
                className={`business-intelligence-chart-section business-intelligence-chart-${chart.type}`}
                style={isWideChart ? { gridColumn: '1 / -1', width: '100%' } : { gridColumn: 'span 1' }}
            >
                <h3 className="business-intelligence-content-card-title" style={{ fontSize: '1.125rem', marginBottom: 'var(--spacing-4)' }}>{chart.title}</h3>
                {renderChartContent(chart)}
            </section>
        )
    }

    const renderTableCell = (column: string, cell: string) => {
        if (column.trim().toLowerCase().includes('sla')) {
            const status = getSlaStatus(cell, _t)
            if (status) {
                return <StatusCell tone={status.tone} label={status.label} />
            }
        }

        return cell
    }

    const renderTable = (table: TableSection) => (
        <div className="business-intelligence-table-shell">
            <table className="business-intelligence-table">
                <thead>
                    <tr>
                        {table.columns.map((col, colIndex) => (
                            <th key={colIndex} className={getTableColumnClassName(col)}>{col}</th>
                        ))}
                    </tr>
                </thead>
                <tbody>
                    {table.rows.map((row, rowIndex) => (
                        <tr key={rowIndex}>
                            {row.map((cell, cellIndex) => {
                                const column = table.columns[cellIndex] || ''
                                const cellClasses = [
                                    getTableColumnClassName(column),
                                    column.trim().toLowerCase().includes('sla') ? 'business-intelligence-table-status' : '',
                                    (column.trim().toLowerCase().includes('title') || column.trim().toLowerCase().includes('标题')) ? 'business-intelligence-table-title-cell' : '',
                                ].filter(Boolean).join(' ')

                                return (
                                    <td key={cellIndex} className={cellClasses}>
                                        {renderTableCell(column, cell)}
                                    </td>
                                )
                            })}
                        </tr>
                    ))}
                </tbody>
            </table>
        </div>
    )

    if (isIncidentAnalysisTab(localizedTab)) {
        const trendCharts = localizedTab.charts.filter(chart => chart.type !== 'pie')
        const distributionCharts = localizedTab.charts.filter(chart => chart.type === 'pie')

        return (
            <div className="business-intelligence-tab-layout">
                {localizedTab.cards.length > 0 ? (
                    <section className="business-intelligence-section-group">
                        <h2 className="business-intelligence-section-title">{getIncidentSectionTitle('incidentSnapshot', localizedTab, _t)}</h2>
                        <div
                            className="business-intelligence-stat-grid business-intelligence-stat-grid-snapshot ui-metric-grid"
                            style={{ gridTemplateColumns: `repeat(${localizedTab.cards.length % 3 === 0 ? 3 : 2}, minmax(0, 1fr))` }}
                        >
                            {localizedTab.cards.map(card => {
                                const tone = getMetricTone(card.tone)
                                return (
                                    <StatCard
                                        key={card.id}
                                        label={card.label}
                                        value={card.value}
                                        tone={tone}
                                        icon={tone === 'neutral' ? null : <StatusIcon tone={tone} />}
                                    />
                                )
                            })}
                        </div>
                    </section>
                ) : null}

                {trendCharts.length > 0 ? (
                    <section className="business-intelligence-section-group">
                        <h2 className="business-intelligence-section-title">{getIncidentSectionTitle('incidentTrends', localizedTab, _t)}</h2>
                        <div className="business-intelligence-section-stack">
                            {trendCharts.map(chart => (
                                <SectionCard
                                    key={chart.id}
                                    title={chart.title}
                                    action={<ChartHeaderLegend items={getChartLegendItems(chart, _t)} />}
                                >
                                    <div className={`business-intelligence-chart-surface business-intelligence-chart-surface-${chart.type}`}>
                                        {renderChartContent(chart, { hideLegend: true })}
                                    </div>
                                </SectionCard>
                            ))}
                        </div>
                    </section>
                ) : null}

                {distributionCharts.length > 0 ? (
                    <section className="business-intelligence-section-group">
                        <h2 className="business-intelligence-section-title">{getIncidentSectionTitle('distribution', localizedTab, _t)}</h2>
                        <div className="business-intelligence-distribution-grid">
                            {distributionCharts.map(chart => (
                                <PieDistributionCard
                                    key={chart.id}
                                    title={chart.title}
                                    items={chart.items}
                                    colors={chart.config?.colors || ['#5b8db8', '#10b981', '#f59e0b', '#ef4444', '#8b7fc7', '#c97082']}
                                    otherLabel={_t('common.other')}
                                />
                            ))}
                        </div>
                    </section>
                ) : null}

                {localizedTab.tables.length > 0 ? (
                    <section className="business-intelligence-section-group">
                        <h2 className="business-intelligence-section-title">{getIncidentSectionTitle('operationalTables', localizedTab, _t)}</h2>
                        <div className="business-intelligence-section-stack">
                            {localizedTab.tables.map(table => (
                                <AnalyticsTableCard key={table.id} title={table.title}>
                                    {renderTable(table)}
                                </AnalyticsTableCard>
                            ))}
                        </div>
                    </section>
                ) : null}
            </div>
        )
    }

    return (
        <div className="business-intelligence-content-card">
            <div className="business-intelligence-content-card-header">
                <div className="business-intelligence-content-card-copy">
                    <h2 className="business-intelligence-content-card-title">{localizedTab.label}</h2>
                    <p className="business-intelligence-content-card-description">{localizedTab.description}</p>
                </div>
            </div>

            <div className="business-intelligence-content-card-body">
                {/* Cards Section */}
                {localizedTab.cards.length > 0 && (
                    <div className="business-intelligence-section">
                        <div className="business-intelligence-cards" style={{ gridTemplateColumns: `repeat(${localizedTab.cards.length % 3 === 0 ? 3 : 2}, minmax(0, 1fr))` }}>
                            {localizedTab.cards.map(card => (
                                <div key={card.id} className={`business-intelligence-card ${getToneClass(card.tone)}`}>
                                    <div className="business-intelligence-card-head">
                                        <div>
                                            <div className="business-intelligence-card-label">{card.label}</div>
                                            <div className="business-intelligence-card-value">{card.value}</div>
                                        </div>
                                    </div>
                                </div>
                            ))}
                        </div>
                    </div>
                )}

                {/* Charts Section */}
                {localizedTab.charts.length > 0 && (
                    <div className="business-intelligence-section">
                        <div className="business-intelligence-content-grid">
                            {localizedTab.charts.map(chart => renderChart(chart))}
                        </div>
                    </div>
                )}

                {/* Tables Section */}
                {localizedTab.tables.length > 0 && (
                    <div className="business-intelligence-section">
                        {localizedTab.tables.map(table => (
                            <section key={table.id} className="business-intelligence-table-section">
                                <h3 className="business-intelligence-content-card-title" style={{ fontSize: '1.125rem', marginBottom: 'var(--spacing-3)' }}>{table.title}</h3>
                                {renderTable(table)}
                            </section>
                        ))}
                    </div>
                )}
            </div>
        </div>
    )
}

export default function BusinessIntelligence() {
    const { t } = useTranslation()
    const { showToast } = useToast()
    const [overview, setOverview] = useState<OverviewResponse | null>(null)
    const [activeTabId, setActiveTabId] = useState<string>('executive-summary')
    const [loading, setLoading] = useState(true)
    const [refreshing, setRefreshing] = useState(false)
    const [error, setError] = useState<string | null>(null)
    const [reportingPeriod, setReportingPeriod] = useState<ReportingPeriod>(getDefaultReportingPeriod())

    const loadOverview = useCallback(async (options?: { forceRefresh?: boolean; startDate?: string; endDate?: string }) => {
        const forceRefresh = options?.forceRefresh === true
        const startDate = options?.startDate
        const endDate = options?.endDate

        if (forceRefresh) {
            setRefreshing(true)
        } else {
            setLoading(true)
        }
        setError(null)
        try {
            const params = new URLSearchParams()
            if (startDate) params.append('startDate', startDate)
            if (endDate) params.append('endDate', endDate)
            const queryString = params.toString()
            const baseUrl = forceRefresh ? `${BUSINESS_INTELLIGENCE_SERVICE_URL}/refresh` : `${BUSINESS_INTELLIGENCE_SERVICE_URL}/overview`
            const url = queryString ? `${baseUrl}?${queryString}` : baseUrl

            const response = await fetch(url, {
                method: forceRefresh ? 'POST' : 'GET',
            })

            const contentType = response.headers.get('content-type') || ''
            const isJson = contentType.includes('application/json')

            if (!response.ok) {
                if (!isJson) {
                    throw new Error(t('businessIntelligence.serviceUnavailable', {
                        status: response.status,
                        statusText: response.statusText,
                    }))
                }

                const errorPayload = await response.json().catch(() => null) as { message?: string } | null
                throw new Error(errorPayload?.message || `${response.status} ${response.statusText}`)
            }

            if (!isJson) {
                throw new Error(t('businessIntelligence.invalidJsonResponse'))
            }

            const data = await response.json() as OverviewResponse
            setOverview(data)
            setActiveTabId(current => (data.tabs.length > 0 && !data.tabs.some(tab => tab.id === current) ? data.tabs[0].id : current))
            if (forceRefresh) {
                showToast('success', t('businessIntelligence.refreshSuccess'))
            }
        } catch (requestError) {
            const message = requestError instanceof Error ? requestError.message : t('common.unknownError')
            setError(message)
            if (forceRefresh) {
                showToast('error', t('businessIntelligence.refreshFailed', { error: message }))
            }
        } finally {
            setLoading(false)
            setRefreshing(false)
        }
    }, [showToast, t])

    // Refresh all data when reporting period changes
    useEffect(() => {
        if (reportingPeriod.startDate && reportingPeriod.endDate) {
            void loadOverview({
                startDate: reportingPeriod.startDate,
                endDate: reportingPeriod.endDate,
            })
        }
    }, [reportingPeriod.startDate, reportingPeriod.endDate])

    useEffect(() => {
        void loadOverview()
    }, [loadOverview])

    const activeTab = useMemo(() => {
        if (!overview) return null
        return overview.tabContents[activeTabId] || overview.tabContents[overview.tabs[0]?.id || ''] || null
    }, [activeTabId, overview])

    return (
        <div className="page-container sidebar-top-page page-shell-wide business-intelligence-page">
            <PageHeader
                title={t('businessIntelligence.title')}
                subtitle={t('businessIntelligence.subtitle')}
                action={(
                    <div className="business-intelligence-header-controls">
                        <div className="business-intelligence-toolbar-actions">
                        <FilterInlineGroup>
                            <ReportingPeriodSelector
                                value={reportingPeriod}
                                onChange={setReportingPeriod}
                                disabled={loading || refreshing}
                            />
                        </FilterInlineGroup>
                        <Button
                            variant="secondary"
                            size="sm"
                            iconOnly
                            className="business-intelligence-refresh-button"
                            onClick={() => void loadOverview({ forceRefresh: true })}
                            disabled={refreshing}
                            aria-label={refreshing ? t('businessIntelligence.refreshing') : t('businessIntelligence.refresh')}
                            title={refreshing ? t('businessIntelligence.refreshing') : t('businessIntelligence.refresh')}
                            leadingIcon={<RefreshCw size={15} className={refreshing ? 'business-intelligence-refresh-icon spinning' : 'business-intelligence-refresh-icon'} />}
                        />
                        </div>
                    </div>
                )}
            />

            {error ? (
                <div className="conn-banner conn-banner-error">
                    {t('common.connectionError', { error })}
                </div>
            ) : null}

            {loading ? (
                <div className="empty-state">
                    <div className="empty-state-title">{t('common.loading')}</div>
                    <div className="empty-state-description">{t('businessIntelligence.loadingDescription')}</div>
                </div>
            ) : overview && activeTab ? (
                <>
                    <div className="config-tabs" role="tablist" aria-label={t('businessIntelligence.tabsLabel')}>
                        {overview.tabs.map(tab => (
                            <button
                                key={tab.id}
                                type="button"
                                role="tab"
                                aria-selected={tab.id === activeTab.id}
                                className={`config-tab ${tab.id === activeTab.id ? 'config-tab-active' : ''}`}
                                onClick={() => setActiveTabId(tab.id)}
                            >
                                {getBusinessIntelligenceTabLabel(tab, t)}
                            </button>
                        ))}
                    </div>

                    {activeTab.id === 'executive-summary' && activeTab.executiveSummary ? (
                        <ExecutiveSummaryPanel summary={activeTab.executiveSummary} cards={activeTab.cards} t={t} />
                    ) : (
                        <GenericTabPanel tab={activeTab} t={t} />
                    )}
                </>
            ) : (
                <div className="empty-state">
                    <h3 className="empty-state-title">{t('businessIntelligence.emptyTitle')}</h3>
                    <p className="empty-state-description">{t('businessIntelligence.emptyDescription')}</p>
                </div>
            )}
        </div>
    )
}
