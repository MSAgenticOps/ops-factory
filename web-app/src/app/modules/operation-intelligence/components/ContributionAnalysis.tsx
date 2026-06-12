import { useState, useEffect } from 'react'
import { useTranslation } from 'react-i18next'
import { useUser } from '../../../platform/providers/UserContext'
import PieDistributionCard from '../../../platform/ui/primitives/PieDistributionCard'
import SectionCard from '../../../platform/ui/primitives/SectionCard'
import { getContributionData } from '../../../../services/operationIntelligenceAPI'
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
        if (type === 'A') {
            return t('operationIntelligence.availability')
        }
        if (type === 'P') {
            return t('operationIntelligence.performance')
        }
        return t('operationIntelligence.resource')
    }

    if (data.length === 0) {
        return (
            <SectionCard
                title={t('operationIntelligence.contributionDetail')}
                className="contribution-card"
                bodyClassName="contribution-card-empty-body"
            >
                <div className="empty-state">
                    <div className="empty-state-title">{t('operationIntelligence.noData')}</div>
                </div>
            </SectionCard>
        )
    }

    return (
        <PieDistributionCard
            title={t('operationIntelligence.contributionDetail')}
            items={data.map(item => ({
                label: typeLabel(item.type),
                value: Number((item.contribution * 100).toFixed(2)),
            }))}
            colors={[CHART_COLORS.availability, CHART_COLORS.performance, CHART_COLORS.resource]}
            otherLabel={t('operationIntelligence.other')}
            maxVisibleItems={3}
            maxHeight={180}
            className="contribution-card"
        />
    )
}
