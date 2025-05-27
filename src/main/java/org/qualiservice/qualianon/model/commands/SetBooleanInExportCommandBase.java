package org.qualiservice.qualianon.model.commands;

import org.qualiservice.qualianon.model.anonymization.AnonymizationProfile;
import org.qualiservice.qualianon.model.anonymization.CategoryProfile;


public abstract class SetBooleanInExportCommandBase extends Command {

    protected final AnonymizationProfile anonymizationProfile;
    protected final CategoryProfile categoryProfile;
    protected final boolean isEnabled;
    protected boolean wasEnabled;


    public SetBooleanInExportCommandBase(AnonymizationProfile anonymizationProfile, CategoryProfile categoryProfile, boolean isEnabled) {
        this.anonymizationProfile = anonymizationProfile;
        this.categoryProfile = categoryProfile;
        this.isEnabled = isEnabled;
    }

    @Override
    public boolean run() {
        messageLogger.logInfo(getDescription(), this);
        return true;
    }

    @Override
    public boolean undo() {
        messageLogger.logInfo("Undo " + getDescription(), this);
        return true;
    }

    @Override
    public String toString() {
        return "SetBooleanInExportCommandBase{" +
                "project=" + project +
                ", anonymizationProfile=" + anonymizationProfile +
                ", categoryProfile=" + categoryProfile +
                ", isEnabled=" + isEnabled +
                ", wasEnabled=" + wasEnabled +
                '}';
    }
}
