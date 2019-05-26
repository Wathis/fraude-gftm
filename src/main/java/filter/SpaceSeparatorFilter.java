package filter;

import com.github.difflib.DiffUtils;
import com.github.difflib.algorithm.DiffException;
import com.github.difflib.patch.AbstractDelta;
import com.github.difflib.patch.Patch;
import model.Exam;
import model.Student;

import java.util.LinkedList;
import java.util.List;

public class SpaceSeparatorFilter implements FilterVisitor {

    @Override
    public void visit(Exam exam) {
        List<Student> students = exam.getStudents();
        students.forEach(student -> {
            List<String> studentCodeLines = student.getFilesLines();
            List<String> studentCodeLinesWithoutSpaces = new LinkedList<>();
            studentCodeLines.forEach(codeLine -> {
                studentCodeLinesWithoutSpaces.add(removeSpaces(codeLine));
            });
            student.setFileLines(studentCodeLinesWithoutSpaces);
        });
        exam.setStudents(students);
    }

    public static String removeSpaces(String codeLine) {
        codeLine = codeLine.trim();
        String[] words = codeLine.split("\\n");
        codeLine = String.join(" ",words);
        return codeLine;
    }

}
