package org.qualiservice.qualianon.model.commands;

import org.junit.Before;
import org.junit.Test;
import org.mockito.InOrder;
import org.qualiservice.qualianon.audit.MessageLogger;

import java.util.NoSuchElementException;

import static org.mockito.Mockito.*;


public class CommandListTest {

    private CommandList.CommandListListener listener;
    private CommandList commandList;
    private Command command1;
    private Command command2;

    @Before
    public void setup() {
        final MessageLogger logger = mock(MessageLogger.class);
        listener = mock(CommandList.CommandListListener.class);
        commandList = new CommandList(logger);
        commandList.addListener(listener);
        command1 = mock(Command.class);
        when(command1.getDescription()).thenReturn("Command 1");
        when(command1.run()).thenReturn(true);
        when(command1.undo()).thenReturn(true);
        command2 = mock(Command.class);
        when(command2.getDescription()).thenReturn("Command 2");
        when(command2.run()).thenReturn(true);
        when(command2.undo()).thenReturn(true);
    }

    @Test
    public void runCommandTest() {
        commandList.runCommand(command1);
        verify(command1).run();
        verify(listener).onUndoRedoChanged(eq(true), eq("Command 1"), eq(false), eq(""));
    }

    @Test
    public void runAndUndoCommandTest() {
        commandList.runCommand(command1);
        commandList.undo();

        final InOrder inOrder = inOrder(command1, listener);
        inOrder.verify(command1).run();
        inOrder.verify(listener).onUndoRedoChanged(eq(true), eq("Command 1"), eq(false), eq(""));
        inOrder.verify(command1).undo();
        inOrder.verify(listener).onUndoRedoChanged(eq(false), eq(""), eq(true), eq("Command 1"));
    }

    @Test(expected = NoSuchElementException.class)
    public void undoWithoutRunFailsTest() {
        commandList.undo();
    }

    @Test
    public void run2AndUndo2Test() {
        commandList.runCommand(command1);
        commandList.runCommand(command2);
        commandList.undo();
        commandList.undo();

        final InOrder inOrder = inOrder(command1, command2, listener);
        inOrder.verify(command1).run();
        inOrder.verify(listener).onUndoRedoChanged(eq(true), eq("Command 1"), eq(false), eq(""));
        inOrder.verify(command2).run();
        inOrder.verify(listener).onUndoRedoChanged(eq(true), eq("Command 2"), eq(false), eq(""));
        inOrder.verify(command2).undo();
        inOrder.verify(listener).onUndoRedoChanged(eq(true), eq("Command 1"), eq(true), eq("Command 2"));
        inOrder.verify(command1).undo();
        inOrder.verify(listener).onUndoRedoChanged(eq(false), eq(""), eq(true), eq("Command 1"));
    }

    @Test
    public void runUndoRedoTest() {
        commandList.runCommand(command1);
        commandList.runCommand(command2);
        commandList.undo();
        commandList.undo();
        commandList.redo();
        commandList.redo();

        final InOrder inOrder = inOrder(command1, command2, listener);
        inOrder.verify(command1).run();
        inOrder.verify(listener).onUndoRedoChanged(eq(true), eq("Command 1"), eq(false), eq(""));
        inOrder.verify(command2).run();
        inOrder.verify(listener).onUndoRedoChanged(eq(true), eq("Command 2"), eq(false), eq(""));
        inOrder.verify(command2).undo();
        inOrder.verify(listener).onUndoRedoChanged(eq(true), eq("Command 1"), eq(true), eq("Command 2"));
        inOrder.verify(command1).undo();
        inOrder.verify(listener).onUndoRedoChanged(eq(false), eq(""), eq(true), eq("Command 1"));
        inOrder.verify(command1).run();
        inOrder.verify(listener).onUndoRedoChanged(eq(true), eq("Command 1"), eq(true), eq("Command 2"));
        inOrder.verify(command2).run();
        inOrder.verify(listener).onUndoRedoChanged(eq(true), eq("Command 2"), eq(false), eq(""));
    }

    @Test(expected = NoSuchElementException.class)
    public void redoFailsTest() {
        commandList.redo();
    }

    @Test(expected = NoSuchElementException.class)
    public void runUndoRunRedoFailsTest() {
        commandList.runCommand(command1);
        commandList.undo();
        commandList.runCommand(command2);
        commandList.redo();
    }

    @Test
    public void commandRunFailsTest() {
        final Command commandFail = mock(Command.class);
        when(commandFail.run()).thenReturn(false);
        commandList.runCommand(command1);
        commandList.runCommand(commandFail);
        commandList.undo();
        verify(command1).undo();
    }

}
