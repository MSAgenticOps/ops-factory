import { useCallback } from 'react'
import { useTranslation } from 'react-i18next'
import type { ReactNode } from 'react'

type BaseFormData = {
    name: string
    code: string
    description: string
    color: string
    knowledge: string
}

type Props<T extends BaseFormData> = {
    title: string
    form: T
    setForm: React.Dispatch<React.SetStateAction<T>>
    saving: boolean
    onSave: () => void
    onClose: () => void
    extraFields?: ReactNode
    isEditing?: boolean
}

export default function TypeFormModal<T extends BaseFormData>({
    title, form, setForm, saving, onSave, onClose, extraFields, isEditing = false,
}: Props<T>) {
    const { t } = useTranslation()
    const requiredStar = <span style={{ color: 'var(--color-error, #ef4444)', marginLeft: 2 }}>*</span>

    const update = useCallback(<K extends keyof BaseFormData>(key: K, value: BaseFormData[K]) => {
        setForm(f => ({ ...f, [key]: value }) as T)
    }, [setForm])

    return (
        <div className="hr-host-modal modal-overlay">
            <div className="modal-content">
                <div className="modal-header">
                    <h3>{title}</h3>
                    <button className="modal-close" onClick={onClose}>×</button>
                </div>
                <div className="modal-body">
                    <div className="form-group">
                        <label className="form-label">{t('hostResource.typeName')}{requiredStar}</label>
                        <input
                            className="form-input"
                            value={form.name}
                            onChange={e => update('name', e.target.value)}
                            placeholder={t('hostResource.typeName')}
                            maxLength={100}
                        />
                    </div>
                    <div className="form-group">
                        <label className="form-label">{t('hostResource.typeCode')}{requiredStar}</label>
                        <input
                            className="form-input"
                            value={form.code}
                            onChange={e => update('code', e.target.value)}
                            placeholder={t('hostResource.typeCode')}
                            maxLength={50}
                            disabled={isEditing}
                            style={isEditing ? {
                                backgroundColor: 'var(--surface-background, #f1f5f9)',
                                color: 'var(--text-secondary, #64748b)',
                                cursor: 'not-allowed'
                            } : undefined}
                        />
                    </div>
                    <div className="form-group">
                        <label className="form-label">{t('hostResource.description')}</label>
                        <input
                            className="form-input"
                            value={form.description}
                            onChange={e => update('description', e.target.value)}
                            maxLength={500}
                        />
                    </div>
                    <div className="form-group">
                        <label className="form-label">{t('hostResource.typeColor')}</label>
                        <input
                            type="color"
                            value={form.color}
                            onChange={e => update('color', e.target.value)}
                            style={{ width: 48, height: 32, padding: 2, cursor: 'pointer' }}
                        />
                    </div>
                    <div className="form-group">
                        <label className="form-label">{t('hostResource.knowledge')}</label>
                        <textarea
                            className="form-input"
                            rows={5}
                            value={form.knowledge}
                            onChange={e => update('knowledge', e.target.value)}
                            placeholder={t('hostResource.knowledgeHint')}
                            maxLength={2000}
                            style={{ resize: 'vertical' }}
                        />
                    </div>
                    {extraFields}
                </div>
                <div className="modal-footer">
                    <button className="btn btn-secondary" onClick={onClose}>
                        {t('common.cancel')}
                    </button>
                    <button
                        className="btn btn-primary"
                        onClick={onSave}
                        disabled={saving || !form.name.trim() || !form.code.trim()}
                    >
                        {saving ? t('common.saving') : t('common.save')}
                    </button>
                </div>
            </div>
        </div>
    )
}
