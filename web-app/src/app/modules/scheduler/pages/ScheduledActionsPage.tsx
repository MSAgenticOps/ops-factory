import SchedulesPanel from '../../../platform/scheduler/SchedulesPanel'

/**
 * Standalone all-agents scheduler overview. The per-agent scheduling UI now lives in each agent's
 * config page ("我的" group); this page is kept (hidden from the sidebar) for cross-agent inspection.
 */
export default function ScheduledActions() {
    return <SchedulesPanel />
}
