import { useState, useCallback } from 'react'
import { csvToObjects } from '../../../../utils/csvExport'
import type { HostGroup, Cluster, Host, HostCreateRequest, BusinessService, ClusterType, BusinessType, HostRelation } from '../../../../types/host'
import type { SopCreateRequest } from '../../../../types/sop'
import type { WhitelistCommand } from '../../../../types/commandWhitelist'

export type ImportType =
    | 'ClusterTypes'
    | 'BusinessTypes'
    | 'HostGroups'
    | 'Clusters'
    | 'Hosts'
    | 'BusinessServices'
    | 'Relations'
    | 'SOPs'
    | 'Whitelist'

export interface ImportError {
    row: number
    message: string
}

export interface ImportProgress {
    current: number
    total: number
    phase: string
}

export interface ImportResult {
    success: number
    failed: number
    errors: ImportError[]
}

interface ImportDeps {
    // Lookups
    fetchGroups: () => Promise<void>
    fetchAllClusters: () => Promise<void>
    fetchAllHosts: () => Promise<void>
    fetchHostRelations: () => Promise<void>
    fetchBusinessServices: () => Promise<void>
    fetchGraph: (clusterId?: string, groupId?: string) => Promise<void>
    fetchWhitelist: () => Promise<void>

    groups: HostGroup[]
    clusters: Cluster[]
    allHosts: Host[]
    businessServices: BusinessService[]
    relations: HostRelation[]
    clusterTypes: ClusterType[]
    businessTypes: BusinessType[]

    // Create functions
    createGroup: (body: Partial<HostGroup>) => Promise<HostGroup>
    updateGroup: (id: string, body: Partial<HostGroup>) => Promise<HostGroup>
    createCluster: (body: Partial<Cluster>) => Promise<Cluster>
    createHost: (body: HostCreateRequest) => Promise<Host>
    createBusinessService: (body: Partial<BusinessService>) => Promise<BusinessService>
    createRelation: (body: Partial<import('../../../../types/host').HostRelation>) => Promise<unknown>
    createClusterType: (body: Partial<ClusterType>) => Promise<unknown>
    createBusinessType: (body: Partial<BusinessType>) => Promise<unknown>
    createSop: (body: SopCreateRequest) => Promise<unknown>
    addWhitelistCommand: (cmd: WhitelistCommand) => Promise<boolean>
}

interface ImportContext {
    groupNameToId: Map<string, string>
    groupCodeToId: Map<string, string>
    clusterNameToId: Map<string, string>
    clusterTypeNameToCode: Map<string, string>
    businessTypeNameToId: Map<string, string>
    hostNameToId: Map<string, string>
    bsNameToId: Map<string, string>
    existingRelationKeys: Set<string>
    createdClusterTypeNames: Set<string>
    createdBusinessTypeNames: Set<string>
    createdSopNames: Set<string>
    createdPatterns: Set<string>
}

function buildImportContext(deps: ImportDeps): ImportContext {
    return {
        groupNameToId: new Map(deps.groups.map(g => [g.name, g.id])),
        groupCodeToId: new Map(deps.groups.map(g => [g.code ?? '', g.id])),
        clusterNameToId: new Map(deps.clusters.map(c => [c.name, c.id])),
        clusterTypeNameToCode: new Map(deps.clusterTypes.map(ct => [ct.name, ct.code])),
        businessTypeNameToId: new Map(deps.businessTypes.map(bt => [bt.name, bt.id])),
        hostNameToId: new Map(deps.allHosts.map(h => [h.name, h.id])),
        bsNameToId: new Map(deps.businessServices.map(bs => [bs.name, bs.id])),
        existingRelationKeys: new Set(deps.relations.map(r => `${r.sourceHostId}->${r.targetHostId}`)),
        createdClusterTypeNames: new Set(deps.clusterTypes.map(ct => ct.name)),
        createdBusinessTypeNames: new Set(deps.businessTypes.map(bt => bt.name)),
        createdSopNames: new Set<string>(),
        createdPatterns: new Set<string>(),
    }
}

function parseEnvVariables(raw: string | undefined) {
    if (!raw) return undefined
    return raw.split(';').filter(Boolean).map(pair => {
        const eq = pair.indexOf('=')
        return { key: eq > 0 ? pair.slice(0, eq) : pair, value: eq > 0 ? pair.slice(eq + 1) : '' }
    })
}

async function importHost(row: Record<string, string>, ctx: ImportContext, deps: ImportDeps): Promise<string | null> {
    if (ctx.hostNameToId.has(row.name)) return `Host "${row.name}" already exists`
    const clusterId = row.cluster ? ctx.clusterNameToId.get(row.cluster) : undefined
    if (!clusterId && row.cluster) return `Cluster "${row.cluster}" not found`
    const roleValue = row.role as string | undefined
    const created = await deps.createHost({
        name: row.name, ip: row.ip, port: row.port ? parseInt(row.port, 10) : 22,
        username: row.username, authType: (row.authtype === 'key' ? 'key' : 'password') as 'password' | 'key',
        credential: row.credential || '', hostname: row.hostname || undefined, businessIp: row.businessip || undefined,
        os: row.os || undefined, location: row.location || undefined, business: row.business || undefined,
        clusterId, purpose: row.purpose || undefined,
        role: (roleValue === 'primary' || roleValue === 'backup') ? roleValue : undefined,
        tags: row.tags ? row.tags.split(';').map(t => t.trim()).filter(Boolean) : [],
        description: row.description || undefined,
    })
    ctx.hostNameToId.set(row.name, created.id)
    return null
}

async function importBusinessService(row: Record<string, string>, ctx: ImportContext, deps: ImportDeps): Promise<string | null> {
    if (ctx.bsNameToId.has(row.name)) return `Business service "${row.name}" already exists`
    const groupId = row.group ? (ctx.groupNameToId.get(row.group) || ctx.groupCodeToId.get(row.group)) : undefined
    const businessTypeId = row.businesstype ? ctx.businessTypeNameToId.get(row.businesstype) : undefined
    if (!groupId && row.group) return `Group "${row.group}" not found`
    const created = await deps.createBusinessService({
        name: row.name, code: row.code, groupId, businessTypeId, description: row.description || '',
        tags: row.tags ? row.tags.split(';').map(t => t.trim()).filter(Boolean) : [],
        priority: row.priority || '', contactInfo: row.contactinfo || '',
    })
    ctx.bsNameToId.set(row.name, created.id)
    return null
}

async function importRow(
    type: ImportType, row: Record<string, string>, ctx: ImportContext, deps: ImportDeps,
): Promise<string | null> {
    switch (type) {
        case 'ClusterTypes': {
            if (ctx.createdClusterTypeNames.has(row.name)) return `Cluster type "${row.name}" already exists`
            await deps.createClusterType({
                name: row.name, code: row.code, description: row.description || '',
                knowledge: row.knowledge || '', commandPrefix: row.commandprefix || '',
                envVariables: parseEnvVariables(row.envvariables),
            })
            ctx.createdClusterTypeNames.add(row.name)
            return null
        }
        case 'BusinessTypes': {
            if (ctx.createdBusinessTypeNames.has(row.name)) return `Business type "${row.name}" already exists`
            await deps.createBusinessType({
                name: row.name, code: row.code, description: row.description || '', knowledge: row.knowledge || '',
            })
            ctx.createdBusinessTypeNames.add(row.name)
            return null
        }
        case 'HostGroups': {
            if (ctx.groupNameToId.has(row.name)) return `Group "${row.name}" already exists`
            const created = await deps.createGroup({ name: row.name, code: row.code || undefined, description: row.description || '' })
            ctx.groupNameToId.set(row.name, created.id)
            if (row.code) ctx.groupCodeToId.set(row.code, created.id)
            return null
        }
        case 'Clusters': {
            if (ctx.clusterNameToId.has(row.name)) return `Cluster "${row.name}" already exists`
            const groupId = row.group ? (ctx.groupNameToId.get(row.group) || ctx.groupCodeToId.get(row.group)) : undefined
            if (!groupId && row.group) return `Group "${row.group}" not found`
            const typeCode = row.type ? (ctx.clusterTypeNameToCode.get(row.type) || row.type) : ''
            const created = await deps.createCluster({ name: row.name, type: typeCode, purpose: row.purpose || '', groupId, description: row.description || '' })
            ctx.clusterNameToId.set(row.name, created.id)
            return null
        }
        case 'Hosts': return importHost(row, ctx, deps)
        case 'BusinessServices': return importBusinessService(row, ctx, deps)
        case 'Relations': {
            const sourceBsId = ctx.bsNameToId.get(row.sourcenode)
            const sourceHostId = ctx.hostNameToId.get(row.sourcenode)
            const destHostId = ctx.hostNameToId.get(row.destnode)
            if (!destHostId) return `Target host "${row.destnode}" not found`
            if (!sourceBsId && !sourceHostId) return `Source node "${row.sourcenode}" not found as host or business service`
            const relationKey = `${sourceBsId || sourceHostId}->${destHostId}`
            if (ctx.existingRelationKeys.has(relationKey)) return `Relation "${row.sourcenode}" -> "${row.destnode}" already exists`
            ctx.existingRelationKeys.add(relationKey)
            await deps.createRelation({
                sourceHostId: sourceBsId || sourceHostId!, targetHostId: destHostId,
                description: row.description || '', sourceType: sourceBsId ? 'business-service' : 'host',
            })
            return null
        }
        case 'SOPs': {
            if (ctx.createdSopNames.has(row.name)) return `SOP "${row.name}" already exists`
            await deps.createSop({
                name: row.name, description: row.description || '', version: row.version || '',
                triggerCondition: row.triggercondition || '', enabled: row.enabled !== 'false',
                mode: (row.mode === 'natural_language' ? 'natural_language' : 'structured') as 'structured' | 'natural_language',
                stepsDescription: row.stepsdescription || '',
                tags: row.tags ? row.tags.split(';').map(t => t.trim()).filter(Boolean) : [],
            })
            ctx.createdSopNames.add(row.name)
            return null
        }
        case 'Whitelist': {
            if (ctx.createdPatterns.has(row.pattern)) return `Whitelist pattern "${row.pattern}" already exists`
            await deps.addWhitelistCommand({ pattern: row.pattern, description: row.description || '', enabled: row.enabled !== 'false' })
            ctx.createdPatterns.add(row.pattern)
            return null
        }
        default: return null
    }
}

async function updateParentGroups(
    rows: Record<string, string>[], ctx: ImportContext, deps: ImportDeps, errors: ImportError[],
) {
    const parentRows = rows.filter(r => r.parentgroup)
    for (let i = 0; i < parentRows.length; i++) {
        const row = parentRows[i]
        const groupId = ctx.groupNameToId.get(row.name)
        const parentId = ctx.groupNameToId.get(row.parentgroup) || ctx.groupCodeToId.get(row.parentgroup)
        if (groupId && parentId) {
            try { await deps.updateGroup(groupId, { parentId }) }
            catch (err) { errors.push({ row: i + 1, message: `Failed to set parent: ${err instanceof Error ? err.message : String(err)}` }) }
        }
    }
}

async function refreshAllData(deps: ImportDeps) {
    try {
        await Promise.all([
            deps.fetchGroups(), deps.fetchAllClusters(), deps.fetchAllHosts(),
            deps.fetchHostRelations(), deps.fetchBusinessServices(), deps.fetchGraph(), deps.fetchWhitelist(),
        ])
    } catch { /* non-critical refresh failure */ }
}

export function useResourceImport(deps: ImportDeps) {
    const [importing, setImporting] = useState(false)
    const [progress, setProgress] = useState<ImportProgress | null>(null)

    const importCsv = useCallback(async (type: ImportType, csvText: string): Promise<ImportResult> => {
        const rows = csvToObjects(csvText)
        if (rows.length === 0) return { success: 0, failed: 0, errors: [{ row: 0, message: 'No data rows found' }] }

        setImporting(true)
        setProgress({ current: 0, total: rows.length, phase: type })
        const ctx = buildImportContext(deps)
        const errors: ImportError[] = []
        let success = 0

        // Build lookup maps from current data
        const groupNameToId = new Map(deps.groups.map(g => [g.name, g.id]))
        const groupCodeToId = new Map(deps.groups.map(g => [g.code ?? '', g.id]))
        const clusterNameToId = new Map(deps.clusters.map(c => [c.name, c.id]))
        const clusterTypeNameSet = new Set(deps.clusterTypes.map(ct => ct.name))
        const clusterTypeCodeToName = new Map(deps.clusterTypes.map(ct => [ct.code, ct.name]))
        const businessTypeNameToId = new Map(deps.businessTypes.map(bt => [bt.name, bt.id]))
        const hostNameToId = new Map(deps.allHosts.map(h => [h.name, h.id]))
        const bsNameToId = new Map(deps.businessServices.map(bs => [bs.name, bs.id]))

        for (let i = 0; i < rows.length; i++) {
            setProgress({ current: i + 1, total: rows.length, phase: type })
            try {
                switch (type) {
                    case 'ClusterTypes': {
                        await deps.createClusterType({
                            name: row.name,
                            code: row.code,
                            description: row.description || '',
                            knowledge: row.knowledge || '',
                            commandPrefix: row.commandprefix || '',
                            envVariables: row.envvariables
                                ? row.envvariables.split(';').filter(Boolean).map(pair => {
                                    const eq = pair.indexOf('=')
                                    return { key: eq > 0 ? pair.slice(0, eq) : pair, value: eq > 0 ? pair.slice(eq + 1) : '' }
                                })
                                : undefined,
                        })
                        success++
                        break
                    }

                    case 'BusinessTypes': {
                        await deps.createBusinessType({
                            name: row.name,
                            code: row.code,
                            description: row.description || '',
                            knowledge: row.knowledge || '',
                        })
                        success++
                        break
                    }

                    case 'HostGroups': {
                        const created = await deps.createGroup({
                            name: row.name,
                            code: row.code || undefined,
                            description: row.description || '',
                        })
                        groupNameToId.set(row.name, created.id)
                        if (row.code) groupCodeToId.set(row.code, created.id)
                        success++
                        break
                    }

                    case 'Clusters': {
                        const groupId = row.group
                            ? (groupNameToId.get(row.group) || groupCodeToId.get(row.group))
                            : undefined
                        let typeName = row.type || ''
                        if (typeName && !clusterTypeNameSet.has(typeName)) {
                            typeName = clusterTypeCodeToName.get(typeName) || typeName
                        }
                        if (!groupId && row.group) {
                            errors.push({ row: i + 1, message: `Group "${row.group}" not found` })
                            continue
                        }
                        const created = await deps.createCluster({
                            name: row.name,
                            type: typeName,
                            purpose: row.purpose || '',
                            groupId,
                            description: row.description || '',
                        })
                        clusterNameToId.set(row.name, created.id)
                        success++
                        break
                    }

                    case 'Hosts': {
                        const clusterId = row.cluster
                            ? clusterNameToId.get(row.cluster)
                            : undefined
                        if (!clusterId && row.cluster) {
                            errors.push({ row: i + 1, message: `Cluster "${row.cluster}" not found` })
                            continue
                        }
                        const created = await deps.createHost({
                            name: row.name,
                            ip: row.ip,
                            port: row.port ? parseInt(row.port, 10) : 22,
                            username: row.username,
                            authType: (row.authtype === 'key' ? 'key' : 'password') as 'password' | 'key',
                            credential: row.credential || '',
                            hostname: row.hostname || undefined,
                            businessIp: row.businessip || undefined,
                            os: row.os || undefined,
                            location: row.location || undefined,
                            business: row.business || undefined,
                            clusterId,
                            purpose: row.purpose || undefined,
                            tags: row.tags ? row.tags.split(';').map(t => t.trim()).filter(Boolean) : [],
                            description: row.description || undefined,
                        })
                        hostNameToId.set(row.name, created.id)
                        success++
                        break
                    }

                    case 'BusinessServices': {
                        const groupId = row.group
                            ? (groupNameToId.get(row.group) || groupCodeToId.get(row.group))
                            : undefined
                        const businessTypeId = row.businesstype
                            ? businessTypeNameToId.get(row.businesstype)
                            : undefined
                        if (!groupId && row.group) {
                            errors.push({ row: i + 1, message: `Group "${row.group}" not found` })
                            continue
                        }
                        const created = await deps.createBusinessService({
                            name: row.name,
                            code: row.code,
                            groupId,
                            businessTypeId,
                            description: row.description || '',
                            tags: row.tags ? row.tags.split(';').map(t => t.trim()).filter(Boolean) : [],
                            priority: row.priority || '',
                            contactInfo: row.contactinfo || '',
                        })
                        bsNameToId.set(row.name, created.id)
                        success++
                        break
                    }

                    case 'Relations': {
                        const sourceBsId = bsNameToId.get(row.sourcenode)
                        const sourceHostId = hostNameToId.get(row.sourcenode)
                        const destHostId = hostNameToId.get(row.destnode)
                        if (!destHostId) {
                            errors.push({ row: i + 1, message: `Target host "${row.destnode}" not found` })
                            continue
                        }
                        if (!sourceBsId && !sourceHostId) {
                            errors.push({ row: i + 1, message: `Source node "${row.sourcenode}" not found as host or business service` })
                            continue
                        }
                        await deps.createRelation({
                            sourceHostId: sourceBsId || sourceHostId!,
                            targetHostId: destHostId,
                            description: row.description || '',
                            sourceType: sourceBsId ? 'business-service' : 'host',
                        })
                        success++
                        break
                    }

                    case 'SOPs': {
                        const tags = row.tags
                            ? row.tags.split(';').map(t => t.trim()).filter(Boolean)
                            : []
                        await deps.createSop({
                            name: row.name,
                            description: row.description || '',
                            version: row.version || '',
                            triggerCondition: row.triggercondition || '',
                            enabled: row.enabled !== 'false',
                            mode: (row.mode === 'natural_language' ? 'natural_language' : 'structured') as 'structured' | 'natural_language',
                            stepsDescription: row.stepsdescription || '',
                            tags,
                        })
                        success++
                        break
                    }

                    case 'Whitelist': {
                        await deps.addWhitelistCommand({
                            pattern: row.pattern,
                            description: row.description || '',
                            enabled: row.enabled !== 'false',
                        })
                        success++
                        break
                    }
                    default:
                        break
                }
            } catch (err) {
                errors.push({ row: i + 1, message: err instanceof Error ? err.message : String(err) })
            }
        }

        if (type === 'HostGroups') await updateParentGroups(rows, ctx, deps, errors)
        await refreshAllData(deps)

        setImporting(false)
        setProgress(null)
        return { success, failed: errors.length, errors }
    }, [deps])

    return { importing, progress, importCsv }
}
