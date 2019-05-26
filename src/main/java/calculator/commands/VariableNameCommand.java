package calculator.commands;

import calculator.IFraudCalculatorCommand;
import filter.SpaceSeparatorFilter;
import model.Exam;
import model.Student;
import utils.DiffPatchMatch;

import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

public class VariableNameCommand implements IFraudCalculatorCommand {
    @Override
    public Double[] execute(Exam exam, Student currentStudent) {
        DiffPatchMatch diffPatchMatch = new DiffPatchMatch();
        Double[] scores = new Double[exam.getStudents().size()];
        scores[exam.getStudents().indexOf(currentStudent)] = 1.;

        String currentStudentCode = "; "+SpaceSeparatorFilter.removeSpaces(String.join(" ",currentStudent.getFilesLines()));
        exam.getStudents().forEach(student -> {
            if (student != currentStudent) {
                int score = 0;
                String studentCode = "; "+SpaceSeparatorFilter.removeSpaces(String.join(" ",student.getFilesLines()));
                LinkedList<DiffPatchMatch.Diff> diffs = diffPatchMatch.diff_main(studentCode,currentStudentCode);

                for (DiffPatchMatch.Diff diff : diffs) {
                    if (diff.operation == DiffPatchMatch.Operation.EQUAL) {
                        String[] words = diff.text.split(" ");
                        for(int i=0;i<words.length;i++){

                            if(words[i].charAt(words[i].length()-1) == ';' && words.length>=i+2){
                                String potentialClassName = words[i+1];
                                String[] privitiveClasses = {"int","char"};
                                List<String> LprimitiveClasses = Arrays.asList(privitiveClasses);
                                boolean isClass = Character.isUpperCase(potentialClassName.charAt(0))
                                        || LprimitiveClasses.contains(potentialClassName);
                                if(isClass){
                                    score++;
                                    i=i+2;//word i : "*;", word i+1 : "int"|"String"|..., word i+2 : variable name, these words are treated
                                }
                            }
                        }
                        System.out.println("[=] " + diff.text);
                    }
                    if (diff.operation == DiffPatchMatch.Operation.INSERT) {
                        System.out.print("[+] " + diff.text);
                    }
                    if (diff.operation == DiffPatchMatch.Operation.DELETE) {
                        System.out.println("[-] " + diff.text);
                    }
                }
                scores[exam.getStudents().indexOf(student)] = (double) score / (double) studentCode.length();
            }

        });
        return scores;
    }

}

