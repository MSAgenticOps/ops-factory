import { fireEvent, render, screen, waitFor } from '@testing-library/react'
import { beforeEach, describe, expect, it, vi } from 'vitest'
import BusinessIntelligence from '../pages/BusinessIntelligence'

const showToast = vi.fn()
const translate = (key: string, params?: Record<string, unknown>) => {
    if (params?.error) return `${key}:${String(params.error)}`
    return key
}

vi.mock('react-i18next', () => ({
    useTranslation: () => ({
        t: translate,
    }),
}))

vi.mock('../contexts/ToastContext', () => ({
    useToast: () => ({
        showToast,
    }),
}))

vi.mock('../config/runtime', () => ({
    BUSINESS_INTELLIGENCE_SERVICE_URL: 'http://127.0.0.1:8093/business-intelligence',
}))

vi.mock('echarts-for-react', () => ({
    default: () => <div data-testid="echarts-mock" />,
}))

describe('BusinessIntelligence page', () => {
    beforeEach(() => {
        vi.clearAllMocks()
        vi.stubGlobal('fetch', vi.fn((input: string | URL | Request, init?: RequestInit) => {
            const url = String(input)
            const method = init?.method ?? 'GET'

            if (method === 'GET' && url.endsWith('/business-intelligence/overview')) {
                return Promise.resolve({
                    ok: true,
                    json: async () => ({
                        refreshedAt: '2026-04-01T08:00:00Z',
                        tabs: [
                            { id: 'executive-summary', label: '执行摘要' },
                            { id: 'sla-analysis', label: 'SLA分析' },
                        ],
                        tabContents: {
                            'executive-summary': {
                                id: 'executive-summary',
                                label: '执行摘要',
                                description: '摘要说明',
                                executiveSummary: {
                                    hero: {
                                        score: '82',
                                        grade: 'Watch',
                                        summary: '整体运行可控，但变更与问题流程需要优先关注。',
                                        changeHint: '较上期下降 3.2 分。',
                                        periodLabel: '2026-03-01 至 2026-03-31',
                                    },
                                    processHealths: [
                                        { id: 'incident', label: '事件', score: '78', tone: 'warning', summary: 'SLA 96.0%，MTTR 5.4h' },
                                        { id: 'change', label: '变更', score: '64', tone: 'danger', summary: '成功率 84.0%，致事件率 18.0%' },
                                    ],
                                    riskSummary: {
                                        critical: 1,
                                        warning: 2,
                                        attention: 1,
                                        topRisks: [
                                            { id: 'r1', priority: 'Critical', title: '变更失败率偏高', impact: '发布稳定性下降。', process: '变更', value: '8' },
                                            { id: 'r2', priority: 'Warning', title: '未关闭问题偏多', impact: '根因治理积压。', process: '问题', value: '26' },
                                        ],
                                    },
                                    trend: {
                                        title: '月度健康趋势',
                                        subtitle: '健康分与高优先级事件同步观察。',
                                        points: [
                                            { label: '2025-12', score: 81, signal: 3 },
                                            { label: '2026-01', score: 83, signal: 2 },
                                            { label: '2026-02', score: 79, signal: 4 },
                                            { label: '2026-03', score: 82, signal: 3 },
                                        ],
                                    },
                                },
                                slaAnalysis: null,
                                cards: [{ id: 'c1', label: '事件 SLA 达成率', value: '96.0%', tone: 'success' }],
                                charts: [],
                                tables: [],
                            },
                            'sla-analysis': {
                                id: 'sla-analysis',
                                label: 'SLA分析',
                                description: 'SLA说明',
                                executiveSummary: null,
                                slaAnalysis: {
                                    hero: {
                                        summary: '响应履约整体稳定，但解决履约在高优先级与高频类别上持续承压。',
                                        overallComplianceRate: '72.4%',
                                        responseComplianceRate: '99.8%',
                                        resolutionComplianceRate: '64.4%',
                                        breachedCount: 320,
                                        highPriorityComplianceRate: '19.4%',
                                    },
                                    response: {
                                        title: '响应 SLA',
                                        complianceRate: '99.8%',
                                        averageDuration: '0.3m',
                                        p90Duration: '1.0m',
                                        breachedCount: 11,
                                        tone: 'success',
                                        assessment: '整体稳定',
                                    },
                                    resolution: {
                                        title: '解决 SLA',
                                        complianceRate: '64.4%',
                                        averageDuration: '29.6h',
                                        p90Duration: '43.0h',
                                        breachedCount: 319,
                                        tone: 'danger',
                                        assessment: '当前主要风险',
                                    },
                                    priorityRows: [
                                        { priority: 'P1', totalCount: 448, responseComplianceRate: '98.9%', resolutionComplianceRate: '6.9%', breachedCount: 417, averageResolutionDuration: '43.7h' },
                                        { priority: 'P2', totalCount: 1346, responseComplianceRate: '99.7%', resolutionComplianceRate: '15.5%', breachedCount: 1138, averageResolutionDuration: '29.2h' },
                                    ],
                                    priorityComparison: {
                                        title: '优先级响应 vs 解决对比',
                                        items: [
                                            { priority: 'P1', responseComplianceRate: 98.9, resolutionComplianceRate: 6.9 },
                                            { priority: 'P2', responseComplianceRate: 99.7, resolutionComplianceRate: 15.5 },
                                        ],
                                    },
                                    categoryRisks: [
                                        { label: 'Digital View Monitoring', totalCount: 3936, responseComplianceRate: '100.0%', resolutionComplianceRate: '60.4%', breachedCount: 1557, averageResolutionDuration: '24.6h' },
                                    ],
                                    resolverRisks: [
                                        { label: 'Jessica Smith', totalCount: 414, responseComplianceRate: '99.8%', resolutionComplianceRate: '61.1%', breachedCount: 161, averageResolutionDuration: '27.3h' },
                                    ],
                                    trends: [
                                        { period: '2026-01', overallComplianceRate: 74.1, responseComplianceRate: 99.7, resolutionComplianceRate: 66.2, breachedCount: 102 },
                                        { period: '2026-02', overallComplianceRate: 71.4, responseComplianceRate: 99.8, resolutionComplianceRate: 63.1, breachedCount: 113 },
                                    ],
                                    violationBreakdown: {
                                        responseBreached: 11,
                                        resolutionBreached: 319,
                                        bothBreached: 10,
                                    },
                                    violationSamples: [
                                        { orderNumber: 'INC-1001', orderName: 'Payment timeout', priority: 'P1', category: 'Card', resolver: 'Jessica Smith', responseDuration: '12.0m', resolutionDuration: '41.2h', violationType: '双违约' },
                                    ],
                                },
                                cards: [{ id: 'c2', label: '综合达成率', value: '72.4%', tone: 'warning' }],
                                charts: [],
                                tables: [],
                            },
                        },
                    }),
                } as Response)
            }

            if (method === 'POST' && url.endsWith('/business-intelligence/refresh')) {
                return Promise.resolve({
                    ok: true,
                    json: async () => ({
                        refreshedAt: '2026-04-01T09:00:00Z',
                        tabs: [
                            { id: 'executive-summary', label: '执行摘要' },
                            { id: 'sla-analysis', label: 'SLA分析' },
                        ],
                        tabContents: {
                            'executive-summary': {
                                id: 'executive-summary',
                                label: '执行摘要',
                                description: '摘要说明',
                                executiveSummary: {
                                    hero: {
                                        score: '84',
                                        grade: 'Stable',
                                        summary: '整体运行稳定，但仍需持续关注问题积压。',
                                        changeHint: '较上期提升 2.0 分。',
                                        periodLabel: '2026-03-01 至 2026-03-31',
                                    },
                                    processHealths: [
                                        { id: 'incident', label: '事件', score: '81', tone: 'success', summary: 'SLA 97.0%，MTTR 4.8h' },
                                    ],
                                    riskSummary: {
                                        critical: 0,
                                        warning: 1,
                                        attention: 1,
                                        topRisks: [
                                            { id: 'r1', priority: 'Warning', title: '未关闭问题偏多', impact: '根因治理积压。', process: '问题', value: '22' },
                                        ],
                                    },
                                    trend: {
                                        title: '月度健康趋势',
                                        subtitle: '健康分与高优先级事件同步观察。',
                                        points: [
                                            { label: '2026-02', score: 82, signal: 3 },
                                            { label: '2026-03', score: 84, signal: 2 },
                                        ],
                                    },
                                },
                                slaAnalysis: null,
                                cards: [{ id: 'c1', label: '事件 SLA 达成率', value: '97.0%', tone: 'success' }],
                                charts: [],
                                tables: [],
                            },
                            'sla-analysis': {
                                id: 'sla-analysis',
                                label: 'SLA分析',
                                description: 'SLA说明',
                                executiveSummary: null,
                                slaAnalysis: {
                                    hero: {
                                        summary: '响应履约稳定，解决环节较上期略有改善。',
                                        overallComplianceRate: '75.2%',
                                        responseComplianceRate: '99.9%',
                                        resolutionComplianceRate: '67.0%',
                                        breachedCount: 301,
                                        highPriorityComplianceRate: '22.6%',
                                    },
                                    response: {
                                        title: '响应 SLA',
                                        complianceRate: '99.9%',
                                        averageDuration: '0.2m',
                                        p90Duration: '1.0m',
                                        breachedCount: 8,
                                        tone: 'success',
                                        assessment: '整体稳定',
                                    },
                                    resolution: {
                                        title: '解决 SLA',
                                        complianceRate: '67.0%',
                                        averageDuration: '27.2h',
                                        p90Duration: '40.5h',
                                        breachedCount: 300,
                                        tone: 'warning',
                                        assessment: '需重点关注',
                                    },
                                    priorityRows: [],
                                    priorityComparison: { title: '优先级响应 vs 解决对比', items: [] },
                                    categoryRisks: [],
                                    resolverRisks: [],
                                    trends: [
                                        { period: '2026-03', overallComplianceRate: 75.2, responseComplianceRate: 99.9, resolutionComplianceRate: 67.0, breachedCount: 301 },
                                    ],
                                    violationBreakdown: {
                                        responseBreached: 8,
                                        resolutionBreached: 300,
                                        bothBreached: 7,
                                    },
                                    violationSamples: [],
                                },
                                cards: [{ id: 'c2', label: '综合达成率', value: '75.2%', tone: 'warning' }],
                                charts: [],
                                tables: [],
                            },
                        },
                    }),
                } as Response)
            }

            return Promise.resolve({
                ok: false,
                status: 404,
                statusText: 'Not Found',
                json: async () => ({ message: 'not found' }),
            } as Response)
        }))
    })

    it('loads overview and switches tabs', async () => {
        render(<BusinessIntelligence />)

        await screen.findByRole('tab', { name: '执行摘要' })
        expect(screen.getByRole('tab', { name: '执行摘要' })).toBeInTheDocument()
        expect(screen.getByText('健康分')).toBeInTheDocument()
        expect(screen.getByText('概览结论')).toBeInTheDocument()

        fireEvent.click(screen.getByRole('tab', { name: 'SLA分析' }))

        expect(screen.getByRole('tab', { name: 'SLA分析' })).toBeInTheDocument()
        expect(screen.getByText('待建设')).toBeInTheDocument()
    })

    it('refreshes data', async () => {
        render(<BusinessIntelligence />)

        await screen.findByRole('tab', { name: '执行摘要' })
        fireEvent.click(screen.getByRole('button', { name: 'businessIntelligence.refresh' }))

        await waitFor(() => {
            expect(showToast).toHaveBeenCalledWith('success', 'businessIntelligence.refreshSuccess')
        })
        expect(screen.getByText('健康分')).toBeInTheDocument()
    })
})
