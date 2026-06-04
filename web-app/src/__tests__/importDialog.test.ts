import { describe, expect, it } from 'vitest'
import { translateImportType } from '../app/modules/host-resource/components/ImportDialog'

const mockT = (key: string) => `__${key}__`

describe('translateImportType', () => {
    it('returns empty string for undefined or empty input', () => {
        expect(translateImportType(undefined, mockT)).toBe('')
        expect(translateImportType('', mockT)).toBe('')
    })

    it('translates known single-form types', () => {
        expect(translateImportType('Whitelist', mockT)).toBe('__hostResource.importType_Whitelist__')
    })

    it('translates known plural/singular type pairs to the same key', () => {
        expect(translateImportType('ClusterType', mockT)).toBe('__hostResource.importType_ClusterTypes__')
        expect(translateImportType('ClusterTypes', mockT)).toBe('__hostResource.importType_ClusterTypes__')

        expect(translateImportType('BusinessType', mockT)).toBe('__hostResource.importType_BusinessTypes__')
        expect(translateImportType('BusinessTypes', mockT)).toBe('__hostResource.importType_BusinessTypes__')

        expect(translateImportType('HostGroup', mockT)).toBe('__hostResource.importType_HostGroups__')
        expect(translateImportType('HostGroups', mockT)).toBe('__hostResource.importType_HostGroups__')

        expect(translateImportType('Cluster', mockT)).toBe('__hostResource.importType_Clusters__')
        expect(translateImportType('Clusters', mockT)).toBe('__hostResource.importType_Clusters__')

        expect(translateImportType('Host', mockT)).toBe('__hostResource.importType_Hosts__')
        expect(translateImportType('Hosts', mockT)).toBe('__hostResource.importType_Hosts__')

        expect(translateImportType('BusinessService', mockT)).toBe('__hostResource.importType_BusinessServices__')
        expect(translateImportType('BusinessServices', mockT)).toBe('__hostResource.importType_BusinessServices__')

        expect(translateImportType('Relation', mockT)).toBe('__hostResource.importType_Relations__')
        expect(translateImportType('Relations', mockT)).toBe('__hostResource.importType_Relations__')

        expect(translateImportType('SOP', mockT)).toBe('__hostResource.importType_SOPs__')
        expect(translateImportType('SOPs', mockT)).toBe('__hostResource.importType_SOPs__')
    })

    it('falls back to raw type for unknown values', () => {
        expect(translateImportType('UnknownType', mockT)).toBe('UnknownType')
        expect(translateImportType('FooBar', mockT)).toBe('FooBar')
    })
})
