import { describe, expect, it } from 'vitest'
import { readFileSync } from 'node:fs'
import { resolve } from 'node:path'

const ROOT = resolve(process.cwd())

function read(path: string): string {
    return readFileSync(resolve(ROOT, path), 'utf-8')
}

function nestedKeys(value: unknown, prefix = ''): string[] {
    if (!value || typeof value !== 'object' || Array.isArray(value)) {
        return [prefix]
    }
    return Object.entries(value as Record<string, unknown>).flatMap(([key, child]) => {
        const next = prefix ? `${prefix}.${key}` : key
        return nestedKeys(child, next)
    })
}

describe('solution type feature structure', () => {
    it('defines the SolutionType interface in host types', () => {
        const typesSource = read('src/types/host.ts')

        expect(typesSource).toContain('export interface SolutionType')
        expect(typesSource).toContain('id: string')
        expect(typesSource).toContain('name: string')
        expect(typesSource).toContain('code: string')
        expect(typesSource).toContain('description: string')
        expect(typesSource).toContain('color: string')
        expect(typesSource).toContain('knowledge: string')
        expect(typesSource).toContain('createdAt: string')
        expect(typesSource).toContain('updatedAt: string')
    })

    it('adds solutionType field to ClusterType interface', () => {
        const typesSource = read('src/types/host.ts')

        expect(typesSource).toContain('solutionType?: string')
    })

    it('simplifies SOP model without mode/nodes/tags, with targetSolution', () => {
        const sopSource = read('src/types/sop.ts')

        expect(sopSource).toContain('targetSolution?: string')
        expect(sopSource).not.toContain("mode?: 'structured' | 'natural_language'")
    })

    it('provides the useSolutionTypes hook with CRUD methods', () => {
        const hookSource = read('src/app/modules/host-resource/hooks/useSolutionTypes.ts')

        expect(hookSource).toContain('export function useSolutionTypes')
        expect(hookSource).toContain('solutionTypes')
        expect(hookSource).toContain('fetchSolutionTypes')
        expect(hookSource).toContain('createSolutionType')
        expect(hookSource).toContain('updateSolutionType')
        expect(hookSource).toContain('deleteSolutionType')
        expect(hookSource).toContain('/solution-types')
    })

    it('renders SolutionTypeTab using shared TypeCard and TypeFormModal', () => {
        const tabSource = read('src/app/modules/host-resource/components/SolutionTypeTab.tsx')

        expect(tabSource).toContain("import TypeCard from './TypeCard'")
        expect(tabSource).toContain("import TypeFormModal from './TypeFormModal'")
        expect(tabSource).toContain('SolutionType')
        expect(tabSource).toContain('hr-type-def-grid')
    })

    it('registers solution-types tab in HostResourcePage', () => {
        const pageSource = read('src/app/modules/host-resource/pages/HostResourcePage.tsx')

        expect(pageSource).toContain("'solution-types'")
        expect(pageSource).toContain('tabSolutionTypes')
        expect(pageSource).toContain('useSolutionTypes')
        expect(pageSource).toContain('SolutionTypeTab')
        expect(pageSource).toContain("import SolutionTypeTab from '../components/SolutionTypeTab'")
        expect(pageSource).toContain("import { useSolutionTypes } from '../hooks/useSolutionTypes'")
    })

    it('passes solutionTypes to ClusterTypeTab and SopsTab', () => {
        const pageSource = read('src/app/modules/host-resource/pages/HostResourcePage.tsx')

        expect(pageSource).toContain('solutionTypes={solutionTypesHook.solutionTypes}')
        expect(pageSource).toContain('solutionTypes={solutionTypesHook.solutionTypes}')
    })

    it('adds solution filter bar and solutionType form field to ClusterTypeTab', () => {
        const tabSource = read('src/app/modules/host-resource/components/ClusterTypeTab.tsx')

        expect(tabSource).toContain('solutionTypes: SolutionType[]')
        expect(tabSource).toContain('solutionFilter')
        expect(tabSource).toContain('hr-solution-filter-bar')
        expect(tabSource).toContain('hr-solution-filter-pill')
        expect(tabSource).toContain("solutionType: 'universal'")
        expect(tabSource).toContain('filterAll')
        expect(tabSource).toContain('filterUniversal')
    })

    it('simplifies SopsTab to natural language only with targetSolution', () => {
        const tabSource = read('src/app/modules/host-resource/components/SopsTab.tsx')

        expect(tabSource).toContain('solutionTypes: SolutionType[]')
        expect(tabSource).toContain('targetSolution')
        expect(tabSource).toContain('hr-solution-filter-bar')
        expect(tabSource).not.toContain("mode === 'structured'")
        expect(tabSource).not.toContain('useClusterTypes')
        expect(tabSource).not.toContain('sopTags')
    })

    it('keeps solution type i18n keys aligned in both locales', () => {
        const en = JSON.parse(read('src/i18n/en.json'))
        const zh = JSON.parse(read('src/i18n/zh.json'))

        const solutionKeys = [
            'tabSolutionTypes',
            'createSolutionType',
            'editSolutionType',
            'noSolutionTypes',
            'searchSolutionTypes',
            'confirmDeleteSolutionType',
            'filterAll',
            'filterUniversal',
            'solutionType',
            'universal',
            'targetSolution',
        ]

        for (const key of solutionKeys) {
            expect(en.hostResource[key]).toBeDefined()
            expect(zh.hostResource[key]).toBeDefined()
        }

        expect(en.hostResource.tabSolutionTypes).toBe('Solution Types')
        expect(zh.hostResource.tabSolutionTypes).toBe('解决方案类型')
        expect(en.hostResource.filterAll).toBe('All')
        expect(zh.hostResource.filterAll).toBe('全部')
        expect(en.hostResource.universal).toBe('Universal')
        expect(zh.hostResource.universal).toBe('通用')
    })

    it('provides CSS styles for solution filter bar', () => {
        const cssSource = read('src/app/modules/host-resource/styles/host-resource.css')

        expect(cssSource).toContain('.hr-solution-filter-bar')
        expect(cssSource).toContain('.hr-solution-filter-pill')
        expect(cssSource).toContain('.hr-solution-filter-pill.active')
    })
})
