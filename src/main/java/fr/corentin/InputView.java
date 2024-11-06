package fr.corentin.views;

import fr.corentin.controllers.MainController;
import fr.corentin.controllers.viewcontrollers.InputViewController;

import javafx.scene.control.TextField;
import javafx.stage.Stage;
import javafx.scene.layout.VBox;
import javafx.scene.control.Label;
import javafx.scene.control.Button;
import javafx.geometry.Pos;
import javafx.stage.FileChooser;

import java.io.File;

public class InputView extends DefaultView {
    
    private final InputViewController _controller;

    private final TextField _inputTextfield = new TextField();
    private final Label _errorLabel = new Label();

    public InputView(Stage stage, MainController main) {
        super(stage);

        this._controller = new InputViewController(main);

        this._errorLabel.setStyle("-fx-text-fill: red; -fx-font-size: 16px;");
    }

    @Override
    public InputView build() {
        
        VBox root = new VBox();
        root.setAlignment(Pos.CENTER);

        Label inputLabel = new Label("Enter input file path here:");

        Button button_file = new Button("Choose file !");
        button_file.setOnAction(actionEvent -> choose());

        Button button_go = new Button("GO !");
        button_go.setOnAction(actionEvent -> go());

        root.getChildren().addAll(inputLabel, this._inputTextfield, button_file, button_go, this._errorLabel);
        super.setContent(root);

        return this;
    }

    private void choose() {

        FileChooser fileChooser = new FileChooser();

        File selectedFile = fileChooser.showOpenDialog(super._stage);

        if (selectedFile != null) {
            this._inputTextfield.setText(selectedFile.getAbsolutePath());
            this._controller.process_file(selectedFile);
        }
    }

    private void go() {

        String content = this._inputTextfield.getText();

        if (content.isEmpty())
            this._errorLabel.setText("Text field is empty");
        else {
            this._errorLabel.setText(null);

            File file = new File(content);

            if (!file.isFile())
                this._errorLabel.setText("Invalid filepath");
            else
                this._controller.process_file(file);
        }
    }
}