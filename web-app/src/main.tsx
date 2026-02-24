import React from 'react'
import ReactDOM from 'react-dom/client'
import { BrowserRouter } from 'react-router-dom'
import App from './App'
import { UserProvider } from './contexts/UserContext'
import { GoosedProvider } from './contexts/GoosedContext'
import ErrorBoundary from './components/ErrorBoundary'
import './i18n'
import './App.css'

ReactDOM.createRoot(document.getElementById('root')!).render(
    <React.StrictMode>
        <ErrorBoundary>
            <BrowserRouter>
                <UserProvider>
                    <GoosedProvider>
                        <App />
                    </GoosedProvider>
                </UserProvider>
            </BrowserRouter>
        </ErrorBoundary>
    </React.StrictMode>,
)

