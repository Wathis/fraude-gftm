package filter;

import model.CompatibleLanguage;
import model.Exam;
import model.Student;

import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

public class KeywordFilter implements FilterVisitor {

    private final String javaKeywords[] = {"abstract","boolean","break","byte",
            "case","catch","char","class","const","continue","default",
            "do","double","else","extends","final","finally","float",
            "for","goto","if","implements","import","instanceof","int",
            "interface","long","native","new","null","package","private",
            "protected","public","return","short","static","super","switch",
            "synchronized","this","throw","throws","transient","try","void",
            "volatile","while","assert","enum","strictfp"};

    private String keywords[];


    public KeywordFilter() {
        setUpKeywordLanguage(CompatibleLanguage.JAVA);
    }

    public KeywordFilter(CompatibleLanguage language) {
        setUpKeywordLanguage(language);
    }


    private void setUpKeywordLanguage(CompatibleLanguage language) {
        switch (language) {
            case JAVA:
                keywords = javaKeywords;
                break;
            default:
                keywords = javaKeywords;
        }
    }

    @Override
    public void visit(Exam exam) {
        List<Student> students = exam.getStudents();
        students.forEach(student -> {
            List<String> studentCodeLines = student.getFilesLines();
            List<String> studentCodeLinesWithoutKeyword = new LinkedList<>();
            studentCodeLines.forEach(codeLine -> {
                studentCodeLinesWithoutKeyword.add(removeKeywordsFromCodeLine(codeLine));
            });
            student.setFileLines(studentCodeLinesWithoutKeyword);
        });
        exam.setStudents(students);
    }

    public String removeKeywordsFromCodeLine(String codeLine) {
        String newCodeLine = codeLine;
        for (String keyword : keywords) {
            if (newCodeLine.length() > keyword.length()) {
                if (newCodeLine.indexOf(keyword) == 0) {
                    newCodeLine = newCodeLine.replace(keyword + " ", "XXX ");
                }
                if (keyword.equals(newCodeLine.substring(newCodeLine.length() - keyword.length(), newCodeLine.length() - 1))) {
                    newCodeLine = newCodeLine.replace(" " + keyword, "XXX");
                }
                newCodeLine = newCodeLine.replace(" " + keyword + " ", "XXX");
            }
        }
        return newCodeLine;
    }
}
