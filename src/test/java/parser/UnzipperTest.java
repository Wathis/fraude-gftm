package parser;

import static org.junit.Assert.*;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;

@RunWith(Parameterized.class)
public class UnzipperTest {

	@Parameterized.Parameters(name = "{0}")
    public static Collection<Object[]> data() {
        return Arrays.asList(new Object[][] {
                {"src/test/ressources/modele.zip","src/test/ressources/DEPOT2019-1.zip"}
        });
    }

    @Parameterized.Parameter
    public String pathZipModele;

    @Parameterized.Parameter(1)
    public String pathZipStudents;

    /**
     * Test for the unzip of the model
     * @throws IOException
     */
	@Test
	public void testModeleUnzip() throws IOException {
		File modeleFile = new File(pathZipModele);
		String repositoryZip = modeleFile.getAbsolutePath();
		modeleFile = new File(repositoryZip);
		String repositoryTemp = modeleFile.getParent()+"/temp";
		File repositoryFile = new File(repositoryTemp);
		Unzipper.unzip(pathZipModele, repositoryTemp, false, true);
		FileReader reader = new FileReader();
		List<File> liste = reader.getFilesFromDirectory(repositoryFile);
		assertTrue(!liste.isEmpty());
	}

	/**
	 * Test the unzip of the zip for students
	 * @throws IOException
	 */
	@Test
	public void testStudentUnzip() throws IOException {
		File studentFile = new File(pathZipStudents);
		String repositoryZip = studentFile.getAbsolutePath();
		studentFile = new File(repositoryZip);
		String repositoryTemp = studentFile.getParent()+"/temp";
		File repositoryFile = new File(repositoryTemp);
		Unzipper.unzip(pathZipStudents, repositoryTemp, true, false);
		FileReader reader = new FileReader();
		List<File> liste = reader.getFilesFromDirectory(repositoryFile);
		assertTrue(!liste.isEmpty());
	}

}
