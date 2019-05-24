package command;

import calculator.commands.SimilarCodeCommand;
import model.AuthorType;
import model.Exam;
import model.File;
import model.Student;
import org.junit.Assert;
import org.junit.Test;

import java.util.Arrays;
import java.util.List;

import static utils.TestUtils.MODEL_PROF;
import static utils.TestUtils.STUDENT_1;
import static utils.TestUtils.STUDENT_2;

public class SimilarCodeCommandTest {


    public static final List<String> FIRST_CODE = Arrays.asList(
            "{",
            "int azert = 0;",
            "}"
    );

    public static final List<String> SECOND_CODE = Arrays.asList(
            "{",
            "int uiopq = 0;",
            "}"
    );


    @Test
    public void testExecute() {
        Student firstStudent = new Student("","");
        firstStudent.setFiles(Arrays.asList(new File("",FIRST_CODE, AuthorType.STUDENT,firstStudent)));
        Student secondStudent = new Student("","");
        secondStudent.setFiles(Arrays.asList(new File("",SECOND_CODE, AuthorType.STUDENT,secondStudent)));
        Exam exam = new Exam(MODEL_PROF,Arrays.asList(firstStudent,secondStudent));
        SimilarCodeCommand similarCodeCommand = new SimilarCodeCommand();
        Double[] scores  = similarCodeCommand.execute(exam,firstStudent);
        Assert.assertEquals(0.72,scores[1],0.01); // Because of \n added
    }
}

