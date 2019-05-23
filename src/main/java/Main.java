import com.github.difflib.DiffUtils;
import com.github.difflib.algorithm.DiffException;
import com.github.difflib.patch.AbstractDelta;
import com.github.difflib.patch.Patch;
import com.github.difflib.patch.PatchFailedException;
import filter.ModelSuppressionFilter;
import utils.DiffPatchMatch;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.Arrays;
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
        List<List<String>> studentsMinusProfessor = ModelSuppressionFilter.compute(MODEL_PROF,Arrays.asList(STUDENT_2));

        System.out.println(studentsMinusProfessor);

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
