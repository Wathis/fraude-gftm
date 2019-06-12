package parser;

import model.Student;
import model.Teacher;

import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

/**
 * This class permits to take a zip archive and to extract all the file to a
 * destination. Create the instance of Student during the process. This list is
 * static
 *
 * @author Thomas
 *
 */
public class Unzipper {

	/**
	 * Size of the buffer to read/write data
	 */
	private static final int BUFFER_SIZE = 4096;

	/**
	 * List of students
	 */
	private static List<Student> students;

	private static Teacher teacher;

	private Unzipper() {
		throw new IllegalStateException("Utility class");
	}

	/**
	 * Get the list of students
	 *
	 * @return the List of students
	 */
	public static List<Student> getStudents() {
		return students;
	}

	/**
	 * get the teacher to access to the path the model
	 * @return
	 */
	public static Teacher getTeacher(){
		return teacher;
	}

	/**
	 * Extracts a zip file specified by the zipFilePath to a directory specified
	 * by destDirectory (will be created if does not exists)
	 *
	 * @param zipFilePath
	 *            the path of the zip file
	 * @param destDirectory
	 *            the path to save unzip files
	 * @throws IOException
	 */
	public static void unzip(String zipFilePath, String destDirectory, boolean recursive, boolean modele)
			throws IOException {

		if (students == null) {
			students = new ArrayList<Student>();
		}

		ZipInputStream zipIn = null;

		try {
			zipIn = new ZipInputStream(new FileInputStream(zipFilePath));
			ZipEntry entry = zipIn.getNextEntry();

			// iterates over entries in the zip file
			while (entry != null) {
				createFiles(destDirectory, zipIn, entry, recursive, modele);
				zipIn.closeEntry();
				entry = zipIn.getNextEntry();
			}

		} catch (IOException e) {
			System.err.println(e.getMessage());

		} finally {
			if (zipIn != null) {
				zipIn.close();
			}
		}
	}

	/**
	 * Create the ZipEntry file or directory and its children
	 *
	 * @param destDirectory
	 *            path where create the file
	 * @param zipIn
	 *            the zip input stream
	 * @param entry
	 *            the zip entry
	 * @throws IOException
	 */
	private static void createFiles(String destDirectory, ZipInputStream zipIn, ZipEntry entry, boolean recursive,
			boolean modele) throws IOException {

		String filePath = destDirectory + File.separator + entry.getName();
		if (!entry.isDirectory()) {

			boolean isZipFile = filePath.endsWith(".zip");
			if (modele && (teacher == null) && !entry.getName().contains("/bin/")) {
				File file = new File(filePath);
				String modeleDir = file.getParentFile().getAbsolutePath();
				File dir = new File(modeleDir);
				dir.mkdir();
				teacher = new Teacher("modele", modeleDir);
			}
			if (isZipFile) {
				createParent(filePath);
				writeFile(zipIn, filePath);
				if (recursive) {
					if (!filePath.contains("_MACOSX")) {
						String studentDir = addStudentToList(entry, filePath);
						unzip(filePath, studentDir, false, false);
					}
				}
			} else {
				writeFile(zipIn, filePath);
			}
		} else {
			File dir = new File(filePath);
			dir.mkdir();
		}
	}

	private static String addStudentToList(ZipEntry entry, String filePath) {
		File file = new File(filePath);

		String studentName = getStudentName(entry);

		String studentDir = file.getParentFile().getAbsolutePath() + "/" + studentName;
		File dir = new File(studentDir);
		dir.mkdir();
		students.add(new Student(studentName, studentDir));
		return studentDir;
	}

	private static String getStudentName(ZipEntry entry) {
		String parts[] = entry.getName().split("/");
		String studentName;

		if (parts.length > 1) {
			studentName = parts[1].split("_")[0];
		} else {
			studentName = entry.getName().split("_")[0];
		}
		return studentName;
	}

	/**
	 * Extracts a zip entry (file entry)
	 *
	 * @param zipIn
	 *            the ZipInputStream
	 * @param filePath
	 *            the file path
	 * @throws IOException
	 */
	private static void writeFile(ZipInputStream zipIn, String filePath) throws IOException {
		createParent(filePath);

		BufferedOutputStream bos = null;

		try {
			bos = new BufferedOutputStream(new FileOutputStream(filePath));
			byte[] bytesIn = new byte[BUFFER_SIZE];

			int read = 0;
			while ((read = zipIn.read(bytesIn)) != -1) {
				bos.write(bytesIn, 0, read);
			}

		} catch (IOException e) {
			System.err.println(e.getMessage());

		} finally {
			if (bos != null) {
				bos.close();
			}
		}
	}

	private static void createParent(String filePath) {
		String parent = "";
		if(filePath.contains("/"))
			parent = filePath.replace(filePath.substring(filePath.lastIndexOf('/')), "");
		else
			parent = filePath.replace(filePath.substring(filePath.lastIndexOf('\\')), "");
		createDirectory(parent);
	}

	/**
	 * Create the tree files of the given path
	 *
	 * @param path
	 *            path of our tree
	 */
	private static void createDirectory(String path) {

		File file = new File(path);
		if (!file.exists()) {
			file.mkdirs();
		}
	}

	private static String removeExtension(String path) {
		return path.replace(path.substring(path.lastIndexOf('.')), "");
	}
}
