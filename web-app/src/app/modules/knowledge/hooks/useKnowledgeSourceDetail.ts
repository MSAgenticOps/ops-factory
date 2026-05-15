import { useCallback, useEffect, useState } from 'react'
import { runtime } from '../../../../config/runtime'
import { getErrorMessage } from '../../../../utils/errorMessages'
import type {
    KnowledgeCapabilities,
    KnowledgeDefaults,
    KnowledgeMaintenanceFailure,
    KnowledgeMaintenanceOverview,
    KnowledgeSource,
    KnowledgeSourceProfileConfig,
    KnowledgeSourceStats,
    KnowledgeSourceUpdateRequest,
} from '../../../../types/knowledge'

interface SaveSourceResult {
    success: boolean
    data?: KnowledgeSource
    error?: string
}

interface DeleteSourceResult {
    success: boolean
    error?: string
}

interface SaveProfileResult {
    success: boolean
    data?: KnowledgeSourceProfileConfig
    error?: string
}

interface UseKnowledgeSourceDetailResult {
    source: KnowledgeSource | null
    stats: KnowledgeSourceStats | null
    capabilities: KnowledgeCapabilities | null
    defaults: KnowledgeDefaults | null
    indexProfileDetail: KnowledgeSourceProfileConfig | null
    retrievalProfileDetail: KnowledgeSourceProfileConfig | null
    maintenance: KnowledgeMaintenanceOverview | null
    isLoading: boolean
    error: string | null
    hasSupportingDataError: boolean
    reload: () => Promise<void>
    loadMaintenanceFailures: (jobId: string) => Promise<KnowledgeMaintenanceFailure[]>
    saveSource: (updates: KnowledgeSourceUpdateRequest) => Promise<SaveSourceResult>
    saveIndexProfile: (updates: { name?: string; config?: Record<string, unknown> }) => Promise<SaveProfileResult>
    saveRetrievalProfile: (updates: { name?: string; config?: Record<string, unknown> }) => Promise<SaveProfileResult>
    resetIndexProfile: () => Promise<SaveProfileResult>
    resetRetrievalProfile: () => Promise<SaveProfileResult>
    deleteSource: () => Promise<DeleteSourceResult>
}

function createEmptyStats(sourceId: string): KnowledgeSourceStats {
    return {
        sourceId,
        documentCount: 0,
        indexedDocumentCount: 0,
        failedDocumentCount: 0,
        processingDocumentCount: 0,
        chunkCount: 0,
        userEditedChunkCount: 0,
        lastIngestionAt: null,
    }
}

async function requestJson<T>(url: string, init?: RequestInit): Promise<T> {
    const response = await fetch(url, {
        ...init,
        signal: init?.signal ?? AbortSignal.timeout(10000),
    })
    const data = await response.json().catch(() => null)

    if (!response.ok) {
        const message = data && typeof data === 'object' && 'message' in data
            ? String((data as { message?: string }).message || response.statusText)
            : response.statusText
        throw new Error(`HTTP ${response.status}: ${message}`)
    }

    return data as T
}

function resetDetailState(
    setSource: React.Dispatch<React.SetStateAction<KnowledgeSource | null>>,
    setStats: React.Dispatch<React.SetStateAction<KnowledgeSourceStats | null>>,
    setCapabilities: React.Dispatch<React.SetStateAction<KnowledgeCapabilities | null>>,
    setDefaults: React.Dispatch<React.SetStateAction<KnowledgeDefaults | null>>,
    setIndexProfile: React.Dispatch<React.SetStateAction<KnowledgeSourceProfileConfig | null>>,
    setRetrievalProfile: React.Dispatch<React.SetStateAction<KnowledgeSourceProfileConfig | null>>,
    setMaintenance: React.Dispatch<React.SetStateAction<KnowledgeMaintenanceOverview | null>>,
) {
    setSource(null); setStats(null); setCapabilities(null); setDefaults(null)
    setIndexProfile(null); setRetrievalProfile(null); setMaintenance(null)
}

async function fetchSupportingData(sourceId: string, sourceData: KnowledgeSource) {
    const baseUrl = runtime.KNOWLEDGE_SERVICE_URL
    return Promise.allSettled([
        requestJson<KnowledgeSourceStats>(`${baseUrl}/sources/${sourceId}/stats`),
        requestJson<KnowledgeCapabilities>(`${baseUrl}/capabilities`),
        requestJson<KnowledgeDefaults>(`${baseUrl}/system/defaults`),
        sourceData.indexProfileId ? requestJson<KnowledgeSourceProfileConfig>(`${baseUrl}/sources/${sourceId}/config/index-profile`) : Promise.resolve(null),
        sourceData.retrievalProfileId ? requestJson<KnowledgeSourceProfileConfig>(`${baseUrl}/sources/${sourceId}/config/retrieval-profile`) : Promise.resolve(null),
        requestJson<KnowledgeMaintenanceOverview>(`${baseUrl}/sources/${sourceId}/maintenance`),
    ])
}

function applySettledResults(
    results: PromiseSettledResult<unknown>[],
    sourceData: KnowledgeSource,
    setStats: React.Dispatch<React.SetStateAction<KnowledgeSourceStats | null>>,
    setCapabilities: React.Dispatch<React.SetStateAction<KnowledgeCapabilities | null>>,
    setDefaults: React.Dispatch<React.SetStateAction<KnowledgeDefaults | null>>,
    setIndexProfile: React.Dispatch<React.SetStateAction<KnowledgeSourceProfileConfig | null>>,
    setRetrievalProfile: React.Dispatch<React.SetStateAction<KnowledgeSourceProfileConfig | null>>,
    setMaintenance: React.Dispatch<React.SetStateAction<KnowledgeMaintenanceOverview | null>>,
): boolean {
    const [statsR, capsR, defsR, idxR, retR, maintR] = results
    const fulfilled = <T>(r: PromiseSettledResult<T>) => r.status === 'fulfilled' ? r.value : null
    setStats(fulfilled(statsR as PromiseSettledResult<KnowledgeSourceStats>) ?? createEmptyStats(sourceData.id))
    setCapabilities(fulfilled(capsR as PromiseSettledResult<KnowledgeCapabilities>))
    setDefaults(fulfilled(defsR as PromiseSettledResult<KnowledgeDefaults>))
    setIndexProfile(fulfilled(idxR as PromiseSettledResult<KnowledgeSourceProfileConfig | null>))
    setRetrievalProfile(fulfilled(retR as PromiseSettledResult<KnowledgeSourceProfileConfig | null>))
    setMaintenance(fulfilled(maintR as PromiseSettledResult<KnowledgeMaintenanceOverview>))
    return results.some(r => r.status === 'rejected')
}

async function doSaveProfile(
    path: string, method: string, body: unknown, setter: (d: KnowledgeSourceProfileConfig) => void, reload: () => Promise<void>, setError: (e: string | null) => void,
): Promise<SaveProfileResult> {
    setError(null)
    try {
        const detail = await requestJson<KnowledgeSourceProfileConfig>(
            `${runtime.KNOWLEDGE_SERVICE_URL}${path}`,
            { method, headers: { 'Content-Type': 'application/json' }, ...(body ? { body: JSON.stringify(body) } : {}) },
        )
        setter(detail)
        await reload()
        return { success: true, data: detail }
    } catch (err) {
        const message = getErrorMessage(err)
        setError(message)
        return { success: false, error: message }
    }
}

export function useKnowledgeSourceDetail(sourceId: string | undefined): UseKnowledgeSourceDetailResult {
    const [source, setSource] = useState<KnowledgeSource | null>(null)
    const [stats, setStats] = useState<KnowledgeSourceStats | null>(null)
    const [capabilities, setCapabilities] = useState<KnowledgeCapabilities | null>(null)
    const [defaults, setDefaults] = useState<KnowledgeDefaults | null>(null)
    const [indexProfileDetail, setIndexProfileDetail] = useState<KnowledgeSourceProfileConfig | null>(null)
    const [retrievalProfileDetail, setRetrievalProfileDetail] = useState<KnowledgeSourceProfileConfig | null>(null)
    const [maintenance, setMaintenance] = useState<KnowledgeMaintenanceOverview | null>(null)
    const [isLoading, setIsLoading] = useState(false)
    const [error, setError] = useState<string | null>(null)
    const [hasSupportingDataError, setHasSupportingDataError] = useState(false)

    const resetAll = useCallback(() => {
        resetDetailState(setSource, setStats, setCapabilities, setDefaults, setIndexProfileDetail, setRetrievalProfileDetail, setMaintenance)
        setError(null); setHasSupportingDataError(false); setIsLoading(false)
    }, [])

    const reload = useCallback(async () => {
        if (!sourceId) { resetAll(); return }
        setIsLoading(true); setError(null); setHasSupportingDataError(false)
        try {
            const sourceData = await requestJson<KnowledgeSource>(`${runtime.KNOWLEDGE_SERVICE_URL}/sources/${sourceId}`)
            const results = await fetchSupportingData(sourceId, sourceData)
            setSource(sourceData)
            const failed = applySettledResults(results, sourceData, setStats, setCapabilities, setDefaults, setIndexProfileDetail, setRetrievalProfileDetail, setMaintenance)
            setHasSupportingDataError(failed)
        } catch (err) {
            resetDetailState(setSource, setStats, setCapabilities, setDefaults, setIndexProfileDetail, setRetrievalProfileDetail, setMaintenance)
            setError(getErrorMessage(err))
        } finally { setIsLoading(false) }
    }, [sourceId, resetAll])

    useEffect(() => { void reload() }, [reload])

    const saveSource = useCallback(async (updates: KnowledgeSourceUpdateRequest): Promise<SaveSourceResult> => {
        if (!sourceId) return { success: false, error: 'Missing source id' }
        setError(null)
        try {
            const updatedSource = await requestJson<KnowledgeSource>(
                `${runtime.KNOWLEDGE_SERVICE_URL}/sources/${sourceId}`,
                { method: 'PATCH', headers: { 'Content-Type': 'application/json' }, body: JSON.stringify(updates) },
            )
            setSource(updatedSource)
            return { success: true, data: updatedSource }
        } catch (err) { const message = getErrorMessage(err); setError(message); return { success: false, error: message } }
    }, [sourceId])

    const saveIndexProfile = useCallback(async (updates: { name?: string; config?: Record<string, unknown> }): Promise<SaveProfileResult> => {
        if (!source?.indexProfileId || !sourceId) return { success: false, error: 'Missing index profile id' }
        return doSaveProfile(`/sources/${sourceId}/config/index-profile`, 'PUT', updates, setIndexProfileDetail, reload, setError)
    }, [reload, source?.indexProfileId, sourceId])

    const saveRetrievalProfile = useCallback(async (updates: { name?: string; config?: Record<string, unknown> }): Promise<SaveProfileResult> => {
        if (!source?.retrievalProfileId || !sourceId) return { success: false, error: 'Missing retrieval profile id' }
        return doSaveProfile(`/sources/${sourceId}/config/retrieval-profile`, 'PUT', updates, setRetrievalProfileDetail, reload, setError)
    }, [reload, source?.retrievalProfileId, sourceId])

    const resetIndexProfile = useCallback(async (): Promise<SaveProfileResult> => {
        if (!source?.indexProfileId || !sourceId) return { success: false, error: 'Missing index profile id' }
        return doSaveProfile(`/sources/${sourceId}/config/index-profile:reset`, 'POST', null, setIndexProfileDetail, reload, setError)
    }, [reload, source?.indexProfileId, sourceId])

    const resetRetrievalProfile = useCallback(async (): Promise<SaveProfileResult> => {
        if (!source?.retrievalProfileId || !sourceId) return { success: false, error: 'Missing retrieval profile id' }
        return doSaveProfile(`/sources/${sourceId}/config/retrieval-profile:reset`, 'POST', null, setRetrievalProfileDetail, reload, setError)
    }, [reload, source?.retrievalProfileId, sourceId])

    const deleteSource = useCallback(async (): Promise<DeleteSourceResult> => {
        if (!sourceId) return { success: false, error: 'Missing source id' }
        setError(null)
        try {
            await requestJson<{ sourceId: string; deleted: boolean }>(`${runtime.KNOWLEDGE_SERVICE_URL}/sources/${sourceId}`, { method: 'DELETE' })
            return { success: true }
        } catch (err) { const message = getErrorMessage(err); setError(message); return { success: false, error: message } }
    }, [sourceId])

    const loadMaintenanceFailures = useCallback(async (jobId: string): Promise<KnowledgeMaintenanceFailure[]> => {
        const response = await requestJson<{ jobId: string; items: KnowledgeMaintenanceFailure[] }>(`${runtime.KNOWLEDGE_SERVICE_URL}/jobs/${jobId}/failures`)
        return response.items || []
    }, [])

    return {
        source, stats, capabilities, defaults, indexProfileDetail, retrievalProfileDetail,
        maintenance, isLoading, error, hasSupportingDataError, reload, loadMaintenanceFailures,
        saveSource, saveIndexProfile, saveRetrievalProfile, resetIndexProfile, resetRetrievalProfile, deleteSource,
    }
}
