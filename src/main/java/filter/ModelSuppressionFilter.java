package filter;

import com.github.difflib.DiffUtils;
import com.github.difflib.algorithm.DiffException;
import com.github.difflib.patch.AbstractDelta;
import com.github.difflib.patch.Patch;

import java.util.LinkedList;
import java.util.List;

public class ModelSuppressionFilter {


    public static List<List<String>> compute(List<String> professorRows, List<List<String>> students) {
        List<List<String>> studentsWithoutProfessorCode = new LinkedList<>();
        students.stream().forEach(studentRows -> {
            Patch<String> patch = null;
            try {
                patch = DiffUtils.diff(professorRows, studentRows);
            } catch (DiffException e) {
                System.out.println(e.getMessage());
                return;
            }
            List<String> studentCode = new LinkedList<>();
            for (AbstractDelta<String> delta : patch.getDeltas()) {
                switch (delta.getType()) {
                    case CHANGE:
                        studentCode.addAll(delta.getTarget().getLines());
                        break;
                    case DELETE:
                        studentCode.addAll(delta.getSource().getLines());
                        break;
                    case INSERT:
                        studentCode.addAll(delta.getTarget().getLines());
                        break;
                }
            }
            studentsWithoutProfessorCode.add(studentCode);
        });
        return studentsWithoutProfessorCode;
    }


}
