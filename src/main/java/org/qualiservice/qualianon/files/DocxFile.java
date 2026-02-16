package org.qualiservice.qualianon.files;

import org.apache.poi.xwpf.usermodel.*;

import java.io.*;

public class DocxFile {

    /**
     * Reads a DOCX as plain text, preserving paragraph boundaries as newlines.
     */
    public static String read(File file) throws IOException {
        try (FileInputStream fis = new FileInputStream(file)) {
            final StringBuilder sb = new StringBuilder();
            final XWPFDocument document = new XWPFDocument(fis);
            readDocument(document, sb);
            return sb.toString();
        }
    }

    /**
     * Converts document paragraphs and runs into plain text output.
     */
    private static void readDocument(XWPFDocument document, StringBuilder sb) {
        for (IBodyElement e : document.getParagraphs()) {
                readParagraph((XWPFParagraph) e, document, sb);
                sb.append('\n');
        }
    }

    private static void readParagraph(XWPFParagraph paragraph, XWPFDocument document, StringBuilder sb) {
        for (IRunElement run : paragraph.getRuns()) {
            sb.append(run);

            if (run instanceof XWPFHyperlinkRun) {
                XWPFHyperlink link = ((XWPFHyperlinkRun) run).getHyperlink(document);
                if (link != null)
                    sb.append(" <").append(link.getURL()).append(">");
            }
        }
    }

    /**
     * Writes each text line as a separate paragraph in the DOCX.
     */
    public static void write(File file, String text) throws IOException {
        final XWPFDocument doc = new XWPFDocument();

        final String[] lines = text.split("\n");
        for (final String line : lines) {
            final XWPFParagraph currentParagraph = doc.createParagraph();
            final XWPFRun currentRun = currentParagraph.createRun();
            currentRun.setText(line);
        }

        try (final FileOutputStream out = new FileOutputStream(file)) {
            doc.write(out);
            doc.close();
        }
    }

}
