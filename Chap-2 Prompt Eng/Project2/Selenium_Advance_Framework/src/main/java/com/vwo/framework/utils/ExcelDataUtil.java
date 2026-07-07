package com.vwo.framework.utils;

import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * Reads an .xlsx sheet into a list of row-maps keyed by header. No sample .xlsx ships with
 * this repo (a binary spreadsheet cannot be authored as plain text) - point filePath at a
 * real workbook such as src/test/resources/testdata/login_data.xlsx once one is added.
 */
public final class ExcelDataUtil {

    private ExcelDataUtil() {
    }

    public static List<Map<String, String>> readSheet(String filePath, String sheetName) {
        List<Map<String, String>> records = new ArrayList<>();

        try (InputStream input = new FileInputStream(filePath);
             Workbook workbook = new XSSFWorkbook(input)) {

            Sheet sheet = workbook.getSheet(sheetName);
            if (sheet == null) {
                throw new IllegalArgumentException("Sheet '" + sheetName + "' not found in " + filePath);
            }

            DataFormatter formatter = new DataFormatter();
            Row headerRow = sheet.getRow(0);
            int lastCol = headerRow.getLastCellNum();

            for (int r = 1; r <= sheet.getLastRowNum(); r++) {
                Row row = sheet.getRow(r);
                if (row == null) {
                    continue;
                }
                Map<String, String> record = new LinkedHashMap<>();
                for (int c = 0; c < lastCol; c++) {
                    String header = formatter.formatCellValue(headerRow.getCell(c));
                    String value = formatter.formatCellValue(row.getCell(c));
                    record.put(header, value);
                }
                records.add(record);
            }
        } catch (IOException e) {
            throw new IllegalStateException("Failed to read Excel test data: " + filePath, e);
        }

        return records;
    }
}
