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
import parser.FileReader;
import utils.DiffPatchMatch;

import java.io.IOException;
import java.util.Arrays;
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;

public class Main {


    public static void main(String[] args) throws DiffException, IOException, PatchFailedException {


        Student student = new Student("PI_2017_TP03Note_ABDEALY_mickael","/Users/mathisdelaunay/Desktop");
        FileReader.addListOfFilesToStudent(student);
        System.out.println(student);

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
