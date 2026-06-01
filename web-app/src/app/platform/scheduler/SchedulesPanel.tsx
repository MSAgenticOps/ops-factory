import { useEffect, useMemo, useRef, useState } from 'react'
import { useNavigate } from 'react-router-dom'
import { useTranslation } from 'react-i18next'
import type { ScheduledJob, ScheduleSessionInfo } from '@goosed/sdk'
import { buildChatSessionState } from '../chat/chatRouteState'
import { useGoosed } from '../providers/GoosedContext'
import { useToast } from '../providers/ToastContext'
import { useConfirmDialog } from '../providers/ConfirmDialogContext'
import { useInbox } from '../providers/InboxContext'
import { useUser } from '../providers/UserContext'
import CardGrid from '../ui/cards/CardGrid'
import CardWorkbench from '../ui/cards/CardWorkbench'
import PageHeader from '../ui/primitives/PageHeader'
import FilterBar from '../ui/filters/FilterBar'
import FilterInlineGroup from '../ui/filters/FilterInlineGroup'
import FilterSelect from '../ui/filters/FilterSelect'
import ListSearchInput from '../ui/list/ListSearchInput'
import DetailDialog from '../ui/primitives/DetailDialog'
import { slugify } from '../../../config/runtime'
import ResourceCard, {
    ResourceCardActionGroup,
    ResourceCardDeleteAction,
    ResourceCardConfigureAction,
    type ResourceStatusTone,
} from '../ui/primitives/ResourceCard'
import './schedules-panel.css'

interface FormState {
    name: string
    instruction: string
    cron: string
}

interface ScheduleDraftMap {
    [agentId: string]: {
        [scheduleId: string]: {
            name: string
            instruction: string
        }
    }
}

const DEFAULT_CRON = '0 0 9 * * *'
const ALL_AGENTS = '__all__'

interface ScheduledJobRecord extends ScheduledJob {
    agentId: string
    agentName: string
}

/**
 * Reusable scheduled-actions panel.
 * - `agentId` set → single-agent mode (no agent selector / agent tags); used by the agent config page.
 * - `agentId` omitted → all-agents overview (used by the standalone scheduler page).
 * - `embedded` → renders inside a config section (no page shell / PageHeader).
 */
interface SchedulesPanelProps {
    agentId?: string
    embedded?: boolean
}

function getScheduleDraftsKey(userId: string): string {
    return `opsfactory:${userId}:scheduler:drafts:v1`
}

function ensureUniqueId(base: string, existingIds: Set<string>): string {
    if (!existingIds.has(base)) return base
    let counter = 2
    while (existingIds.has(`${base}-${counter}`)) {
        counter += 1
    }
    return `${base}-${counter}`
}

function isCronLikelyValid(cron: string): boolean {
    const parts = cron.trim().split(/\s+/)
    return parts.length === 5 || parts.length === 6
}

function loadDrafts(storageKey: string): ScheduleDraftMap {
    if (typeof window === 'undefined') return {}
    try {
        const raw = window.localStorage.getItem(storageKey)
        if (!raw) return {}
        const parsed = JSON.parse(raw) as ScheduleDraftMap
        return parsed && typeof parsed === 'object' ? parsed : {}
    } catch {
        return {}
    }
}

function saveDrafts(storageKey: string, drafts: ScheduleDraftMap): void {
    if (typeof window === 'undefined') return
    window.localStorage.setItem(storageKey, JSON.stringify(drafts))
}

function getScheduleStatusTone(job: ScheduledJob): ResourceStatusTone {
    if (job.currently_running) return 'warning'
    if (job.paused) return 'neutral'
    return 'success'
}

export default function SchedulesPanel({ agentId: fixedAgentId, embedded = false }: SchedulesPanelProps) {
    const { t } = useTranslation()
    const navigate = useNavigate()
    const { userId } = useUser()
    const { agents, getClient, isConnected, error } = useGoosed()
    const { showToast } = useToast()
    const { requestConfirm } = useConfirmDialog()
    const { markSessionRead } = useInbox()

    const singleAgent = !!fixedAgentId

    const draftsKey = getScheduleDraftsKey(userId || 'anonymous')
    const [selectedAgent, setSelectedAgent] = useState(fixedAgentId || ALL_AGENTS)
    const [jobs, setJobs] = useState<ScheduledJobRecord[]>([])
    const [loading, setLoading] = useState(false)
    const [submitting, setSubmitting] = useState(false)
    const [editingJob, setEditingJob] = useState<ScheduledJobRecord | null>(null)
    const [createAgentId, setCreateAgentId] = useState('')
    const [runs, setRuns] = useState<ScheduleSessionInfo[]>([])
    const [runsLoading, setRunsLoading] = useState(false)
    const [showModal, setShowModal] = useState(false)
    const [searchTerm, setSearchTerm] = useState('')
    const [drafts, setDrafts] = useState<ScheduleDraftMap>(() => loadDrafts(draftsKey))
    const [form, setForm] = useState<FormState>({
        name: '',
        instruction: '',
        cron: DEFAULT_CRON,
    })

    // Keep the active agent in sync if the host passes a different fixed agent.
    useEffect(() => {
        if (fixedAgentId) setSelectedAgent(fixedAgentId)
    }, [fixedAgentId])

    const agentOptions = useMemo(() => (
        // Only the all-agents overview renders the agent selector; skip the work in single-agent mode.
        singleAgent ? [] : [
            {
                value: ALL_AGENTS,
                label: t('scheduler.allAgents'),
            },
            ...agents.map((agent) => ({
                value: agent.id,
                label: agent.name,
            })),
        ]
    ), [agents, t, singleAgent])

    const getDraftForJob = (job: ScheduledJobRecord) => drafts[job.agentId]?.[job.id]

    const getClientForJob = (job: ScheduledJobRecord) => getClient(job.agentId)

    const loadSchedules = async () => {
        if (agents.length === 0) {
            setJobs([])
            return
        }

        setLoading(true)
        try {
            if (!singleAgent && selectedAgent === ALL_AGENTS) {
                const scheduleGroups = await Promise.all(
                    agents.map(async (agent) => {
                        const list = await getClient(agent.id).listSchedules()
                        return list.map((job) => ({
                            ...job,
                            agentId: agent.id,
                            agentName: agent.name,
                        }))
                    }),
                )
                setJobs(scheduleGroups.flat())
                return
            }

            const activeAgentId = fixedAgentId || selectedAgent
            const selectedAgentInfo = agents.find((agent) => agent.id === activeAgentId)
            if (!selectedAgentInfo) {
                setJobs([])
                return
            }

            const list = await getClient(selectedAgentInfo.id).listSchedules()
            setJobs(list.map((job) => ({
                ...job,
                agentId: selectedAgentInfo.id,
                agentName: selectedAgentInfo.name,
            })))
        } catch (err) {
            showToast('error', err instanceof Error ? err.message : t('scheduler.loadFailed'))
        } finally {
            setLoading(false)
        }
    }

    const loadRuns = async (job: ScheduledJobRecord) => {
        setRunsLoading(true)
        try {
            const scheduleRuns = await getClientForJob(job).listScheduleSessions(job.id, 30)
            setRuns(scheduleRuns)
        } catch (err) {
            showToast('error', err instanceof Error ? err.message : t('scheduler.loadRunsFailed'))
        } finally {
            setRunsLoading(false)
        }
    }

    useEffect(() => {
        void loadSchedules()
        // eslint-disable-next-line react-hooks/exhaustive-deps
    }, [selectedAgent, agents])

    const getScheduleStatusLabel = (job: ScheduledJob) => {
        if (job.paused) return t('scheduler.paused')
        if (job.currently_running) return t('scheduler.running')
        return t('scheduler.active')
    }

    const filteredJobs = useMemo(() => {
        if (!searchTerm.trim()) return jobs

        const term = searchTerm.toLowerCase()
        return jobs.filter((job) =>
            job.id.toLowerCase().includes(term) ||
            job.agentName.toLowerCase().includes(term) ||
            job.cron.toLowerCase().includes(term) ||
            getScheduleStatusLabel(job).toLowerCase().includes(term) ||
            (getDraftForJob(job)?.instruction || '').toLowerCase().includes(term),
        )
    }, [jobs, searchTerm, drafts, t])

    // Auto-refresh schedule list every 15s (like official desktop UI)
    const loadSchedulesRef = useRef(loadSchedules)
    loadSchedulesRef.current = loadSchedules
    useEffect(() => {
        const id = setInterval(() => {
            if (!submitting) loadSchedulesRef.current()
        }, 15000)
        return () => clearInterval(id)
    }, [submitting])

    const openCreateModal = () => {
        if (agents.length === 0) {
            showToast('warning', t('common.noAgents'))
            return
        }
        setEditingJob(null)
        setRuns([])
        setCreateAgentId(fixedAgentId || (selectedAgent === ALL_AGENTS ? (agents[0]?.id || '') : selectedAgent))
        setForm({ name: '', instruction: '', cron: DEFAULT_CRON })
        setShowModal(true)
    }

    const openEditModal = async (job: ScheduledJobRecord) => {
        setEditingJob(job)
        setCreateAgentId(job.agentId)
        const draft = getDraftForJob(job)
        setForm({
            name: draft?.name || job.id,
            instruction: draft?.instruction || '',
            cron: job.cron,
        })
        setShowModal(true)
        await loadRuns(job)
    }

    const handleSubmit = async () => {
        const targetAgentId = editingJob?.agentId || createAgentId || fixedAgentId
            || (selectedAgent === ALL_AGENTS ? '' : selectedAgent)
        if (!targetAgentId) return

        const targetClient = getClient(targetAgentId)
        if (!isCronLikelyValid(form.cron)) {
            showToast('warning', t('scheduler.cronInvalid'))
            return
        }

        const cleanedName = slugify(form.name)
        if (!cleanedName) {
            showToast('warning', t('scheduler.nameRequired'))
            return
        }
        if (!form.instruction.trim()) {
            showToast('warning', t('scheduler.instructionRequired'))
            return
        }

        setSubmitting(true)
        try {
            const existingIds = new Set(jobs.filter(job => job.agentId === targetAgentId).map(job => job.id))
            const scheduleId = editingJob
                ? (() => {
                    if (cleanedName === editingJob.id) return cleanedName
                    return ensureUniqueId(cleanedName, existingIds)
                })()
                : ensureUniqueId(cleanedName, existingIds)

            const recipe = {
                title: form.name.trim(),
                description: `Scheduled action: ${form.name.trim()}`,
                instructions: form.instruction.trim(),
            }

            if (editingJob) {
                const original = jobs.find(job => job.agentId === targetAgentId && job.id === editingJob.id)
                const wasPaused = !!original?.paused

                if (scheduleId === editingJob.id) {
                    await targetClient.deleteSchedule(editingJob.id)
                    await targetClient.createSchedule({ id: scheduleId, recipe, cron: form.cron.trim() })
                } else {
                    await targetClient.createSchedule({ id: scheduleId, recipe, cron: form.cron.trim() })
                    await targetClient.deleteSchedule(editingJob.id)
                }

                if (wasPaused) {
                    await targetClient.pauseSchedule(scheduleId)
                }
                showToast('success', t('scheduler.updated'))
            } else {
                await targetClient.createSchedule({ id: scheduleId, recipe, cron: form.cron.trim() })
                showToast('success', t('scheduler.created'))
            }

            const nextDrafts: ScheduleDraftMap = {
                ...drafts,
                [targetAgentId]: {
                    ...(drafts[targetAgentId] || {}),
                    [scheduleId]: {
                        name: form.name.trim(),
                        instruction: form.instruction.trim(),
                    },
                },
            }
            setDrafts(nextDrafts)
            saveDrafts(draftsKey, nextDrafts)

            if (editingJob) {
                await loadRuns({ ...editingJob, id: scheduleId })
            }
            setShowModal(false)
            await loadSchedules()
        } catch (err) {
            showToast('error', err instanceof Error ? err.message : t('scheduler.operationFailed'))
        } finally {
            setSubmitting(false)
        }
    }

    const handlePause = async (job: ScheduledJobRecord) => {
        try {
            await getClientForJob(job).pauseSchedule(job.id)
            showToast('success', t('scheduler.pausedToast', { id: job.id }))
            await loadSchedules()
            if (editingJob?.id === job.id && editingJob.agentId === job.agentId) {
                setEditingJob({ ...job, paused: true, currently_running: false })
            }
        } catch (err) {
            showToast('error', err instanceof Error ? err.message : t('scheduler.operationFailed'))
        }
    }

    const handleUnpause = async (job: ScheduledJobRecord) => {
        try {
            await getClientForJob(job).unpauseSchedule(job.id)
            showToast('success', t('scheduler.resumedToast', { id: job.id }))
            await loadSchedules()
            if (editingJob?.id === job.id && editingJob.agentId === job.agentId) {
                setEditingJob({ ...job, paused: false, currently_running: false })
            }
        } catch (err) {
            showToast('error', err instanceof Error ? err.message : t('scheduler.operationFailed'))
        }
    }

    const handleKill = async (job: ScheduledJobRecord) => {
        try {
            await getClientForJob(job).killSchedule(job.id)
            showToast('success', t('scheduler.killedToast', { id: job.id }))
            await loadSchedules()
            if (editingJob?.id === job.id && editingJob.agentId === job.agentId) {
                setEditingJob({ ...job, currently_running: false })
            }
        } catch (err) {
            showToast('error', err instanceof Error ? err.message : t('scheduler.operationFailed'))
        }
    }

    const handleRunNow = async (job: ScheduledJobRecord) => {
        try {
            const sessionId = await getClientForJob(job).runScheduleNow(job.id)
            showToast('success', sessionId === 'CANCELLED'
                ? t('scheduler.runCancelledToast', { id: job.id })
                : t('scheduler.triggeredToast', { id: job.id }))
            await loadSchedules()
            await loadRuns(job)
        } catch (err) {
            showToast('error', err instanceof Error ? err.message : t('scheduler.operationFailed'))
        }
    }

    const handleDelete = async (job: ScheduledJobRecord) => {
        const confirmed = await requestConfirm({
            title: t('common.confirmTitle'),
            message: t('scheduler.deleteConfirm', { id: job.id }),
            variant: 'danger',
            confirmLabel: t('common.delete'),
        })
        if (!confirmed) return
        try {
            await getClientForJob(job).deleteSchedule(job.id)
            showToast('success', t('scheduler.deletedToast', { id: job.id }))
            if (editingJob?.id === job.id && editingJob.agentId === job.agentId) {
                setShowModal(false)
                setEditingJob(null)
                setRuns([])
            }
            await loadSchedules()
        } catch (err) {
            showToast('error', err instanceof Error ? err.message : t('scheduler.deleteFailed'))
        }
    }

    const handleOpenRunSession = (job: ScheduledJobRecord, sessionId: string) => {
        markSessionRead(job.agentId, sessionId)
        navigate('/chat', {
            state: buildChatSessionState(sessionId, job.agentId),
        })
    }

    const createButton = (
        <button type="button" className="btn btn-primary" onClick={openCreateModal}>
            {t('scheduler.createAction')}
        </button>
    )

    return (
        <div className={embedded ? 'schedules-panel-embedded' : 'page-container sidebar-top-page page-shell-wide scheduled-page'}>
            {embedded ? (
                <div className="schedules-panel-header">
                    <div>
                        <h2 className="schedules-panel-title">{t('scheduler.title')}</h2>
                        <p className="schedules-panel-subtitle">{t('scheduler.subtitleAgent')}</p>
                    </div>
                    {createButton}
                </div>
            ) : (
                <PageHeader
                    title={t('scheduler.title')}
                    subtitle={t('scheduler.subtitle')}
                    action={createButton}
                />
            )}
            <div className="scheduled-toolbar">
                <FilterBar
                    primary={(
                        <FilterInlineGroup>
                            <ListSearchInput
                                value={searchTerm}
                                placeholder={t('scheduler.searchPlaceholder')}
                                onChange={setSearchTerm}
                            />
                            {!singleAgent && (
                                <FilterSelect
                                    value={selectedAgent}
                                    options={agentOptions}
                                    onChange={setSelectedAgent}
                                    disabled={agents.length === 0}
                                />
                            )}
                        </FilterInlineGroup>
                    )}
                />
            </div>

            {error && <div className="conn-banner conn-banner-error">{t('common.connectionError', { error })}</div>}
            {!isConnected && !error && <div className="conn-banner conn-banner-warning">{t('common.connectingGateway')}</div>}

            {loading && (
                <div className="empty-state">
                    <h3 className="empty-state-title">{t('scheduler.loadingSchedules')}</h3>
                </div>
            )}
            {!loading && jobs.length === 0 && (
                <div className="empty-state">
                    <h3 className="empty-state-title">{t('scheduler.noSchedules')}</h3>
                    <p className="empty-state-description">{t('scheduler.noSchedulesHint')}</p>
                </div>
            )}
            {!loading && jobs.length > 0 && searchTerm && filteredJobs.length === 0 && (
                <div className="empty-state">
                    <h3 className="empty-state-title">{t('common.noResults')}</h3>
                    <p className="empty-state-description">{t('scheduler.noMatchSchedules', { term: searchTerm })}</p>
                </div>
            )}
            {!loading && filteredJobs.length > 0 && (
                <CardWorkbench>
                    <CardGrid className="scheduled-grid">
                        {filteredJobs.map(job => (
                            <ResourceCard
                                key={`${job.agentId}:${job.id}`}
                                className="scheduled-card"
                                title={job.id}
                                statusLabel={getScheduleStatusLabel(job)}
                                statusTone={getScheduleStatusTone(job)}
                                tags={!singleAgent ? (
                                    <div className="resource-card-tags">
                                        <span className="resource-card-tag" title={job.agentName}>
                                            {job.agentName}
                                        </span>
                                    </div>
                                ) : undefined}
                                summary={(
                                    <div className="resource-card-summary-stack">
                                        <p className="resource-card-summary-text resource-card-summary-code" title={job.cron}>
                                            {job.cron}
                                        </p>
                                        <p className={['resource-card-summary-text', !getDraftForJob(job)?.instruction ? 'resource-card-summary-placeholder' : ''].filter(Boolean).join(' ')}>
                                            {getDraftForJob(job)?.instruction || t('scheduler.summaryUnavailable')}
                                        </p>
                                    </div>
                                )}
                                metrics={[
                                    { label: t('scheduler.cron'), value: job.cron, valueClassName: 'scheduled-card-code' },
                                    { label: t('scheduler.lastRun'), value: job.last_run ? new Date(job.last_run).toLocaleString() : t('scheduler.never') },
                                ]}
                                footer={(
                                    <ResourceCardActionGroup>
                                        <ResourceCardConfigureAction
                                            onClick={() => void openEditModal(job)}
                                            label={t('scheduler.configure')}
                                        />
                                        <ResourceCardDeleteAction
                                            onClick={() => handleDelete(job)}
                                            label={t('common.delete')}
                                        />
                                    </ResourceCardActionGroup>
                                )}
                            />
                        ))}
                    </CardGrid>
                </CardWorkbench>
            )}

            {showModal && (
                <DetailDialog
                    title={editingJob ? t('scheduler.editAction') : t('scheduler.createAction')}
                    onClose={() => setShowModal(false)}
                    variant="wide"
                    className="scheduled-modal-wide"
                    bodyClassName="scheduled-modal-body"
                    footer={(
                        <div className="scheduled-modal-footer">
                            <div className="scheduled-modal-footer-group scheduled-modal-footer-group-right">
                                {editingJob && (() => {
                                    if (editingJob.currently_running) {
                                        return (
                                            <button type="button" className="btn btn-secondary" onClick={() => void handleKill(editingJob)}>
                                                {t('scheduler.kill')}
                                            </button>
                                        )
                                    }
                                    return (
                                        <>
                                            {editingJob.paused ? (
                                                <button type="button" className="btn btn-secondary" onClick={() => void handleUnpause(editingJob)}>
                                                    {t('scheduler.resume')}
                                                </button>
                                            ) : (
                                                <button type="button" className="btn btn-secondary" onClick={() => void handlePause(editingJob)}>
                                                    {t('scheduler.pause')}
                                                </button>
                                            )}
                                            <button type="button" className="btn btn-secondary" onClick={() => void handleRunNow(editingJob)}>
                                                {t('scheduler.runNow')}
                                            </button>
                                        </>
                                    )
                                })()}
                                <button type="button" className="btn btn-secondary" onClick={() => setShowModal(false)} disabled={submitting}>
                                    {t('common.cancel')}
                                </button>
                                <button type="button" className="btn btn-primary" onClick={handleSubmit} disabled={submitting || (!editingJob && !createAgentId)}>
                                    {(() => {
                                        if (submitting) return t('scheduler.saving')
                                        return editingJob ? t('common.save') : t('scheduler.create')
                                    })()}
                                </button>
                            </div>
                        </div>
                    )}
                >
                    <section className="scheduled-modal-block scheduled-modal-form">
                        <div className="scheduled-modal-block-header">
                            <div className="scheduled-section-heading">
                                <h4 className="scheduled-modal-section-title">{t('scheduler.basicInfoTitle')}</h4>
                                {editingJob && (
                                    <p className="scheduled-modal-section-description">
                                        {t('scheduler.currentScheduleId', { id: editingJob.id })}
                                    </p>
                                )}
                            </div>
                            {editingJob && (
                                <div className="resource-card-tags">
                                    {!singleAgent && (
                                        <span className="resource-card-tag" title={editingJob.agentName}>
                                            {editingJob.agentName}
                                        </span>
                                    )}
                                    <span className={`resource-status resource-status-${getScheduleStatusTone(editingJob)}`}>
                                        {getScheduleStatusLabel(editingJob)}
                                    </span>
                                </div>
                            )}
                        </div>

                        {!editingJob && !singleAgent && (
                            <label className="scheduled-field-label">
                                {t('scheduler.agent')}
                                <select
                                    className="scheduled-input"
                                    value={createAgentId}
                                    onChange={(e) => setCreateAgentId(e.target.value)}
                                >
                                    {agents.map((agent) => (
                                        <option key={agent.id} value={agent.id}>
                                            {agent.name}
                                        </option>
                                    ))}
                                </select>
                            </label>
                        )}
                        <div className="scheduled-form-grid">
                            <label className="scheduled-field-label">
                                {t('scheduler.name')}
                                <input
                                    className="scheduled-input"
                                    value={form.name}
                                    onChange={(e) => setForm(prev => ({ ...prev, name: e.target.value }))}
                                    placeholder="daily-summary-job"
                                />
                            </label>
                            <label className="scheduled-field-label">
                                {t('scheduler.cron')}
                                <input
                                    className="scheduled-input"
                                    value={form.cron}
                                    onChange={(e) => setForm(prev => ({ ...prev, cron: e.target.value }))}
                                    placeholder="0 0 9 * * *"
                                />
                            </label>
                        </div>
                        <label className="scheduled-field-label">
                            {t('scheduler.instruction')}
                            <textarea
                                className="scheduled-textarea"
                                value={form.instruction}
                                onChange={(e) => setForm(prev => ({ ...prev, instruction: e.target.value }))}
                                placeholder={editingJob ? t('scheduler.instructionPlaceholderEdit') : t('scheduler.instructionPlaceholderNew')}
                                rows={5}
                            />
                        </label>
                        <p className="scheduled-hint">{t('scheduler.cronHint')}</p>
                        {editingJob && (
                            <div className="scheduled-form-meta">
                                <span className="scheduled-form-meta-label">{t('scheduler.lastRun')}</span>
                                <span className="scheduled-form-meta-value">
                                    {editingJob.last_run ? new Date(editingJob.last_run).toLocaleString() : t('scheduler.never')}
                                </span>
                            </div>
                        )}
                    </section>

                    {editingJob && (
                        <section className="scheduled-modal-block scheduled-modal-block-separated">
                            <div className="scheduled-modal-block-header">
                                <div className="scheduled-section-heading">
                                    <h4 className="scheduled-modal-section-title">{t('scheduler.recentRuns')}</h4>
                                    <p className="scheduled-modal-section-description">{t('scheduler.runsSubtitle')}</p>
                                </div>
                            </div>
                            {runsLoading && (
                                <div className="empty-state">
                                    <h3 className="empty-state-title">{t('scheduler.loadingRuns')}</h3>
                                </div>
                            )}
                            {!runsLoading && runs.length === 0 && (
                                <div className="empty-state">
                                    <h3 className="empty-state-title">{t('scheduler.noRuns')}</h3>
                                    <p className="empty-state-description">{t('scheduler.noRunsHint')}</p>
                                </div>
                            )}
                            {!runsLoading && runs.length > 0 && (
                                <div className="scheduled-runs-list">
                                    {runs.map(run => (
                                        <div key={run.id} className="scheduled-run-item">
                                            <div className="scheduled-run-main">
                                                <div className="scheduled-run-topline">
                                                    <div className="scheduled-run-name">{run.name || run.id}</div>
                                                    {run.name && run.name !== run.id && (
                                                        <span className="resource-card-tag scheduled-run-id" title={run.id}>
                                                            {run.id}
                                                        </span>
                                                    )}
                                                </div>
                                                <div className="scheduled-run-meta">
                                                    <span>{new Date(run.createdAt).toLocaleString()}</span>
                                                    <span>{run.messageCount} {t('common.messages')}</span>
                                                    {run.totalTokens !== undefined && run.totalTokens !== null && (
                                                        <span>{run.totalTokens.toLocaleString()} {t('common.tokens')}</span>
                                                    )}
                                                </div>
                                            </div>
                                            <div className="scheduled-run-actions">
                                                <button
                                                    type="button"
                                                    className="btn btn-secondary"
                                                    onClick={() => handleOpenRunSession(editingJob, run.id)}
                                                >
                                                    {t('scheduler.openSession')}
                                                </button>
                                            </div>
                                        </div>
                                    ))}
                                </div>
                            )}
                        </section>
                    )}
                </DetailDialog>
            )}
        </div>
    )
}
