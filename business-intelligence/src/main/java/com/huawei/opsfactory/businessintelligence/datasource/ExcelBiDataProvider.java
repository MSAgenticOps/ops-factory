package com.huawei.opsfactory.businessintelligence.datasource;

import com.huawei.opsfactory.businessintelligence.config.BusinessIntelligenceRuntimeProperties;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.DataFormatter;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.usermodel.WorkbookFactory;
import org.springframework.stereotype.Component;

@Component
public class ExcelBiDataProvider implements BiDataProvider {

    private static final String INCIDENTS_FILE = "Incidents-exported.xlsx";
    private static final String CHANGES_FILE = "Changes-exported.xlsx";
    private static final String REQUESTS_FILE = "Requests-exported.xlsx";
    private static final String PROBLEMS_FILE = "Problems-exported.xlsx";

    private final BusinessIntelligenceRuntimeProperties runtimeProperties;
    private final DataFormatter formatter = new DataFormatter();

    public ExcelBiDataProvider(BusinessIntelligenceRuntimeProperties runtimeProperties) {
        this.runtimeProperties = runtimeProperties;
    }

    @Override
    public BiRawData load() {
        Path baseDir = Paths.get(runtimeProperties.getBaseDir()).toAbsolutePath().normalize();
        List<Map<String, String>> incidents = readRows(baseDir.resolve(INCIDENTS_FILE), "Data");
        List<Map<String, String>> incidentSlaCriteria = readRows(baseDir.resolve(INCIDENTS_FILE), "SLA_Criteria");
        List<Map<String, String>> changes = readRows(baseDir.resolve(CHANGES_FILE), "Data");
        List<Map<String, String>> requests = readRows(baseDir.resolve(REQUESTS_FILE), "Data");
        List<Map<String, String>> problems = readRows(baseDir.resolve(PROBLEMS_FILE), "Data");
        return new BiRawData(incidents, incidentSlaCriteria, changes, requests, problems);
    }

    private List<Map<String, String>> readRows(Path file, String sheetName) {
        if (!Files.exists(file)) {
            return List.of();
        }
        try (InputStream inputStream = Files.newInputStream(file);
             Workbook workbook = WorkbookFactory.create(inputStream)) {
            Sheet sheet = workbook.getSheet(sheetName);
            if (sheet == null) {
                return List.of();
            }
            Row headerRow = sheet.getRow(sheet.getFirstRowNum());
            if (headerRow == null) {
                return List.of();
            }

            List<String> headers = new ArrayList<>();
            int lastCellNum = Math.max(headerRow.getLastCellNum(), 0);
            for (int cellIndex = 0; cellIndex < lastCellNum; cellIndex++) {
                headers.add(readCell(headerRow.getCell(cellIndex)));
            }

            List<Map<String, String>> rows = new ArrayList<>();
            for (int rowIndex = sheet.getFirstRowNum() + 1; rowIndex <= sheet.getLastRowNum(); rowIndex++) {
                Row row = sheet.getRow(rowIndex);
                if (row == null) {
                    continue;
                }
                Map<String, String> values = new LinkedHashMap<>();
                boolean hasValue = false;
                for (int cellIndex = 0; cellIndex < headers.size(); cellIndex++) {
                    String header = headers.get(cellIndex);
                    if (header == null || header.isBlank()) {
                        continue;
                    }
                    String value = readCell(row.getCell(cellIndex));
                    if (!value.isBlank()) {
                        hasValue = true;
                    }
                    values.put(header, value);
                }
                if (hasValue) {
                    rows.add(values);
                }
            }
            return rows;
        } catch (IOException exception) {
            throw new IllegalStateException("Failed to read data file: " + file, exception);
        }
    }

    private String readCell(Cell cell) {
        return cell == null ? "" : formatter.formatCellValue(cell).trim();
    }
}

