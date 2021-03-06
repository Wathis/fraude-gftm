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
    String[] primitiveClasses = {"byte","short","int","long","float","double","char","boolean"};
    @Override
    public Double[] execute(Exam exam, Student currentStudent) {
        Double[] scores = new Double[exam.getStudents().size()];
        scores[exam.getStudents().indexOf(currentStudent)] = -1.;

        ArrayList<String> currentStudentCodeVars = getVars(String.join(" ",currentStudent.getFilesLines()));
        exam.getStudents().forEach(student -> {
            if (student != currentStudent) {
                ArrayList<String> studentCodeVars = getVars(String.join(" ",student.getFilesLines()));
                int nbVar = studentCodeVars.size();
                if(nbVar==0){
                    scores[exam.getStudents().indexOf(student)]=0.0;
                } else {
                    int score=0;
                    for(String var : studentCodeVars) {
                        if (currentStudentCodeVars.contains(var)) {
                            Logger.info("[" + student.getName() + "][" + currentStudent.getName() + "] New common variable : " + var);
                            String varName = var.split(" ")[1];//On ne compte pas le type pour la taille de la variable
                            score+=Math.pow(varName.length(),2);//On met à la puissance 2 car plus le nom de la variable est long, plus c'est louche (de maniere quadratique)
                        }
                    }
                    scores[exam.getStudents().indexOf(student)] = Math.min((double) score/1000,1);
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

