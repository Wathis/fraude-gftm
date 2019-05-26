package model;

import java.util.HashMap;
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;

public class Student extends Person{


    private HashMap<String, Double> scores;

    public Student(String name, String directoryPath) {
        super(name,directoryPath);
        this.scores = new HashMap<String,Double>();
    }

    public Student(String name, String directoryPath, List<File> files) {
        super(name,directoryPath);
        setFiles(files);
    }


    public HashMap<String, Double> getScores() {
        return scores;
    }

    public void setScores(HashMap<String, Double> scores) {
        this.scores = scores;
    }

    @Override
    public String toString() {
        return "Student{" +
                "name='" + getName() + '\'' +
                ", directoryPath='" + getDirectoryPath() + '\'' +
                '}';
    }
}
