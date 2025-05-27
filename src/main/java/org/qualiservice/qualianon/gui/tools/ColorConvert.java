package org.qualiservice.qualianon.gui.tools;

import javafx.scene.paint.Color;


public class ColorConvert {

    public static String toHighlightStyle(Color color) {
        String style = "-rtfx-background-color: " + toCss(color) + ";";

        double luminance = 0.2126 * color.getRed() + 0.7152 * color.getGreen() + 0.0722 * color.getBlue();
        if (luminance < 0.6) {
            style += " -fx-fill: #ffffff;";
        }

        return style;
    }

    public static String toCss(Color color) {
        return "#" + color.toString().substring(2);
    }

}
