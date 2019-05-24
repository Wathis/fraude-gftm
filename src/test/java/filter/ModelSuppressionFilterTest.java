package filter;


import model.AuthorType;
import model.Exam;
import model.File;
import model.Student;
import org.junit.Assert;
import org.junit.Test;

import java.util.Arrays;

import static utils.TestUtils.MODEL_PROF;
import static utils.TestUtils.MODEL_PROF_MINUS_STUDENT_2;
import static utils.TestUtils.STUDENT_2;

public class ModelSuppressionFilterTest {

    @Test
    public void testNominalCase() {
        Student student = new Student("","");
        student.setFiles(Arrays.asList(new File(STUDENT_2, AuthorType.STUDENT,student)));
        Exam exam = new Exam(MODEL_PROF,Arrays.asList(student));
        exam.accept(new ModelSuppressionFilter());
        String fileLines = exam.getStudents().get(0).getFilesLines().toString();
        Assert.assertEquals(MODEL_PROF_MINUS_STUDENT_2,fileLines);
    }
}
