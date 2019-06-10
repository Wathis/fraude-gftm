package filter;

import model.Exam;

public interface FilterVisitor {
    public void visit(Exam exam);
}
