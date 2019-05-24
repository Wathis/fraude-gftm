package command;

import calculator.CalculatorCommandFactory;
import calculator.commands.ExampleCommand;
import calculator.commands.SimilarWordOrderedCommand;
import model.AuthorType;
import model.Exam;
import model.File;
import model.Student;
import org.junit.Assert;
import org.junit.Test;
import utils.DiffPatchMatch;

import java.util.Arrays;
import java.util.LinkedList;

import static utils.TestUtils.MODEL_PROF;
import static utils.TestUtils.STUDENT_1;
import static utils.TestUtils.STUDENT_2;

public class SimilarWordOrderedCommandTest {

    @Test
    public void testExecute() {
        Student firstStudent = new Student("","");
        firstStudent.setFiles(Arrays.asList(new File("",STUDENT_2, AuthorType.STUDENT,firstStudent)));
        Student secondStudent = new Student("","");
        secondStudent.setFiles(Arrays.asList(new File("",STUDENT_1, AuthorType.STUDENT,secondStudent)));
        Exam exam = new Exam(MODEL_PROF,Arrays.asList(firstStudent,secondStudent));
        SimilarWordOrderedCommand similarWordOrderedCommand = new SimilarWordOrderedCommand();
        similarWordOrderedCommand.execute(exam,firstStudent);
    }
}

