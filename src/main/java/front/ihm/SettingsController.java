package front.ihm;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;

public class SettingsController {

    @FXML
    public void back(ActionEvent actionEvent) {
        ScreenController.getInstance().activate("fileChooser");
    }
}
