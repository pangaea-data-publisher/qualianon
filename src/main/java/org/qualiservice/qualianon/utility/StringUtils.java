package org.qualiservice.qualianon.utility;

import java.util.List;
import java.util.Locale;

public class StringUtils {

    public static String ellipsis(String text, int max) {
        if (text.length() <= max) return text.replace("\n", " ");
        return text.substring(0, max - 3).replace("\n", " ") + "...";
    }

    public static String stripFileEnding(String name) {
        final int i = name.lastIndexOf('.');
        if (i == -1) return name;
        return name.substring(0, i);
    }

    public static String textWithIndicator(String text, boolean modified) {
        final StringBuilder sb = new StringBuilder(text);
        if (modified) {
            sb.append(" *");
        }
        return sb.toString();
    }

    public static String getUniqueName(List<String> names, String newNamePattern) {
        return names.stream()
                .filter(name -> name.toLowerCase(Locale.ROOT).startsWith(newNamePattern.toLowerCase(Locale.ROOT)))
                .map(name -> {
                    if (!name.endsWith(")") || !name.contains("(")) return 0;
                    final String str = name.substring(name.lastIndexOf("(") + 1, name.length() - 1);
                    try {
                        return Integer.parseInt(str);
                    } catch (NumberFormatException e) {
                        return 0;
                    }
                })
                .reduce(Math::max)
                .map(integer -> integer + 1)
                .map(integer -> newNamePattern + " (" + integer + ")")
                .orElse(newNamePattern);
    }

    public static boolean isBlank(String str) {
        return str == null || str.trim().isEmpty();
    }

}
