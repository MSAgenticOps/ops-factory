import { readFileSync, existsSync } from 'node:fs'
import { join, resolve, dirname } from 'node:path'
import { fileURLToPath } from 'node:url'
import { parse } from 'yaml'

export interface AgentConfig {
  id: string
  name: string
  port: number
  host: string
  secret_key: string
}

export interface GatewayAgentsConfig {
  agents: Array<{
    id: string
    name: string
    port: number
  }>
}

export interface GatewayConfig {
  host: string
  port: number
  secretKey: string
  projectRoot: string
  agentsDir: string
  goosedBin: string
  agents: AgentConfig[]
}

const __filename = fileURLToPath(import.meta.url)
const __dirname = dirname(__filename)

export function loadGatewayConfig(): GatewayConfig {
  const host = process.env.GATEWAY_HOST || '127.0.0.1'
  const port = parseInt(process.env.GATEWAY_PORT || '3000', 10)
  const secretKey = process.env.GATEWAY_SECRET_KEY || 'test'
  const projectRoot = resolve(process.env.PROJECT_ROOT || process.cwd())
  const agentsDir = resolve(process.env.AGENTS_DIR || join(projectRoot, 'agents'))
  const goosedBin = process.env.GOOSED_BIN || 'goosed'

  // Load centralized agents config
  const gatewayConfigDir = resolve(__dirname, '../config')
  const agentsConfigPath = join(gatewayConfigDir, 'agents.yaml')

  let gatewayAgentsConfig: GatewayAgentsConfig = {
    agents: []
  }

  if (existsSync(agentsConfigPath)) {
    const raw = readFileSync(agentsConfigPath, 'utf-8')
    gatewayAgentsConfig = parse(raw) as GatewayAgentsConfig
  } else {
    console.warn(`Warning: Gateway agents config not found at ${agentsConfigPath}`)
  }

  // Convert to AgentConfig array with host and secret_key
  const agents: AgentConfig[] = (gatewayAgentsConfig.agents || []).map(agent => ({
    id: agent.id,
    name: agent.name,
    port: agent.port,
    host,
    secret_key: secretKey,
  }))

  return {
    host,
    port,
    secretKey,
    projectRoot,
    agentsDir,
    goosedBin,
    agents,
  }
}
