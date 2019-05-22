package model;

import java.util.Collection;

public class File {
    private Collection<String> lines;
    private AuthorType authorType;
    private Student author;

    public File(Collection<String> lines, AuthorType authorType, Student author) {
        this.lines = lines;
        this.authorType = authorType;
        this.author = author;
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

    public Student getAuthor() {
        return author;
    }

    public void setAuthor(Student author) {
        this.author = author;
    }
}
