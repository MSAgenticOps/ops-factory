import { useCallback, useState } from 'react'
import { runtime, controlCenterHeaders } from '../../../../config/runtime'
import { getErrorMessage } from '../../../../utils/errorMessages'
import { useUser } from '../../../platform/providers/UserContext'

export type ControlCenterAction = 'probe' | 'restart' | 'start' | 'stop'

export interface ServiceActionResult {
    serviceId: string
    action: string
    success: boolean
    startedAt: number
    finishedAt: number
    exitCode: number
    message: string
}

export type InstanceAction = Exclude<ControlCenterAction, 'probe'>

export interface InstanceActionResult {
    success: boolean
    status?: string
    error?: string
}

export function useControlCenterActions() {
    const { userId } = useUser()
    const [pendingServiceId, setPendingServiceId] = useState<string | null>(null)
    const [pendingAction, setPendingAction] = useState<ControlCenterAction | null>(null)

    const runAction = useCallback(async (serviceId: string, action: ControlCenterAction): Promise<ServiceActionResult | Record<string, unknown>> => {
        setPendingServiceId(serviceId)
        setPendingAction(action)
        try {
            const response = await fetch(`${runtime.CONTROL_CENTER_URL}/services/${serviceId}/actions/${action}`, {
                method: 'POST',
                headers: controlCenterHeaders(userId),
                signal: AbortSignal.timeout(30_000),
            })
            if (!response.ok) {
                const text = await response.text().catch(() => '')
                throw new Error(`HTTP ${response.status}: ${text}`)
            }
            return await response.json()
        } catch (error) {
            throw new Error(getErrorMessage(error))
        } finally {
            setPendingServiceId(null)
            setPendingAction(null)
        }
    }, [userId])

    return {
        runAction,
        pendingServiceId,
        pendingAction,
        isPending: pendingServiceId !== null,
    }
}

export function useInstanceActions() {
    const { userId } = useUser()
    const [pendingInstanceKey, setPendingInstanceKey] = useState<string | null>(null)
    const [pendingAction, setPendingAction] = useState<InstanceAction | null>(null)

    const runInstanceAction = useCallback(async (
        agentId: string,
        instanceUserId: string,
        action: InstanceAction,
    ): Promise<InstanceActionResult> => {
        setPendingInstanceKey(`${agentId}:${instanceUserId}`)
        setPendingAction(action)
        try {
            const url = `${runtime.CONTROL_CENTER_URL}/runtime/instances/`
                + `${encodeURIComponent(agentId)}/${encodeURIComponent(instanceUserId)}/${action}`
            const response = await fetch(url, {
                method: 'POST',
                headers: controlCenterHeaders(userId),
                signal: AbortSignal.timeout(30_000),
            })
            if (!response.ok) {
                const text = await response.text().catch(() => '')
                throw new Error(`HTTP ${response.status}: ${text}`)
            }
            return await response.json() as InstanceActionResult
        } catch (error) {
            throw new Error(getErrorMessage(error))
        } finally {
            setPendingInstanceKey(null)
            setPendingAction(null)
        }
    }, [userId])

    return {
        runInstanceAction,
        pendingInstanceKey,
        pendingAction,
        isPending: pendingInstanceKey !== null,
    }
}
