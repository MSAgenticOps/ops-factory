import type { SkillEntry } from '../../types/skill'

interface SkillCardProps {
    skill: SkillEntry
}

export default function SkillCard({ skill }: SkillCardProps) {
    return (
        <div className="skill-card">
            <div className="skill-card-header">
                <span className="skill-card-name">{skill.name}</span>
            </div>
            <p className="skill-card-description">
                {skill.description || 'No description'}
            </p>
            <div className="skill-card-path">
                <code>{skill.path}</code>
            </div>
        </div>
    )
}
