package filter;

import com.github.difflib.DiffUtils;
import com.github.difflib.algorithm.DiffException;
import com.github.difflib.patch.AbstractDelta;
import com.github.difflib.patch.Patch;
import model.Exam;
import model.Student;

import java.util.LinkedList;
import java.util.List;

public class ModelSuppressionFilter implements FilterVisitor {

    @Override
    public void visit(Exam exam) {
        List<String> professorLines = exam.getProfessorLines();
        List<Student> students = exam.getStudents();
        students.stream().forEach(student -> {
            Patch<String> patch = null;
            try {
                patch = DiffUtils.diff(professorLines, student.getFilesLines());
            } catch (DiffException e) {
                System.out.println(e.getMessage());
                return;
            }
            List<String> studentLines = new LinkedList<>();
            for (AbstractDelta<String> delta : patch.getDeltas()) {
                switch (delta.getType()) {
                    case CHANGE:
                        studentLines.addAll(delta.getTarget().getLines());
                        break;
                    case DELETE:
                        studentLines.addAll(delta.getSource().getLines());
                        break;
                    case INSERT:
                        studentLines.addAll(delta.getTarget().getLines());
                        break;
                }
            }
            student.setFileLines(studentLines);
        });
        exam.setProfessorLines(professorLines);
        exam.setStudents(students);
    }


}
