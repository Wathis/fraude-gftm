package calculator;

import model.Exam;
import model.Student;

public interface IFraudCalculatorCommand {

    Double[] execute(Exam exam, Student currentStudent);

}
