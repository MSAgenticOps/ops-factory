import ThreadPage from './pages/ThreadPage'
import type { AppModule } from '../../platform/module-types'

const threadModule: AppModule = {
    id: 'thread',
    owner: 'platform',
    routes: [
        { id: 'thread.index', path: '/thread', component: ThreadPage, access: 'authenticated' },
    ],
    navItems: [
        {
            id: 'thread.nav',
            type: 'route',
            group: 'primary',
            // Right under Home (10), above New chat (20) — the proactive assistant is the primary daily entry.
            order: 15,
            titleKey: 'sidebar.thread',
            icon: 'thread',
            routeId: 'thread.index',
            badge: 'threadUnread',
        },
    ],
}

export default threadModule
