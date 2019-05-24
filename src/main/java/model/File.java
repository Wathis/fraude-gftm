package model;

import java.util.Collection;

public class File {
	private String name;
	private Collection<String> lines;
	private AuthorType authorType;
	private Person author;

	public File(String name, Collection<String> lines, AuthorType authorType, Person person) {
		this.name = name;
		this.lines = lines;
		this.authorType = authorType;
		this.author = person;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public Collection<String> getLines() {
		return lines;
	}

	public void setLines(Collection<String> lines) {
		this.lines = lines;
	}

	public AuthorType getAuthorType() {
		return authorType;
	}

	public void setAuthorType(AuthorType authorType) {
		this.authorType = authorType;
	}

	public Person getAuthor() {
		return author;
	}

	public void setAuthor(Person author) {
		this.author = author;
	}
}
