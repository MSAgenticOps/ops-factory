/**
 * Health Curve Types
 * Aligned with QoS backend design document (0~1 scale)
 */

export interface HealthScore {
  systemName: string;
  overallScore: number;
  availabilityScore: number;
  performanceScore: number;
  resourceScore: number;
  healthLevel: 'Good' | 'Warning' | 'Orange' | 'Critical';
  timestamp: string;
}

export interface HealthMetrics {
  systemUptime: number;
  serviceAvailability: number;
  incidentCount: number;
  throughput: number;
  latency: number;
  errorRate: number;
  cpuUsage: number;
  memoryUsage: number;
  diskUsage: number;
  networkUsage: number;
}

export interface HealthStatistics {
  totalSystems: number;
  goodCount: number;
  warningCount: number;
  orangeCount: number;
  criticalCount: number;
  averageScore: number;
}

export interface HealthCurveConfig {
  weights: {
    availability: number;
    performance: number;
    resource: number;
  };
  thresholds: {
    good: number;
    warning: number;
    bad: number;
  };
}

export interface ApiResponse<T> {
  data: T;
  success: boolean;
  message?: string;
}

export interface HealthScoreRequest {
  systemName: string;
  metrics: HealthMetrics;
}

export interface HealthIndicatorPoint {
  timestamp: number;
  value: string;
}

export interface IndicatorDetailRequest {
  envCode: string;
  startTime: number;
  endTime: number;
  pageIndex: number;
  pageSize: number;
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
