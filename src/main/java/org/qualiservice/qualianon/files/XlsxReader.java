package org.qualiservice.qualianon.files;

import org.apache.poi.ss.usermodel.*;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;


public class XlsxReader {

    public static List<List<String>> read(String filename) throws IOException {
        return read(new File(filename), 0);
    }

    public static List<List<String>> read(String filename, int sheetIndex) throws IOException {
        return read(new File(filename), sheetIndex);
    }

    public static List<List<String>> read(File file) throws IOException {
        return read(file, 0);
    }

    public static List<List<String>> read(File file, int sheetIndex) throws IOException {
        final List<List<String>> matrix = new LinkedList<>();
        try (InputStream inp = new FileInputStream(file)) {
            Workbook wb = WorkbookFactory.create(inp);
            Sheet sheet = wb.getSheetAt(sheetIndex);

            final Iterator<Row> rowIterator = sheet.rowIterator();
            for (Row row = rowIterator.next(); ; row = rowIterator.next()) {
                final List<String> rowExport = new LinkedList<>();
                for (int j = 0; j < row.getLastCellNum(); j++) {
                    final Cell cell = row.getCell(j);
                    if (cell == null) {
                        rowExport.add("");
                    } else if (cell.getCellType() == CellType.NUMERIC) {
                        String v = String.valueOf(cell.getNumericCellValue());
                        if (v.endsWith(".0")) {
                            v = v.substring(0, v.length() - 2);
                        }
                        rowExport.add(v);
                    } else {
                        rowExport.add(cell.getStringCellValue());
                    }
                }
                matrix.add(rowExport);
                if (!rowIterator.hasNext()) break;
            }
        }

        return matrix;
    }

}
