package calculator;

import java.util.HashMap;

public class CommandFactory {
    private final HashMap<String, IFraudCalculatorCommand> commands;

    private CommandFactory() {
        this.commands = new HashMap<>();
    }

    public void addCommand(String name, IFraudCalculatorCommand command) {
        this.commands.put(name, command);
    }

    public void executeCommand(String name) {
        if (this.commands.containsKey(name) ) {
            this.commands.get(name).execute();
        }
    }

    public void listCommands() {
        System.out.println("Commands enabled :");
        this.commands.keySet().stream().forEach(System.out::println);
    }

    public static CommandFactory init() {
        CommandFactory cf = new CommandFactory();
        // Initialize commands
        return cf;
    }
}
