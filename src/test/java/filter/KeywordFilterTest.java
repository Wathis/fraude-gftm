package filter;


import model.CompatibleLanguage;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;

import java.util.Arrays;
import java.util.Collection;

@RunWith(Parameterized.class)
public class KeywordFilterTest {

    @Parameterized.Parameters(name = "{0}")
    public static Collection<Object[]> data() {
        return Arrays.asList(new Object[][] {
                {"int salut = 0;", "XXX salut = 0;"},
                {"public", "XXX"},
                {"", ""},
                {"int salut = 0; public", "XXX salut = 0; XXX"},
        });
    }

    @Parameterized.Parameter
    public String input;

    @Parameterized.Parameter(1)
    public String expected;

    KeywordFilter keywordFilter;

    @Before
    public void setUp() {
        keywordFilter = new KeywordFilter(CompatibleLanguage.JAVA);
    }

    @Test
    public void testWith() {
        KeywordFilter keywordFilter = new KeywordFilter(CompatibleLanguage.JAVA);
        String stringWithoutKeywords = keywordFilter.removeKeywordsFromCodeLine(input);
        Assert.assertEquals(expected, stringWithoutKeywords);
    }
}
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
