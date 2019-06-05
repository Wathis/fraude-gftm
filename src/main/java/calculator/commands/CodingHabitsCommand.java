package calculator.commands;

import calculator.IFraudCalculatorCommand;
import filter.SpaceSeparatorFilter;
import model.Exam;
import model.Student;
import utils.DiffPatchMatch;

import java.util.Arrays;
import java.util.List;

public class CodingHabitsCommand implements IFraudCalculatorCommand {

    @Override
    public Double[] execute(Exam exam, Student currentStudent) {
        DiffPatchMatch diffPatchMatch = new DiffPatchMatch();
        Double[] scores = new Double[exam.getStudents().size()];
        Arrays.fill(scores,-1.);
        scores[exam.getStudents().indexOf(currentStudent)] = 1.;
        String camelCasePattern = "([a-z0-9]+[A-Z0-9]+\\w+)+";
        String snakeCasePattern = "[a-z0-9]*(_[a-z0-9]+)*";

        String currentStudentCode = "; "+SpaceSeparatorFilter.removeSpaces(String.join(" ",currentStudent.getFilesLines()));
        int camelCaseWordNumber = 0;
        int snakeCaseWordNumber = 0;
        int numberOfWord = 0;
        String[] words = currentStudentCode.split(" ");
        for(int i=0;i<words.length;i++){
            if(words[i].length() > 0 && words[i].charAt(words[i].length()-1) == ';' && words.length>=i+2){
                String potentialClassName = words[i+1];
                if (potentialClassName.length() > 0) {
                    String[] privitiveClasses = {"int","char"};
                    List<String> LprimitiveClasses = Arrays.asList(privitiveClasses);
                    boolean isClassOrPrimitiveType = Character.isUpperCase(potentialClassName.charAt(0))
                            || LprimitiveClasses.contains(potentialClassName);
                    if(isClassOrPrimitiveType){
                        numberOfWord++;
                        i=i+2;//word i : "*;", word i+1 : "int"|"String"|..., word i+2 : variable name, these words are treated
                        boolean isCamelCase = words[i].matches(camelCasePattern);
                        boolean isSnakeCase = words[i].matches(snakeCasePattern);
                        if (isCamelCase) {
                            camelCaseWordNumber++;
                        }
                        if (isSnakeCase) {
                            snakeCaseWordNumber++;
                        }
                    }
                }
            }
        }

        int maxBetweenSnakeAndCamel = Math.max(camelCaseWordNumber,snakeCaseWordNumber);
        if (maxBetweenSnakeAndCamel == 0) {
            scores[exam.getStudents().indexOf(currentStudent)] = 0.;
            return scores;
        }
        scores[exam.getStudents().indexOf(currentStudent)] = (double) Math.min(camelCaseWordNumber,snakeCaseWordNumber) / (double)(camelCaseWordNumber + snakeCaseWordNumber);
        return scores;
    }

}
