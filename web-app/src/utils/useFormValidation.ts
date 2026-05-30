import { useCallback } from 'react'
import { useToast } from '../app/platform/providers/ToastContext'
import { validateAndSanitize, validateEnvValue } from './inputValidation'

export interface ValidationResult {
    valid: boolean
    sanitized: string
    error?: string
}

export interface FormValidationResult<T> {
    valid: boolean
    sanitized: T
}

/**
 * Custom hook for validating and sanitizing form data.
 * Provides XSS protection for form inputs.
 *
 * @example
 * const { validateFormData } = useFormValidation()
 * const result = validateFormData(
 *     { name: 'test', code: 'abc' },
 *     ['name', 'code'],
 *     { name: 'Name', code: 'Code' }
 * )
 */
export function useFormValidation() {
    const { showToast } = useToast()

    /**
     * Validate and sanitize a single string field
     */
    const validateField = useCallback((
        value: string,
        _fieldName: string,
        fieldLabel: string
    ): ValidationResult => {
        const result = validateAndSanitize(value, fieldLabel)
        return result
    }, [])

    /**
     * Validate and sanitize multiple form fields
     *
     * @param formData - The form data object
     * @param fieldsToValidate - Array of field names to validate
     * @param fieldLabels - Object mapping field names to their display labels
     * @param errorMessages - i18n keys or custom error messages (optional)
     * @returns Object with valid flag and sanitized data
     */
    const validateFormData = useCallback(<T extends Record<string, any>>(
        formData: T,
        fieldsToValidate: (keyof T)[],
        fieldLabels: Record<string, string>,
        errorMessages?: Record<string, string>
    ): FormValidationResult<T> => {
        const sanitized = { ...formData }
        let hasError = false

        for (const field of fieldsToValidate) {
            const value = formData[field] as string
            const fieldLabel = fieldLabels[field as string] || field as string

            // Skip validation if value is empty (optional fields)
            if (!value || !value.trim()) {
                continue
            }

            const result = validateAndSanitize(value, fieldLabel)

            if (!result.valid) {
                hasError = true
                // Use the specific error from validation result, or fall back to generic message
                const message = result.error || errorMessages?.[field as string] || 'hostResource.invalidChars'
                showToast('error', message)
                break
            }

            // Type assertion: we only validate string fields, so this is safe
            sanitized[field] = result.sanitized as T[keyof T]
        }

        return {
            valid: !hasError,
            sanitized
        }
    }, [showToast])

    /**
     * Validate and sanitize environment variables array
     *
     * @param envVariables - Array of {key, value} objects
     * @param errorMessages - i18n keys or custom error messages (optional)
     * @returns Object with valid flag and sanitized env variables
     */
    const validateEnvVariables = useCallback((
        envVariables: Array<{ key: string; value: string }>,
        errorMessages?: Record<string, string>
    ): FormValidationResult<Array<{ key: string; value: string }>> => {
        const sanitized = [...envVariables]
        let hasError = false

        for (let i = 0; i < envVariables.length; i++) {
            const env = envVariables[i]

            // Validate key with strict XSS check
            if (env.key && env.key.trim()) {
                const keyResult = validateAndSanitize(env.key, 'envVariables.key')
                if (!keyResult.valid) {
                    hasError = true
                    const message = errorMessages?.['envKey'] || 'hostResource.invalidChars'
                    showToast('error', message)
                    break
                }
                sanitized[i] = { ...env, key: keyResult.sanitized }
            }

            // Validate value with permissive check (allows / for paths and URLs)
            if (env.value && env.value.trim()) {
                const valueResult = validateEnvValue(env.value, 'envVariables.value')
                if (!valueResult.valid) {
                    hasError = true
                    const message = errorMessages?.['envValue'] || 'hostResource.invalidChars'
                    showToast('error', message)
                    break
                }
                sanitized[i] = { ...sanitized[i], value: valueResult.sanitized }
            }
        }

        return {
            valid: !hasError,
            sanitized
        }
    }, [showToast])

    return {
        validateField,
        validateFormData,
        validateEnvVariables
    }
}
