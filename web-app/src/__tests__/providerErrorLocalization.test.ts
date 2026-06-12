import { describe, it, expect, beforeAll } from 'vitest'
import { localizeProviderBackendError } from '../utils/providerErrorLocalization'
import i18n from '../i18n'

describe('localizeProviderBackendError', () => {
    beforeAll(async () => {
        await i18n.changeLanguage('zh')
    })

    it('localizes duplicate provider name error', () => {
        const result = localizeProviderBackendError("Provider 'hello' already exists")
        expect(result).toBe('同名 Provider 已存在，请使用其他名称。')
    })

    it('localizes required provider name error', () => {
        const result = localizeProviderBackendError('Provider name is required')
        expect(result).toBe('Provider 名称不能为空。')
    })

    it('localizes invalid provider name format error', () => {
        const result = localizeProviderBackendError('Provider name contains unsupported characters')
        expect(result).toBe('Provider 名称只能包含字母、数字、点、下划线和连字符。')
    })

    it('localizes provider name too long error', () => {
        const result = localizeProviderBackendError('Provider name must not exceed 200 characters')
        expect(result).toBe('Provider 名称不能超过 200 个字符。')
    })

    it('localizes display name too long error', () => {
        const result = localizeProviderBackendError('Display name must not exceed 255 characters')
        expect(result).toBe('显示名称 不能超过 255 个字符。')
    })

    it('localizes base url too long error', () => {
        const result = localizeProviderBackendError('Base URL must not exceed 500 characters')
        expect(result).toBe('Base URL 不能超过 500 个字符。')
    })

    it('localizes api key too long error', () => {
        const result = localizeProviderBackendError('API key must not exceed 5000 characters')
        expect(result).toBe('API Key 不能超过 5000 个字符。')
    })

    it('localizes description too long error', () => {
        const result = localizeProviderBackendError('Description must not exceed 1000 characters')
        expect(result).toBe('描述 不能超过 1000 个字符。')
    })

    it('localizes model name too long error', () => {
        const result = localizeProviderBackendError('Model name must not exceed 255 characters')
        expect(result).toBe('模型名称 不能超过 255 个字符。')
    })

    it('falls back to original message for unknown errors', () => {
        const unknown = 'Some unexpected backend failure'
        const result = localizeProviderBackendError(unknown)
        expect(result).toBe(unknown)
    })

    it('localizes missing models error', () => {
        const result = localizeProviderBackendError('At least one model is required')
        expect(result).toBe('请填写有效的 Provider 名称和 Base URL。')
    })
})
