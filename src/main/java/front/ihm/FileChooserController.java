package front.ihm;

import calculator.CalculatorCommandFactory;
import filter.FilterFactory;
import io.XLSWriter;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.stage.FileChooser;
import model.Exam;
import model.Student;
import model.Teacher;
import parser.Unzipper;
import utils.Logger;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;

import static parser.FileReader.addListOfFilesToPerson;


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
            runCommands(studentsZipFile.getParent() + File.separator + "out/");
        }).start();
    }

    public void runCommands(String resultFolder) {
        Exam exam = new Exam(new ArrayList<>(),Unzipper.getStudents());
        exam.getStudents().forEach(student -> {
            addListOfFilesToPerson(student);
        });
        Teacher teacher = Unzipper.getTeacher();
        addListOfFilesToPerson(teacher);

        Logger.info("Applying filter ...");

        exam.setProfessorLines(teacher.getFilesLines());
        FilterFactory.getInstance().executeAllActivatedFilters(exam);

        Logger.info("Starting commands ...");
        for(Student currentStudent : exam.getStudents()){
            CalculatorCommandFactory factory = CalculatorCommandFactory.init(exam,currentStudent);
            HashMap<String,Double[]> commandsScores = factory.executeAllCommands();
            currentStudent.setScores(commandsScores);
        }
        exam.sortStudentsByScore();
        for(Student currentStudent : exam.getStudents()){
            System.out.println(currentStudent.getName()+" - "+currentStudent.getMaxScore());
        }

        try {
            XLSWriter.write(exam.getStudents(),resultFolder);
        } catch (IOException e) {
            Logger.err("Impossible d'exporter au format csv");
            Logger.err(e.getMessage());
        }
    }

    @FXML
    public void settings(ActionEvent actionEvent) {
        ScreenController.getInstance().activate("settings");
    }

}
