import { useState, useEffect, useMemo, useCallback } from 'react'
import { useTranslation } from 'react-i18next'
import { useSops } from '../hooks/useSops'
import { useToast } from '../../../platform/providers/ToastContext'
import { useConfirmDialog } from '../../../platform/providers/ConfirmDialogContext'
import { useUser } from '../../../platform/providers/UserContext'
import { runtime, gatewayHeaders } from '../../../../config/runtime'
import DetailDialog from '../../../platform/ui/primitives/DetailDialog'
import ListSearchInput from '../../../platform/ui/list/ListSearchInput'
import ListResultsMeta from '../../../platform/ui/list/ListResultsMeta'
import type { Sop, SopCreateRequest } from '../../../../types/sop'
import type { SolutionType } from '../../../../types/host'

// ---------------------------------------------------------------------------
// Helpers
// ---------------------------------------------------------------------------

function TrashIcon() {
    return (
        <svg viewBox="0 0 20 20" fill="none" width="18" height="18" aria-hidden="true">
            <path
                d="M6.5 5.5h7m-6 0V4.75A1.75 1.75 0 0 1 9.25 3h1.5A1.75 1.75 0 0 1 12.5 4.75v.75m-8 0h11m-1 0-.6 8.39a1.75 1.75 0 0 1-1.75 1.61H7.85A1.75 1.75 0 0 1 6.1 13.89L5.5 5.5m2.75 2.5v4m4-4v4"
                stroke="currentColor"
                strokeWidth="1.8"
                strokeLinecap="round"
                strokeLinejoin="round"
            />
        </svg>
    )
}

function EditIcon() {
    return (
        <svg viewBox="0 0 20 20" fill="none" width="16" height="16" aria-hidden="true">
            <path
                d="M4.75 13.95 4 16l2.05-.75 8.5-8.5-1.3-1.3-8.5 8.5Z"
                stroke="currentColor"
                strokeWidth="1.7"
                strokeLinecap="round"
                strokeLinejoin="round"
            />
            <path
                d="m11.95 6.05 1.3 1.3m.65-.65 1.05-1.05a1.15 1.15 0 0 0 0-1.6l-.5-.5a1.15 1.15 0 0 0-1.6 0L11.8 4.6"
                stroke="currentColor"
                strokeWidth="1.7"
                strokeLinecap="round"
                strokeLinejoin="round"
            />
            <path
                d="M4 16h12"
                stroke="currentColor"
                strokeWidth="1.7"
                strokeLinecap="round"
            />
        </svg>
    )
}

// ---------------------------------------------------------------------------
// SOP Form Modal (natural language only)
// ---------------------------------------------------------------------------

function SopFormModal({
    sop,
    solutionTypes,
    onClose,
    onSave,
}: {
    sop: Sop | null
    solutionTypes: SolutionType[]
    onClose: () => void
    onSave: (data: SopCreateRequest) => Promise<void>
}) {
    const { t } = useTranslation()
    const [name, setName] = useState(sop?.name ?? '')
    const [description, setDescription] = useState(sop?.description ?? '')
    const [version, setVersion] = useState(sop?.version ?? '1.0')
    const [triggerCondition, setTriggerCondition] = useState(sop?.triggerCondition ?? '')
    const [saving, setSaving] = useState(false)
    const [error, setError] = useState<string | null>(null)
    const [enabled, setEnabled] = useState(sop?.enabled ?? true)
    const [stepsDescription, setStepsDescription] = useState(sop?.stepsDescription ?? '')
    const [targetSolution, setTargetSolution] = useState(sop?.targetSolution ?? 'universal')

    const handleSave = useCallback(async () => {
        setError(null)
        if (!name.trim()) {
            setError(t('remoteDiagnosis.hosts.nameRequired'))
            return
        }

        setSaving(true)
        try {
            const payload: SopCreateRequest = {
                name: name.trim(),
                description: description.trim(),
                version: version.trim(),
                triggerCondition: triggerCondition.trim(),
                enabled,
                stepsDescription: stepsDescription.trim(),
                targetSolution,
            }
            await onSave(payload)
        } catch (err) {
            setError(err instanceof Error ? err.message : 'Unknown error')
        } finally {
            setSaving(false)
        }
    }, [name, description, version, triggerCondition, enabled, stepsDescription, targetSolution, onSave, t])

    return (
        <DetailDialog
            title={sop ? t('remoteDiagnosis.sops.editSop') : t('remoteDiagnosis.sops.addSop')}
            onClose={onClose}
            variant="wide"
            className="sop-workflow-modal-wide"
            bodyClassName="sop-workflow-modal-body"
            footer={(
                <>
                    <button className="btn btn-secondary" onClick={onClose} disabled={saving}>
                        {t('common.cancel')}
                    </button>
                    <button className="btn btn-primary" onClick={handleSave} disabled={saving || !name.trim()}>
                        {saving ? t('common.saving') : t('common.save')}
                    </button>
                </>
            )}
        >
            {error && (
                <div className="agents-alert agents-alert-error">
                    {error}
                </div>
            )}

            <section className="knowledge-section-card sop-workflow-form-section">
                <div className="knowledge-section-header knowledge-section-header-compact">
                    <div>
                        <h3 className="knowledge-section-title">
                            {t('remoteDiagnosis.sops.editSop')}
                        </h3>
                        <p className="knowledge-section-description">
                            {t('remoteDiagnosis.sops.subtitle')}
                        </p>
                    </div>
                </div>

                <div className="sop-workflow-modal-grid">
                    <div className="form-group">
                        <label className="form-label">{t('remoteDiagnosis.sops.name')} <span className="form-required">*</span></label>
                        <input
                            className="form-input"
                            type="text"
                            value={name}
                            onChange={e => setName(e.target.value)}
                            autoFocus
                        />
                    </div>
                    <div className="form-group">
                        <label className="form-label">{t('remoteDiagnosis.sops.version')}</label>
                        <input
                            className="form-input"
                            type="text"
                            value={version}
                            onChange={e => setVersion(e.target.value)}
                        />
                    </div>
                </div>

                <div className="sop-workflow-modal-grid">
                    <div className="form-group">
                        <label className="form-label">{t('hostResource.targetSolution')}</label>
                        <select
                            className="form-input"
                            value={targetSolution}
                            onChange={e => setTargetSolution(e.target.value)}
                        >
                            <option value="universal">{t('hostResource.universal')}</option>
                            {solutionTypes.map(st => (
                                <option key={st.id} value={st.id}>{st.name}</option>
                            ))}
                        </select>
                    </div>
                    <div className="form-group">
                        <label className="form-label">{t('remoteDiagnosis.sops.sopEnabled')}</label>
                        <div style={{ display: 'flex', alignItems: 'center', gap: 8, paddingTop: 6 }}>
                            <span
                                className={`sop-workflow-switch${enabled ? ' is-on' : ''}`}
                                role="switch"
                                aria-checked={enabled}
                                tabIndex={0}
                                onClick={() => setEnabled(prev => !prev)}
                                onKeyDown={e => { if (e.key === 'Enter' || e.key === ' ') { e.preventDefault(); setEnabled(prev => !prev) } }}
                            >
                                <span className="sop-workflow-switch-thumb" />
                            </span>
                            <span style={{ fontSize: 'var(--font-size-sm)', color: 'var(--color-text-secondary)' }}>
                                {enabled ? t('remoteDiagnosis.sops.sopEnabled') : t('remoteDiagnosis.sops.sopDisabled')}
                            </span>
                        </div>
                    </div>
                </div>

                <div className="form-group">
                    <label className="form-label">{t('remoteDiagnosis.sops.description')}</label>
                    <textarea
                        className="form-input"
                        rows={2}
                        value={description}
                        onChange={e => setDescription(e.target.value)}
                    />
                </div>

                <div className="form-group">
                    <label className="form-label">
                        {t('remoteDiagnosis.sops.triggerCondition')}
                    </label>
                    <input
                        className="form-input"
                        type="text"
                        value={triggerCondition}
                        onChange={e => setTriggerCondition(e.target.value)}
                        maxLength={500}
                    />
                </div>
            </section>

            <section className="knowledge-section-card sop-workflow-node-editor">
                <div className="sop-workflow-node-editor-head">
                    <div className="sop-workflow-node-editor-copy">
                        <h3 className="sop-workflow-node-editor-title">
                            {t('remoteDiagnosis.sops.stepsDescriptionTitle')}
                        </h3>
                        <p className="sop-workflow-node-editor-description">
                            {t('remoteDiagnosis.sops.stepsDescriptionHint')}
                        </p>
                    </div>
                </div>
                <div className="form-group">
                    <label className="form-label">{t('remoteDiagnosis.sops.stepsDescription')}</label>
                    <textarea
                        className="form-input sop-workflow-steps-textarea"
                        placeholder={t('remoteDiagnosis.sops.stepsDescriptionPlaceholder')}
                        value={stepsDescription}
                        onChange={e => setStepsDescription(e.target.value)}
                        maxLength={2000}
                    />
                </div>
            </section>
        </DetailDialog>
    )
}

// ---------------------------------------------------------------------------
// Expandable SOP Row
// ---------------------------------------------------------------------------

function SopExpandableRow({ sop, solutionTypes, onEdit, onDelete, onToggleEnabled }: {
    sop: Sop
    solutionTypes: SolutionType[]
    onEdit: (sop: Sop) => void
    onDelete: (sop: Sop) => void
    onToggleEnabled: (sop: Sop, enabled: boolean) => void
}) {
    const { t } = useTranslation()
    const [expanded, setExpanded] = useState(false)

    const solutionName = useMemo(() => {
        const sol = sop.targetSolution ?? 'universal'
        if (sol === 'universal') return t('hostResource.universal')
        const match = solutionTypes.find(st => st.id === sol)
        return match ? match.name : sol
    }, [sop.targetSolution, solutionTypes, t])

    return (
        <>
            <tr className="sop-workflow-table-row">
                <td>
                    <button
                        type="button"
                        className="sop-workflow-expand-button"
                        onClick={() => setExpanded(prev => !prev)}
                    >
                        <svg
                            viewBox="0 0 20 20"
                            fill="currentColor"
                            className={`sop-workflow-expand-icon${expanded ? ' expanded' : ''}`}
                        >
                            <path fillRule="evenodd" d="M7.21 14.77a.75.75 0 01.02-1.06L11.168 10 7.23 6.29a.75.75 0 111.04-1.08l4.5 4.25a.75.75 0 010 1.08l-4.5 4.25a.75.75 0 01-1.06-.02z" clipRule="evenodd" />
                        </svg>
                        <span className="sop-workflow-text-truncate" style={{ fontWeight: 700 }} title={sop.name.trim()}>
                            {sop.name.trim()}
                        </span>
                    </button>
                </td>
                <td className="sop-workflow-muted-text sop-workflow-text-truncate" title={sop.description || ''}>
                    {sop.description?.trim() || '—'}
                </td>
                <td className="sop-workflow-text-truncate" title={sop.triggerCondition || ''}>
                    {sop.triggerCondition?.trim() || '—'}
                </td>
                <td style={{ textAlign: 'center' }}>
                    <span className="sop-workflow-meta-tag">{solutionName}</span>
                </td>
                <td style={{ textAlign: 'center' }}>
                    <div className="sop-workflow-enabled-cell">
                        <button
                            type="button"
                            className={`sop-workflow-switch${sop.enabled !== false ? ' is-on' : ''}`}
                            role="switch"
                            aria-checked={sop.enabled !== false}
                            onClick={() => onToggleEnabled(sop, sop.enabled === false)}
                        >
                            <span className="sop-workflow-switch-thumb" />
                        </button>
                    </div>
                </td>
                <td>
                    <div className="sop-workflow-table-actions">
                        <button
                            type="button"
                            className="hr-host-card-action"
                            onClick={() => onEdit(sop)}
                            aria-label={t('common.edit')}
                            title={t('common.edit')}
                        >
                            <EditIcon />
                        </button>
                        <button
                            type="button"
                            className="knowledge-doc-action-btn knowledge-doc-action-icon danger"
                            onClick={() => onDelete(sop)}
                            aria-label={t('common.delete')}
                        >
                            <TrashIcon />
                        </button>
                    </div>
                </td>
            </tr>
            {expanded && (
                <tr className="sop-workflow-detail-row">
                    <td colSpan={6}>
                        <div className="sop-workflow-detail-panel">
                            <div>
                                <span className="sop-workflow-node-label">
                                    {t('hostResource.targetSolution')}:
                                </span>
                                <span className="sop-workflow-meta-tag" style={{ marginLeft: 8 }}>{solutionName}</span>
                            </div>
                            <div style={{ marginTop: 'var(--spacing-3)' }}>
                                <span className="sop-workflow-node-label">
                                    {t('remoteDiagnosis.sops.stepsDescription')}:
                                </span>
                                <pre style={{ whiteSpace: 'pre-wrap', margin: 'var(--spacing-2) 0 0', fontSize: 'var(--font-size-sm)', lineHeight: 1.6, color: 'var(--color-text-primary)' }}>
                                    {sop.stepsDescription || '—'}
                                </pre>
                            </div>
                        </div>
                    </td>
                </tr>
            )}
        </>
    )
}

// ---------------------------------------------------------------------------
// Sops Tab
// ---------------------------------------------------------------------------

export function SopsTab({ solutionTypes }: { solutionTypes: SolutionType[] }) {
    const { t } = useTranslation()
    const { sops, isLoading, error, fetchSops, createSop, updateSop, deleteSop } = useSops()
    const { showToast } = useToast()
    const { requestConfirm } = useConfirmDialog()
    const { userId } = useUser()

    const PAGE_SIZE = 10
    const [currentPage, setCurrentPage] = useState(1)
    const [editingSop, setEditingSop] = useState<Sop | null>(null)
    const [showAddModal, setShowAddModal] = useState(false)
    const [searchTerm, setSearchTerm] = useState('')
    const [solutionFilter, setSolutionFilter] = useState<string>('all')

    useEffect(() => {
        fetchSops()
    }, [fetchSops])

    const handleSaveSop = useCallback(
        async (data: SopCreateRequest) => {
            if (editingSop) {
                await updateSop(editingSop.id, data)
                showToast('success', t('remoteDiagnosis.sops.editSuccess', { name: data.name }))
            } else {
                await createSop(data)
                showToast('success', t('remoteDiagnosis.sops.addSuccess', { name: data.name }))
            }
            setShowAddModal(false)
            setEditingSop(null)
            await fetchSops()
        },
        [editingSop, updateSop, createSop, fetchSops, showToast, t],
    )

    const handleDelete = useCallback(
        async (sop: Sop) => {
            const confirmed = await requestConfirm({
                title: t('common.confirmTitle'),
                message: t('remoteDiagnosis.sops.deleteConfirm', { name: sop.name }),
                variant: 'danger',
                confirmLabel: t('common.delete'),
            })
            if (!confirmed) return
            deleteSop(sop.id)
                .then(() => {
                    showToast('success', t('remoteDiagnosis.sops.deleteSuccess', { name: sop.name }))
                    fetchSops()
                })
                .catch((err: unknown) => {
                    showToast('error', err instanceof Error ? err.message : 'Delete failed')
                })
        },
        [deleteSop, fetchSops, showToast, t],
    )

    const handleToggleEnabled = useCallback(
        async (sop: Sop, enabled: boolean) => {
            try {
                const res = await fetch(`${runtime.GATEWAY_URL}/sops/${sop.id}`, {
                    method: 'PUT',
                    headers: gatewayHeaders(userId),
                    body: JSON.stringify({ enabled }),
                    signal: AbortSignal.timeout(10000),
                })
                if (!res.ok) {
                    const text = await res.text()
                    let msg = text
                    try { msg = JSON.parse(text).error || text } catch { /* use raw */ }
                    throw new Error(msg)
                }
                showToast('success', t('remoteDiagnosis.sops.toggleSuccess', {
                    name: sop.name.trim(),
                    status: enabled ? t('remoteDiagnosis.sops.sopEnabled') : t('remoteDiagnosis.sops.sopDisabled'),
                }))
                await fetchSops()
            } catch (err) {
                showToast('error', err instanceof Error ? err.message : 'Update failed')
            }
        },
        [userId, fetchSops, showToast, t],
    )

    const filteredSops = useMemo(() => {
        let result = sops
        if (solutionFilter !== 'all') {
            result = result.filter(s => {
                const sol = s.targetSolution ?? 'universal'
                return sol === solutionFilter || sol === 'universal'
            })
        }
        if (!searchTerm.trim()) return result
        const term = searchTerm.toLowerCase()
        return result.filter(s => s.name.toLowerCase().includes(term))
    }, [sops, searchTerm, solutionFilter])

    return (
        <div className="hr-type-tab-content">
            <section className="knowledge-section-card sop-workflow-section-card">
                <div className="knowledge-section-header sop-workflow-section-header">
                    <div>
                        <h2 className="knowledge-section-title">{t('remoteDiagnosis.sops.title')}</h2>
                        <p className="knowledge-section-description">
                            {t('remoteDiagnosis.sops.subtitle')}
                        </p>
                    </div>
                    <div className="knowledge-doc-toolbar-actions sop-workflow-toolbar-actions">
                        <button className="btn btn-primary" onClick={() => setShowAddModal(true)}>
                            {t('remoteDiagnosis.sops.addSop')}
                        </button>
                    </div>
                </div>

                {error && <div className="conn-banner conn-banner-error">{error}</div>}

                {solutionTypes.length > 0 && (
                    <div className="hr-solution-filter-bar">
                        <button
                            className={`hr-solution-filter-pill ${solutionFilter === 'all' ? 'active' : ''}`}
                            onClick={() => setSolutionFilter('all')}
                        >
                            {t('hostResource.filterAll')}
                        </button>
                        <button
                            className={`hr-solution-filter-pill ${solutionFilter === 'universal' ? 'active' : ''}`}
                            onClick={() => setSolutionFilter('universal')}
                        >
                            {t('hostResource.filterUniversal')}
                        </button>
                        {solutionTypes.map(st => (
                            <button
                                key={st.id}
                                className={`hr-solution-filter-pill ${solutionFilter === st.id ? 'active' : ''}`}
                                onClick={() => setSolutionFilter(st.id)}
                            >
                                {st.name}
                            </button>
                        ))}
                    </div>
                )}

                {(() => {
                    if (isLoading) {
                        return (
                            <div className="sop-workflow-empty-shell">
                                <div className="empty-state">
                                    <h3 className="empty-state-title">{t('common.loading')}</h3>
                                </div>
                            </div>
                        )
                    }
                    if (sops.length === 0) {
                        return (
                            <div className="sop-workflow-empty-shell">
                                <div className="empty-state">
                                    <svg
                                        className="empty-state-icon"
                                        viewBox="0 0 24 24"
                                        fill="none"
                                        stroke="currentColor"
                                        strokeWidth="1.5"
                                    >
                                        <path d="M9 5H7a2 2 0 00-2 2v12a2 2 0 002 2h10a2 2 0 002-2V7a2 2 0 00-2-2h-2M9 5a2 2 0 002 2h2a2 2 0 002-2M9 5a2 2 0 012-2h2a2 2 0 012 2" />
                                    </svg>
                                    <h3 className="empty-state-title">{t('remoteDiagnosis.sops.noSops')}</h3>
                                    <p className="empty-state-description">
                                        {t('remoteDiagnosis.sops.noSopsHint')}
                                    </p>
                                </div>
                            </div>
                        )
                    }
                    const totalPages = Math.max(1, Math.ceil(filteredSops.length / PAGE_SIZE))
                    const safePage = Math.min(currentPage, totalPages)
                    const paginatedSops = filteredSops.slice((safePage - 1) * PAGE_SIZE, safePage * PAGE_SIZE)
                    return <>
                    <div style={{ display: 'flex', alignItems: 'center', justifyContent: 'space-between', gap: 'var(--spacing-3)', marginBottom: 'var(--spacing-3)' }}>
                        <ListSearchInput
                            value={searchTerm}
                            placeholder={t('hostResource.searchSops')}
                            onChange={setSearchTerm}
                        />
                        {searchTerm && (
                            <ListResultsMeta>
                                {t('common.resultsFound', { count: filteredSops.length })}
                            </ListResultsMeta>
                        )}
                    </div>
                    <div className="sop-workflow-list-shell">
                        <div className="sop-workflow-table-wrap">
                            <table className="sop-workflow-table">
                                <thead>
                                    <tr>
                                        <th>{t('remoteDiagnosis.sops.name')}</th>
                                        <th>{t('remoteDiagnosis.sops.description')}</th>
                                        <th>{t('remoteDiagnosis.sops.triggerCondition')}</th>
                                        <th style={{ textAlign: 'center' }}>
                                            {t('hostResource.targetSolution')}
                                        </th>
                                        <th style={{ textAlign: 'center' }}>
                                            {t('remoteDiagnosis.sops.status')}
                                        </th>
                                        <th style={{ textAlign: 'right' }}>{t('remoteDiagnosis.sops.actions')}</th>
                                    </tr>
                                </thead>
                                <tbody>
                                    {paginatedSops.map(sop => (
                                        <SopExpandableRow
                                            key={sop.id}
                                            sop={sop}
                                            solutionTypes={solutionTypes}
                                            onEdit={s => {
                                                setEditingSop(s)
                                                setShowAddModal(true)
                                            }}
                                            onDelete={handleDelete}
                                            onToggleEnabled={handleToggleEnabled}
                                        />
                                    ))}
                                </tbody>
                            </table>
                        </div>
                    </div>
                    {totalPages > 1 && (
                        <div className="sop-workflow-pagination">
                            <span className="sop-workflow-pagination-info">
                                {t('common.showing', {
                                    start: (safePage - 1) * PAGE_SIZE + 1,
                                    end: Math.min(safePage * PAGE_SIZE, filteredSops.length),
                                    total: filteredSops.length,
                                })}
                            </span>
                            <div className="sop-workflow-pagination-controls">
                                <button className="sop-workflow-pagination-btn"
                                    disabled={safePage <= 1}
                                    onClick={() => setCurrentPage(safePage - 1)}>
                                    {t('common.previousPage')}
                                </button>
                                <span className="sop-workflow-pagination-page">{safePage} / {totalPages}</span>
                                <button className="sop-workflow-pagination-btn"
                                    disabled={safePage >= totalPages}
                                    onClick={() => setCurrentPage(safePage + 1)}>
                                    {t('common.nextPage')}
                                </button>
                            </div>
                        </div>
                    )}
                </>
                })()}
            </section>

            {(showAddModal || editingSop) && (
                <SopFormModal
                    sop={editingSop}
                    solutionTypes={solutionTypes}
                    onClose={() => {
                        setShowAddModal(false)
                        setEditingSop(null)
                    }}
                    onSave={handleSaveSop}
                />
            )}
        </div>
    )
}
