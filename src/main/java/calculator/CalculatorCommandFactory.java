package calculator;

import model.Exam;
import model.Student;

import java.util.Arrays;
import java.util.HashMap;

public class CalculatorCommandFactory {
    private final HashMap<String, IFraudCalculatorCommand> commands;
    private Exam exam;
    private Student currentStudent;

    private CalculatorCommandFactory(Exam exam, Student currentStudent) {
        this.commands = new HashMap<>();
        this.exam = exam;
        this.currentStudent = currentStudent;
    }

    public void addCommand(String name, IFraudCalculatorCommand command) {
        this.commands.put(name, command);
    }

    public void removeCommand(String name) { this.commands.remove(name); }

    public Double[] executeCommand(String name) {
        if (this.commands.containsKey(name) ) {
            return this.commands.get(name).execute(exam,currentStudent);
        }
        return null;
    }

    public Double[] executeAllCommands() {
        if (commands.size() == 0) {
            return null;
        }
        Double[] scores = new Double[exam.getStudents().size()];
        commands.keySet().forEach(commandName -> {
            Double[] commandScores = executeCommand(commandName);
            if (commandScores != null) {
                for (int i = 0 ; i < scores.length ; i++) {
                    scores[i] += commandScores[i];
                }
            }
        });
        return scores;
    }

    public void listCommands() {
        System.out.println("Commands enabled :");
        this.commands.keySet().stream().forEach(System.out::println);
    }

    public static CalculatorCommandFactory init(Exam exam, Student currentStudent) {
        CalculatorCommandFactory cf = new CalculatorCommandFactory(exam,currentStudent);
        //PUT SOME DEFAULT COMMANDS
        return cf;
    }
}
