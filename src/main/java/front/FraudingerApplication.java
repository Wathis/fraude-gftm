package front;

import model.Exam;

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
        System.out.print("Enter a directory zip or folder location : \n > ");
        Scanner scanner = new Scanner(System.in);
        String path = scanner.nextLine();
        System.out.println("Unzipping ...");
    }

}
