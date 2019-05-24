package model;

import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;

public class Person {

	private String name;

	private String directoryPath;

	private Collection<File> files;

	private List<String> fileLines;

	public Person(String name, String directoryPath) {
		super();
		this.name = name;
		this.directoryPath = directoryPath;
	}

	public void setFileLines(List<String> fileLines) {
		this.fileLines = fileLines;
	}

	public List<String> getFilesLines() {
		if (fileLines != null) {
			return fileLines;
		}
		List<String> fileLines = new LinkedList<>();
		for (File file : files) {
			fileLines.addAll(file.getLines());
		}
		return fileLines;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public void setDirectoryPath(String directoryPath) {
		this.directoryPath = directoryPath;
	}

	public String getDirectoryPath() {
		return directoryPath;
	}

	public Collection<File> getFiles() {
		return this.files;
	}

	public void setFiles(Collection<File> files) {
		this.files = files;
	}

}
