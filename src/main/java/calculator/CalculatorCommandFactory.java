package calculator;

import java.util.HashMap;

public class CalculatorCommandFactory {
    private final HashMap<String, IFraudCalculatorCommand> commands;

    private CalculatorCommandFactory() {
        this.commands = new HashMap<>();
    }

    public void addCommand(String name, IFraudCalculatorCommand command) {
        this.commands.put(name, command);
    }

    public void removeCommand(String name) { this.commands.remove(name); }

    public double executeCommand(String name) {
        if (this.commands.containsKey(name) ) {
            return this.commands.get(name).execute();
        }
        return -1;
    }

    public double executeAllCommands() {
        if (commands.size() == 0) {
            return 0;
        }
        double score = commands.keySet().stream().mapToDouble(this::executeCommand).sum();
        return score / commands.size();
    }

    public void listCommands() {
        System.out.println("Commands enabled :");
        this.commands.keySet().stream().forEach(System.out::println);
    }

    public static CalculatorCommandFactory init() {
        CalculatorCommandFactory cf = new CalculatorCommandFactory();
        //PUT SOME DEFAULT COMMANDS
        return cf;
    }
}
