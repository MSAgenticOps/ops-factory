import { useCallback, useState } from 'react'
import { runtime, gatewayHeaders } from '../../../../config/runtime'
import { getErrorMessage } from '../../../../utils/errorMessages'
import { useUser } from '../../../platform/providers/UserContext'
import type {
    ChannelDetail,
    ChannelLoginState,
    ChannelMutationResponse,
    ChannelSelfTestResult,
    ChannelSummary,
    ChannelUpsertRequest,
    ChannelVerificationResult,
} from '../../../../types/channel'

interface UseChannelsResult {
    channels: ChannelSummary[]
    channel: ChannelDetail | null
    isLoading: boolean
    isSaving: boolean
    error: string | null
    fetchChannels: () => Promise<void>
    fetchChannel: (channelId: string) => Promise<void>
    createChannel: (request: ChannelUpsertRequest) => Promise<ChannelMutationResponse>
    updateChannel: (channelId: string, request: ChannelUpsertRequest) => Promise<ChannelMutationResponse>
    deleteChannel: (channelId: string) => Promise<{ success: boolean; error?: string }>
    setChannelEnabled: (channelId: string, enabled: boolean) => Promise<ChannelMutationResponse>
    verifyChannel: (channelId: string) => Promise<{ success: boolean; verification?: ChannelVerificationResult; error?: string }>
    startLogin: (channelId: string) => Promise<{ success: boolean; state?: ChannelLoginState; error?: string }>
    fetchLoginState: (channelId: string) => Promise<{ success: boolean; state?: ChannelLoginState; error?: string }>
    logoutChannel: (channelId: string) => Promise<{ success: boolean; state?: ChannelLoginState; error?: string }>
    runSelfTest: (channelId: string, text: string) => Promise<{ success: boolean; result?: ChannelSelfTestResult; error?: string }>
}

function defaultMutationError(message: string): ChannelMutationResponse {
    return { success: false, error: message }
}

async function channelFetch(userId: string, path: string, options?: RequestInit) {
    const response = await fetch(`${runtime.GATEWAY_URL}/channels${path}`, {
        ...options, headers: gatewayHeaders(userId), signal: AbortSignal.timeout(10_000),
    })
    return { ok: response.ok, data: await response.json() }
}

function useChannelState() {
    const [channels, setChannels] = useState<ChannelSummary[]>([])
    const [channel, setChannel] = useState<ChannelDetail | null>(null)
    const [isLoading, setIsLoading] = useState(false)
    const [isSaving, setIsSaving] = useState(false)
    const [error, setError] = useState<string | null>(null)
    return { channels, setChannels, channel, setChannel, isLoading, setIsLoading, isSaving, setIsSaving, error, setError }
}

function useChannelHelpers(userId: string | null | undefined, state: ReturnType<typeof useChannelState>) {
    const { setIsSaving, setError, setChannel, setIsLoading } = state

    const mutation = useCallback(async (path: string, options: RequestInit, failMsg: string): Promise<ChannelMutationResponse> => {
        setIsSaving(true); setError(null)
        try {
            const { ok, data } = await channelFetch(userId!, path, options)
            const result = data as ChannelMutationResponse
            if (!ok || !result.success) { const msg = result.error || failMsg; setError(msg); return defaultMutationError(msg) }
            if (result.channel) setChannel(result.channel)
            return result
        } catch (err) { const msg = getErrorMessage(err); setError(msg); return defaultMutationError(msg) }
        finally { setIsSaving(false) }
    }, [userId, setIsSaving, setError, setChannel])

    const action = useCallback(async <T>(path: string, options: RequestInit, failMsg: string, okCheck?: (data: any) => boolean): Promise<T> => {
        setIsSaving(true); setError(null)
        try {
            const { ok, data } = await channelFetch(userId!, path, options)
            if (!ok || (okCheck && !okCheck(data))) { const msg = data.error || failMsg; setError(msg); return { success: false, error: msg } as any }
            return data as T
        } catch (err) { const msg = getErrorMessage(err); setError(msg); return { success: false, error: msg } as any }
        finally { setIsSaving(false) }
    }, [userId, setIsSaving, setError])

    const fetchListOrDetail = useCallback(async (path: string, onOk: (data: any) => void) => {
        setIsLoading(true); setError(null)
        try {
            const { ok, data } = await channelFetch(userId!, path)
            if (!ok) throw new Error('Failed to fetch')
            onOk(data)
        } catch (err) { setError(getErrorMessage(err)) }
        finally { setIsLoading(false) }
    }, [userId, setIsLoading, setError])

    return { mutation, action, fetchListOrDetail }
}

export function useChannels(): UseChannelsResult {
    const { userId } = useUser()
    const state = useChannelState()
    const { mutation, action, fetchListOrDetail } = useChannelHelpers(userId, state)

    const fetchChannels = useCallback(() => fetchListOrDetail('', d => state.setChannels(d.channels ?? [])), [fetchListOrDetail, state])
    const fetchChannel = useCallback((id: string) => fetchListOrDetail(`/${id}`, d => state.setChannel(d as ChannelDetail)), [fetchListOrDetail, state])

    const createChannel = useCallback((request: ChannelUpsertRequest) =>
        mutation('', { method: 'POST', body: JSON.stringify(request) }, 'Failed to create channel'), [mutation])

    const updateChannel = useCallback((channelId: string, request: ChannelUpsertRequest) =>
        mutation(`/${channelId}`, { method: 'PUT', body: JSON.stringify(request) }, 'Failed to update channel'), [mutation])

    const deleteChannel = useCallback((channelId: string) =>
        action(`/${channelId}`, { method: 'DELETE' }, 'Failed to delete channel', d => d.success),
    [action]) as (channelId: string) => Promise<{ success: boolean; error?: string }>

    const setChannelEnabled = useCallback((channelId: string, enabled: boolean) =>
        mutation(`/${channelId}/${enabled ? 'enable' : 'disable'}`, { method: 'POST' }, 'Failed to update channel status'), [mutation])

    const verifyChannel = useCallback((channelId: string) =>
        action<{}>(`/${channelId}/verify`, { method: 'POST' }, 'Failed to verify channel'),
    [action]) as (channelId: string) => Promise<{ success: boolean; verification?: ChannelVerificationResult; error?: string }>

    const startLogin = useCallback((channelId: string) =>
        action<{}>(`/${channelId}/login`, { method: 'POST' }, 'Failed to start login', d => d.success),
    [action]) as (channelId: string) => Promise<{ success: boolean; state?: ChannelLoginState; error?: string }>

    const fetchLoginState = useCallback(async (channelId: string) => {
        state.setIsSaving(true); state.setError(null)
        try {
            const { ok, data } = await channelFetch(userId!, `/${channelId}/login-state`)
            if (!ok || !data.state) { const msg = data.error || 'Failed to fetch login state'; state.setError(msg); return { success: false, error: msg } }
            return { success: true, state: data.state as ChannelLoginState }
        } catch (err) { const msg = getErrorMessage(err); state.setError(msg); return { success: false, error: msg } }
        finally { state.setIsSaving(false) }
    }, [userId, state])

    const logoutChannel = useCallback((channelId: string) =>
        action<{}>(`/${channelId}/logout`, { method: 'POST' }, 'Failed to clear login state', d => d.success),
    [action]) as (channelId: string) => Promise<{ success: boolean; state?: ChannelLoginState; error?: string }>

    const runSelfTest = useCallback((channelId: string, text: string) =>
        action<{}>(`/${channelId}/self-test`, { method: 'POST', body: JSON.stringify({ text }) }, 'Failed to run self-test', d => d.success),
    [action]) as (channelId: string, text: string) => Promise<{ success: boolean; result?: ChannelSelfTestResult; error?: string }>

    return {
        channels: state.channels, channel: state.channel, isLoading: state.isLoading, isSaving: state.isSaving, error: state.error,
        fetchChannels, fetchChannel, createChannel, updateChannel, deleteChannel,
        setChannelEnabled, verifyChannel, startLogin, fetchLoginState, logoutChannel, runSelfTest,
    }
}
