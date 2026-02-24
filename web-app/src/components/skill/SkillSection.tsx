import { useEffect } from 'react'
import { useTranslation } from 'react-i18next'
import { useSkills } from '../../hooks/useSkills'
import SkillCard from './SkillCard'

interface SkillSectionProps {
    agentId: string
}

export default function SkillSection({ agentId }: SkillSectionProps) {
    const { t } = useTranslation()
    const { skills, isLoading, error, fetchSkills } = useSkills()

    useEffect(() => {
        if (agentId) {
            fetchSkills(agentId)
        }
    }, [agentId, fetchSkills])

    if (!agentId) return null

    return (
        <div className="skill-section">
            <div className="skill-section-header">
                <h3 className="skill-section-title">{t('skill.title')}</h3>
                <span className="skill-section-count">{skills.length}</span>
            </div>

            {error && (
                <div className="skill-alert skill-alert-error">{error}</div>
            )}

            {isLoading ? (
                <div className="skill-loading">{t('skill.loadingSkills')}</div>
            ) : skills.length > 0 ? (
                <div className="skill-grid">
                    {skills.map(skill => (
                        <SkillCard key={skill.name} skill={skill} />
                    ))}
                </div>
            ) : (
                <div className="skill-empty">
                    <p>{t('skill.noSkills')}</p>
                </div>
            )}
        </div>
    )
}
