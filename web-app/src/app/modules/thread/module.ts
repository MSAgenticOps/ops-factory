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
            order: 45,
            titleKey: 'sidebar.thread',
            icon: 'thread',
            routeId: 'thread.index',
            badge: 'threadUnread',
        },
    ],
}

export default threadModule
