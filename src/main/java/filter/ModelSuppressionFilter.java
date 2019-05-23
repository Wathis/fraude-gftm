package filter;

import com.github.difflib.DiffUtils;
import com.github.difflib.algorithm.DiffException;
import com.github.difflib.patch.AbstractDelta;
import com.github.difflib.patch.Patch;
import model.Student;

import java.util.LinkedList;
import java.util.List;

public class ModelSuppressionFilter {


    public static List<Student> compute(List<String> professorRows, List<Student> students) {
        students.stream().forEach(student -> {
            Patch<String> patch = null;
            try {
                patch = DiffUtils.diff(professorRows, student.getFilesLines());
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
        return students;
    }


}
