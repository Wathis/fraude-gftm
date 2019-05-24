package model;

import filter.FilterVisitor;
import model.Student;

import java.util.List;

public class Exam {

    private List<String> professorLines;
    private List<Student> students;

    public Exam(List<String> professorLines, List<Student> students) {
        this.professorLines =  professorLines;
        this.students = students;
    }

    public void accept(FilterVisitor filterVisitor) {
        filterVisitor.visit(this);
    }

    public List<String> getProfessorLines() {
        return professorLines;
    }

    public void setProfessorLines(List<String> professorLines) {
        this.professorLines = professorLines;
    }

    public List<Student> getStudents() {
        return students;
    }

    public void setStudents(List<Student> students) {
        this.students = students;
    }
}
