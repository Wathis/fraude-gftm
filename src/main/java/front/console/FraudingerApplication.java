package front.console;

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

        String pathToUnzip = "/Users/mathisdelaunay/Desktop/triche/DEPOT2019.zip";
        String pathToDest = "/Users/mathisdelaunay/Desktop/triche";
        String modelToDest = "/Users/mathisdelaunay/Desktop/triche";
        String modelPath = "/Users/mathisdelaunay/Desktop/triche/IHM_Projet_JDK8.zip";
        String resultFolder = "/Users/mathisdelaunay/Desktop/triche/out";

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
        startCommands(resultFolder);
    }

    public static void startCommands(String resultFolder) {
        Exam exam = new Exam(new ArrayList<>(),Unzipper.getStudents());
        exam.getStudents().forEach(student -> {
            addListOfFilesToPerson(student);
        });
        Teacher teacher = Unzipper.getTeacher();
        addListOfFilesToPerson(teacher);

        Logger.info("Applying filter ...");

        exam.setProfessorLines(teacher.getFilesLines());
        exam.accept(new ModelSuppressionFilter());
        exam.accept(new SpaceSeparatorFilter());

        Logger.info("Starting commands ...");
        for(Student currentStudent : exam.getStudents()){
            CalculatorCommandFactory factory = CalculatorCommandFactory.init();
            HashMap<String,Double[]> commandsScores = factory.executeAllCommands(exam,currentStudent);
            currentStudent.setScores(commandsScores);
            printCommandsForOneStudent(currentStudent,exam.getStudents(),commandsScores);
        }

        try {
            XLSWriter.write(exam,resultFolder);
        } catch (IOException e) {
            Logger.err("Impossible d'exporter au format csv");
            Logger.err(e.getMessage());
        }
    }

    private static void printCommandsForOneStudent(Student student, List<Student> students, HashMap<String,Double[]> commandsScores) {
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
