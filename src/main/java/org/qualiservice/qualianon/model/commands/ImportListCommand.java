package org.qualiservice.qualianon.model.commands;

import org.apache.commons.io.FileUtils;
import org.qualiservice.qualianon.gui.components.UIInterface;
import org.qualiservice.qualianon.gui.components.listimport.Importer;
import org.qualiservice.qualianon.model.categories.CategoryListScheme;
import org.qualiservice.qualianon.model.categories.CategoryScheme;
import org.qualiservice.qualianon.model.categories.CodingList;
import org.qualiservice.qualianon.model.categories.LabelScheme;

import java.io.File;
import java.io.IOException;
import java.util.LinkedList;
import java.util.List;


public class ImportListCommand extends Command {

    private final CategoryScheme categoryScheme;
    private final CodingList codingList;
    private final File categoriesDirectory;
    private final File file;
    private final Importer importer;
    private final UIInterface uiInterface;

    private List<LabelScheme> addedLabels;

    public ImportListCommand(CategoryScheme categoryScheme, CodingList codingList, File categoriesDirectory, File file, Importer importer, UIInterface uiInterface) {
        this.categoryScheme = categoryScheme;
        this.codingList = codingList;
        this.categoriesDirectory = categoriesDirectory;
        this.file = file;
        this.importer = importer;
        this.uiInterface = uiInterface;
    }

    @Override
    public String getDescription() {
        return "Import list " + file.getName();
    }

    @Override
    public boolean run() {

        addedLabels = new LinkedList<>();
        for (String parameter : codingList.getHeader()) {
            if (categoryScheme.hasLabel(parameter)) continue;
            final LabelScheme label = new LabelScheme(parameter);
            categoryScheme.addLabel(label);
            addedLabels.add(label);
        }

        try {
            final File destFile = new File(categoriesDirectory, file.getName());
            if (!file.getCanonicalPath().equals(destFile.getCanonicalPath())) {
                FileUtils.copyFile(file, destFile);
            }
        } catch (IOException e) {
            uiInterface.getMessageLogger().logError("Error copying listfile into project: ", e);
        }

        final CategoryListScheme categoryListScheme = new CategoryListScheme()
                .setFile(file.getName())
                .setStyle(importer.getSelectionStyle())
                .setImporter(importer);
        categoryScheme.setCategoryList(categoryListScheme);

        messageLogger.logInfo(getDescription(), this);
        return true;
    }

    @Override
    public boolean undo() {
        categoryScheme.setCategoryList(null);
        addedLabels.forEach(categoryScheme::removeLabel);
        messageLogger.logInfo("Undo " + getDescription(), this);
        return true;
    }

    @Override
    public String toString() {
        return "ImportListCommand{" +
                "project=" + project +
                ", categoryScheme=" + categoryScheme +
                ", codingList=" + codingList +
                ", categoriesDirectory=" + categoriesDirectory +
                ", file=" + file +
                ", importer=" + importer +
                ", addedLabels=" + addedLabels +
                '}';
    }
}
