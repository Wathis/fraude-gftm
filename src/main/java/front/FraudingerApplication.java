package front;

import calculator.CalculatorCommandFactory;
import filter.ModelSuppressionFilter;
import filter.SpaceSeparatorFilter;
import model.Exam;
import model.Student;
import model.Teacher;
import parser.FileReader;
import parser.Unzipper;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Scanner;

import static parser.FileReader.addListOfFilesToPerson;

public class FraudingerApplication {

    public void launch() {
        System.out.println(
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
//        System.out.print("Enter the student zip or folder location : \n > ");
//        String pathToUnzip = scanner.nextLine();
//        System.out.println("Enter a directory where you want to extract zip");
//        String pathToDest = scanner.nextLine();
//        System.out.println("Enter the model folder location or no if there is no model");
//        String modelPath = scanner.nextLine();

        String pathToUnzip = "/Users/mathisdelaunay/Desktop/mprojet/projet.zip";
        String pathToDest = "/Users/mathisdelaunay/Desktop/mprojet";
        String modelToDest = "/Users/mathisdelaunay/Desktop/mprojet";
        String modelPath = "/Users/mathisdelaunay/Desktop/mprojet/model.zip";
        System.out.println("Unzipping ...");
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

        for(Student currentStrudent : exam.getStudents()){
           CalculatorCommandFactory factory = CalculatorCommandFactory.init(exam,currentStrudent);
           HashMap<String,Double[]> commandsScores = factory.executeAllCommands();
           printCommandsForOneStudent(currentStrudent,exam.getStudents(),commandsScores);
        }
    }

    private void printCommandsForOneStudent(Student student, List<Student> students, HashMap<String,Double[]> commandsScores) {
        System.out.println("\n---------------------------------");
        System.out.println("|       " + student.getName());
        System.out.println("---------------------------------");
        commandsScores.keySet().forEach((commandName) -> {
            System.out.println("\n----" + commandName.toUpperCase() + "----");
            int i = 0;
            for (Double score : commandsScores.get(commandName)) {
                System.out.println(students.get(i).getName() + " : " + score);
                i++;
            }
            System.out.println("\n");
        });
    }

}
