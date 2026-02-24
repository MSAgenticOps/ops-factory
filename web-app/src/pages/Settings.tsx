import { useNavigate } from 'react-router-dom'
import { useTranslation } from 'react-i18next'
import { useUser } from '../contexts/UserContext'

const EMOJI_AVATARS = ['🦆', '🐱', '🐶', '🦊', '🐸', '🐼', '🐨', '🦉', '🐙', '🦄', '🐝', '🦋']

function getAvatarForUser(username: string): string {
    let hash = 0
    for (let i = 0; i < username.length; i++) {
        hash = ((hash << 5) - hash) + username.charCodeAt(i)
        hash |= 0
    }
    return EMOJI_AVATARS[Math.abs(hash) % EMOJI_AVATARS.length]
}

export { getAvatarForUser }

export default function Settings() {
    const { t, i18n } = useTranslation()
    const { userId, logout } = useUser()
    const navigate = useNavigate()

    const avatar = userId ? getAvatarForUser(userId) : '🦆'

    const handleLogout = () => {
        logout()
        navigate('/login', { replace: true })
    }

    const handleLanguageChange = (lng: string) => {
        i18n.changeLanguage(lng)
    }

    return (
        <div className="settings-page">
            <h1 className="page-title">{t('settings.title')}</h1>

            <div className="settings-section">
                <h2 className="settings-section-title">{t('settings.profile')}</h2>
                <div className="settings-profile-card">
                    <div className="settings-avatar">{avatar}</div>
                    <div className="settings-user-info">
                        <div className="settings-username">{userId}</div>
                        <div className="settings-user-label">{t('settings.loggedInUser')}</div>
                    </div>
                </div>
            </div>

            <div className="settings-section">
                <h2 className="settings-section-title">{t('settings.language')}</h2>
                <p style={{
                    fontSize: 'var(--font-size-sm)',
                    color: 'var(--color-text-secondary)',
                    marginBottom: 'var(--spacing-3)'
                }}>
                    {t('settings.languageDescription')}
                </p>
                <select
                    className="form-input"
                    value={i18n.language?.startsWith('zh') ? 'zh' : 'en'}
                    onChange={(e) => handleLanguageChange(e.target.value)}
                    style={{ maxWidth: '240px' }}
                >
                    <option value="en">English</option>
                    <option value="zh">中文</option>
                </select>
            </div>

            <div className="settings-section">
                <h2 className="settings-section-title">{t('settings.account')}</h2>
                <button className="btn btn-secondary" onClick={handleLogout}>
                    {t('settings.logout')}
                </button>
            </div>
        </div>
    )
}
