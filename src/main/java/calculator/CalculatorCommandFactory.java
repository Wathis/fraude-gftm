package calculator;

import calculator.commands.CodingHabitsCommand;
import calculator.commands.OrdonateWordCommand;
import calculator.commands.SimilarCodeCommand;
import calculator.commands.VariableNameCommand;
import utils.Logger;
import model.Exam;
import model.Student;

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

    public HashMap<String,Double[]> executeAllCommands() {
        HashMap<String, Double[]> commandsScores = new HashMap<>();
        if (commands.size() == 0) {
            return null;
        }
        commands.keySet().forEach(commandName -> {
            Double[] commandScores = executeCommand(commandName);
            commandsScores.put(commandName,commandScores);
        });
        return commandsScores;
    }

    public void listCommands() {
        Logger.info("Commands enabled :");
        this.commands.keySet().stream().forEach(System.out::println);
    }

    public static CalculatorCommandFactory init(Exam exam, Student currentStudent) {
        CalculatorCommandFactory cf = new CalculatorCommandFactory(exam,currentStudent);
        cf.addCommand("Similar code command", new SimilarCodeCommand());
        cf.addCommand("Coding habits", new CodingHabitsCommand());
        cf.addCommand("Variable Name Command", new VariableNameCommand());
        cf.addCommand("Ordonate Word Command", new OrdonateWordCommand());
        return cf;
    }
}
