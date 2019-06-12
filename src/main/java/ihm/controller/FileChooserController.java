package ihm.controller;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.stage.FileChooser;

import java.io.File;


public class FileChooserController {

    @FXML private Label studentZipPathLabel;
    @FXML private Label modelZipPathLabel;

    @FXML
    public void handleChooseModelZipFile(ActionEvent actionEvent) {
        FileChooser fileChooser = new FileChooser();
        FileChooser.ExtensionFilter fileExtensions = new FileChooser.ExtensionFilter("ZIP", "*.zip");
        fileChooser.getExtensionFilters().add(fileExtensions);
        fileChooser.setTitle("Open Resource File");
        File file = fileChooser.showOpenDialog(modelZipPathLabel.getScene().getWindow());
        if (file != null) {
            modelZipPathLabel.setText(file.getName());
        }
    }

    @FXML
    public void handleChooseStudentsZipFile(ActionEvent actionEvent) {
        FileChooser fileChooser = new FileChooser();
        FileChooser.ExtensionFilter fileExtensions = new FileChooser.ExtensionFilter("ZIP", "*.zip");
        fileChooser.getExtensionFilters().add(fileExtensions);
        fileChooser.setTitle("Open Resource File");
        File file = fileChooser.showOpenDialog(studentZipPathLabel.getScene().getWindow());
        if (file != null) {
            studentZipPathLabel.setText(file.getName());
        }
    }

    @FXML
    public void startCommands(ActionEvent actionEvent) {
        System.out.println("Starting commands");
    }

    @FXML
    public void settings(ActionEvent actionEvent) {
        ScreenController.getInstance().activate("settings");
    }
}
