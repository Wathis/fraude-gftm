package filter;

import com.github.difflib.DiffUtils;
import com.github.difflib.algorithm.DiffException;
import com.github.difflib.patch.AbstractDelta;
import com.github.difflib.patch.Patch;

import java.util.LinkedList;
import java.util.List;

public class ModelSuppressionFilter {


    public static void compute(List<String> professorRows, List<List<String>> students) {
        students.stream().forEach(studentRows -> {
            Patch<String> patch = null;
            try {
                patch = DiffUtils.diff(professorRows, studentRows);
            } catch (DiffException e) {
                System.out.println("");
            }
            List<String> result = new LinkedList<>();
            for (AbstractDelta<String> delta : patch.getDeltas()) {
                switch (delta.getType()) {
                    case CHANGE:
                        result.addAll(delta.getTarget().getLines());
                        break;
                    case DELETE:
                        result.addAll(delta.getTarget().getLines());
                        break;
                    case INSERT:
                        result.addAll(delta.getTarget().getLines());
                        break;
                }
                System.out.println(delta);
            }
        });
    }


}
