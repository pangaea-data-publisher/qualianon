package org.qualiservice.qualianon.gui.components;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.RadioButton;
import javafx.scene.control.Slider;
import javafx.scene.control.ToggleGroup;
import javafx.stage.Stage;
import org.qualiservice.qualianon.utility.UserPreferences;

import java.net.URL;
import java.util.ResourceBundle;

public class PreferencesController implements Initializable {

    private final Stage stage;
    private MainController mainController;
    // Import Controls
    @FXML
    public Slider lineWidthSlider;
    @FXML
    public ToggleGroup lineBreakGroup;
    @FXML
    public RadioButton preserveParagraphButton;
    @FXML
    public RadioButton breakLineAtNCharactersButton;
    private boolean breakLine;
    private String radioButtonText;
    // Export Controls
    @FXML
    public ToggleGroup lineNumberingGroup;
    @FXML
    public RadioButton enableLineNumberingButton;
    @FXML
    public RadioButton disableLineNumberingButton;
    private boolean enableLineNumbering;

    public PreferencesController(Stage stage, MainController mainController) {
        this.stage = stage;
        this.mainController = mainController;
        this.breakLine = UserPreferences.getLoadedLinebreakSettings();
        this.enableLineNumbering = UserPreferences.getExportLineNumbers();
        this.radioButtonText = String.format("Break lines at %d Characters", UserPreferences.getLoadedDocLineWidth());
    }

    private void IntializeSlider() {
        lineWidthSlider.setMajorTickUnit(10);
        lineWidthSlider.setMinorTickCount(0); // No minor ticks
        lineWidthSlider.setBlockIncrement(10);
        lineWidthSlider.setSnapToTicks(true);
        lineWidthSlider.setValue(UserPreferences.getLoadedDocLineWidth());
        lineWidthSlider.valueProperty().addListener((observable, oldValue, newValue) -> {
            handleSliderValueChange(newValue.intValue());
        });
    }

    private void IntializeLineBreakButtons() {
        IntializeSlider();
        breakLineAtNCharactersButton.setText(this.radioButtonText);
        if (UserPreferences.getLoadedLinebreakSettings()) {
            lineBreakGroup.selectToggle(breakLineAtNCharactersButton);
            lineWidthSlider.setDisable(false);
        } else {
            lineBreakGroup.selectToggle(preserveParagraphButton);
            lineWidthSlider.setDisable(true);
        }
    }

    private void IntializeLineNumberingButtons() {
        if (UserPreferences.getExportLineNumbers()) {
            lineNumberingGroup.selectToggle(enableLineNumberingButton);
        } else {
            lineNumberingGroup.selectToggle(disableLineNumberingButton);
        }

    }
    @Override
    public void initialize(URL location, ResourceBundle resources) {
        this.stage.setResizable(false);
        this.stage.setTitle("Import/Export Preferences");
        IntializeLineBreakButtons();
        IntializeLineNumberingButtons();

    }

    //Import Controls
    @FXML
    private void handleBreakLineAtNCharacters(ActionEvent event) {
        lineWidthSlider.setDisable(false);
        this.breakLine = true;
    }
    @FXML
    private void handlePreserveParagraphButton(ActionEvent event) {
        lineWidthSlider.setDisable(true);
        this.breakLine = false;
    }
    @FXML
    private void handleSliderValueChange(int newValue) {
        this.radioButtonText = String.format("Break lines at %d Characters", newValue);
        breakLineAtNCharactersButton.setText(this.radioButtonText);
    }
    //Export Controls
    @FXML
    private void handleEnableLineNumbering(ActionEvent event){
        this.enableLineNumbering=true;
    }
    @FXML
    private void handleDisableLineNumbering(ActionEvent event){
        this.enableLineNumbering=false;
    }
    //General Controls
    @SuppressWarnings("unused")
    public void onSavePreferences(ActionEvent actionEvent) {
        System.out.println(lineWidthSlider.getValue());
        UserPreferences.saveDocLineWidth((int) lineWidthSlider.getValue());
        UserPreferences.saveLinebreakSettings(this.breakLine);
        UserPreferences.saveExportLineNumbers(enableLineNumbering);
        stage.close();
    }

    public void onCancelPreferences(ActionEvent actionEvent) {
        stage.close();
    }
}
