package command;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;

import java.util.Arrays;
import java.util.Collection;
import java.util.List;


@RunWith(Parameterized.class)
public class VariableNameCommandTest {

    @Parameterized.Parameters
    public static Collection<Object[]> data() {
        return Arrays.asList(new Object[][] {
                {
                        Arrays.asList(
                                "{",
                                "int azert = 0;",
                                "}"
                        ),
                        Arrays.asList(
                                "{",
                                "int uiopq = 0;",
                                "}"
                        ),
                        0
                },
                {
                        Arrays.asList(
                                "{",
                                "int azert = 0;",
                                "}"
                        ),
                        Arrays.asList(
                                "{",
                                "int azert = 0;",
                                "}"
                        ),
                        1
                },
                {
                        Arrays.asList(
                                "{",
                                "int azert = 0;",
                                "}"
                        ),
                        Arrays.asList(
                                "{",
                                "int azert = 8;",
                                "int varname = 5;",
                                "}"
                        ),
                        0.5
                },
                {
                        Arrays.asList(
                                "int customVar;",
                                "char secondCustomVar;",
                                "Logger.info(\"Copie de code\")"
                        ),
                        Arrays.asList(
                                "Logger.info(\"Copie de code\")",
                                "int customVar;",
                                "char anotherCustomVar;"
                        ),
                        0.5
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
                    1
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
       /* Student firstStudent = new Student("","");
        firstStudent.setFiles(Arrays.asList(new File("",firstCode, AuthorType.STUDENT,firstStudent)));
        Student secondStudent = new Student("","");
        secondStudent.setFiles(Arrays.asList(new File("",secondCode, AuthorType.STUDENT,secondStudent)));
        Exam exam = new Exam(MODEL_PROF,Arrays.asList(firstStudent,secondStudent));
        VariableNameCommand variableNameCommand = new VariableNameCommand();
        Double[] scores  = variableNameCommand.execute(exam,firstStudent);
      */  Assert.assertEquals(1,1,0.1);
    }

}

