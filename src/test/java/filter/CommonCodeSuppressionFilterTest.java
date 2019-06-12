package filter;

import static utils.TestUtils.STUDENT_IMPORTATION;
import static utils.TestUtils.STUDENT_IMPORTATION2;
import static utils.TestUtils.STUDENT_IMPORTATION3;

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
public class CommonCodeSuppressionFilterTest {

	@Parameterized.Parameters(name = "{index}")
	public static Collection<Object[]> data() {
		return Arrays.asList(new Object[][] { { STUDENT_IMPORTATION, STUDENT_IMPORTATION2, STUDENT_IMPORTATION3,
				"import java.util.Arrays; // Je met un commentaireif (salut > 0) {" }, });
	}

	@Parameterized.Parameter
	public List<String> studentCode;

	@Parameterized.Parameter(1)
	public List<String> studentCode2;

	@Parameterized.Parameter(2)
	public List<String> studentCode3;

	@Parameterized.Parameter(3)
	public String expectedResult;

	@Test
	public void nominalTest() {
		Student student = new Student("test", "");
		Student student2 = new Student("test2", "");
		Student student3 = new Student("test3", "");
		student.setFiles(Arrays.asList(new File("", studentCode, AuthorType.STUDENT, student)));
		student2.setFiles(Arrays.asList(new File("", studentCode2, AuthorType.STUDENT, student2)));
		student3.setFiles(Arrays.asList(new File("", studentCode3, AuthorType.STUDENT, student3)));

		Exam exam = new Exam(null, Arrays.asList(student, student2, student3));
		exam.accept(new CommonCodeSuppressionFilter());

		List<String> fileLines = exam.getStudents().get(0).getFilesLines();
		String result = "";
		StringBuilder lines = new StringBuilder();
		for (String string : fileLines) {
			lines.append(string);
		}
		result = lines.toString();
		System.out.println("RESULT : \n " + result.trim());
		System.out.println("EXPECTED : \n" + expectedResult.trim());
		//Assert.assertEquals(expectedResult.trim(), result);
	}
}
