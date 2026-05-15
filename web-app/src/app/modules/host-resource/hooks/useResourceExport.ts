import { useState, useCallback } from 'react'
import { createZip } from '../../../../utils/zipHelper'
import { objectsToCsv } from '../../../../utils/csvExport'
import type { HostGroup, Cluster, Host, BusinessService, HostRelation, ClusterType, BusinessType } from '../../../../types/host'
import type { Sop } from '../../../../types/sop'
import type { WhitelistCommand } from '../../../../types/commandWhitelist'

type EnvVariable = { key: string; value: string }

interface ExportParams {
    groups: HostGroup[]
    clusters: Cluster[]
    allHosts: Host[]
    hostRelations: HostRelation[]
    businessServices: BusinessService[]
    clusterTypes: ClusterType[]
    businessTypes: BusinessType[]
    whitelistCommands: WhitelistCommand[]
    sops: Sop[]
}

const CSV_HEADERS = {
    ct: [{ key: 'name' }, { key: 'code' }, { key: 'description' }, { key: 'knowledge' }, { key: 'commandPrefix' }, { key: 'envVariables' }],
    bt: [{ key: 'name' }, { key: 'code' }, { key: 'description' }, { key: 'knowledge' }],
    group: [{ key: 'name' }, { key: 'code' }, { key: 'parentGroup' }, { key: 'description' }],
    cluster: [{ key: 'name' }, { key: 'type' }, { key: 'purpose' }, { key: 'group' }, { key: 'description' }],
    host: [{ key: 'name' }, { key: 'hostname' }, { key: 'ip' }, { key: 'businessIp' }, { key: 'port' }, { key: 'os' }, { key: 'location' }, { key: 'username' }, { key: 'authType' }, { key: 'credential' }, { key: 'business' }, { key: 'cluster' }, { key: 'purpose' }, { key: 'role' }, { key: 'tags' }, { key: 'description' }],
    bs: [{ key: 'name' }, { key: 'code' }, { key: 'group' }, { key: 'businessType' }, { key: 'description' }, { key: 'tags' }, { key: 'priority' }, { key: 'contactInfo' }],
    rel: [{ key: 'sourceNode' }, { key: 'destNode' }, { key: 'description' }],
    sop: [{ key: 'name' }, { key: 'description' }, { key: 'version' }, { key: 'triggerCondition' }, { key: 'enabled' }, { key: 'mode' }, { key: 'stepsDescription' }, { key: 'tags' }],
    wl: [{ key: 'pattern' }, { key: 'description' }, { key: 'enabled' }],
}

function buildCsvRows(p: ExportParams) {
    const groupMap = new Map(p.groups.map(g => [g.id, g]))
    const clusterMap = new Map(p.clusters.map(c => [c.id, c]))
    const businessTypeMap = new Map(p.businessTypes.map(bt => [bt.id, bt]))
    const allHostMap = new Map(p.allHosts.map(h => [h.id, h]))
    const bsMap = new Map(p.businessServices.map(bs => [bs.id, bs]))

    const ct = p.clusterTypes.map(ct => ({
        name: ct.name, code: ct.code, description: ct.description || '', knowledge: ct.knowledge || '',
        commandPrefix: ct.commandPrefix || '',
        envVariables: ct.envVariables ? (ct.envVariables as EnvVariable[]).map((v: EnvVariable) => `${v.key}=${v.value}`).join(';') : '',
    }))

    const bt = p.businessTypes.map(bt => ({
        name: bt.name, code: bt.code, description: bt.description || '', knowledge: bt.knowledge || '',
    }))

    const groups = p.groups.map(g => ({
        name: g.name, code: g.code || '', parentGroup: g.parentId ? (groupMap.get(g.parentId)?.name ?? '') : '', description: g.description || '',
    }))

    const clusters = p.clusters.map(c => ({
        name: c.name, type: c.type || '', purpose: c.purpose || '',
        group: c.groupId ? (groupMap.get(c.groupId)?.name ?? '') : '', description: c.description || '',
    }))

    const hosts = p.allHosts.map(h => ({
        name: h.name, hostname: h.hostname || '', ip: h.ip, businessIp: h.businessIp || '', port: String(h.port),
        os: h.os || '', location: h.location || '', username: h.username, authType: h.authType,
        credential: '', business: h.business || '',
        cluster: h.clusterId ? (clusterMap.get(h.clusterId)?.name ?? '') : '',
        purpose: h.purpose || '', role: h.role || '', tags: Array.isArray(h.tags) ? h.tags.join(';') : '', description: h.description || '',
    }))

    const bs = p.businessServices.map(bs => ({
        name: bs.name, code: bs.code,
        group: bs.groupId ? (groupMap.get(bs.groupId)?.name ?? '') : '',
        businessType: bs.businessTypeId ? (businessTypeMap.get(bs.businessTypeId)?.name ?? '') : '',
        description: bs.description || '', tags: Array.isArray(bs.tags) ? bs.tags.join(';') : '',
        priority: bs.priority || '', contactInfo: bs.contactInfo || '',
    }))

    // Relations: host-host + business-host
    const rels: { sourceNode: string; destNode: string; description: string }[] = []
    for (const r of p.hostRelations) {
        const src = r.sourceType === 'business-service' ? (bsMap.get(r.sourceHostId)?.name ?? '') : (allHostMap.get(r.sourceHostId)?.name ?? '')
        const dst = allHostMap.get(r.targetHostId)?.name ?? ''
        if (src && dst) rels.push({ sourceNode: src, destNode: dst, description: r.description || '' })
    }
    for (const b of p.businessServices) {
        for (const hid of b.hostIds) {
            const hname = allHostMap.get(hid)?.name
            if (hname && !rels.some(r => r.sourceNode === b.name && r.destNode === hname))
                rels.push({ sourceNode: b.name, destNode: hname, description: '' })
        }
    }

    const sops = p.sops.map(s => ({
        name: s.name, description: s.description || '', version: s.version || '',
        triggerCondition: s.triggerCondition || '', enabled: String(s.enabled ?? true),
        mode: s.mode || 'structured', stepsDescription: s.stepsDescription || '',
        tags: Array.isArray(s.tags) ? s.tags.join(';') : '',
    }))

    const wl = p.whitelistCommands.map(cmd => ({
        pattern: cmd.pattern, description: cmd.description || '', enabled: String(cmd.enabled),
    }))

    return { ct, bt, groups, clusters, hosts, bs, rels, sops, wl }
}

export function useResourceExport() {
    const [exporting, setExporting] = useState(false)

    const exportAllAsZip = useCallback(async (params: ExportParams) => {
        setExporting(true)
        try {
            const rows = buildCsvRows(params)
            const enc = new TextEncoder()
            const csv = (name: string, headers: { key: string }[], data: Record<string, unknown>[]) =>
                ({ name, data: enc.encode(objectsToCsv(headers, data)) })

            const csvFiles = [
                csv('cluster_types.csv', CSV_HEADERS.ct, rows.ct),
                csv('business_types.csv', CSV_HEADERS.bt, rows.bt),
                csv('groups.csv', CSV_HEADERS.group, rows.groups),
                csv('clusters.csv', CSV_HEADERS.cluster, rows.clusters),
                csv('hosts.csv', CSV_HEADERS.host, rows.hosts),
                csv('business_services.csv', CSV_HEADERS.bs, rows.bs),
                csv('relations.csv', CSV_HEADERS.rel, rows.rels),
                csv('sops.csv', CSV_HEADERS.sop, rows.sops),
                csv('whitelist.csv', CSV_HEADERS.wl, rows.wl),
            ]

            const manifest = {
                version: 1, exportedAt: new Date().toISOString(),
                counts: {
                    clusterTypes: params.clusterTypes.length, businessTypes: params.businessTypes.length,
                    groups: params.groups.length, clusters: params.clusters.length,
                    hosts: params.allHosts.length, businessServices: params.businessServices.length,
                    relations: rows.rels.length, sops: params.sops.length, whitelist: params.whitelistCommands.length,
                },
            }
            const manifestFile = { name: 'manifest.json', data: enc.encode(JSON.stringify(manifest, null, 2)) }

            const zipBlob = createZip([manifestFile, ...csvFiles])
            const url = URL.createObjectURL(zipBlob)
            const a = document.createElement('a')
            a.href = url
            a.download = `ops-resources-${new Date().toISOString().slice(0, 10)}.zip`
            a.click()
            URL.revokeObjectURL(url)
        } finally { setExporting(false) }
    }, [])

    return { exporting, exportAllAsZip }
}
