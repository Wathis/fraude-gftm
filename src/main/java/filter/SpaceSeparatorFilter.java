package filter;

import model.Exam;

public class SpaceSeparatorFilter implements FilterVisitor {

    @Override
    public void visit(Exam exam) {
        System.out.println("Do nothing");
    }
}
