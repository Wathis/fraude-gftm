package command;

import calculator.commands.SimilarCodeCommand;
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
import static utils.TestUtils.STUDENT_1;
import static utils.TestUtils.STUDENT_2;

@RunWith(Parameterized.class)
public class SimilarCodeCommandTest {

    @Parameterized.Parameters(name = "{2}")
    public static Collection<Object[]> data() {
        return Arrays.asList(new Object[][] {
                {
                    Arrays.asList(
                            "{",
                            "int azert = 0;",
                            "}"
                    ), Arrays.asList(
                            "{",
                            "int uiopq = 0;",
                            "}"
                    ),
                    0.75
                },
                {
                    Arrays.asList(
                            "{",
                            "}"
                    ), Arrays.asList(
                            "{",
                            "}"
                    ),
                    1
                },
                {
                    Arrays.asList(
                            "private static void checkDirectory(File directory) {",
                            "if (!directory.exists()) {",
                            "directory.mkdir();",
                            "} else {",
                            "cleanDirectory(directory);",
                            "directory.mkdir();",
                            "}",
                            "}"
                    ), Arrays.asList(
                            " try{",
                            " Launcher.compareStudents();",
                            "String path =  XlsWritter.write();",
                            "text.setText(\"Un fichier comparatif est disponible: \\n\"+path);",
                            "//scene.setRoot(new StudentView(scene));",
                            "} catch(Exception e){",
                            "e.printStackTrace();",
                            "},"
                    ),
                    0
                },
        });
    }

    @Parameterized.Parameter
    public List<String> firstCode;

    @Parameterized.Parameter(1)
    public List<String> secondCode;

    @Parameterized.Parameter(2)
    public double expectedScore;

    @Test
    public void testExecute() {
        Student firstStudent = new Student("","");
        firstStudent.setFiles(Arrays.asList(new File("",firstCode, AuthorType.STUDENT,firstStudent)));
        Student secondStudent = new Student("","");
        secondStudent.setFiles(Arrays.asList(new File("",secondCode, AuthorType.STUDENT,secondStudent)));
        Exam exam = new Exam(MODEL_PROF,Arrays.asList(firstStudent,secondStudent));
        SimilarCodeCommand similarCodeCommand = new SimilarCodeCommand();
        Double[] scores  = similarCodeCommand.execute(exam,firstStudent);
        Assert.assertEquals(expectedScore,scores[1],0.1);
    }
}

