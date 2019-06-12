package calculator.commands;

import calculator.IFraudCalculatorCommand;
import filter.SpaceSeparatorFilter;
import model.Exam;
import model.Student;
import utils.Logger;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class VariableNameCommand implements IFraudCalculatorCommand {
    String[] primitiveClasses = {"int","char"};
    @Override
    public Double[] execute(Exam exam, Student currentStudent) {
        Double[] scores = new Double[exam.getStudents().size()];
        scores[exam.getStudents().indexOf(currentStudent)] = 1.;

        ArrayList<String> currentStudentCodeVars = getVars(String.join(" ",currentStudent.getFilesLines()));
        exam.getStudents().forEach(student -> {
            if (student != currentStudent) {
                ArrayList<String> studentCodeVars = getVars(String.join(" ",student.getFilesLines()));
                int nbVar = studentCodeVars.size();
                if(nbVar==0){
                    scores[exam.getStudents().indexOf(student)]=0.0;
                }else{
                    int nbCommonVars = 0;
                    for(String var : studentCodeVars) {
                        if (currentStudentCodeVars.contains(var)) {
                            nbCommonVars++;
                            Logger.info("[" + student.getName() + "][" + currentStudent.getName() + "] New common variable : " + var);
                        }
                    }
                    scores[exam.getStudents().indexOf(student)] = (double) nbCommonVars / (double) nbVar;

                }

            }

        });
        return scores;
    }

    private ArrayList<String> getVars(String studentCode){
        studentCode = "; "+SpaceSeparatorFilter.removeSpaces(studentCode);
        ArrayList<String> studentCodeVars = new ArrayList<>();
        String[] studentCodeWords = studentCode.split(" ");
        for(int i=0;i<studentCodeWords.length;i++){
            if(studentCodeWords.length>i+2 && studentCodeWords[i].length()!=0){
                char lastChar = studentCodeWords[i].charAt(studentCodeWords[i].length()-1);
                if( !(studentCodeWords[i+1]+" "+studentCodeWords[i+2]).contains("\"")
                        && !(studentCodeWords[i+1]+" "+studentCodeWords[i+2]).contains("'")
                        && (lastChar == ';' || lastChar == '{' || lastChar == '}')
                        && studentCodeWords.length>i+2
                        && studentCodeWords[i+1].length()!=0
                        && studentCodeWords[i+2].length()!=0 ) {
                    String potentialClassName2 = studentCodeWords[i + 1];
                    if (potentialClassName2.length() != 0) {
                        List<String> LprimitiveClasses = Arrays.asList(primitiveClasses);
                        boolean isClass = Character.isUpperCase(potentialClassName2.charAt(0))
                                || LprimitiveClasses.contains(potentialClassName2);
                        if (isClass) {
                            studentCodeVars.add(studentCodeWords[i+1]+" "+studentCodeWords[i+2]);
                            i = i + 2;//word i : "*;", word i+1 : "int"|"String"|..., word i+2 : variable name, these words are treated
                        }
                    }
                }
            }
        }
        return studentCodeVars;
    }

}

