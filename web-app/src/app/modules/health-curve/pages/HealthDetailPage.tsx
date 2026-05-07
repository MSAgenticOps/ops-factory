import { useParams } from 'react-router-dom'
import { useTranslation } from 'react-i18next'
import HealthCurveChart from '../components/HealthCurveChart'
import { useUser } from '../../../platform/providers/UserContext'
import { getHealthIndicator } from '../../../../services/healthCurveAPI'
import type { HealthIndicatorPoint } from '../../../../types/health'
import { useState, useEffect, useCallback } from 'react'

export default function HealthDetailPage() {
    const { envCode } = useParams<{ envCode: string }>()
    const { t } = useTranslation()
    const { userId } = useUser()
    const [points, setPoints] = useState<HealthIndicatorPoint[]>([])

    const fetchData = useCallback(async () => {
        if (!envCode) return
        try {
            const end = Date.now()
            const start = end - 3600000
            const res = await getHealthIndicator(envCode, start, end, userId)
            setPoints(res.results || [])
        } catch { setPoints([]) }
    }, [envCode, userId])

    useEffect(() => { fetchData() }, [fetchData])

    return (
        <div className="health-detail-page">
            <h1>{t('healthCurve.detailTitle')} - {envCode}</h1>
            <HealthCurveChart points={points} loading={false} />
        </div>
    )
}
