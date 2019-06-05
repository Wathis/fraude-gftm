package utils;

import java.util.Arrays;
import java.util.List;

public class TestUtils {

    public static final List<String> MODEL_PROF = Arrays.asList(
            "{",
            "int salut = 0;",
            "// DO THE REST",
            "}",
            "{",
            "// TO DO COMPLETE",
            "}"
    );

    public static final List<String> STUDENT_1 = Arrays.asList(
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
            "Logger.info(\"Voici salut : \" + salut)",
            "}"
    );

    public static final List<String> STUDENT_2 = Arrays.asList(
            "{",
            "int salut = 0;",
            "salut = salut + 1;",
            "// Je met un commentaire",
            "if (salut > 0) {",
            "   salut = salut + 1;",
            "}",
            "Logger.info(\"Voici salut : \" + salut)",
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
                    "Logger.info(\"Voici salut : \" + salut)\n" +
                    "}\n" +
                    "{\n" +
                    "// code d'un eleve qui a copié\n" +
                    "}\n";


    public static final String MODEL_PROF_MINUS_STUDENT_2 = "[salut = salut + 1;, // Je met un commentaire, if (salut > 0) {,    salut = salut + 1;, Logger.info(\"Voici salut : \" + salut), }, // code d'un eleve qui a copié]";

}
