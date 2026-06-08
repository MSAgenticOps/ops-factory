import type { ReactNode } from 'react'
import type { IconKey } from './module-types'

type IconFrameProps = {
    children: ReactNode
    strokeWidth?: number
}

function IconFrame({ children, strokeWidth = 1.85 }: IconFrameProps) {
    return (
        <svg
            viewBox="0 0 24 24"
            fill="none"
            stroke="currentColor"
            strokeWidth={strokeWidth}
            strokeLinecap="round"
            strokeLinejoin="round"
            aria-hidden="true"
        >
            {children}
        </svg>
    )
}

function HomeIcon() {
    return (
        <IconFrame strokeWidth={1.85}>
            <path d="M4.8 10.6L12 4.7l7.2 5.9v7.85A1.8 1.8 0 0 1 17.4 20.25H6.6a1.8 1.8 0 0 1-1.8-1.8z" />
            <path d="M9.5 20.25v-4.9a1.05 1.05 0 0 1 1.05-1.05h2.9a1.05 1.05 0 0 1 1.05 1.05v4.9" />
        </IconFrame>
    )
}

function PlusIcon() {
    return (
        <IconFrame>
            <path d="M12 5.5v13" />
            <path d="M5.5 12h13" />
        </IconFrame>
    )
}

function HistoryIcon() {
    return (
        <IconFrame strokeWidth={1.85}>
            <path d="M5 11.95a7 7 0 1 0 2.05-4.95" />
            <path d="M5 6.45v3.5h3.5" />
            <path d="M12 8.55v3.95l2.7 1.65" />
        </IconFrame>
    )
}

function InboxIcon() {
    return (
        <IconFrame strokeWidth={1.85}>
            <path d="M5.95 7.5h12.1a1.35 1.35 0 0 1 1.23.8l1.02 2.28a1.4 1.4 0 0 1 .12.57v5.3a1.8 1.8 0 0 1-1.8 1.8H5.38a1.8 1.8 0 0 1-1.8-1.8v-5.3c0-.2.04-.4.12-.57L4.72 8.3a1.35 1.35 0 0 1 1.23-.8z" />
            <path d="M3.7 11.8h4.62l1.32 1.82h4.72l1.32-1.82h4.62" />
        </IconFrame>
    )
}

function FilesIcon() {
    return (
        <IconFrame strokeWidth={1.85}>
            <path d="M7.3 3.75h5.9l4.5 4.45v10.95a1.7 1.7 0 0 1-1.7 1.7H7.3a1.7 1.7 0 0 1-1.7-1.7V5.45a1.7 1.7 0 0 1 1.7-1.7z" />
            <path d="M13.2 3.95v4.35h4.35" />
            <path d="M9.1 12.2h5.8" />
            <path d="M9.1 15.95h5.8" />
        </IconFrame>
    )
}

function ThreadIcon() {
    // 我的助理:对话气泡 + AI 火花,与「渠道」的聚合节点彻底区分
    return (
        <IconFrame strokeWidth={1.85}>
            <path d="M6 4.6h12a2 2 0 0 1 2 2v6.4a2 2 0 0 1-2 2h-6.3l-3.5 2.9v-2.9H6a2 2 0 0 1-2-2V6.6a2 2 0 0 1 2-2z" />
            <path d="M12 6.9c.33 1.6 1.05 2.32 2.65 2.65-1.6.33-2.32 1.05-2.65 2.65-.33-1.6-1.05-2.32-2.65-2.65 1.6-.33 2.32-1.05 2.65-2.65z" />
        </IconFrame>
    )
}

function ChannelsIcon() {
    // 渠道:中心枢纽 + 3 个接入端点,表达「多渠道聚合」,不再用对话气泡
    return (
        <IconFrame strokeWidth={1.85}>
            <circle cx="12" cy="12" r="2.5" />
            <circle cx="12" cy="5.1" r="1.85" />
            <circle cx="5.7" cy="16.4" r="1.85" />
            <circle cx="18.3" cy="16.4" r="1.85" />
            <path d="M12 9.5V6.95" />
            <path d="M10.1 13.4l-2.75 1.85" />
            <path d="M13.9 13.4l2.75 1.85" />
        </IconFrame>
    )
}

function BusinessIntelligenceIcon() {
    return (
        <IconFrame strokeWidth={1.85}>
            <path d="M4.75 19.25h14.5" />
            <path d="M7.05 17.1v-2.95" />
            <path d="M11.95 17.1V8.45" />
            <path d="M16.85 17.1v-5.2" />
            <path d="M6.15 10.25l2.4-2.25 2.95 1.55 4.3-4.05" />
            <path d="M13.95 5.5h1.85v1.85" />
        </IconFrame>
    )
}

function OperationIntelligenceIcon() {
    // 运维智能:仪表盘/健康度,作为「业务智能」柱状图的指标家族同伴,轮廓清晰可读
    return (
        <IconFrame strokeWidth={1.85}>
            <path d="M4.8 15.4a7.2 7.2 0 0 1 14.4 0" />
            <path d="M5.6 12.2l1.55.9" />
            <path d="M18.4 12.2l-1.55.9" />
            <path d="M12 15.4l3.2-3.9" />
            <circle cx="12" cy="15.4" r="1.15" />
        </IconFrame>
    )
}

function FinOpsIcon() {
    // Token 运营:代币/额度 = 一枚硬币 + 价值火花,替换通用六边形
    return (
        <IconFrame strokeWidth={1.85}>
            <circle cx="12" cy="12" r="7.3" />
            <path d="M12 8.58c.38 2.05 1.37 3.04 3.42 3.42-2.05.38-3.04 1.37-3.42 3.42-.38-2.05-1.37-3.04-3.42-3.42 2.05-.38 3.04-1.37 3.42-3.42z" />
        </IconFrame>
    )
}

function WorkflowIcon() {
    return (
        <IconFrame strokeWidth={1.85}>
            <rect x="4.4" y="5.2" width="5" height="5" rx="1.25" />
            <rect x="14.6" y="5.2" width="5" height="5" rx="1.25" />
            <rect x="9.5" y="14" width="5" height="5" rx="1.25" />
            <path d="M9.4 7.7h2.6" />
            <path d="M12 7.7v4.9" />
            <path d="M14.6 7.7H12" />
        </IconFrame>
    )
}

function AgentsIcon() {
    return (
        <IconFrame strokeWidth={1.85}>
            <path d="M8.1 7.15h7.8a2.8 2.8 0 0 1 2.8 2.8v5.3a2.8 2.8 0 0 1-2.8 2.8H8.1a2.8 2.8 0 0 1-2.8-2.8v-5.3a2.8 2.8 0 0 1 2.8-2.8z" />
            <path d="M12 4.25v2.1" />
            <path d="M8.7 12.15h.01" />
            <path d="M15.3 12.15h.01" />
            <path d="M9.55 15.2c.7.55 1.55.82 2.45.82s1.75-.27 2.45-.82" />
            <path d="M5.3 10.8H3.95" />
            <path d="M20.05 10.8H18.7" />
        </IconFrame>
    )
}

function KnowledgeIcon() {
    return (
        <IconFrame strokeWidth={1.85}>
            <path d="M6.55 5.1h4.15c1 0 1.92.45 2.55 1.2.63-.75 1.55-1.2 2.55-1.2h2.65v13.8H15.8c-1 0-1.92.45-2.55 1.2-.63-.75-1.55-1.2-2.55-1.2H6.55z" />
            <path d="M13.25 6.3v13.8" />
            <path d="M8.4 9.15h1.85" />
            <path d="M8.4 12.05h1.85" />
            <path d="M15.15 9.15h1.55" />
        </IconFrame>
    )
}

function KnowledgeGraphIcon() {
    return (
        <IconFrame strokeWidth={1.8}>
            <circle cx="6.2" cy="7.2" r="2.15" />
            <circle cx="17.8" cy="7.2" r="2.15" />
            <circle cx="12" cy="17.05" r="2.25" />
            <path d="M8.18 8.05l2.55 6.75" />
            <path d="M15.82 8.05l-2.55 6.75" />
            <path d="M8.35 7.2h7.3" />
            <path d="M6.2 9.35v4.05a3.65 3.65 0 0 0 3.65 3.65" />
            <path d="M17.8 9.35v4.05a3.65 3.65 0 0 1-3.65 3.65" />
        </IconFrame>
    )
}

function SkillMarketIcon() {
    // 技能市场:3 块已装 + 1 块待装(菱形),经典「扩展/市场」语汇,不再像拓扑图
    return (
        <IconFrame strokeWidth={1.85}>
            <rect x="4.3" y="4.3" width="6" height="6" rx="1.5" />
            <rect x="4.3" y="13.7" width="6" height="6" rx="1.5" />
            <rect x="13.7" y="13.7" width="6" height="6" rx="1.5" />
            <path d="M16.7 4.05l3.25 3.25-3.25 3.25-3.25-3.25z" />
        </IconFrame>
    )
}

function SchedulerIcon() {
    return (
        <IconFrame strokeWidth={1.85}>
            <path d="M9.1 4.4h5.8" />
            <path d="M12 4.4v2" />
            <circle cx="12" cy="13.2" r="6.55" />
            <path d="M12 9.8v3.65l2.3 1.55" />
            <path d="M8.35 7.9l-1.1-1.15" />
        </IconFrame>
    )
}

function MonitoringIcon() {
    return (
        <IconFrame strokeWidth={1.85}>
            <path d="M3.8 13.2h3.2l1.75-4.55 3.1 8.7 2.45-5.75h5.9" />
            <path d="M19.45 13.2h.75" />
        </IconFrame>
    )
}

function HostResourceIcon() {
    // 系统资源:两层服务器,状态点改用真实空心圆(原 h.01 圆头点显得过重/像实心)
    return (
        <IconFrame strokeWidth={1.85}>
            <rect x="4.2" y="5.1" width="15.6" height="5.4" rx="1.5" />
            <rect x="4.2" y="13.5" width="15.6" height="5.4" rx="1.5" />
            <circle cx="7.5" cy="7.8" r="0.95" />
            <circle cx="7.5" cy="16.2" r="0.95" />
            <path d="M10.7 7.8h6.1" />
            <path d="M10.7 16.2h6.1" />
        </IconFrame>
    )
}

const ICONS: Record<IconKey, () => ReactNode> = {
    home: HomeIcon,
    plus: PlusIcon,
    history: HistoryIcon,
    inbox: InboxIcon,
    thread: ThreadIcon,
    files: FilesIcon,
    channels: ChannelsIcon,
    diagnosis: WorkflowIcon,
    businessIntelligence: BusinessIntelligenceIcon,
    operationIntelligence: OperationIntelligenceIcon,
    finops: FinOpsIcon,
    agents: AgentsIcon,
    skillMarket: SkillMarketIcon,
    knowledge: KnowledgeIcon,
    knowledgeGraph: KnowledgeGraphIcon,
    scheduler: SchedulerIcon,
    monitoring: MonitoringIcon,
    hostResource: HostResourceIcon,
}

export function renderIcon(icon: IconKey): ReactNode {
    const Icon = ICONS[icon]
    return <Icon />
}
