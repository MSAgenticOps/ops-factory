import { useCallback, useState } from 'react'
import type { SkillMarketDetail, SkillMarketEntry, SkillMarketListResponse } from '../../../../types/skillMarket'
import { runtime, gatewayHeaders } from '../../../../config/runtime'
import { getErrorMessage } from '../../../../utils/errorMessages'
import { useUser } from '../../../platform/providers/UserContext'

interface ApiErrorBody {
    code?: string
    message?: string
    error?: string
}

interface UseSkillMarketResult {
    skills: SkillMarketEntry[]
    isLoading: boolean
    error: string | null
    fetchSkills: (query?: string) => Promise<void>
    fetchSkill: (skillId: string) => Promise<{ success: boolean; skill?: SkillMarketDetail; error?: string }>
    createSkill: (payload: { id: string; name: string; description: string; instructions: string }) => Promise<{ success: boolean; error?: string }>
    updateSkill: (skillId: string, payload: { name: string; description: string; instructions: string }) => Promise<{ success: boolean; error?: string }>
    importSkill: (file: File, id?: string) => Promise<{ success: boolean; error?: string }>
    deleteSkill: (skillId: string) => Promise<{ success: boolean; error?: string }>
    installSkill: (agentId: string, skillId: string) => Promise<{ success: boolean; error?: string }>
}

async function skillFetch<T>(path: string, init?: RequestInit): Promise<T> {
    const response = await fetch(`${runtime.SKILL_MARKET_SERVICE_URL}${path}`, {
        signal: AbortSignal.timeout(10000),
        ...init,
    })
    if (!response.ok) throw new Error(await response.text())
    return response.json() as Promise<T>
}

function extractApiError(text: string): string {
    try {
        const body = JSON.parse(text) as ApiErrorBody
        if (body.code === 'SKILL_ALREADY_EXISTS') return 'SKILL_ALREADY_EXISTS'
        return body.message || body.error || text
    } catch { return text }
}

async function skillMutation(path: string, init: RequestInit, refresh: () => Promise<void>) {
    try {
        const response = await fetch(`${runtime.SKILL_MARKET_SERVICE_URL}${path}`, init)
        if (!response.ok) {
            const text = await response.text()
            throw new Error(extractApiError(text))
        }
        await response.json()
        await refresh()
        return { success: true }
    } catch (err) {
        return { success: false, error: getErrorMessage(err) }
    }
}

export function useSkillMarket(): UseSkillMarketResult {
    const { userId } = useUser()
    const [skills, setSkills] = useState<SkillMarketEntry[]>([])
    const [isLoading, setIsLoading] = useState(false)
    const [error, setError] = useState<string | null>(null)

    const fetchSkills = useCallback(async (query = '') => {
        setIsLoading(true); setError(null)
        try {
            const params = query.trim() ? `?q=${encodeURIComponent(query.trim())}` : ''
            const data = await skillFetch<SkillMarketListResponse>(`/skills${params}`)
            setSkills(data.items || [])
        } catch (err) { setError(getErrorMessage(err)) }
        finally { setIsLoading(false) }
    }, [])

    const createSkill = useCallback(async (payload: { id: string; name: string; description: string; instructions: string }) =>
        skillMutation('/skills', { method: 'POST', headers: { 'Content-Type': 'application/json' }, body: JSON.stringify(payload) }, fetchSkills),
    [fetchSkills])

    const fetchSkill = useCallback(async (skillId: string) => {
        try {
            const skill = await skillFetch<SkillMarketDetail>(`/skills/${encodeURIComponent(skillId)}`)
            return { success: true, skill }
        } catch (err) { return { success: false, error: getErrorMessage(err) } }
    }, [])

    const updateSkill = useCallback(async (skillId: string, payload: { name: string; description: string; instructions: string }) =>
        skillMutation(`/skills/${encodeURIComponent(skillId)}`, { method: 'PUT', headers: { 'Content-Type': 'application/json' }, body: JSON.stringify(payload) }, fetchSkills),
    [fetchSkills])

    const importSkill = useCallback(async (file: File, id?: string) => {
        const formData = new FormData()
        formData.append('file', file)
        if (id?.trim()) formData.append('id', id.trim())
        return skillMutation('/skills:import', { method: 'POST', body: formData }, fetchSkills)
    }, [fetchSkills])

    const deleteSkill = useCallback(async (skillId: string) =>
        skillMutation(`/skills/${encodeURIComponent(skillId)}`, { method: 'DELETE' }, fetchSkills),
    [fetchSkills])

    const installSkill = useCallback(async (agentId: string, skillId: string) => {
        try {
            const response = await fetch(`${runtime.GATEWAY_URL}/agents/${encodeURIComponent(agentId)}/skills/install`, {
                method: 'POST', headers: gatewayHeaders(userId), body: JSON.stringify({ skillId }),
            })
            if (!response.ok) throw new Error(await response.text())
            return { success: true }
        } catch (err) { return { success: false, error: getErrorMessage(err) } }
    }, [userId])

    return { skills, isLoading, error, fetchSkills, fetchSkill, createSkill, updateSkill, importSkill, deleteSkill, installSkill }
}
