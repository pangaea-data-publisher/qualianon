package org.qualiservice.qualianon.gui.components.listimport;

import javafx.scene.control.Hyperlink;
import javafx.scene.control.Label;
import javafx.scene.layout.Region;
import javafx.stage.FileChooser;
import org.qualiservice.qualianon.utility.YJFileChooser;
import org.qualiservice.qualianon.audit.MessageLogger;
import org.qualiservice.qualianon.gui.tools.Browser;

import java.awt.*;
import java.util.Arrays;
import java.util.List;


public class ImportSettingsFactory {

    public static List<ImportSettings> make(MessageLogger messageLogger) {

        return Arrays.asList(
                new ImportSettings(
                        "ISCO-08 Occupations 2008",
                        Arrays.asList(
                                label("ISCO-08"),
                                label("International Standard Classification of Occupations, 2008 version"),
                                spacer(25),
                                label("Download instructions"),
                                spacer(15),
                                label("1. Open in your browser:"),
                                hyperlink("https://ec.europa.eu/eurostat/ramon/nomenclatures/index.cfm?TargetUrl=LST_CLS_DLD&StrNom=CL_ISCO08", messageLogger),
                                spacer(7),
                                label("2. Select language"),
                                spacer(7),
                                label("3. Select the XML download link"),
                                spacer(7),
                                label("4. Save the file to your computer"),
                                spacer(25),
                                label("Import the downloaded file CL_ISCO08_[...].xml")
                        ),
                        new FileChooser.ExtensionFilter("CL_ISCO08_[...].xml", "*.xml"),
                        Importer.CLASET
                ),
                new ImportSettings(
                        "NACE-08 Economic Activities 2008",
                        Arrays.asList(
                                label("NACE Rev. 2"),
                                label("Statistical Classification of Economic Activities in the European Community, Rev. 2 (2008)"),
                                spacer(25),
                                label("Download instructions"),
                                spacer(15),
                                label("1. Open in your browser:"),
                                hyperlink("https://ec.europa.eu/eurostat/ramon/nomenclatures/index.cfm?TargetUrl=LST_CLS_DLD&StrNom=NACE_REV2&StrLayoutCode=HIERARCHIC", messageLogger),
                                spacer(7),
                                label("2. Select language"),
                                spacer(7),
                                label("3. Select the XML download link"),
                                spacer(7),
                                label("4. Save the file to your computer"),
                                spacer(25),
                                label("Import the downloaded file CL_ISCO08_[...].xml")
                        ),
                        new FileChooser.ExtensionFilter("NACE_REV2_[...].xml", "*.xml"),
                        Importer.CLASET
                ),
                new ImportSettings(
                        "ICD-10 Diseases and Related Health Problems",
                        Arrays.asList(
                                label("ICD-10"),
                                label("International Statistical Classification of Diseases and Related Health Problems"),
                                label("WHO version"),
                                spacer(25),
                                label("Download instructions"),
                                spacer(15),
                                label("1. Open in your browser:"),
                                hyperlink("https://apps.who.int/classifications/apps/icd/ClassificationDownload/DLArea/Download.aspx", messageLogger),
                                spacer(7),
                                label("2. Create an account, if you are not registered with the WHO"),
                                spacer(7),
                                label("3. Log in with your account"),
                                spacer(7),
                                label("4. Download a ClaML classification and extract the zip file"),
                                spacer(25),
                                label("Import the downloaded file icd[...].xml")
                        ),
                        new FileChooser.ExtensionFilter("icd[...].xml", "*.xml"),
                        Importer.CLAML
                ),
                new ImportSettings(
                        "ICD-10-GM Diseases and Related Health Problems (German)",
                        Arrays.asList(
                                label("ICD-10-GM"),
                                label("International Statistical Classification of Diseases and Related Health Problems"),
                                label("German, version 2021"),
                                spacer(25),
                                label("Download instructions"),
                                spacer(15),
                                label("1. Open in your browser:"),
                                hyperlink("https://www.dimdi.de/dynamic/.downloads/klassifikationen/icd-10-gm/version2021/icd10gm2021syst-claml-20201111.zip", messageLogger),
                                spacer(7),
                                label("2. Accept the license"),
                                spacer(7),
                                label("3. Save the zip file to your computer and extract it"),
                                spacer(25),
                                label("Import the extracted file Klassifikationsdateien/icd10gm2021[...].xml")
                        ),
                        new FileChooser.ExtensionFilter("icd[...].xml", "*.xml"),
                        Importer.CLAML
                ),
                new ImportSettings(
                        "SGTYP Stadt- und Gemeindetypen (German)",
                        Arrays.asList(
                                label("SGTYP"),
                                label("Referenzdatei: Stadt- und Gemeindetyp, BBSR Bonn 2017"),
                                spacer(25),
                                label("Download instructions"),
                                spacer(15),
                                label("1. Open in your browser:"),
                                hyperlink("https://www.bbsr.bund.de/BBSR/DE/forschung/raumbeobachtung/Raumabgrenzungen/deutschland/gemeinden/StadtGemeindetyp/download-ref-sgtyp.xlsx?__blob=publicationFile&v=1", messageLogger),
                                spacer(7),
                                label("2. Save the xlsx file to your computer"),
                                spacer(25),
                                label("Import the file download-ref-sgtyp.xlsx")
                        ),
                        new FileChooser.ExtensionFilter("download-ref-sgtyp.xlsx", "*.xlsx"),
                        Importer.SGTYP
                ),
                new ImportSettings(
                        "CLASET/XML General",
                        Arrays.asList(
                                label("CLASET/XML"),
                                label("Electronic Format for Exchange of Classifications"),
                                spacer(25),
                                label("Claset/XML is format for the exchange of classifications."),
                                label("A number of different files are available, that can be"),
                                label("imported with this option."),
                                label("Especially the EU uses this format for many of their classifications."),
                                spacer(15),
                                label("You should be able to import most of the classifications from RAMON:"),
                                spacer(15),
                                label("1. Open in your browser:"),
                                hyperlink("https://ec.europa.eu/eurostat/ramon/nomenclatures/index.cfm?TargetUrl=LST_NOM&StrGroupCode=CLASSIFIC", messageLogger),
                                spacer(7),
                                label("2. Select a classification"),
                                spacer(7),
                                label("3. Click \"Further files and information\""),
                                spacer(7),
                                label("4. Select language"),
                                spacer(7),
                                label("5. Select the XML download link"),
                                spacer(7),
                                label("6. Save the file to your computer"),
                                spacer(25),
                                label("Import the downloaded file *.xml")
                        ),
                        new FileChooser.ExtensionFilter("*.xml", "*.xml"),
                        Importer.CLASET
                ),
                new ImportSettings(
                        "ClaML/XML General",
                        Arrays.asList(
                                label("ClaML/XML"),
                                label("Classification Markup Language"),
                                spacer(25),
                                label("ClaML has been adopted by the WHO to distribute their"),
                                label("family of international classifications.")
                        ),
                        new FileChooser.ExtensionFilter("*.xml", "*.xml"),
                        Importer.CLASET
                ),
                new ImportSettings(
                        "Custom XLSX Sheet",
                        Arrays.asList(
                                label("Custom XLSX Sheet"),
                                label("Create your own lists in XLSX format"),
                                spacer(25),
                                label("Format definition"),
                                spacer(15),
                                label("Cell A1: Citation information, source of data"),
                                spacer(7),
                                label("Row 2: Header row"),
                                spacer(7),
                                label("Rows 3 and following: Data rows"),
                                spacer(25),
                                label("Save the file to your computer"),
                                spacer(15),
                                label("* Save as XLSX format"),
                                spacer(7),
                                label("* Do *not* put the file into the QualiAnon project folder."),
                                label("   QualiAnon will copy it into the project during import."),
                                spacer(7),
                                label("* Use MS Excel or LibreOffice/OpenOffice"),
                                spacer(25),
                                label("Finally, import the XLSX file.")
                        ),
                        new FileChooser.ExtensionFilter("Office Open XML (*.xlsx)", "*.xlsx"),
                        Importer.CUSTOM_XLSX
                )
        );
    }

    private static Label label(String text) {
        return new Label(text);
    }

    private static Hyperlink hyperlink(String url, MessageLogger messageLogger) {
        final Hyperlink hyperlink = new Hyperlink(url);
        hyperlink.setOnAction(actionEvent -> {
            if (Desktop.isDesktopSupported() && Desktop.getDesktop().isSupported(Desktop.Action.BROWSE)) {
                messageLogger.logInfo("Download list at " + url);
                Browser.openBrowser(url, messageLogger);
            }
        });
        return hyperlink;
    }

    private static Region spacer(double height) {
        final Region region = new Region();
        region.setPrefHeight(height);
        return region;
    }

}
