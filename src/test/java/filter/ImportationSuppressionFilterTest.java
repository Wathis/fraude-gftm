package filter;

import static utils.TestUtils.STUDENT_MINUS_IMPORTATION;
import static utils.TestUtils.STUDENT_IMPORTATION;

import java.util.Arrays;
import java.util.Collection;
import java.util.List;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;

import model.AuthorType;
import model.Exam;
import model.File;
import model.Student;

@RunWith(Parameterized.class)
public class ImportationSuppressionFilterTest {

	@Parameterized.Parameters(name = "{index}")
	public static Collection<Object[]> data() {
		return Arrays.asList(new Object[][] { {STUDENT_IMPORTATION, STUDENT_MINUS_IMPORTATION }, });
	}

	@Parameterized.Parameter
	public List<String> studentCode;

	@Parameterized.Parameter(1)
	public String expectedResult;

	@Test
	public void nominalTest(){
		Student student = new Student("test","");
        student.setFiles(Arrays.asList(new File("",studentCode, AuthorType.STUDENT,student)));
        Exam exam = new Exam(null,Arrays.asList(student));
        exam.accept(new ImportationSuppressionFilter());
        List<String> fileLines = exam.getStudents().get(0).getFilesLines();
        String result = "";
        StringBuilder lines = new StringBuilder();
        for (String string : fileLines) {
			lines.append(string);
		}
        result = lines.toString();
        Assert.assertEquals(expectedResult.trim(),result);
	}

}
