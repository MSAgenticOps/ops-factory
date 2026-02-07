interface ZipEntry {
    name: string
    compressionMethod: number
    compressedSize: number
    uncompressedSize: number
    localHeaderOffset: number
}

const textDecoder = new TextDecoder('utf-8')

function findEndOfCentralDirectory(view: DataView): number {
    const signature = 0x06054b50
    const minOffset = Math.max(0, view.byteLength - 0xffff - 22)
    for (let i = view.byteLength - 22; i >= minOffset; i -= 1) {
        if (view.getUint32(i, true) === signature) return i
    }
    throw new Error('Invalid zip archive: end of central directory not found')
}

function readZipEntries(buffer: ArrayBuffer): Map<string, ZipEntry> {
    const view = new DataView(buffer)
    const eocd = findEndOfCentralDirectory(view)
    const totalEntries = view.getUint16(eocd + 10, true)
    const centralDirectoryOffset = view.getUint32(eocd + 16, true)

    const entries = new Map<string, ZipEntry>()
    let offset = centralDirectoryOffset

    for (let i = 0; i < totalEntries; i += 1) {
        const signature = view.getUint32(offset, true)
        if (signature !== 0x02014b50) {
            throw new Error('Invalid zip archive: malformed central directory')
        }

        const compressionMethod = view.getUint16(offset + 10, true)
        const compressedSize = view.getUint32(offset + 20, true)
        const uncompressedSize = view.getUint32(offset + 24, true)
        const fileNameLength = view.getUint16(offset + 28, true)
        const extraFieldLength = view.getUint16(offset + 30, true)
        const commentLength = view.getUint16(offset + 32, true)
        const localHeaderOffset = view.getUint32(offset + 42, true)

        const nameStart = offset + 46
        const nameEnd = nameStart + fileNameLength
        const name = textDecoder.decode(new Uint8Array(buffer.slice(nameStart, nameEnd)))

        entries.set(name, {
            name,
            compressionMethod,
            compressedSize,
            uncompressedSize,
            localHeaderOffset,
        })

        offset = nameEnd + extraFieldLength + commentLength
    }

    return entries
}

async function inflateRaw(data: Uint8Array): Promise<Uint8Array> {
    if (typeof DecompressionStream === 'undefined') {
        throw new Error('Current browser does not support DecompressionStream')
    }

    const source = data.buffer as ArrayBuffer
    const payload = source.slice(data.byteOffset, data.byteOffset + data.byteLength)
    const stream = new Blob([payload]).stream().pipeThrough(new DecompressionStream('deflate-raw'))
    const inflated = await new Response(stream).arrayBuffer()
    return new Uint8Array(inflated)
}

async function extractEntry(buffer: ArrayBuffer, entry: ZipEntry): Promise<Uint8Array> {
    const view = new DataView(buffer)
    const localOffset = entry.localHeaderOffset
    const signature = view.getUint32(localOffset, true)

    if (signature !== 0x04034b50) {
        throw new Error(`Invalid zip archive: missing local header for ${entry.name}`)
    }

    const fileNameLength = view.getUint16(localOffset + 26, true)
    const extraFieldLength = view.getUint16(localOffset + 28, true)
    const dataStart = localOffset + 30 + fileNameLength + extraFieldLength
    const dataEnd = dataStart + entry.compressedSize
    const compressed = new Uint8Array(buffer.slice(dataStart, dataEnd))

    if (entry.compressionMethod === 0) return compressed
    if (entry.compressionMethod === 8) return inflateRaw(compressed)

    throw new Error(`Unsupported zip compression method: ${entry.compressionMethod}`)
}

async function readZipTextFile(buffer: ArrayBuffer, path: string): Promise<string | null> {
    const entries = readZipEntries(buffer)
    const entry = entries.get(path)
    if (!entry) return null
    const bytes = await extractEntry(buffer, entry)
    return textDecoder.decode(bytes)
}

function parseXml(xmlText: string): Document {
    const xml = new DOMParser().parseFromString(xmlText, 'application/xml')
    if (xml.querySelector('parsererror')) {
        throw new Error('Failed to parse xml')
    }
    return xml
}

function normalizeText(input: string): string {
    return input
        .replace(/\u00a0/g, ' ')
        .replace(/\r/g, '')
        .replace(/\n{3,}/g, '\n\n')
        .trim()
}

function columnRefToIndex(cellRef: string): number {
    const match = cellRef.match(/^[A-Z]+/i)
    if (!match) return 0

    const letters = match[0].toUpperCase()
    let result = 0
    for (let i = 0; i < letters.length; i += 1) {
        result = result * 26 + (letters.charCodeAt(i) - 64)
    }
    return Math.max(result - 1, 0)
}

function parseCsvLike(input: string, delimiter: ',' | '\t'): string[][] {
    const rows: string[][] = []
    const normalized = input.replace(/\r\n/g, '\n').replace(/\r/g, '\n')

    let row: string[] = []
    let value = ''
    let inQuote = false

    const pushCell = () => {
        row.push(value)
        value = ''
    }

    const pushRow = () => {
        if (row.length > 0 || value.length > 0) {
            pushCell()
            rows.push(row)
        }
        row = []
    }

    for (let i = 0; i < normalized.length; i += 1) {
        const char = normalized[i]

        if (char === '"') {
            const nextChar = normalized[i + 1]
            if (inQuote && nextChar === '"') {
                value += '"'
                i += 1
            } else {
                inQuote = !inQuote
            }
            continue
        }

        if (!inQuote && char === delimiter) {
            pushCell()
            continue
        }

        if (!inQuote && char === '\n') {
            pushRow()
            continue
        }

        value += char
    }

    pushRow()
    return rows
}

function toRectangularTable(rows: string[][]): string[][] {
    const maxColumns = rows.reduce((max, row) => Math.max(max, row.length), 0)
    return rows.map(row => {
        if (row.length === maxColumns) return row
        return [...row, ...new Array(maxColumns - row.length).fill('')]
    })
}

export async function extractDocxText(buffer: ArrayBuffer): Promise<string> {
    const documentXml = await readZipTextFile(buffer, 'word/document.xml')
    if (!documentXml) {
        throw new Error('DOCX missing word/document.xml')
    }

    const xml = parseXml(documentXml)
    const paragraphs = Array.from(xml.getElementsByTagName('w:p'))

    const lines = paragraphs.map(p => {
        const parts: string[] = []
        const texts = Array.from(p.getElementsByTagName('w:t'))
        for (const t of texts) {
            parts.push(t.textContent || '')
        }
        const hasBreak = p.getElementsByTagName('w:br').length > 0
        const line = parts.join('')
        return hasBreak ? `${line}\n` : line
    })

    const merged = lines.join('\n')
    const cleaned = normalizeText(merged)
    return cleaned || '(empty document)'
}

export async function extractXlsxTable(buffer: ArrayBuffer): Promise<string[][]> {
    const workbookXml = await readZipTextFile(buffer, 'xl/workbook.xml')
    if (!workbookXml) {
        throw new Error('XLSX missing xl/workbook.xml')
    }

    const relsXml = await readZipTextFile(buffer, 'xl/_rels/workbook.xml.rels')
    const sharedStringsXml = await readZipTextFile(buffer, 'xl/sharedStrings.xml')

    const workbook = parseXml(workbookXml)
    const sheetNode = workbook.querySelector('sheets > sheet')
    if (!sheetNode) {
        throw new Error('No worksheet found in workbook')
    }

    const relationId = sheetNode.getAttribute('r:id')
    let worksheetPath = 'xl/worksheets/sheet1.xml'

    if (relationId && relsXml) {
        const rels = parseXml(relsXml)
        const relation = Array.from(rels.getElementsByTagName('Relationship')).find(r => r.getAttribute('Id') === relationId)
        const target = relation?.getAttribute('Target')
        if (target) {
            const normalized = target.replace(/^\//, '')
            worksheetPath = normalized.startsWith('xl/') ? normalized : `xl/${normalized}`
        }
    }

    const worksheetXml = await readZipTextFile(buffer, worksheetPath)
    if (!worksheetXml) {
        throw new Error(`Missing worksheet xml: ${worksheetPath}`)
    }

    const sharedStrings: string[] = []
    if (sharedStringsXml) {
        const shared = parseXml(sharedStringsXml)
        const siNodes = Array.from(shared.getElementsByTagName('si'))
        for (const si of siNodes) {
            const tNodes = Array.from(si.getElementsByTagName('t'))
            sharedStrings.push(tNodes.map(t => t.textContent || '').join(''))
        }
    }

    const worksheet = parseXml(worksheetXml)
    const rowNodes = Array.from(worksheet.getElementsByTagName('row'))
    const grid: string[][] = []

    for (const rowNode of rowNodes) {
        const rowValues: string[] = []
        const cells = Array.from(rowNode.getElementsByTagName('c'))

        for (const cell of cells) {
            const ref = cell.getAttribute('r') || ''
            const index = ref ? columnRefToIndex(ref) : rowValues.length
            const type = cell.getAttribute('t') || ''

            const valueNode = cell.getElementsByTagName('v')[0]
            const inlineNode = cell.getElementsByTagName('t')[0]
            const rawValue = (valueNode?.textContent || inlineNode?.textContent || '').trim()

            let value = rawValue
            if (type === 's' && rawValue) {
                const sharedIndex = Number.parseInt(rawValue, 10)
                if (!Number.isNaN(sharedIndex)) {
                    value = sharedStrings[sharedIndex] || ''
                }
            }

            while (rowValues.length < index) rowValues.push('')
            rowValues[index] = value
        }

        grid.push(rowValues)
    }

    return toRectangularTable(grid)
}

export function parseCsvTable(text: string, delimiter: ',' | '\t'): string[][] {
    return toRectangularTable(parseCsvLike(text, delimiter))
}
