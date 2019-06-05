package io;

import model.Student;
import org.junit.Assert;
import org.junit.Test;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.HashMap;

public class XLSWriterTest {


    final String FOLDER_OUT = "src/test/ressources/xls/";
    final String FIRST_COMMAND_SCORE = "FIRST_COMMAND_SCORE";
    final String SECOND_COMMAND_SCORE = "SECOND_COMMAND_SCORE";

    @Test
    public void testWrite() throws IOException {

        deleteDirectory(new File(FOLDER_OUT));

        Student jeanStudent = new Student("Jean",File.separator + "home" + File.separator + "jean.zip");

        HashMap<String,Double[]> jeanScores = new HashMap<>();
        jeanScores.put(FIRST_COMMAND_SCORE,new Double[]{1.0,0.0});
        jeanScores.put(SECOND_COMMAND_SCORE,new Double[]{0.0,1.0});
        jeanStudent.setScores(jeanScores);

        Student baptisteStudent = new Student("Baptiste",File.separator + "home" + File.separator + "baptiste.zip");

        HashMap<String,Double[]> baptisteScore = new HashMap<>();
        baptisteScore.put(FIRST_COMMAND_SCORE,new Double[]{0.0,1.0});
        baptisteScore.put(SECOND_COMMAND_SCORE,new Double[]{1.0,0.0});
        baptisteStudent.setScores(baptisteScore);

        XLSWriter.write(Arrays.asList(jeanStudent,baptisteStudent),FOLDER_OUT);
        File firstCommand = new File(FOLDER_OUT + File.separator + FIRST_COMMAND_SCORE + ".xlsx");
        File secondCommand = new File(FOLDER_OUT + File.separator + SECOND_COMMAND_SCORE + ".xlsx");

        Assert.assertTrue(firstCommand.exists() );
        Assert.assertTrue(secondCommand.exists() );
    }

    boolean deleteDirectory(File directoryToBeDeleted) {
        File[] allContents = directoryToBeDeleted.listFiles();
        if (allContents != null) {
            for (File file : allContents) {
                deleteDirectory(file);
            }
        }
        return directoryToBeDeleted.delete();
    }
}
