package command;

import calculator.CalculatorCommandFactory;
import calculator.commands.VariableNameCommand;
import model.AuthorType;
import model.Exam;
import model.File;
import model.Student;
import org.junit.Assert;
import org.junit.Test;

import java.util.Arrays;
import java.util.HashMap;

import static utils.TestUtils.*;

public class CalculatorCommandFactoryTest {

    @Test
    public void testExampleCommand() {
        Student firstStudent = new Student("","");
        firstStudent.setFiles(Arrays.asList(new File("",STUDENT_2, AuthorType.STUDENT,firstStudent)));
        Student secondStudent = new Student("","");
        secondStudent.setFiles(Arrays.asList(new File("",STUDENT_1, AuthorType.STUDENT,secondStudent)));
        Exam exam = new Exam(MODEL_PROF,Arrays.asList(firstStudent,secondStudent));
        CalculatorCommandFactory calculatorCommandFactory = CalculatorCommandFactory.init(exam,firstStudent);
        calculatorCommandFactory.addCommand("Example Command", new VariableNameCommand());
        HashMap<String,Double[]> score = calculatorCommandFactory.executeAllCommands();
        Assert.assertNotNull(score);
    }

}
