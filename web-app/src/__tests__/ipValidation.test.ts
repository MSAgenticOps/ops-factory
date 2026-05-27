import { describe, it, expect } from 'vitest'
import { isValidIp } from '../utils/ip-validation'

describe('isValidIp', () => {
    describe('IPv4', () => {
        it('accepts valid IPv4 addresses', () => {
            expect(isValidIp('192.168.1.1')).toBe(true)
            expect(isValidIp('0.0.0.0')).toBe(true)
            expect(isValidIp('255.255.255.255')).toBe(true)
            expect(isValidIp('10.0.0.1')).toBe(true)
        })

        it('rejects invalid IPv4 addresses', () => {
            expect(isValidIp('256.1.1.1')).toBe(false)
            expect(isValidIp('1.1.1')).toBe(false)
            expect(isValidIp('1.1.1.1.1')).toBe(false)
            expect(isValidIp('abc')).toBe(false)
            expect(isValidIp('')).toBe(false)
        })

        it('trims whitespace before validation', () => {
            expect(isValidIp('  192.168.1.1  ')).toBe(true)
        })
    })

    describe('IPv6', () => {
        it('accepts valid IPv6 addresses', () => {
            expect(isValidIp('::1')).toBe(true)
            expect(isValidIp('fe80::1')).toBe(true)
            expect(isValidIp('2001:0db8:85a3:0000:0000:8a2e:0370:7334')).toBe(true)
        })

        it('rejects invalid IPv6 addresses', () => {
            expect(isValidIp('gggg::1')).toBe(false)
        })
    })
})
