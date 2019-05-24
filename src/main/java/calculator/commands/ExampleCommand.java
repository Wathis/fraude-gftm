package calculator.commands;

import calculator.IFraudCalculatorCommand;

public class ExampleCommand implements IFraudCalculatorCommand {

    @Override
    public double execute() {
        System.out.println("Executing example command ...");
        // Do nothing, it's just a simple example
        // Normally, you must return a score
        return 0;
    }
}
