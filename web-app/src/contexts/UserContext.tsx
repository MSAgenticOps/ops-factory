import { createContext, useContext, useState, useCallback, ReactNode } from 'react'
import { Navigate } from 'react-router-dom'

const STORAGE_KEY = 'opsfactory:userId'

interface UserContextType {
    userId: string | null
    login: (username: string) => void
    logout: () => void
}

const UserContext = createContext<UserContextType | null>(null)

export function UserProvider({ children }: { children: ReactNode }) {
    const [userId, setUserId] = useState<string | null>(() => {
        return localStorage.getItem(STORAGE_KEY)
    })

    const login = useCallback((username: string) => {
        const trimmed = username.trim()
        if (!trimmed) return
        localStorage.setItem(STORAGE_KEY, trimmed)
        setUserId(trimmed)
    }, [])

    const logout = useCallback(() => {
        localStorage.removeItem(STORAGE_KEY)
        setUserId(null)
    }, [])

    return (
        <UserContext.Provider value={{ userId, login, logout }}>
            {children}
        </UserContext.Provider>
    )
}

export function useUser(): UserContextType {
    const context = useContext(UserContext)
    if (!context) {
        throw new Error('useUser must be used within a UserProvider')
    }
    return context
}

/** Redirect to /login if not authenticated */
export function ProtectedRoute({ children }: { children: ReactNode }) {
    const { userId } = useUser()

    if (!userId) {
        return <Navigate to="/login" replace />
    }

    return <>{children}</>
}
