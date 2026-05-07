import { useState, useEffect } from 'react'
import { useTranslation } from 'react-i18next'
import { useUser } from '../../../platform/providers/UserContext'
import { getEnvironments, getProductConfigRule } from '../../../../services/healthCurveAPI'
import type { EnvironmentInfo } from '../../../../services/healthCurveAPI'

interface HealthCurveFiltersProps {
    envCode: string
    onEnvCodeChange: (v: string) => void
    onTimeRangeChange: (start: number, end: number) => void
    onRefresh: () => void
}

export default function HealthCurveFilters({
    envCode, onEnvCodeChange, onTimeRangeChange, onRefresh,
}: HealthCurveFiltersProps) {
    const { t } = useTranslation()
    const { userId } = useUser()
    const [products, setProducts] = useState<string[]>([])
    const [selectedProduct, setSelectedProduct] = useState('')
    const [envOptions, setEnvOptions] = useState<EnvironmentInfo[]>([])
    const [alarmScoreMax, setAlarmScoreMax] = useState<number | null>(null)
    const [weights, setWeights] = useState<{ a: string; p: string; r: string } | null>(null)
    const now = Date.now()

    useEffect(() => {
        getEnvironments(userId)
            .then(res => {
                const envs = res.results || []
                setEnvOptions(envs)
                const productTypes = [...new Set(envs.map((e: { agentSolutionType: string }) => e.agentSolutionType))]
                setProducts(productTypes)
                if (envs.length > 0 && !envCode) {
                    onEnvCodeChange(envs[0].envCode)
                }
            })
            .catch(() => { setEnvOptions([]); setProducts([]) })
    }, [userId])

    useEffect(() => {
        const product = selectedProduct || products[0]
        if (!product) return
        getProductConfigRule(product, userId)
            .then(res => {
                const rule = res.result as Record<string, unknown> | null
                if (rule) {
                    if (rule.alarmScoreMax != null) setAlarmScoreMax(rule.alarmScoreMax as number)
                    if (rule.healthWeight && typeof rule.healthWeight === 'string') {
                        const parts = rule.healthWeight.split(',')
                        if (parts.length === 3) {
                            setWeights({ a: parts[0].trim(), p: parts[1].trim(), r: parts[2].trim() })
                        }
                    }
                }
            })
            .catch(() => { setAlarmScoreMax(null); setWeights(null) })
    }, [selectedProduct, products, userId])

    const filteredEnvs = selectedProduct
        ? envOptions.filter(e => e.agentSolutionType === selectedProduct)
        : envOptions

    useEffect(() => {
        if (filteredEnvs.length > 0 && !filteredEnvs.some(e => e.envCode === envCode)) {
            onEnvCodeChange(filteredEnvs[0].envCode)
        }
    }, [filteredEnvs])

    const [timeWindow, setTimeWindow] = useState(1)

    const timeOptions = [
        { value: 0.25, label: t('healthCurve.last15Min') },
        { value: 1, label: t('healthCurve.lastHour') },
        { value: 2, label: t('healthCurve.last2Hours') },
        { value: 12, label: t('healthCurve.last12Hours') },
        { value: 24, label: t('healthCurve.last24Hours') },
        { value: 48, label: t('healthCurve.last48Hours') },
    ]

    const handleTimeWindow = (hours: number) => {
        setTimeWindow(hours)
        onTimeRangeChange(now - hours * 3600000, now)
    }

    return (
        <div className="health-curve-filters">
            <select value={selectedProduct} onChange={e => setSelectedProduct(e.target.value)}>
                {products.map(p => (
                    <option key={p} value={p}>{p}</option>
                ))}
            </select>
            <select value={envCode} onChange={e => onEnvCodeChange(e.target.value)}>
                {filteredEnvs.map(e => (
                    <option key={e.envCode} value={e.envCode}>{e.envCode}</option>
                ))}
            </select>
            <select value={timeWindow} onChange={e => handleTimeWindow(Number(e.target.value))}>
                {timeOptions.map(o => (
                    <option key={o.value} value={o.value}>{o.label}</option>
                ))}
            </select>
            {alarmScoreMax != null && (
                <span className="hc-filter-label">{t('healthCurve.alarmScoreMax')}: {alarmScoreMax}</span>
            )}
            {weights && (
                <span className="hc-filter-label">
                    {t('healthCurve.availability')}: {weights.a}  {t('healthCurve.performance')}: {weights.p}  {t('healthCurve.resource')}: {weights.r}
                </span>
            )}
            <button onClick={onRefresh}>{t('healthCurve.refresh')}</button>
        </div>
    )
}
