package org.qualiservice.qualianon.files;

import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.usermodel.WorkbookFactory;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.qualiservice.qualianon.audit.MessageLogger;
import org.qualiservice.qualianon.model.categories.Categories;
import org.qualiservice.qualianon.model.categories.CategoryScheme;
import org.qualiservice.qualianon.model.categories.LabelScheme;
import org.qualiservice.qualianon.model.project.Label;
import org.qualiservice.qualianon.model.project.Replacement;

import java.io.*;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;


public class ReplacementsXlsxFile {

    /**
     * Writes replacements into an XLSX file with one sheet per category scheme.
     */
    public static void write(File file, List<Replacement> replacements) throws IOException {

        final Map<CategoryScheme, List<Replacement>> categorySchemes = replacements.stream()
                .map(Replacement::getCategoryScheme)
                .collect(Collectors.toSet())
                .stream()
                .collect(Collectors.toMap(o -> o, o -> new LinkedList<>()));

        replacements.forEach(replacement ->
                categorySchemes.get(replacement.getCategoryScheme()).add(replacement)
        );

        final XSSFWorkbook workbook = new XSSFWorkbook();
        categorySchemes.forEach((key, value) -> writeSheet(key, value, workbook));

        try (final FileOutputStream fos = new FileOutputStream(file)) {
            workbook.write(fos);
        }
    }

    /**
     * Writes a single category sheet with ID and label columns.
     */
    private static void writeSheet(CategoryScheme categoryScheme, List<Replacement> replacements, XSSFWorkbook workbook) {
        final XSSFSheet sheet = workbook.createSheet(categoryScheme.getName());

        final XSSFRow headerRow = sheet.createRow(0);
        headerRow.createCell(0).setCellValue("ID");
        int colnum = 1;
        for (final LabelScheme labelScheme : categoryScheme.getLabels()) {
            headerRow.createCell(colnum).setCellValue(labelScheme.getName());
            colnum++;
        }

        int rownum = 1;
        for (final Replacement replacement : replacements) {
            final XSSFRow row = sheet.createRow(rownum);
            row.createCell(0).setCellValue(replacement.getId().toString());
            colnum = 1;
            for (final LabelScheme labelScheme : categoryScheme.getLabels()) {
                final String value = replacement.getLabels().stream()
                        .filter(label -> label.getLevel().equals(labelScheme.getName()))
                        .map(Label::getValue)
                        .findAny()
                        .orElse("");
                row.createCell(colnum).setCellValue(value);
                colnum++;
            }
            rownum++;
        }

        for (colnum = 0; colnum < categoryScheme.getLabels().size() + 1; colnum++) {
            sheet.autoSizeColumn(colnum);
        }
    }

    /**
     * Reads replacements from an XLSX file using category sheet names.
     */
    public static List<Replacement> read(File file, Categories categories, MessageLogger messageLogger) throws IOException {

        try (final InputStream is = new FileInputStream(file)) {
            final List<Replacement> result = new LinkedList<>();
            final Workbook workbook = WorkbookFactory.create(is);

            for (int sheetnum = 0; sheetnum < workbook.getNumberOfSheets(); sheetnum++) {
                final Sheet sheet = workbook.getSheetAt(sheetnum);
                final CategoryScheme categoryScheme = categories.getByName(sheet.getSheetName());
                final List<String> labelNames = new LinkedList<>();
                final Row headerRow = sheet.getRow(0);
                for (int colnum = 1; colnum < headerRow.getLastCellNum(); colnum++) {
                    labelNames.add(headerRow.getCell(colnum).getStringCellValue());
                }
                for (int rownum = 1; rownum <= sheet.getLastRowNum(); rownum++) {
                    final Row row = sheet.getRow(rownum);
                    final Replacement replacement = new Replacement(categoryScheme, messageLogger);
                    replacement.setId(UUID.fromString(row.getCell(0).getStringCellValue()));
                    int colnum = 1;
                    for (final String labelName : labelNames) {
                        replacement.addLabel(new Label(
                                labelName,
                                MarkerXlsxFile.getCellValueOrNull(row, colnum)
                        ));
                        colnum++;
                    }
                    result.add(replacement);
                }
            }
            return result;
        }

    }

}
