import com.github.difflib.DiffUtils;
import com.github.difflib.algorithm.DiffException;
import com.github.difflib.patch.AbstractDelta;
import com.github.difflib.patch.Patch;
import com.github.difflib.patch.PatchFailedException;
import filter.KeywordFilter;
import filter.ModelSuppressionFilter;
import filter.SpaceSeparatorFilter;
import model.AuthorType;
import model.Exam;
import model.File;
import model.Student;
import utils.DiffPatchMatch;

import java.io.IOException;
import java.util.Arrays;
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;

public class Main {


    private static final List<String> MODEL_PROF = Arrays.asList(
            "{",
            "int salut = 0;",
            "// DO THE REST",
            "}",
            "{",
            "// TO DO COMPLETE",
            "}"
    );

    private static final List<String> STUDENT_1 = Arrays.asList(
            "{",
            "// code d'un l'eleve",
            "}",
            "{",
            "int salut = 0;",
            "salut = salut + 1;",
            "// Je met un commentaire",
            "if (salut > 0) {",
            "   salut = salut + 1;",
            "}",
            "System.out.println(\"Voici salut : \" + salut)",
            "}"
     );

    private static final List<String> STUDENT_2 = Arrays.asList(
            "{",
            "int salut = 0;",
            "salut = salut + 1;",
            "// Je met un commentaire",
            "if (salut > 0) {",
            "   salut = salut + 1;",
            "}",
            "System.out.println(\"Voici salut : \" + salut)",
            "}",
            "{",
            "// code d'un eleve qui a copié",
            "}"
    );

    public static final String CODE_PROF =
            "{\n" +
            "int salut = 0;\n"  +
            "// DO THE REST\n"  +
            "}\n"  +
            "{\n"  +
            "// TO DO COMPLETE\n" +
            "}\n";

    public static final String CODE_ELEVE =
            "{\n" +
            "int salut = 0;\n" +
            "salut = salut + 1;\n" +
            "// Je met un commentaire\n" +
            "if (salut > 0) {\n" +
            "   salut = salut + 1;\n" +
            "}\n" +
            "System.out.println(\"Voici salut : \" + salut)\n" +
            "}\n" +
            "{\n" +
            "// code d'un eleve qui a copié\n" +
            "}\n";


    public static void main(String[] args) throws DiffException, IOException, PatchFailedException {
        Student student = new Student("","");
        student.setFiles(Arrays.asList(new File(STUDENT_2, AuthorType.STUDENT,student)));
        Exam exam = new Exam(MODEL_PROF,Arrays.asList(student));
        exam.accept(new ModelSuppressionFilter());
        exam.accept(new SpaceSeparatorFilter());
        exam.accept(new KeywordFilter());
        System.out.println(exam.getStudents().get(0).getFilesLines());

//
//        [GOOGLE CHAR DIFF]
//
//        DiffPatchMatch diffPatchMatch = new DiffPatchMatch();
//
//        LinkedList<DiffPatchMatch.Diff> diffs = diffPatchMatch.diff_main(CODE_PROF,CODE_ELEVE);
//
//        System.out.println(diffs);
//        for (DiffPatchMatch.Diff diff : diffs) {
//            if (diff.operation == DiffPatchMatch.Operation.EQUAL) {
//                System.out.println("= " + diff.text);
//            }
//            if (diff.operation == DiffPatchMatch.Operation.INSERT) {
//                System.out.print(diff.text);
//            }
//            if (diff.operation == DiffPatchMatch.Operation.DELETE) {
//                System.out.println("-" + diff.text);
//            }
//        }

    }
}
