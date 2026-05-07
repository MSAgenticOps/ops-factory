import HealthCurvePage from './pages/HealthCurvePage'
import HealthDetailPage from './pages/HealthDetailPage'
import type { AppModule } from '../../platform/module-types'

const healthCurveModule: AppModule = {
    id: 'health-curve',
    owner: 'platform',
    routes: [
        { id: 'health-curve.index', path: '/health-curve', component: HealthCurvePage, access: 'admin' },
        { id: 'health-curve.detail', path: '/health-curve/:envCode', component: HealthDetailPage, access: 'admin', hidden: true },
    ],
    navItems: [
        {
            id: 'health-curve.nav',
            type: 'route',
            group: 'monitoring',
            order: 8,
            titleKey: 'sidebar.healthCurve',
            icon: 'monitoring',
            routeId: 'health-curve.index',
        },
    ],
}

export default healthCurveModule
