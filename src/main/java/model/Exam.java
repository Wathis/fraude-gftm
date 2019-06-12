package model;

import filter.FilterVisitor;

import java.util.*;

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

    public void sortStudentsByScore(){
        List<Student> students = this.getStudents();
        for (Student student : students){
            HashMap<String , Double[]> studentScores = student.getScores();
            Iterator it = studentScores.entrySet().iterator();
            Double max = 0d;
            while (it.hasNext()) {
                Map.Entry pair = (Map.Entry)it.next();
                Double[] scores = (Double[]) pair.getValue();

                for (Double score : scores){
                    if(score > max){
                        max = score;
                    }
                }
            }
            student.setMaxScore(max);
        }
        Collections.sort(students, Comparator.comparing(Student::getMaxScore));
        this.setStudents(students);
    }

    public List<Student> getStudents() {
        return students;
    }

    public void setStudents(List<Student> students) {
        this.students = students;
    }
}
