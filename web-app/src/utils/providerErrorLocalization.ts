import i18n from '../i18n'

/**
 * Map backend English provider error messages to localized i18n strings.
 *
 * The provider endpoints return English-only messages (e.g. "Provider 'hello' already exists"),
 * so we pattern-match them here and return a user-friendly translated message instead.
 */
export function localizeProviderBackendError(backendError: string): string {
    const t = i18n.t

    const duplicateMatch = backendError.match(/Provider '(.+?)' already exists/)
    if (duplicateMatch) {
        return t('agentConfigure.providerDuplicateName')
    }

    if (/Provider name is required/i.test(backendError)) {
        return t('agentConfigure.providerNameRequired')
    }

    if (/Provider name contains unsupported characters/i.test(backendError)) {
        return t('agentConfigure.providerNameFormat')
    }

    const nameTooLongMatch = backendError.match(/Provider name must not exceed (\d+) characters/i)
    if (nameTooLongMatch) {
        return t('agentConfigure.providerNameTooLong', { max: parseInt(nameTooLongMatch[1], 10) })
    }

    const fieldTooLongMatch = backendError.match(
        /^(Display name|Base URL|API key|Description|Model name) must not exceed (\d+) characters$/
    )
    if (fieldTooLongMatch) {
        const fieldKeyMap: Record<string, string> = {
            'Display name': 'agentConfigure.providerDisplayName',
            'Base URL': 'agentConfigure.baseUrl',
            'API key': 'agentConfigure.apiKey',
            'Description': 'agentConfigure.providerDescription',
            'Model name': 'agentConfigure.modelName',
        }
        return t('agentConfigure.providerFieldTooLong', {
            field: t(fieldKeyMap[fieldTooLongMatch[1]]),
            max: parseInt(fieldTooLongMatch[2], 10),
        })
    }

    if (/At least one model is required/i.test(backendError)) {
        return t('agentConfigure.providerValidation')
    }

    return backendError
}
