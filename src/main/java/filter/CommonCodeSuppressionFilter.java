package filter;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import com.github.difflib.DiffUtils;
import com.github.difflib.algorithm.DiffException;
import com.github.difflib.patch.AbstractDelta;
import com.github.difflib.patch.DeltaType;
import com.github.difflib.patch.Patch;

import model.Exam;
import model.Student;

public class CommonCodeSuppressionFilter implements FilterVisitor{

	@Override
	public void visit(Exam exam) {
        List<Student> students = exam.getStudents();
        List<List<AbstractDelta<String>>> commonCodes = new ArrayList<List<AbstractDelta<String>>>();
        students.forEach(student -> {
        	// Get other students
        	List<Student> otherStudents = students;
        	otherStudents.remove(student);
        	// Browse the list of the other student to compare
        	for(Student browsedStudent : otherStudents){
        		// get the delta between current student with the student who is browsed
        		List<AbstractDelta<String>> deltas = getDeltas(student.getFilesLines(), browsedStudent.getFilesLines());
        		// Filter to get only the deltas where the lines are EQUALS
        		deltas = getEqualsDeltas(deltas);
        		deltas.get(0).getSource();
        		commonCodes.add(deltas);
        	}

        });
	}

	private <T> List<AbstractDelta<T>> getDeltas(List<T> list1, List<T> list2){
		Patch<T> patch = null;
    	try {
            patch = DiffUtils.diff(list1, list2);
        } catch (DiffException e) {
            System.out.println(e.getMessage());
        }
    	return patch.getDeltas();
	}

	private <T> List<AbstractDelta<T>> getEqualsDeltas(List<AbstractDelta<T>> allDeltas){
		List<AbstractDelta<T>> deltas = new ArrayList<AbstractDelta<T>>();
		for (AbstractDelta<T> delta : allDeltas) {
			if(delta.getType() == DeltaType.EQUAL){
				deltas.add(delta);
			}
		}
		return deltas;
	}
}
