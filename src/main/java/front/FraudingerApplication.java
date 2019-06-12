package front;

import calculator.CalculatorCommandFactory;
import filter.ModelSuppressionFilter;
import filter.SpaceSeparatorFilter;
import org.apache.poi.util.SystemOutLogger;
import utils.Logger;
import io.XLSWriter;
import model.Exam;
import model.Student;
import model.Teacher;
import parser.Unzipper;

import java.io.IOException;
import java.util.*;

import static parser.FileReader.addListOfFilesToPerson;

public class FraudingerApplication {

    public void launch() {
        Logger.info(
                "-------------------------------------------------------\n" +
                "  ______                   _ _                       \n" +
                " |  ____|                 | (_)                      \n" +
                " | |__ _ __ __ _ _   _  __| |_ _ __   __ _  ___ _ __ \n" +
                " |  __| '__/ _` | | | |/ _` | | '_ \\ / _` |/ _ \\ '__|\n" +
                " | |  | | | (_| | |_| | (_| | | | | | (_| |  __/ |   \n" +
                " |_|  |_|  \\__,_|\\__,_|\\__,_|_|_| |_|\\__, |\\___|_|   \n" +
                "                                      __/ |          \n" +
                "                                     |___/           \n" +
                "-------------------------------------------------------"
        );
        Scanner scanner = new Scanner(System.in);
//        Logger.info("Enter the student zip or folder location : \n > ");
//        String pathToUnzip = scanner.nextLine();
//        Logger.info("Enter a directory where you want to extract zip");
//        String pathToDest = scanner.nextLine();
//        Logger.info("Enter the model folder location or no if there is no model");
//        String modelPath = scanner.nextLine();
//        Logger.info("Enter the folder path you want the CSV result");
//        String resultFolder = scanner.nextLine();

        String pathToUnzip = "D:\\workspace\\fraude-gftm\\IHMA2S1-Lien de dépôt-47347_2017_TP4Note.zip";
        String pathToDest = "D:\\workspace\\fraude-gftm\\folderTounzip";
        String modelToDest = "D:\\workspace\\fraude-gftmUnzipedModel";
        String modelPath = "D:\\workspace\\fraude-gftm\\IHM_2017_TP4Note_NOM_prenom.zip";
        String resultFolder = "D:\\workspace\\fraude-gftm";

        Logger.info("Unzipping ...");

        if(modelPath.equals("no")){
            try {
                Unzipper.unzip(pathToUnzip,pathToDest,true,false);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }else{
            try {
                Unzipper.unzip(pathToUnzip,pathToDest,true,false);
                Unzipper.unzip(modelPath,modelToDest,false,true);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        Exam exam = new Exam(new ArrayList<>(),Unzipper.getStudents());
        exam.getStudents().forEach(student -> {
            addListOfFilesToPerson(student);
        });
        Teacher teacher = Unzipper.getTeacher();
        addListOfFilesToPerson(teacher);

        exam.setProfessorLines(teacher.getFilesLines());
        exam.accept(new ModelSuppressionFilter());
        exam.accept(new SpaceSeparatorFilter());

        for(Student currentStudent : exam.getStudents()){
           CalculatorCommandFactory factory = CalculatorCommandFactory.init(exam,currentStudent);
           HashMap<String,Double[]> commandsScores = factory.executeAllCommands();
            currentStudent.setScores(commandsScores);
            printCommandsForOneStudent(currentStudent,exam.getStudents(),commandsScores);
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

    private void printCommandsForOneStudent(Student student, List<Student> students, HashMap<String,Double[]> commandsScores) {
        Logger.info("\n---------------------------------");
        Logger.info("|       " + student.getName());
        Logger.info("---------------------------------");
        commandsScores.keySet().forEach((commandName) -> {
            Logger.info("\n----" + commandName.toUpperCase() + "----");
            int i = 0;
            for (Double score : commandsScores.get(commandName)) {
                Logger.info(students.get(i).getName() + " : " + score);
                i++;
            }
            Logger.info("\n");
        });
    }


}
