import { useEffect } from 'react'
import { useSkills } from '../../hooks/useSkills'
import SkillCard from './SkillCard'

interface SkillSectionProps {
    agentId: string
}

export default function SkillSection({ agentId }: SkillSectionProps) {
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
                <h3 className="skill-section-title">Skills</h3>
                <span className="skill-section-count">{skills.length}</span>
            </div>

            {error && (
                <div className="skill-alert skill-alert-error">{error}</div>
            )}

            {isLoading ? (
                <div className="skill-loading">Loading skills...</div>
            ) : skills.length > 0 ? (
                <div className="skill-grid">
                    {skills.map(skill => (
                        <SkillCard key={skill.name} skill={skill} />
                    ))}
                </div>
            ) : (
                <div className="skill-empty">
                    <p>No skills configured.</p>
                </div>
            )}
        </div>
    )
}
