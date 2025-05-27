package org.qualiservice.qualianon.gui.components;

import org.fxmisc.flowless.VirtualizedScrollPane;
import org.fxmisc.richtext.InlineCssTextArea;
import org.fxmisc.richtext.LineNumberFactory;
import org.qualiservice.qualianon.gui.components.documentview.DisplayFont;
import org.qualiservice.qualianon.gui.components.documentview.FontFamily;
import org.qualiservice.qualianon.gui.tools.GuiUtility;
import org.qualiservice.qualianon.gui.tools.tabs.BaseTab;
import org.qualiservice.qualianon.model.exports.Export;
import org.qualiservice.qualianon.model.project.AnonymizedFile;
import org.qualiservice.qualianon.model.project.ReplacementCollection;
import org.qualiservice.qualianon.utility.UpdateListener;


public class AnonymizedDocumentTab extends BaseTab {

    private final AnonymizedFile document;
    private final Export export;
    private final ReplacementCollection replacementCollection;
    private final InlineCssTextArea textArea;
    private final UpdateListener updateListener;


    public AnonymizedDocumentTab(AnonymizedFile document, Export export, ReplacementCollection replacementCollection) {
        super(document.getExportName(export));
        this.document = document;
        this.export = export;
        this.replacementCollection = replacementCollection;

        textArea = new InlineCssTextArea();
        textArea.setEditable(false);
        textArea.setWrapText(true);
        final DisplayFont displayFont = new DisplayFont(FontFamily.sansserif, 18);
        textArea.setStyle(displayFont.getCss());
        textArea.setParagraphGraphicFactory(LineNumberFactory.get(textArea));

        document.addUpdateListener(isDirect -> showDocument());
        updateListener = isDirect -> showDocument();
        export.addUpdateListener(updateListener);
        replacementCollection.addUpdateListener(updateListener);
        showDocument();

        final VirtualizedScrollPane<InlineCssTextArea> scrollPane = new VirtualizedScrollPane<>(textArea);
        GuiUtility.extend(scrollPane);
        GuiUtility.extend(this);
        getChildren().add(scrollPane);
    }

    private void showDocument() {
        textArea.replaceText(document.getDocument().toExport(export.getAnonymizationProfile(), false));
    }

    @Override
    public void onClose() {
        export.removeUpdateListener(updateListener);
        replacementCollection.removeUpdateListener(updateListener);
    }

}
