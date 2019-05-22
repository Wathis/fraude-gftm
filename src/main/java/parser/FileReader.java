package parser;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Collection;

public class FileReader {

	public static Collection<String> parseFile(String fileName) throws Exception {

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
}
