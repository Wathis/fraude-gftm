package calculator.commands;

import calculator.IFraudCalculatorCommand;
import filter.SpaceSeparatorFilter;
import model.Exam;
import model.Student;
import lib.DiffPatchMatch;

import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

public class OrdonateWordCommand implements IFraudCalculatorCommand {

    @Override
    public Double[] execute(Exam exam, Student currentStudent) {
        DiffPatchMatch diffPatchMatch = new DiffPatchMatch();
        Double[] scores = new Double[exam.getStudents().size()];
        scores[exam.getStudents().indexOf(currentStudent)] = -1.;

        String[] notWords={};//Sert a bannir des mots de la recherche (au cas où il y ai certain groupes de caractère
                             // qu'on ne veut pas considérer comme des mots à débugger
        List<String> LnotWords =  Arrays.asList(notWords);

        String currentStudentCode = SpaceSeparatorFilter.removeSpaces(String.join(" ",currentStudent.getFilesLines()));
        exam.getStudents().forEach(student -> {
            if (student != currentStudent) {
                int score = 0;
                int nbWords=0;
                String studentCode = SpaceSeparatorFilter.removeSpaces(String.join(" ",student.getFilesLines()));
                LinkedList<DiffPatchMatch.Diff> diffs = diffPatchMatch.diff_main(studentCode,currentStudentCode);

                String[] studentCodeWords = studentCode.split(" ");
                for(String word : studentCodeWords){
                    if(word.length() > 3 && !LnotWords.contains(word)){
                        nbWords++;
                    }
                }

                if(nbWords==0){
                    scores[exam.getStudents().indexOf(student)] = 0.0;
                }else{
                    for (DiffPatchMatch.Diff diff : diffs) {
                        String[] words = diff.text.split(" ");
                        if (diff.operation == DiffPatchMatch.Operation.EQUAL) {
                            for(int i=0;i<words.length;i++){
                                if(words[i].length() > 3 && !LnotWords.contains(words[i])){
                                    score++;
                                }
                            }
                        }
                    }
                    scores[exam.getStudents().indexOf(student)] = (double) score / (double) nbWords;
                }
            }

        });
        return scores;
    }

}
