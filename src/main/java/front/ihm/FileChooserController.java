package front.ihm;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.stage.FileChooser;
import parser.Unzipper;
import utils.Logger;

import java.io.File;
import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

import static front.console.FraudingerApplication.startCommands;


public class FileChooserController {

    @FXML private Label studentZipPathLabel;
    @FXML private Label modelZipPathLabel;
    @FXML private TextArea textAreaConsole;
    @FXML private Button startButton;

    private File modelZipFile;
    private File studentsZipFile;

    @FXML
    public void initialize() {
        Logger.setLoggerView(textAreaConsole);
    }

    @FXML
    public void handleChooseModelZipFile(ActionEvent actionEvent) {
        FileChooser fileChooser = new FileChooser();
        FileChooser.ExtensionFilter fileExtensions = new FileChooser.ExtensionFilter("ZIP", "*.zip");
        fileChooser.getExtensionFilters().add(fileExtensions);
        fileChooser.setTitle("Open Resource File");
        File file = fileChooser.showOpenDialog(modelZipPathLabel.getScene().getWindow());
        if (file != null) {
            modelZipPathLabel.setText(file.getName());
            modelZipFile = file;
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
            studentsZipFile = file;
            startButton.setDisable(false);
        }
    }

    @FXML
    public void handleStart(ActionEvent actionEvent) {
        new Thread(() -> {
            try {
                if (studentsZipFile != null) {
                    String outDir = studentsZipFile.getAbsolutePath().substring(0, studentsZipFile.getAbsolutePath().lastIndexOf('.'));
                    if (modelZipFile == null) {
                        Logger.info("Unzipping students ...",true);
                        Unzipper.unzip(studentsZipFile.getAbsolutePath(),outDir,true,false);
                        Logger.info("Unzipping done.",true);
                    }
                    if (modelZipFile != null) {
                        Logger.info("Unzipping students ...",true);
                        Unzipper.unzip(studentsZipFile.getAbsolutePath(),outDir, true, false);
                        Logger.info("Unzipping done.",true);
                        Logger.info("Unzipping model ...",true);
                        Unzipper.unzip(modelZipFile.getAbsolutePath(),modelZipFile.getParent(), false, true);
                        Logger.info("Unzipping done.",true);
                    }
                }
            } catch (IOException e) {
                Logger.err("Unzipping failed :\n" + e.getMessage(),true);
                e.printStackTrace();
                return;
            }
            startCommands(studentsZipFile.getParent() + File.separator + "out/");
        }).start();
    }

    @FXML
    public void settings(ActionEvent actionEvent) {
        ScreenController.getInstance().activate("settings");
    }

}
