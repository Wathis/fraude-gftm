package ihm.controller;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.stage.FileChooser;

import java.io.File;

public class SettingsController {

    @FXML
    public void back(ActionEvent actionEvent) {
        ScreenController.getInstance().activate("fileChooser");
    }
}
