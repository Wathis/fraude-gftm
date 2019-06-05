package io;

import com.vgv.excel.io.ERow;
import com.vgv.excel.io.XsRow;
import com.vgv.excel.io.XsSheet;
import com.vgv.excel.io.XsWorkbook;
import com.vgv.excel.io.cells.NumberCells;
import com.vgv.excel.io.cells.TextCell;
import com.vgv.excel.io.cells.TextCells;
import model.Student;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Set;

public class XLSWriter {


    public static void write(List<Student> students,String outFolderPath) throws IOException {
        File directory = new File(outFolderPath);
        if (! directory.exists()){
            directory.mkdirs();
        }

        if (students.size() == 0)
            return;

        HashMap<String,Double[]> firstStudentScores = students.get(0).getScores();

        Set<String> commands = firstStudentScores.keySet();
        String[] headers = new String[students.size() + 1];

        int i = 1;
        headers[0] = "";
        for (Student student : students) {
            headers[i] = student.getName();
            i++;
        }
        for (String command : commands) {
            ERow rows[] = new ERow[students.size() + 1];
            rows[0] = new XsRow().with(new TextCells(headers));
            int j = 1;
            for (Student student : students) {
                Double[] scores = student.getScores().get(command);
                rows[j] = new XsRow().with(new TextCell(student.getName())).with(new NumberCells(scores));
                j++;
            }
            new XsWorkbook(
                    new XsSheet(rows)
            ).saveTo(outFolderPath  + File.separator + command.replace(" ","") + ".xlsx");;
        }
    }


}
