package org.qualiservice.qualianon.gui.components.documentview;


public class DisplayFont {

    private final FontFamily fontFamily;
    private final int fontSize;

    public DisplayFont(FontFamily fontFamily, int fontSize) {
        this.fontFamily = fontFamily;
        this.fontSize = fontSize;
    }

    public String getCss() {
        return "-fx-font: " + fontSize + " " + fontFamily;
    }

}
