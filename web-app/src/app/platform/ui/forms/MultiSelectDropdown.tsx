import { useState, useEffect, useRef } from 'react'

interface MultiSelectDropdownProps {
    options: { value: string; label: string }[]
    selectedIds: string[]
    onChange: (ids: string[]) => void
    placeholder: string
}

export default function MultiSelectDropdown({ options, selectedIds, onChange, placeholder }: MultiSelectDropdownProps) {
    const [open, setOpen] = useState(false)
    const ref = useRef<HTMLDivElement>(null)

    useEffect(() => {
        if (!open) return
        const handler = (e: MouseEvent) => {
            if (ref.current && !ref.current.contains(e.target as Node)) setOpen(false)
        }
        document.addEventListener('mousedown', handler)
        return () => document.removeEventListener('mousedown', handler)
    }, [open])

    const selectedLabels = options.filter(o => selectedIds.includes(o.value))

    return (
        <div className="hr-multiselect" ref={ref}>
            <div className="hr-multiselect-trigger" onClick={() => setOpen(v => !v)}>
                {selectedLabels.length === 0
                    ? <span className="hr-multiselect-placeholder">{placeholder}</span>
                    : <div className="hr-multiselect-tags">
                        {selectedLabels.map(o => (
                            <span key={o.value} className="hr-multiselect-tag">
                                {o.label}
                                <span className="hr-multiselect-tag-remove" onClick={e => { e.stopPropagation(); onChange(selectedIds.filter(id => id !== o.value)) }}>×</span>
                            </span>
                        ))}
                    </div>
                }
                <span className={`hr-multiselect-arrow ${open ? 'is-open' : ''}`}>▾</span>
            </div>
            {open && (
                <div className="hr-multiselect-dropdown">
                    {options.map(o => {
                        const checked = selectedIds.includes(o.value)
                        return (
                            <div key={o.value} className={`hr-multiselect-option ${checked ? 'is-selected' : ''}`} onClick={() => {
                                onChange(checked ? selectedIds.filter(id => id !== o.value) : [...selectedIds, o.value])
                            }}>
                                <span className={`hr-multiselect-check ${checked ? 'is-checked' : ''}`} />
                                {o.label}
                            </div>
                        )
                    })}
                    {options.length === 0 && <div className="hr-multiselect-empty">{placeholder}</div>}
                </div>
            )}
        </div>
    )
}
