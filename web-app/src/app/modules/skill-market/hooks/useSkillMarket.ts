import { useCallback, useState } from 'react'
import type { SkillMarketDetail, SkillMarketEntry } from '../../../../types/skillMarket'
import { getErrorMessage } from '../../../../utils/errorMessages'
import { useUser } from '../../../platform/providers/UserContext'
import {
    fetchSkillList as apiFetchSkillList,
    fetchSkillDetail,
    createSkill as apiCreateSkill,
    updateSkill as apiUpdateSkill,
    deleteSkill as apiDeleteSkill,
    importSkill as apiImportSkill,
    installSkillToAgent,
} from '../../../../services/skillMarketAPI'

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

export function useSkillMarket(): UseSkillMarketResult {
    const { userId } = useUser()
    const [skills, setSkills] = useState<SkillMarketEntry[]>([])
    const [isLoading, setIsLoading] = useState(false)
    const [error, setError] = useState<string | null>(null)

    const fetchSkills = useCallback(async (query = '') => {
        setIsLoading(true)
        setError(null)
        try {
            setSkills(await apiFetchSkillList(query, userId))
        } catch (err) {
            setError(getErrorMessage(err))
        } finally {
            setIsLoading(false)
        }
    }, [userId])

    const createSkill = useCallback(async (payload: { id: string; name: string; description: string; instructions: string }) => {
        try {
            await apiCreateSkill(payload, userId)
            await fetchSkills()
            return { success: true }
        } catch (err) {
            if (err instanceof Error && err.message === 'SKILL_ALREADY_EXISTS') {
                return { success: false, error: 'SKILL_ALREADY_EXISTS' }
            }
            return { success: false, error: getErrorMessage(err) }
        }
    }, [fetchSkills, userId])

    const fetchSkill = useCallback(async (skillId: string) => {
        try {
            const skill = await fetchSkillDetail(skillId, userId)
            return { success: true, skill }
        } catch (err) {
            return { success: false, error: getErrorMessage(err) }
        }
    }, [userId])

    const updateSkill = useCallback(async (skillId: string, payload: { name: string; description: string; instructions: string }) => {
        try {
            await apiUpdateSkill(skillId, payload, userId)
            await fetchSkills()
            return { success: true }
        } catch (err) {
            return { success: false, error: getErrorMessage(err) }
        }
    }, [fetchSkills, userId])

    const importSkill = useCallback(async (file: File, id?: string) => {
        try {
            await apiImportSkill(file, userId, id)
            await fetchSkills()
            return { success: true }
        } catch (err) {
            return { success: false, error: getErrorMessage(err) }
        }
    }, [fetchSkills, userId])

    const deleteSkill = useCallback(async (skillId: string) => {
        try {
            await apiDeleteSkill(skillId, userId)
            await fetchSkills()
            return { success: true }
        } catch (err) {
            return { success: false, error: getErrorMessage(err) }
        }
    }, [fetchSkills, userId])

    const installSkill = useCallback(async (agentId: string, skillId: string) => {
        return installSkillToAgent(agentId, skillId, userId!)
    }, [userId])

    return {
        skills,
        isLoading,
        error,
        fetchSkills,
        fetchSkill,
        createSkill,
        updateSkill,
        importSkill,
        deleteSkill,
        installSkill,
    }
}
