import type { Citation } from '../utils/citationParser'

interface ReferenceListProps {
    citations: Citation[]
}

export default function ReferenceList({ citations }: ReferenceListProps) {
    if (citations.length === 0) return null

    return (
        <div className="reference-list">
            <div className="reference-list-label">
                <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth="2" width="14" height="14">
                    <path d="M4 19.5A2.5 2.5 0 0 1 6.5 17H20" />
                    <path d="M6.5 2H20v20H6.5A2.5 2.5 0 0 1 4 19.5v-15A2.5 2.5 0 0 1 6.5 2z" />
                </svg>
                References ({citations.length})
            </div>
            <div className="reference-capsules">
                {citations.map((cite) => (
                    cite.url ? (
                        <a
                            key={cite.index}
                            className="reference-capsule linked"
                            href={cite.url}
                            target="_blank"
                            rel="noopener noreferrer"
                        >
                            <span className="reference-capsule-index">{cite.index}</span>
                            <span className="reference-capsule-title">{cite.title}</span>
                            <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth="2" width="12" height="12">
                                <path d="M18 13v6a2 2 0 0 1-2 2H5a2 2 0 0 1-2-2V8a2 2 0 0 1 2-2h6" />
                                <polyline points="15 3 21 3 21 9" />
                                <line x1="10" y1="14" x2="21" y2="3" />
                            </svg>
                        </a>
                    ) : (
                        <span key={cite.index} className="reference-capsule">
                            <span className="reference-capsule-index">{cite.index}</span>
                            <span className="reference-capsule-title">{cite.title}</span>
                        </span>
                    )
                ))}
            </div>
        </div>
    )
}
