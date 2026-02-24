import { useState, FormEvent } from 'react'
import { useNavigate, Navigate } from 'react-router-dom'
import { useTranslation } from 'react-i18next'
import { useUser } from '../contexts/UserContext'

export default function Login() {
    const { t } = useTranslation()
    const { userId, login } = useUser()
    const navigate = useNavigate()
    const [username, setUsername] = useState('')

    // Already logged in — redirect to home
    if (userId) {
        return <Navigate to="/" replace />
    }

    const handleSubmit = (e: FormEvent) => {
        e.preventDefault()
        const trimmed = username.trim()
        if (!trimmed) return
        login(trimmed)
        navigate('/', { replace: true })
    }

    return (
        <div className="login-page">
            <div className="login-card">
                <div className="login-logo">🐎</div>
                <h1 className="login-title">Ops Factory</h1>
                <p className="login-subtitle">{t('login.subtitle')}</p>
                <form onSubmit={handleSubmit} className="login-form">
                    <input
                        type="text"
                        value={username}
                        onChange={e => setUsername(e.target.value)}
                        placeholder={t('login.placeholder')}
                        className="login-input"
                        autoFocus
                        autoComplete="username"
                    />
                    <button
                        type="submit"
                        className="btn btn-primary login-button"
                        disabled={!username.trim()}
                    >
                        {t('login.submit')}
                    </button>
                </form>
            </div>
        </div>
    )
}
