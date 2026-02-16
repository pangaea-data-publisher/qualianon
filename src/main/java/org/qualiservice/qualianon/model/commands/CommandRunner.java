package org.qualiservice.qualianon.model.commands;

/**
 * Interface representing a command runner.
 * Command runners are responsible for executing commands within a given context.
 */
public interface CommandRunner {
    /**
     * Executes a command within the current project context.
     */
    void runCommand(Command command);
}
