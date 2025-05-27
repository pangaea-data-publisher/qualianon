package org.qualiservice.qualianon.model.commands;

import org.qualiservice.qualianon.audit.MessageLogger;
import org.qualiservice.qualianon.model.project.Project;

public abstract class Command {

    protected Project project;
    protected MessageLogger messageLogger;

    public void setProject(Project project, MessageLogger messageLogger) {
        this.project = project;
        this.messageLogger = messageLogger;
    }

    public abstract String getDescription();

    public abstract boolean run();

    public abstract boolean undo();

}
