package model;//TODO changer de package

import java.util.HashMap;

public class Student {

    private String name;

    private String directoryPath;

    private HashMap<String, Double> scores;

    public Student(String name, String directoryPath) {
        this.name = name;
        this.directoryPath = directoryPath;
        this.scores = new HashMap<String,Double>();
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

    public HashMap<String, Double> getScores() {
        return scores;
    }

    public void setScores(HashMap<String, Double> scores) {
        this.scores = scores;
    }

    @Override
    public String toString() {
        return "Student{" +
                "name='" + name + '\'' +
                ", directoryPath='" + directoryPath + '\'' +
                '}';
    }

    public String getDirectoryPath() {
        return directoryPath;
    }
}
