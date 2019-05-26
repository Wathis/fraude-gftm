package calculator.commands;

import calculator.IFraudCalculatorCommand;
import model.Exam;
import model.Student;
import utils.DiffPatchMatch;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;

public class SimilarCodeCommand implements IFraudCalculatorCommand {

    /**
     * The student code must be formatted for this command
     * @param exam
     * @param currentStudent
     * @return
     */
    @Override
    public Double[] execute(Exam exam, Student currentStudent) {
        DiffPatchMatch diffPatchMatch = new DiffPatchMatch();
        Double[] scores = new Double[exam.getStudents().size()];
        scores[exam.getStudents().indexOf(currentStudent)] = 1.;
        exam.getStudents().forEach(student -> {
            if (student != currentStudent) {
                int score = 0;
                String studentCode = String.join(" ",student.getFilesLines());
                LinkedList<DiffPatchMatch.Diff> diffs = diffPatchMatch.diff_main(studentCode,String.join(" ",currentStudent.getFilesLines()));

                for (DiffPatchMatch.Diff diff : diffs) {
                    if (diff.operation == DiffPatchMatch.Operation.EQUAL) {
                        if (diff.text.length() > 1) // Avoiding one character comparison
                            score += diff.text.length();
                        System.out.println("[=] " + diff.text);
                    }
                    if (diff.operation == DiffPatchMatch.Operation.INSERT) {
                        System.out.println("[+] " + diff.text);
                    }
                    if (diff.operation == DiffPatchMatch.Operation.DELETE) {
                        System.out.println("[-] " + diff.text);
                    }
                }
                scores[exam.getStudents().indexOf(student)] = (double) score / ((double) studentCode.length());
            }

        });
        return scores;
    }
}

