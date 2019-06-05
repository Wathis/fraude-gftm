package filter;

import java.util.LinkedList;
import java.util.List;

import model.Exam;
import model.Student;

public class ImportationSuppressionFilter implements FilterVisitor {

	@Override
	public void visit(Exam exam) {
		List<Student> students = exam.getStudents();
		students.forEach(student -> {
			System.out.println("NOUVEAU ELEVE");
			List<String> studentCodeLines = student.getFilesLines();
			List<String> studentCodeLinesWithoutImport = new LinkedList<>();
			studentCodeLines.forEach(codeLine -> {
				studentCodeLinesWithoutImport.add(removeImport(codeLine));
			});
			student.setFileLines(studentCodeLinesWithoutImport);
		});
		exam.setStudents(students);
	}

	private String removeImport(String ligne) {
		if (ligne.matches("^(import).*;$")) {
			return "";
		}
		return ligne;
	}

}
