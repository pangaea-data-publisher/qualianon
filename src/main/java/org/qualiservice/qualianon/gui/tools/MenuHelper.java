package org.qualiservice.qualianon.gui.tools;

import javafx.scene.control.MenuItem;
import org.qualiservice.qualianon.model.commands.CommandRunner;
import org.qualiservice.qualianon.model.commands.MoveInListCommand;
import org.qualiservice.qualianon.model.commands.RemoveFromListCommand;
import org.qualiservice.qualianon.utility.ListProperty;
import org.qualiservice.qualianon.utility.Updatable;

import java.util.function.Function;


public class MenuHelper {

    public static <E extends Updatable> MenuItem getDeleteMenuItem(E element, ListProperty<E> list, Function<E, Boolean> preconditionCheck, CommandRunner commandRunner) {
        final MenuItem deleteMenu;
        deleteMenu = new MenuItem("Delete");
        deleteMenu.setOnAction(actionEvent -> {
            if (preconditionCheck != null) {
                final Boolean preconditionOk = preconditionCheck.apply(element);
                if (!preconditionOk) return;
            }
            final RemoveFromListCommand<E> command = new RemoveFromListCommand<>(element, list);
            commandRunner.runCommand(command);
        });
        return deleteMenu;
    }

    public static <E extends Updatable> MenuItem getMoveUpMenuItem(E element, ListProperty<E> list, CommandRunner commandRunner) {
        final MenuItem moveUpMenu;
        moveUpMenu = new MenuItem("Move up");
        final int index = list.indexOf(element);
        if (index > 0) {
            moveUpMenu.setOnAction(actionEvent -> {
                final MoveInListCommand<E> command = new MoveInListCommand<>(element, list, -1);
                commandRunner.runCommand(command);
            });
        } else {
            moveUpMenu.setDisable(true);
        }
        return moveUpMenu;
    }

    public static <E extends Updatable> MenuItem getMoveDownMenuItem(E element, ListProperty<E> list, CommandRunner commandRunner) {
        final MenuItem moveDownMenu;
        moveDownMenu = new MenuItem("Move down");
        final int index = list.indexOf(element);
        if (index < list.size() - 1) {
            moveDownMenu.setOnAction(actionEvent -> {
                final MoveInListCommand<E> command = new MoveInListCommand<>(element, list, 1);
                commandRunner.runCommand(command);
            });
        } else {
            moveDownMenu.setDisable(true);
        }
        return moveDownMenu;
    }


}
