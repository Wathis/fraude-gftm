package command;


import calculator.commands.CodingHabitsCommand;
import model.AuthorType;
import model.Exam;
import model.File;
import model.Student;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;

import java.util.Arrays;
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;

import static utils.TestUtils.MODEL_PROF;

@RunWith(Parameterized.class)
public class CodingHabitsCommandTest {

    @Parameterized.Parameters
    public static Collection<Object[]> data() {
        return Arrays.asList(new Object[][] {
                {
                        Arrays.asList(
                            "int snake_case_var_1 = 0;",
                            "int snake_case_var_2 = 0;",
                            "int snake_case_var_3 = 0;",
                            "int snake_case_var_5 = 0;",
                            "int camelCaseVar1 = 0;",
                            "int camelCaseVar2 = 0;"
                        ),
                        0.25
                },
                {
                        Arrays.asList(
                            ""
                        ),
                        0.
                },
        });
    }

    @Parameterized.Parameter(0)
    public List<String> firstCode;

    @Parameterized.Parameter(1)
    public double expectedScore;

    @Test
    public void testExecute() {
        Assert.assertNotNull(firstCode);
        Student firstStudent = new Student("","");
        firstStudent.setFiles(Arrays.asList(new File("",firstCode, AuthorType.STUDENT,firstStudent)));
        Student secondStudent = new Student("","");
        secondStudent.setFiles(Arrays.asList(new File("",new LinkedList<>(), AuthorType.STUDENT,secondStudent)));
        Exam exam = new Exam(MODEL_PROF,Arrays.asList(firstStudent,secondStudent));
        CodingHabitsCommand codingHabitsCommand = new CodingHabitsCommand();
        Double[] scores  = codingHabitsCommand.execute(exam,firstStudent);
        Assert.assertEquals(expectedScore,scores[0],0.1);
    }

}
