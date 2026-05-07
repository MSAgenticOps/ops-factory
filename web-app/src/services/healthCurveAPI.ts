/**
 * Health Curve API Service
 * Aligned with gateway QoS REST endpoints (/gateway/qos/*)
 */

import { GATEWAY_URL, gatewayHeaders } from '../config/runtime'

import type { HealthIndicatorResponse, IndicatorDetailResponse } from '../types/health';

export interface EnvironmentInfo {
  envCode: string
  agentSolutionType: string
}

async function request<T>(endpoint: string, body?: unknown, method = 'POST', userId?: string | null): Promise<T> {
  const url = `${GATEWAY_URL}${endpoint}`;
  const response = await fetch(url, {
    method,
    headers: gatewayHeaders(userId),
    body: body ? JSON.stringify(body) : undefined,
  });
  if (!response.ok) {
    const errorData = await response.json().catch(() => ({}));
    throw new Error(errorData.message || `HTTP error! status: ${response.status}`);
  }
  return response.json();
}

export async function getHealthIndicator(envCode: string, startTime: number, endTime: number, userId?: string | null): Promise<HealthIndicatorResponse> {
  return request<HealthIndicatorResponse>('/qos/getHealthIndicator', {
    envCode, startTime, endTime,
  }, 'POST', userId);
}

export async function getAvailableIndicatorDetail(envCode: string, startTime: number, endTime: number,
    pageIndex = 1, pageSize = 10, userId?: string | null): Promise<IndicatorDetailResponse> {
  return request<IndicatorDetailResponse>('/qos/getAvailableIndicatorDetail', {
    envCode, startTime, endTime, pageIndex, pageSize,
  }, 'POST', userId);
}

export async function getPerformanceIndicatorDetail(envCode: string, startTime: number, endTime: number,
    pageIndex = 1, pageSize = 10, userId?: string | null): Promise<IndicatorDetailResponse> {
  return request<IndicatorDetailResponse>('/qos/getPerformanceIndicatorDetail', {
    envCode, startTime, endTime, pageIndex, pageSize,
  }, 'POST', userId);
}

export async function getResourceIndicatorDetail(envCode: string, startTime: number, endTime: number,
    userId?: string | null): Promise<{ results: Record<string, unknown>[] }> {
  return request<{ results: Record<string, unknown>[] }>('/qos/getResourceIndicatorDetail', {
    envCode, startTime, endTime,
  }, 'POST', userId);
}

export async function getContributionData(envCode: string, startTime: number, endTime: number,
    userId?: string | null): Promise<{ results: { type: string; contribution: number }[] }> {
  return request<{ results: { type: string; contribution: number }[] }>('/qos/getContributionData', {
    envCode, startTime, endTime,
  }, 'POST', userId);
}

export async function getAlarmIndicatorDetail(envCode: string, startTime: number, endTime: number,
    pageIndex = 1, pageSize = 10, userId?: string | null): Promise<IndicatorDetailResponse> {
  return request<IndicatorDetailResponse>('/qos/getAlarmIndicatorDetail', {
    envCode, startTime, endTime, pageIndex, pageSize,
  }, 'POST', userId);
}

export async function getProductConfigRule(agentSolutionType: string, userId?: string | null): Promise<{ result: unknown }> {
  return request<{ result: unknown }>('/qos/getProductConfigRule', {
    agentSolutionType,
  }, 'POST', userId);
}

export async function getEnvironments(userId?: string | null): Promise<{ results: EnvironmentInfo[] }> {
  return request<{ results: EnvironmentInfo[] }>('/qos/getEnvironments', undefined, 'GET', userId);
}

export async function getIndicatorDetail(
  endpoint: string, envCode: string, startTime: number, endTime: number, pageIndex: number, pageSize: number,
  userId?: string | null,
): Promise<IndicatorDetailResponse> {
  const url = `${GATEWAY_URL}${endpoint}`;
  const response = await fetch(url, {
    method: 'POST',
    headers: gatewayHeaders(userId),
    body: JSON.stringify({ envCode, startTime, endTime, pageIndex, pageSize }),
  });
  if (!response.ok) throw new Error(`HTTP error! status: ${response.status}`);
  return response.json();
}
