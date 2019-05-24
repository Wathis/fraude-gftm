package parser;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.eclipse.jgit.util.FileUtils;

import model.AuthorType;
import model.Student;

public class FileReader {

	public static Collection<String> parseFile(String fileName) throws Exception {

		// Si fichier commence par . -> fichier de conf
		FileInputStream fileStream = null;
		BufferedReader br = null;

		fileStream = new FileInputStream(fileName);
		br = new BufferedReader(new InputStreamReader(fileStream));

		Collection<String> lines = new ArrayList<String>();
		String strLine;

		// Read ClassFile Line By Line
		while ((strLine = br.readLine()) != null) {
			lines.add(strLine);
		}

		closeStreams(fileStream, br);
		return lines;
	}

	/**
	 * Close the streams that are used to read the file
	 *
	 * @param fstream
	 * @param br
	 */

	private static void closeStreams(FileInputStream fstream, BufferedReader br) throws Exception {
		if (br != null) {
			try {
				br.close();
			} catch (IOException e) {
				System.err.println(e.getMessage());
			}
		}

		if (fstream != null) {
			try {
				fstream.close();
			} catch (IOException e) {
				System.err.println(e.getMessage());
			}
		}
	}

	/**
	 * Take a student and add the List of lines that corresponds of his files
	 * (from his directory path)
	 *
	 * @param student
	 * @throws IOException
	 */
	public static void addListOfFilesToStudent(Student student) throws IOException {
		String directoryPath = student.getDirectoryPath();
		File directory = new File(directoryPath);
		// File system
		Collection<File> files = getFilesFromDirectory(directory);
		// File of our model
		Collection<model.File> studentFiles = new ArrayList<model.File>();
		for (File file : files) {
			studentFiles.add(new model.File(getLinesFromFile(file), AuthorType.STUDENT, student));
		}
		student.setFiles(studentFiles);
	}

	/**
	 * Browse the directory and each time it find a File that is not a
	 * directory, add it to the list
	 *
	 * @param directory
	 * @return
	 */
	private static Collection<File> getFilesFromDirectory(File directory) {
		Collection<File> files = new ArrayList<File>();
		// Get the name of all the child in the directory
		String[] nameChilden = directory.list();

		for (String nameFile : nameChilden) {
			File fileChild = new File(directory.getAbsolutePath() + "\\" + nameFile);
			// If it's a directory, recall the method and add the result to the
			// list
			if (fileChild.isDirectory()) {
				files.addAll(getFilesFromDirectory(fileChild));
			} else {
				files.add(fileChild);
			}
		}
		return files;
	}

	/**
	 * Get all the content lines of a system File
	 *
	 * @param file
	 * @return
	 * @throws IOException
	 */
	private static Collection<String> getLinesFromFile(File file) throws IOException {
		FileInputStream inputStream = new FileInputStream(file);
		BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream));

		Collection<String> lines = new ArrayList<String>();
		String line;
		while ((line = bufferedReader.readLine()) != null) {
			lines.add(line);
		}
		try {
			closeStreams(inputStream, bufferedReader);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return lines;

	}
}
