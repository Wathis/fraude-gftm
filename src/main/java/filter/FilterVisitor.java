package filter;

import model.Exam;
import model.Student;

import java.util.List;

public interface FilterVisitor {
    public void visit(Exam exam);
}
