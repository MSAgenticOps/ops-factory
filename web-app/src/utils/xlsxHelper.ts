import * as XLSX from 'xlsx'
import type { ImportType } from '../types/importExport'
import { IMPORT_METADATA, getValidationRuleDescription, getRequiredLabel } from './importExportMetadata'

const MAX_CELL_LENGTH = 32767

function truncateCellValue(value: string): string {
    if (value && value.length > MAX_CELL_LENGTH) {
        return value.substring(0, MAX_CELL_LENGTH - 3) + '...'
    }
    return value || ''
}

export interface XlsxSheet {
    name: string
    data: (string | number | boolean)[][]
}

export interface XlsxParseResult {
    success: boolean
    data?: Record<string, string>[]
    error?: string
}

export function readXlsxFile(file: File): Promise<any> {
    return new Promise((resolve, reject) => {
        const reader = new FileReader()
        reader.onload = (e) => {
            try {
                const data = e.target?.result as ArrayBuffer
                const workbook = XLSX.read(data, { type: 'array' })
                resolve(workbook)
            } catch (error) {
                reject(error)
            }
        }
        reader.onerror = reject
        reader.readAsArrayBuffer(file)
    })
}

export function parseSheetToObjects(workbook: XLSX.WorkBook, sheetName: string): XlsxParseResult {
    const sheet = workbook.Sheets[sheetName]
    if (!sheet) {
        return { success: false, error: `Sheet "${sheetName}" not found` }
    }

    const jsonData = XLSX.utils.sheet_to_json(sheet, { header: 1, raw: false, defval: '' }) as (string | number | boolean)[][]

    if (jsonData.length < 2) {
        return { success: false, error: 'Sheet must have at least a header row and one data row' }
    }

    const headers = (jsonData[0] as (string | number | boolean)[]).map(h => String(h))
    const rows = jsonData.slice(1).map((row) => {
        const obj: Record<string, string> = {}
        headers.forEach((header, index) => {
            const value = row[index] !== undefined ? String(row[index]) : ''
            obj[header] = value
        })
        return obj
    })

    return { success: true, data: rows }
}

export function generateSampleXlsx(importType: ImportType): XLSX.WorkBook {
    const metadata = IMPORT_METADATA[importType]
    const workbook = XLSX.utils.book_new()

    const descriptionData: (string | number | boolean)[][] = [
        ['字段名称', '可选必选', '校验规则', '描述'],
    ]

    metadata.fields.forEach((field) => {
        descriptionData.push([
            field.name,
            getRequiredLabel(field.required),
            getValidationRuleDescription(field.validation),
            field.description || '',
        ])
    })

    const descriptionSheet = XLSX.utils.aoa_to_sheet(descriptionData)
    XLSX.utils.book_append_sheet(workbook, descriptionSheet, metadata.descriptionSheetName)

    const sampleData = metadata.sampleData || []
    const sheetData: (string | number | boolean)[][] = []
    if (sampleData.length > 0) {
        sheetData.push(Object.keys(sampleData[0]))
        sampleData.forEach((row) => {
            sheetData.push(Object.values(row))
        })
    } else {
        sheetData.push(metadata.fields.map((f) => f.name))
    }

    const dataSheet = XLSX.utils.aoa_to_sheet(sheetData)
    XLSX.utils.book_append_sheet(workbook, dataSheet, metadata.sheetName)

    return workbook
}

export function downloadWorkbook(workbook: XLSX.WorkBook, filename: string): void {
    XLSX.writeFile(workbook, filename)
}

export function generateExportXlsx(
    importType: ImportType,
    data: Record<string, string>[]
): XLSX.WorkBook {
    const metadata = IMPORT_METADATA[importType]
    const workbook = XLSX.utils.book_new()

    const descriptionData: (string | number | boolean)[][] = [
        ['字段名称', '可选必选', '校验规则', '描述'],
    ]

    metadata.fields.forEach((field) => {
        descriptionData.push([
            field.name,
            getRequiredLabel(field.required),
            getValidationRuleDescription(field.validation),
            field.description || '',
        ])
    })

    const descriptionSheet = XLSX.utils.aoa_to_sheet(descriptionData)
    XLSX.utils.book_append_sheet(workbook, descriptionSheet, metadata.descriptionSheetName)

    if (data.length > 0) {
        const sheetData: (string | number | boolean)[][] = []
        sheetData.push(Object.keys(data[0]))
        data.forEach((row) => {
            sheetData.push(Object.values(row).map(v => truncateCellValue(String(v))))
        })
        const dataSheet = XLSX.utils.aoa_to_sheet(sheetData)
        XLSX.utils.book_append_sheet(workbook, dataSheet, metadata.sheetName)
    }

    return workbook
}

export function generateMultiSheetExportXlsx(
    sheets: { name: string; data: Record<string, string>[]; metadata: ImportType }[]
): XLSX.WorkBook {
    const workbook = XLSX.utils.book_new()

    sheets.forEach((sheetInfo) => {
        const metadata = IMPORT_METADATA[sheetInfo.metadata]

        const descriptionData: (string | number | boolean)[][] = [
            ['字段名称', '可选必选', '校验规则', '描述'],
        ]

        metadata.fields.forEach((field) => {
            descriptionData.push([
                field.name,
                getRequiredLabel(field.required),
                getValidationRuleDescription(field.validation),
                field.description || '',
            ])
        })

        const descriptionSheet = XLSX.utils.aoa_to_sheet(descriptionData)
        XLSX.utils.book_append_sheet(workbook, descriptionSheet, `${metadata.descriptionSheetName}_${sheetInfo.name}`)

        if (sheetInfo.data.length > 0) {
            const sheetData: (string | number | boolean)[][] = []
            sheetData.push(Object.keys(sheetInfo.data[0]))
            sheetInfo.data.forEach((row) => {
                sheetData.push(Object.values(row).map(v => truncateCellValue(String(v))))
            })
            const dataSheet = XLSX.utils.aoa_to_sheet(sheetData)
            XLSX.utils.book_append_sheet(workbook, dataSheet, sheetInfo.name)
        }
    })

    return workbook
}