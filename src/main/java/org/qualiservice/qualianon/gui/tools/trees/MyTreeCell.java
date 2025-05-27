package org.qualiservice.qualianon.gui.tools.trees;

import javafx.scene.control.*;
import javafx.scene.input.KeyCode;


public class MyTreeCell extends TreeCell<ItemAdapter> {

    private ItemAdapter boundAdapter;

    @Override
    protected void updateItem(ItemAdapter adapter, boolean empty) {
        super.updateItem(adapter, empty);
        clear();
        boundAdapter = null;
        if (empty) return;

        boundAdapter = adapter;
        setContent(adapter);
    }

    @Override
    public void startEdit() {
        if (boundAdapter == null || boundAdapter.getEditMenuItem() == null) return;
        super.startEdit();

        clear();
        final TextField textField = boundAdapter.getEditableTextField();
        setGraphic(textField);
        textField.requestFocus();
        textField.selectAll();
        textField.setOnKeyPressed(keyEvent -> {
            if (keyEvent.getCode() == KeyCode.ENTER) {
                boundAdapter.commitEdit();
                commitEdit(boundAdapter);
            } else if (keyEvent.getCode() == KeyCode.ESCAPE) {
                cancelEdit();
            }
        });
        textField.focusedProperty().addListener((observableValue, aBoolean, t1) -> {
            if (!t1) {
                cancelEdit();
            }
        });
    }

    @Override
    public void commitEdit(ItemAdapter newValue) {
        super.commitEdit(newValue);
    }

    @Override
    public void cancelEdit() {
        if(!isEditing()) return;
        super.cancelEdit();
        clear();
        setContent(boundAdapter);
    }

    private void clear() {
        setText(null);
        setGraphic(null);
        setContextMenu(null);
    }

    private void setContent(ItemAdapter adapter) {
        setGraphic(adapter.getNode());
        if (adapter.getContextMenu() != null) {
            setContextMenu(adapter.getContextMenu());
        }
        setEditable(adapter.getEditMenuItem() != null);
        if (adapter.getEditMenuItem() != null) {
            adapter.getEditMenuItem().setOnAction(actionEvent -> {
                actionEvent.consume();
                startEdit();
            });
        }
    }

}
