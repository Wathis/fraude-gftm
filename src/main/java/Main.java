import com.github.difflib.DiffUtils;
import com.github.difflib.algorithm.DiffException;
import com.github.difflib.patch.AbstractDelta;
import com.github.difflib.patch.Patch;
import com.github.difflib.patch.PatchFailedException;
import filter.ModelSuppressionFilter;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.Arrays;
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
    public static void main(String[] args) throws DiffException, IOException, PatchFailedException {
        ModelSuppressionFilter.compute(STUDENT_1,Arrays.asList(STUDENT_2));
    }
}
