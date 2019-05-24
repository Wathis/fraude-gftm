package filter;


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
import java.util.List;

import static utils.TestUtils.MODEL_PROF;
import static utils.TestUtils.MODEL_PROF_MINUS_STUDENT_2;
import static utils.TestUtils.STUDENT_2;

@RunWith(Parameterized.class)
public class ModelSuppressionFilterTest {

    @Parameterized.Parameters(name = "{index}")
    public static Collection<Object[]> data() {
        return Arrays.asList(new Object[][] {
                {MODEL_PROF, STUDENT_2,MODEL_PROF_MINUS_STUDENT_2},
        });
    }

    @Parameterized.Parameter
    public List<String> profCode;

    @Parameterized.Parameter(1)
    public List<String> studentCode;

    @Parameterized.Parameter(2)
    public String expectedResult;

    @Test
    public void testNominalCase() {
        Student student = new Student("","");
        student.setFiles(Arrays.asList(new File("",studentCode, AuthorType.STUDENT,student)));
        Exam exam = new Exam(profCode,Arrays.asList(student));
        exam.accept(new ModelSuppressionFilter());
        String fileLines = exam.getStudents().get(0).getFilesLines().toString();
        Assert.assertEquals(expectedResult,fileLines);
    }
}
