package calculator.commands;

import calculator.IFraudCalculatorCommand;
import model.Exam;
import model.Student;

public class ExampleCommand implements IFraudCalculatorCommand {

    @Override
    public Double[] execute(Exam exam, Student currentStudent) {
        System.out.println("Executing example command ...");
        // Do nothing, it's just a simple example
        // Normally, you must return a score
        return null;
    }
}
