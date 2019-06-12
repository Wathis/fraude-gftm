package filter;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.ListIterator;
import java.util.Map;
import java.util.Map.Entry;

import javax.crypto.spec.PSource;

import com.github.difflib.DiffUtils;
import com.github.difflib.algorithm.DiffException;
import com.github.difflib.patch.AbstractDelta;
import com.github.difflib.patch.Patch;

import model.Exam;
import model.Student;

public class CommonCodeSuppressionFilter implements FilterVisitor {

	private static Integer ECART = 2;

	@Override
	public void visit(Exam exam) {
		List<Student> students = exam.getStudents();
		/*
		 * Represents the list of common code with others students
		 */
		List<LinkedHashMap<Integer, String>> commonCodes = new ArrayList<LinkedHashMap<Integer, String>>();
		HashMap<Student, HashMap<Integer, String>> codeToDeleteByStudent = new HashMap<Student, HashMap<Integer, String>>();
		for (Student student : students) {
			commonCodes.clear();
			// Get other students
			List<Student> otherStudents = new ArrayList<Student>();
			otherStudents.addAll(students);
			otherStudents.remove(student);
			// Browse the list of the other student to compare
			for (Student browsedStudent : otherStudents) {
				// get the delta between current student with the student who is
				// browsed
				LinkedHashMap<Integer, String> common = getCommonCode(student.getFilesLines(),
						browsedStudent.getFilesLines());
				commonCodes.add(common);
			}
			HashMap<Integer, String> commonLines = getCommonBetweenAllDelta(commonCodes);
			codeToDeleteByStudent.put(student, commonLines);
		}
		deleteCommonCodeOfStudents(students, codeToDeleteByStudent);
	}

	private void deleteCommonCodeOfStudents(List<Student> students,
			HashMap<Student, HashMap<Integer, String>> codeToDeleteByStudent) {
		for (Student student : students) {
			student.setFileLines(removeCommonCode(student.getFilesLines(), codeToDeleteByStudent.get(student)));
		}

	}

	private List<String> removeCommonCode(List<String> studentLine, HashMap<Integer, String> commonLines) {
		List<Integer> positions = new ArrayList<Integer>();
		for (Map.Entry<Integer, String> entry : commonLines.entrySet()) {
			positions.add(entry.getKey());
		}
		Collections.reverse(positions);
		ListIterator<Integer> iterator = positions.listIterator();
		while (iterator.hasNext()) {
			studentLine.remove(iterator.next().intValue());
		}
		return studentLine;
	}

	private HashMap<Integer, String> getCommonBetweenAllDelta(List<LinkedHashMap<Integer, String>> commonCodes) {
		LinkedHashMap<Integer, String> fileReference = new LinkedHashMap<Integer, String>();
		LinkedHashMap<Integer, String> result = new LinkedHashMap<Integer, String>();
		fileReference = commonCodes.get(0);
		commonCodes.remove(0);
		for (Map.Entry<Integer, String> lineAnalyzed : fileReference.entrySet()) {
			if (isCommonWithAllFile(lineAnalyzed, commonCodes)) {
				result.put(lineAnalyzed.getKey(), lineAnalyzed.getValue());
			}
		}
		return result;
	}

	private boolean isCommonWithAllFile(Entry<Integer, String> lineAnalyzed,
			List<LinkedHashMap<Integer, String>> commonCodes) {
		if(commonCodes.size() == 0){
			if(lineAnalyzed.getValue() == null)
				return false;
			return true;
		}
		for (HashMap<Integer, String> hashMap : commonCodes) {
			if (!isCommon(lineAnalyzed, hashMap))
				return false;
		}
		return true;
	}

	private <T> boolean isCommon(Entry<Integer, T> entry, HashMap<Integer, T> hashmap) {
		boolean isCommon = true;
		int position = existInHashMap(entry, hashmap);
		if (position == -1)
			return false;
		isCommon = isClose(entry, position);
		return isCommon;
	}

	private <T> boolean isClose(Entry<Integer, T> entry, int position) {
		if (entry.getKey() - ECART < position && position < entry.getKey() + ECART)
			return true;
		return false;
	}

	private <T> int existInHashMap(Entry<Integer, T> entry, HashMap<Integer, T> hashmap) {
		int previous = entry.getKey();
		int next = entry.getKey();
		if (next >= hashmap.size()) {
			next = hashmap.size() - 1;
			previous = hashmap.size() - 2;
		}
		T entryValue = entry.getValue();
		T value = null;
		if (entryValue == null)
			return -1;
		while (previous > -1 || next < hashmap.size()) {
			if (hashmap.containsKey(next)) {
				value = hashmap.get(next);
				if (value != null) {
					if (value.equals(entryValue))
						return next;
				}
			}
			if (hashmap.containsKey(previous)) {
				value = hashmap.get(previous);
				if (value != null) {
					if (value.equals(entryValue))
						return previous;
				}
			}
			next++;
			previous--;
		}
		return -1;
	}

	private void print(HashMap<Integer, String> n) {
		for (Map.Entry<Integer, String> ite : n.entrySet()) {
			System.out.println("Numero : " + ite.getKey() + " - " + ite.getValue());
		}
	}

	/**
	 * Return the delta (can be change, delete, add) between two list
	 *
	 *
	 * @param list1
	 * @param list2
	 * @return
	 */
	private <T> List<AbstractDelta<T>> getDeltas(List<T> list1, List<T> list2) {
		Patch<T> patch = new Patch<T>();
		try {
			patch = DiffUtils.diff(list1, list2);
		} catch (DiffException e) {
			System.out.println(e.getMessage());
		}
		return patch.getDeltas();
	}

	// TODO : PAsser direct la hashmap des student pour economiser temps de
	// calcul ?
	private <T> LinkedHashMap<Integer, T> getCommonCode(List<T> list1, List<T> list2) {
		LinkedHashMap<Integer, T> numberLine = new LinkedHashMap<Integer, T>();
		for (int i = 0; i < list1.size(); i++) {
			numberLine.put(i, (T) list1.get(i));
		}
		List<AbstractDelta<T>> deltas = getDeltas(list1, list2);
		List<Integer> listOfPositionToDelete = getRealPositionsOfDelta(deltas, numberLine);
		ListIterator<Integer> positionsDelta = listOfPositionToDelete.listIterator();
		while (positionsDelta.hasNext()) {
			Integer position = positionsDelta.next();
			numberLine.put(position, null);
		}
		return numberLine;
	}

	private <T> List<Integer> getRealPositionsOfDelta(List<AbstractDelta<T>> deltas, HashMap<Integer, T> numberLine) {
		List<Integer> positionsToDelete = new ArrayList<Integer>();
		for (AbstractDelta<T> delta : deltas) {
			int nbLine = delta.getSource().getLines().size();
			int position = delta.getSource().getPosition();
			for (int i = position; i < position + nbLine; i++) {
				positionsToDelete.add(i);
			}
		}
		return positionsToDelete;
	}

}
