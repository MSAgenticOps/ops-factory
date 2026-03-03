export interface ExporterConfig {
  port: number
  gatewayUrl: string
  gatewaySecretKey: string
  collectTimeoutMs: number
}

export function loadConfig(): ExporterConfig {
  return {
    port: parseInt(process.env.EXPORTER_PORT || '9091', 10),
    gatewayUrl: (process.env.GATEWAY_URL || 'http://127.0.0.1:3000').replace(/\/$/, ''),
    gatewaySecretKey: process.env.GATEWAY_SECRET_KEY || 'test',
    collectTimeoutMs: parseInt(process.env.COLLECT_TIMEOUT_MS || '5000', 10),
  }
}
