package org.qualiservice.qualianon.model.categories;

import org.qualiservice.qualianon.files.XlsxReader;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Objects;


public class CodingList {

    private final String source;
    private final List<String> header;
    private final List<List<String>> data;

    public CodingList(File file) throws IOException {
        final List<List<String>> sheet = XlsxReader.read(file);
        source = sheet.get(0).get(0);
        header = sheet.get(1);
        data = sheet.subList(2, sheet.size());
    }

    public CodingList(String filename, String directory) throws IOException {
        this(new File(directory, filename));
    }

    public CodingList(String source, List<String> header, List<List<String>> data) {
        this.source = source;
        this.header = header;
        this.data = data;
    }

    public String getSource() {
        return source;
    }

    public List<String> getHeader() {
        return header;
    }

    public List<List<String>> getData() {
        return data;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        CodingList that = (CodingList) o;
        return Objects.equals(source, that.source) && Objects.equals(header, that.header) && Objects.equals(data, that.data);
    }

    @Override
    public int hashCode() {
        return Objects.hash(source, header, data);
    }

    @Override
    public String toString() {
        return "CodingList{" +
                "source='" + source + '\'' +
                ", header=" + header +
                ", data=" + data +
                '}';
    }

}
