import ScheduledActionsPage from './pages/ScheduledActionsPage'
import type { AppModule } from '../../platform/module-types'

const schedulerModule: AppModule = {
    id: 'scheduler',
    owner: 'platform',
    routes: [
        { id: 'scheduler.index', path: '/scheduler', component: ScheduledActionsPage, access: 'authenticated' },
    ],
    navItems: [
        {
            id: 'scheduler.nav',
            type: 'route',
            group: 'config',
            order: 30,
            titleKey: 'sidebar.scheduler',
            icon: 'scheduler',
            routeId: 'scheduler.index',
            // Scheduling now lives in each agent's config page ("我的" group). The standalone
            // cross-agent route stays reachable but is removed from the sidebar.
            hidden: true,
        },
    ],
}

export default schedulerModule
