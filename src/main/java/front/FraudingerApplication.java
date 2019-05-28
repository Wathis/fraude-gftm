package front;

import calculator.CalculatorCommandFactory;
import model.Exam;
import model.Student;
import parser.Unzipper;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Scanner;

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
        System.out.print("Enter the student zip or folder location : \n > ");
        Scanner scanner = new Scanner(System.in);
        String pathToUnzip = scanner.nextLine();
        System.out.println("Enter a directory where you want to extract zip");
        String pathToDest = scanner.nextLine();
        System.out.println("Enter the model folder location or no if there is no model");
        String modelPath = scanner.nextLine();
        if(modelPath.equals("no")){
            try {
                Unzipper.unzip(pathToUnzip,pathToDest,true,false);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }else{
            try {
                Unzipper.unzip(pathToUnzip,pathToDest,false,true);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        System.out.println("Unzipping ...");

        Exam exam = new Exam(new ArrayList<>(),Unzipper.getStudents());
        ArrayList <Student>  studentArrayList = new ArrayList<>( Unzipper.getStudents());

        for(Student currentStrudent : studentArrayList){
           CalculatorCommandFactory factory = CalculatorCommandFactory.init(exam,currentStrudent);
            System.out.println(factory.executeAllCommands());
        }
    }

}
