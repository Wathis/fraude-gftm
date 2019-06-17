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
    private final HashMap<String,IFraudCalculatorCommand> availableCommands = new HashMap<String, IFraudCalculatorCommand>()
    {{
        put("Coding habits",new CodingHabitsCommand());
        put("Ordonate word",new OrdonateWordCommand());
        put("Similar code",new SimilarCodeCommand());
        put("Variable name",new VariableNameCommand());
    }};

    private static CalculatorCommandFactory instance;

    private CalculatorCommandFactory() {
        this.commands = new HashMap<>();
    }

    public void addCommand(String name, IFraudCalculatorCommand command) {
        this.commands.put(name, command);
    }

    public void removeCommand(String name) { this.commands.remove(name); }

    public void removeAllCommands() { this.commands.clear(); }

    public Double[] executeCommand(String name,Exam exam, Student currentStudent) {
        if (this.commands.containsKey(name) ) {
            Logger.info("[" + currentStudent.getName() + "] Executing " + name);
            return this.commands.get(name).execute(exam,currentStudent);
        }
        Logger.err("[" + currentStudent.getName() + "] Can't execute " + name + ". The command doesn't exist" );
        return null;
    }

    public HashMap<String,Double[]> executeAllCommands(Exam exam, Student currentStudent) {
        HashMap<String, Double[]> commandsScores = new HashMap<>();
        if (commands.size() == 0) {
            return null;
        }
        commands.keySet().forEach(commandName -> {
            Double[] commandScores = executeCommand(commandName,exam,currentStudent);
            commandsScores.put(commandName,commandScores);
        });
        return commandsScores;
    }

    public HashMap<String, IFraudCalculatorCommand> getCommands() {
        return commands;
    }

    public HashMap<String, IFraudCalculatorCommand> getAvailableCommands() {
        return availableCommands;
    }

    public void listCommands() {
        Logger.info("Commands enabled :");
        this.commands.keySet().stream().forEach(System.out::println);
    }

    public static CalculatorCommandFactory init() {
        CalculatorCommandFactory cf = getInstance();
        cf.removeAllCommands();
        cf.addCommand("Similar code", new SimilarCodeCommand());
        cf.addCommand("Coding habits", new CodingHabitsCommand());
        cf.addCommand("Variable name", new VariableNameCommand());
        cf.addCommand("Ordonate word", new OrdonateWordCommand());
        return cf;
    }


    public static CalculatorCommandFactory getInstance() {
        if (instance == null) {
            instance = new CalculatorCommandFactory();
            init();
        }
        return instance;
    }

}
