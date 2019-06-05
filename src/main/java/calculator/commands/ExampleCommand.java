package calculator.commands;

import calculator.IFraudCalculatorCommand;
import utils.Logger;
import model.Exam;
import model.Student;

public class ExampleCommand implements IFraudCalculatorCommand {

    @Override
    public Double[] execute(Exam exam, Student currentStudent) {
        Logger.info("Executing example command ...");
        // Do nothing, it's just a simple example
        // Normally, you must return a score
        return null;
    }
}
