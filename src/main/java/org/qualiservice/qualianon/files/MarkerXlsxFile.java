package org.qualiservice.qualianon.files;

import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.usermodel.WorkbookFactory;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.qualiservice.qualianon.model.text.MarkerStorage;

import java.io.*;
import java.util.*;


public class MarkerXlsxFile {

    /**
     * Reads marker storage rows from the document's marker XLSX file.
     */
    public static List<MarkerStorage> read(File file) throws IOException {
        try (final InputStream is = new FileInputStream(file)) {
            final List<MarkerStorage> result = new LinkedList<>();
            final Workbook workbook = WorkbookFactory.create(is);
            final Sheet sheet = workbook.getSheetAt(0);
            final Iterator<Row> rowIterator = sheet.rowIterator();
            rowIterator.next(); // Skip header
            while (rowIterator.hasNext()) {
                final Row row = rowIterator.next();
                final MarkerStorage markerStorage = new MarkerStorage()
                        .setId(UUID.fromString(row.getCell(0).getStringCellValue()))
                        .setReplacementId(UUID.fromString(row.getCell(1).getStringCellValue()))
                        .setOriginal(getCellValueOrNull(row, 2))
                        .setNote(getCellValueOrNull(row, 3));
                result.add(markerStorage);
            }
            return result;
        } catch (FileNotFoundException e) {
            return Collections.emptyList();
        }
    }

    /**
     * Extracts a cell's string value, returning null if the cell is missing.
     */
    public static String getCellValueOrNull(Row row, int cellnum) {
        if (row.getCell(cellnum) == null) return null;
        return row.getCell(cellnum).getStringCellValue();
    }

    /**
     * Writes marker storage rows to a single-sheet XLSX file.
     */
    public static void write(File file, List<MarkerStorage> markers) throws IOException {
        final XSSFWorkbook workbook = new XSSFWorkbook();
        final XSSFSheet sheet = workbook.createSheet("Markers");
        final XSSFRow headerRow = sheet.createRow(0);
        headerRow.createCell(0).setCellValue("Marker ID");
        headerRow.createCell(1).setCellValue("Replacement ID");
        headerRow.createCell(2).setCellValue("Original");
        headerRow.createCell(3).setCellValue("Note");

        int rownum = 1;
        for (final MarkerStorage marker : markers) {
            final XSSFRow row = sheet.createRow(rownum);
            row.createCell(0).setCellValue(marker.getId().toString());
            row.createCell(1).setCellValue(marker.getReplacementId().toString());
            row.createCell(2).setCellValue(marker.getOriginal());
            row.createCell(3).setCellValue(marker.getNote());
            rownum++;
        }

        sheet.autoSizeColumn(0);
        sheet.autoSizeColumn(1);
        sheet.autoSizeColumn(2);
        sheet.autoSizeColumn(3);

        try (final FileOutputStream fos = new FileOutputStream(file)) {
            workbook.write(fos);
        }
    }

}
