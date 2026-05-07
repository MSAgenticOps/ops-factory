/**
 * Health Curve Types
 * Aligned with QoS backend design document (0~1 scale)
 */

export interface HealthIndicatorPoint {
  timestamp: number;
  value: string;
}

export interface HealthIndicatorResponse {
  results: HealthIndicatorPoint[];
}

export interface IndicatorDetailResponse {
  total: number;
  pageIndex: number;
  pageSize: number;
  results: Record<string, unknown>[];
}
