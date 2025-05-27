package org.qualiservice.qualianon.conversion;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.FileOutputStream;
import java.io.IOException;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;


public class ConversionCommon {

    public static void writeTable(String destFile, List<String[]> rows, String[] columns) throws IOException {
        final XSSFWorkbook workbook = new XSSFWorkbook();
        final XSSFSheet sheet = workbook.createSheet();
        final XSSFRow headerRow = sheet.createRow(0);
        setCells(headerRow, columns);

        final AtomicInteger i = new AtomicInteger(1);
        rows.forEach(strings -> {
            final Row row = sheet.createRow(i.getAndIncrement());
            setCells(row, strings);
        });

        // Resize all columns to fit the content size
        for (int j = 0; j < columns.length; j++) {
            sheet.autoSizeColumn(j);
        }

        // Write the output to a file
        final FileOutputStream fileOut = new FileOutputStream(destFile);
        workbook.write(fileOut);
        fileOut.close();

        // Closing the workbook
        workbook.close();
    }

    private static void setCells(Row row, String[] columns) {
        for (int i = 0; i < columns.length; i++) {
            final Cell cell = row.createCell(i);
            cell.setCellValue(columns[i]);
        }
    }

}
