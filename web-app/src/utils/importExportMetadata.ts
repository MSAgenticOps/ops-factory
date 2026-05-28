import { FieldMetadata, ResourceImportMetadata } from '../types/importExport'
import type { ImportType } from '../types/importExport'

export const IMPORT_METADATA: Record<ImportType, ResourceImportMetadata> = {
    ClusterTypes: {
        resourceType: 'ClusterTypes',
        sheetName: 'Cluster Types',
        descriptionSheetName: '字段说明',
        fields: [
            { name: 'name', required: true, description: '集群类型名称', validation: { type: 'string', maxLength: 100 } },
            { name: 'code', required: true, description: '集群类型代码', validation: { type: 'string', maxLength: 50 } },
            { name: 'description', required: false, description: '描述', validation: { type: 'string', maxLength: 500 } },
            { name: 'knowledge', required: false, description: '知识库', validation: { type: 'string' } },
            { name: 'commandPrefix', required: false, description: '命令前缀', validation: { type: 'string' } },
            { name: 'envVariables', required: false, description: '环境变量（分号分隔）', validation: { type: 'array', separator: ';' } },
        ],
        sampleData: [
            {
                name: 'Kubernetes Cluster',
                code: 'k8s',
                description: 'Kubernetes 容器集群',
                knowledge: 'container-orchestration',
                commandPrefix: 'kubectl',
                envVariables: 'KUBECONFIG=/etc/kubernetes/config;CLUSTER_NAME=prod',
            },
        ],
    },
    BusinessTypes: {
        resourceType: 'BusinessTypes',
        sheetName: 'Business Types',
        descriptionSheetName: '字段说明',
        fields: [
            { name: 'name', required: true, description: '业务类型名称', validation: { type: 'string', maxLength: 100 } },
            { name: 'code', required: true, description: '业务类型代码', validation: { type: 'string', maxLength: 50 } },
            { name: 'description', required: false, description: '描述', validation: { type: 'string', maxLength: 500 } },
            { name: 'knowledge', required: false, description: '知识库', validation: { type: 'string' } },
        ],
        sampleData: [
            {
                name: 'Web Application',
                code: 'web-app',
                description: 'Web 应用服务',
                knowledge: 'web-services',
            },
        ],
    },
    HostGroups: {
        resourceType: 'HostGroups',
        sheetName: 'Host Groups',
        descriptionSheetName: '字段说明',
        fields: [
            { name: 'name', required: true, description: '主机组名称', validation: { type: 'string', maxLength: 100 } },
            { name: 'code', required: false, description: '主机组代码', validation: { type: 'string', maxLength: 50 } },
            { name: 'parentGroup', required: false, description: '父主机组', validation: { type: 'string' } },
            { name: 'description', required: false, description: '描述', validation: { type: 'string', maxLength: 500 } },
        ],
        sampleData: [
            {
                name: 'Production Servers',
                code: 'prod-servers',
                parentGroup: 'Data Center A',
                description: '生产环境服务器组',
            },
            {
                name: 'Backup Servers',
                code: 'backup-servers',
                parentGroup: 'Data Center A',
                description: '备份服务器组',
            },
        ],
    },
    Clusters: {
        resourceType: 'Clusters',
        sheetName: 'Clusters',
        descriptionSheetName: '字段说明',
        fields: [
            { name: 'name', required: true, description: '集群名称', validation: { type: 'string', maxLength: 100 } },
            { name: 'type', required: true, description: '集群类型', validation: { type: 'string' } },
            { name: 'purpose', required: false, description: '用途', validation: { type: 'string', maxLength: 500 } },
            { name: 'group', required: false, description: '所属主机组', validation: { type: 'string' } },
            { name: 'description', required: false, description: '描述', validation: { type: 'string', maxLength: 500 } },
        ],
        sampleData: [
            {
                name: 'Web Cluster 01',
                type: 'Kubernetes',
                purpose: 'Web 应用服务',
                group: 'Production Servers',
                description: '生产环境 Web 集群',
            },
            {
                name: 'Database Cluster 01',
                type: 'MySQL',
                purpose: '数据库服务',
                group: 'Production Servers',
                description: '生产环境数据库集群',
            },
        ],
    },
    Hosts: {
        resourceType: 'Hosts',
        sheetName: 'Hosts',
        descriptionSheetName: '字段说明',
        fields: [
            { name: 'name', required: true, description: '主机名称', validation: { type: 'string', maxLength: 100 } },
            { name: 'hostname', required: false, description: '系统主机名', validation: { type: 'string', maxLength: 255 } },
            { name: 'ip', required: true, description: 'SSH IP 地址', validation: { type: 'ip' } },
            { name: 'businessIp', required: false, description: '业务 IP 地址', validation: { type: 'ip' } },
            { name: 'port', required: false, description: '端口', validation: { type: 'number' } },
            { name: 'os', required: false, description: '操作系统', validation: { type: 'string' } },
            { name: 'location', required: false, description: '位置', validation: { type: 'string' } },
            { name: 'username', required: true, description: '用户名', validation: { type: 'custom', customValidator: (value: string) => {
                if (!value) return { valid: false, error: 'Username is required' }
                if (!/^[\x00-\x7F]*$/.test(value)) {
                    return { valid: false, error: 'Username must contain only ASCII characters' }
                }
                return { valid: true }
            } } },
            { name: 'authType', required: false, description: '认证类型', validation: { type: 'enum', enumValues: ['password', 'key'] } },
            { name: 'credential', required: false, description: '凭证', validation: { type: 'custom', customValidator: (value: string) => {
                if (value && value !== '***' && !/^[\x00-\x7F]*$/.test(value)) {
                    return { valid: false, error: 'Credential must contain only ASCII characters' }
                }
                return { valid: true }
            } } },
            { name: 'business', required: false, description: '业务', validation: { type: 'string' } },
            { name: 'cluster', required: false, description: '所属集群', validation: { type: 'string' } },
            { name: 'purpose', required: false, description: '用途', validation: { type: 'string' } },
            { name: 'role', required: false, description: '角色', validation: { type: 'enum', enumValues: ['primary', 'backup'] } },
            { name: 'tags', required: false, description: '标签（分号分隔）', validation: { type: 'array', separator: ';' } },
            { name: 'description', required: false, description: '描述', validation: { type: 'string', maxLength: 500 } },
        ],
        sampleData: [
            {
                name: 'Web Server 01',
                hostname: 'web-01.example.com',
                ip: '192.168.1.10',
                businessIp: '10.0.1.10',
                port: '22',
                os: 'CentOS 7.9',
                location: '机房A-机柜01',
                username: 'opsuser',
                authType: 'key',
                credential: '***',
                business: 'Web应用',
                cluster: 'Web Cluster 01',
                purpose: 'Web服务',
                role: 'primary',
                tags: 'web;production',
                description: '生产环境 Web 服务器',
            },
            {
                name: 'DB Server 01',
                hostname: 'db-01.example.com',
                ip: '192.168.1.20',
                businessIp: '10.0.1.20',
                port: '22',
                os: 'Ubuntu 20.04',
                location: '机房A-机柜02',
                username: 'dbadmin',
                authType: 'password',
                credential: '***',
                business: 'Database',
                cluster: 'Database Cluster 01',
                purpose: '数据库服务',
                role: 'primary',
                tags: 'database;production',
                description: '生产环境数据库服务器',
            },
        ],
    },
    BusinessServices: {
        resourceType: 'BusinessServices',
        sheetName: 'Business Services',
        descriptionSheetName: '字段说明',
        fields: [
            { name: 'name', required: true, description: '业务服务名称', validation: { type: 'string', maxLength: 100 } },
            { name: 'code', required: true, description: '业务服务代码', validation: { type: 'string', maxLength: 50 } },
            { name: 'group', required: false, description: '所属主机组', validation: { type: 'string' } },
            { name: 'businessType', required: false, description: '业务类型', validation: { type: 'string' } },
            { name: 'description', required: false, description: '描述', validation: { type: 'string', maxLength: 500 } },
            { name: 'tags', required: false, description: '标签（分号分隔）', validation: { type: 'array', separator: ';' } },
            { name: 'priority', required: false, description: '优先级', validation: { type: 'string' } },
            { name: 'contactInfo', required: false, description: '联系方式', validation: { type: 'string' } },
        ],
        sampleData: [
            {
                name: '订单服务',
                code: 'order-service',
                group: 'Production Servers',
                businessType: 'Web Application',
                description: '订单处理服务',
                tags: 'core;payment',
                priority: 'P1',
                contactInfo: 'ops-team@example.com',
            },
            {
                name: '用户服务',
                code: 'user-service',
                group: 'Production Servers',
                businessType: 'Web Application',
                description: '用户管理服务',
                tags: 'core;auth',
                priority: 'P1',
                contactInfo: 'user-team@example.com',
            },
        ],
    },
    Relations: {
        resourceType: 'Relations',
        sheetName: 'Relations',
        descriptionSheetName: '字段说明',
        fields: [
            { name: 'sourceNode', required: true, description: '源节点', validation: { type: 'string' } },
            { name: 'destNode', required: true, description: '目标节点', validation: { type: 'string' } },
            { name: 'description', required: false, description: '描述', validation: { type: 'string', maxLength: 500 } },
        ],
        sampleData: [
            {
                sourceNode: 'Web Server 01',
                destNode: 'DB Server 01',
                description: 'Web 应用访问数据库',
            },
            {
                sourceNode: 'Web Server 01',
                destNode: 'Cache Server 01',
                description: 'Web 应用访问缓存服务',
            },
        ],
    },
    SOPs: {
        resourceType: 'SOPs',
        sheetName: 'SOPs',
        descriptionSheetName: '字段说明',
        fields: [
            { name: 'name', required: true, description: 'SOP 名称', validation: { type: 'string', maxLength: 100 } },
            { name: 'description', required: false, description: '描述', validation: { type: 'string', maxLength: 500 } },
            { name: 'version', required: false, description: '版本号', validation: { type: 'string' } },
            { name: 'triggerCondition', required: false, description: '触发条件', validation: { type: 'string', maxLength: 500 } },
            { name: 'enabled', required: false, description: '是否启用', validation: { type: 'enum', enumValues: ['true', 'false'] } },
            { name: 'mode', required: false, description: '模式', validation: { type: 'enum', enumValues: ['structured', 'natural_language'] } },
            { name: 'stepsDescription', required: false, description: '步骤描述', validation: { type: 'string', maxLength: 1000 } },
            { name: 'tags', required: false, description: '标签（分号分隔）', validation: { type: 'array', separator: ';' } },
        ],
        sampleData: [
            {
                name: 'Server Restart',
                description: 'Regularly restart servers to free resources',
                version: 'v1.0',
                triggerCondition: 'Memory usage over 90%',
                enabled: 'true',
                mode: 'structured',
                stepsDescription: '1.Check current memory usage;2.Notify相关人员;3.Execute restart;4.Verify service recovery',
                tags: 'restart;ops',
            },
            {
                name: 'Log Cleanup',
                description: 'Regularly clean up log files',
                version: 'v1.1',
                triggerCondition: 'Disk usage over 80%',
                enabled: 'true',
                mode: 'structured',
                stepsDescription: '1.Check log directory;2.Delete logs older than 7 days;3.Verify disk space',
                tags: 'cleanup;ops',
            },
        ],
    },
    Whitelist: {
        resourceType: 'Whitelist',
        sheetName: 'Whitelist',
        descriptionSheetName: '字段说明',
        fields: [
            { name: 'pattern', required: true, description: '命令模式', validation: { type: 'regex', pattern: '^[a-zA-Z0-9_\\-./\\s]+$' } },
            { name: 'description', required: false, description: '描述', validation: { type: 'string', maxLength: 500 } },
            { name: 'enabled', required: false, description: '是否启用', validation: { type: 'enum', enumValues: ['true', 'false'] } },
        ],
        sampleData: [
            { pattern: 'ls -la', description: 'List files', enabled: 'true' },
            { pattern: 'ps aux', description: 'View processes', enabled: 'true' },
            { pattern: 'cat /var/log/syslog', description: 'View logs', enabled: 'false' },
        ],
    },
}

export function getValidationRuleDescription(validation: FieldMetadata['validation']): string {
    if (!validation) return ''
    const { type, maxLength, minLength, enumValues, pattern, separator } = validation
    const rules: string[] = []

    if (type === 'string') {
        rules.push('字符串')
        if (maxLength) rules.push(`最大${maxLength}字符`)
        if (minLength) rules.push(`最小${minLength}字符`)
    } else if (type === 'number') {
        rules.push('数字')
    } else if (type === 'boolean') {
        rules.push('布尔值')
    } else if (type === 'enum') {
        rules.push(`枚举值：${enumValues?.join('/')}`)
    } else if (type === 'ip') {
        rules.push('IP地址格式')
    } else if (type === 'regex') {
        rules.push(`正则格式：${pattern}`)
    } else if (type === 'array') {
        rules.push(`用${separator}分隔多个值`)
    } else if (type === 'custom') {
        rules.push('自定义验证')
    }

    return rules.join('，')
}

export function getRequiredLabel(required: boolean): string {
    return required ? '必填' : '可选'
}

export function getExcelColumn(index: number): string {
    let column = ''
    while (index >= 0) {
        column = String.fromCharCode(65 + (index % 26)) + column
        index = Math.floor(index / 26) - 1
    }
    return column
}