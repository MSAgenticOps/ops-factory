import { useState, useEffect, useCallback } from 'react'
import { runtime, gatewayHeaders } from '../../../../config/runtime'
import { useUser } from '../../../platform/providers/UserContext'
import type { SolutionType } from '../../../../types/host'

function apiBase() { return `${runtime.GATEWAY_URL}/solution-types` }

export function useSolutionTypes() {
    const { userId } = useUser()
    const [solutionTypes, setSolutionTypes] = useState<SolutionType[]>([])
    const [loading, setLoading] = useState(false)

    const fetchSolutionTypes = useCallback(async () => {
        setLoading(true)
        try {
            const res = await fetch(apiBase(), { headers: gatewayHeaders(userId) })
            if (!res.ok) throw new Error(`HTTP ${res.status}`)
            const data = await res.json()
            setSolutionTypes(data.solutionTypes || [])
        } catch (err) {
            console.error('Failed to fetch solution types', err)
        } finally {
            setLoading(false)
        }
    }, [userId])

    useEffect(() => { fetchSolutionTypes() }, [fetchSolutionTypes])

    const createSolutionType = useCallback(async (body: Partial<SolutionType>) => {
        const res = await fetch(apiBase(), {
            method: 'POST',
            headers: { ...gatewayHeaders(userId), 'Content-Type': 'application/json' },
            body: JSON.stringify(body),
        })
        if (!res.ok) throw new Error(`HTTP ${res.status}`)
        const data = await res.json()
        if (data.success) {
            await fetchSolutionTypes()
            return data.solutionType
        }
        throw new Error(data.error || 'Failed to create solution type')
    }, [userId, fetchSolutionTypes])

    const updateSolutionType = useCallback(async (id: string, body: Partial<SolutionType>) => {
        const res = await fetch(`${apiBase()}/${id}`, {
            method: 'PUT',
            headers: { ...gatewayHeaders(userId), 'Content-Type': 'application/json' },
            body: JSON.stringify(body),
        })
        if (!res.ok) throw new Error(`HTTP ${res.status}`)
        const data = await res.json()
        if (data.success) {
            await fetchSolutionTypes()
            return data.solutionType
        }
        throw new Error(data.error || 'Failed to update solution type')
    }, [userId, fetchSolutionTypes])

    const deleteSolutionType = useCallback(async (id: string) => {
        const res = await fetch(`${apiBase()}/${id}`, {
            method: 'DELETE',
            headers: gatewayHeaders(userId),
        })
        if (!res.ok) throw new Error(`HTTP ${res.status}`)
        const data = await res.json()
        if (data.success) {
            await fetchSolutionTypes()
            return true
        }
        throw new Error(data.error || 'Failed to delete solution type')
    }, [userId, fetchSolutionTypes])

    return { solutionTypes, loading, fetchSolutionTypes, createSolutionType, updateSolutionType, deleteSolutionType }
}
