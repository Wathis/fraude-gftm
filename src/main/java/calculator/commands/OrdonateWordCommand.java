package calculator.commands;

import calculator.IFraudCalculatorCommand;
import filter.SpaceSeparatorFilter;
import model.Exam;
import model.Student;
import utils.DiffPatchMatch;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

public class OrdonateWordCommand implements IFraudCalculatorCommand {

    @Override
    public Double[] execute(Exam exam, Student currentStudent) {
        DiffPatchMatch diffPatchMatch = new DiffPatchMatch();
        Double[] scores = new Double[exam.getStudents().size()];
        scores[exam.getStudents().indexOf(currentStudent)] = 1.;

        String[] notWords={"{","}","(",")","[","]"};
        List<String> LnotWords =  Arrays.asList(notWords);

        String currentStudentCode = SpaceSeparatorFilter.removeSpaces(String.join(" ",currentStudent.getFilesLines()));
        exam.getStudents().forEach(student -> {
            if (student != currentStudent) {
                int score = 0;
                int nbWords=0;
                String studentCode = SpaceSeparatorFilter.removeSpaces(String.join(" ",student.getFilesLines()));
                LinkedList<DiffPatchMatch.Diff> diffs = diffPatchMatch.diff_main(studentCode,currentStudentCode);

                for (DiffPatchMatch.Diff diff : diffs) {
                    String[] words = diff.text.split(" ");
                    if (diff.operation == DiffPatchMatch.Operation.EQUAL) {
                        for(int i=0;i<words.length;i++){
                            if(words[i].length() > 0 && !LnotWords.contains(words[i])){
                                score++;
                                nbWords++;
                            }
                        }
                        System.out.println("[=] " + diff.text);
                    }
                    if (diff.operation == DiffPatchMatch.Operation.INSERT) {
                        for(int i=0;i<words.length;i++) {
                            if (words[i].length() > 0 && !LnotWords.contains(words[i])) {
                                score++;
                                nbWords++;
                            }
                        }
                        System.out.print("[+] " + diff.text);
                    }
                    if (diff.operation == DiffPatchMatch.Operation.DELETE) {
                        for(int i=0;i<words.length;i++) {
                            if (words[i].length() > 0 && !LnotWords.contains(words[i])) {
                                score++;
                                nbWords++;
                            }
                        }
                        System.out.println("[-] " + diff.text);
                    }
                }
                scores[exam.getStudents().indexOf(student)] = (double) score / (double) nbWords;
            }

        });
        return scores;
    }

}
