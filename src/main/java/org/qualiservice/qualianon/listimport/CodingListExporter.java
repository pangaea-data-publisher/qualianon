package org.qualiservice.qualianon.listimport;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.xssf.usermodel.XSSFCell;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;


public class CodingListExporter {

    public static void write(String source, File file, List<List<String>> rows, List<String> headers) throws IOException {
        final XSSFWorkbook workbook = new XSSFWorkbook();
        final XSSFSheet sheet = workbook.createSheet();

        final XSSFRow sourceRow = sheet.createRow(0);
        final XSSFCell sourceCell = sourceRow.createCell(0);
        sourceCell.setCellValue(source);

        final XSSFRow headerRow = sheet.createRow(1);
        setCells(headerRow, headers, headers.size());

        final AtomicInteger i = new AtomicInteger(2);
        rows.forEach(strings -> {
            final Row row = sheet.createRow(i.getAndIncrement());
            setCells(row, strings, headers.size());
        });

        // Resize all columns to fit the content size
        for (int j = 0; j < headers.size(); j++) {
            sheet.autoSizeColumn(j);
        }

        // Write the output to a file
        final FileOutputStream fileOut = new FileOutputStream(file);
        workbook.write(fileOut);
        fileOut.close();

        // Closing the workbook
        workbook.close();
    }

    private static void setCells(Row row, List<String> values, int numCells) {
        for (int i = 0; i < numCells; i++) {
            final Cell cell = row.createCell(i);
            if (values.size() <= i) continue;
            cell.setCellValue(values.get(i));
        }
    }

}
