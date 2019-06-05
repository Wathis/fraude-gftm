package parser;

import model.AuthorType;
import model.Person;

import java.io.*;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Comparator;
import java.util.List;

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
	public static void addListOfFilesToPerson(Person person) {
		String directoryPath = person.getDirectoryPath();
		File directory = new File(directoryPath);
		// File system
		List<File> files = getFilesFromDirectory(directory);
		// File of our model
		List<model.File> personFiles = new ArrayList<model.File>();
		for (File file : files) {
			model.File actualFile = null;
			try {
				actualFile = new model.File(file.getName(),getLinesFromFile(file), AuthorType.STUDENT, person);
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			personFiles.add(actualFile);
		}
		sortListAlphabetically(personFiles);
		person.setFiles(personFiles);
	}

	/**
	 * Browse the directory and each time it find a File that is not a
	 * directory, add it to the list
	 *
	 * @param directory
	 * @return
	 */
	public static List<File> getFilesFromDirectory(File directory) {
		List<File> files = new ArrayList<File>();
		// Get the name of all the child in the directory
		String[] nameChilden = directory.list();

		for (String nameFile : nameChilden) {
			File fileChild = new File(directory.getAbsolutePath() + File.separator + nameFile);
			// If it's a directory, recall the method and add the result to the
			// list
			if (fileChild.isDirectory()) {
				files.addAll(getFilesFromDirectory(fileChild));
			}
			// Temp to only get source
			if(nameFile.contains(".java")){
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

	private static void sortListAlphabetically(List<model.File> personFiles){
		personFiles.sort(Comparator.comparing(model.File::getName));
	}
}
