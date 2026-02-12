import { useNavigate } from 'react-router-dom'
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
    const { userId, logout } = useUser()
    const navigate = useNavigate()

    const avatar = userId ? getAvatarForUser(userId) : '🦆'

    const handleLogout = () => {
        logout()
        navigate('/login', { replace: true })
    }

    return (
        <div className="settings-page">
            <h1 className="page-title">Settings</h1>

            <div className="settings-section">
                <h2 className="settings-section-title">Profile</h2>
                <div className="settings-profile-card">
                    <div className="settings-avatar">{avatar}</div>
                    <div className="settings-user-info">
                        <div className="settings-username">{userId}</div>
                        <div className="settings-user-label">Logged in user</div>
                    </div>
                </div>
            </div>

            <div className="settings-section">
                <h2 className="settings-section-title">Account</h2>
                <button className="btn btn-secondary" onClick={handleLogout}>
                    Log out
                </button>
            </div>
        </div>
    )
}
