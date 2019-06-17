package io;

import com.vgv.excel.io.*;
import com.vgv.excel.io.cells.NumberCell;
import com.vgv.excel.io.cells.NumberCells;
import com.vgv.excel.io.cells.TextCell;
import com.vgv.excel.io.cells.TextCells;
import com.vgv.excel.io.styles.FillPattern;
import com.vgv.excel.io.styles.FontStyle;
import com.vgv.excel.io.styles.ForegroundColor;
import model.Exam;
import model.Student;
import org.apache.poi.ss.usermodel.FillPatternType;
import org.apache.poi.ss.usermodel.IndexedColors;
import utils.Logger;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

public class XLSWriter {


    public static void write(Exam exam, String outFolderPath) throws IOException {
        File directory = new File(outFolderPath);
        if (! directory.exists()){
            directory.mkdirs();
        }

        List<Student> students = exam.getStudents();

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
                ECell eCells[] = new ECell[students.size()];
                for (int x = 0; x < scores.length ; x++) {
                    short color = IndexedColors.LIGHT_GREEN.getIndex();
                    if (scores[x] >= 0.4) {
                        color = IndexedColors.LIGHT_ORANGE.getIndex();
                    }
                    if (scores[x] >= 0.75) {
                        color = IndexedColors.ROSE.getIndex();
                    }
                    eCells[x] = new NumberCell(scores[x])
                            .with(
                                new XsStyle(
                                    new ForegroundColor(color),
                                    new FillPattern(FillPatternType.SOLID_FOREGROUND)
                                )
                            );
                }
                int cellColor = 0;
                rows[j] = new XsRow()
                        .with(new TextCell(student.getName()))
                        .with(eCells);
                j++;
            }
            new XsWorkbook(
                    new XsSheet(rows)
            ).saveTo(outFolderPath  + File.separator + command.replace(" ","") + ".xlsx");
        }
        saveRanking(exam,outFolderPath);
    }



    public static void saveRanking(Exam exam,String outFolderPath) throws IOException {
        exam.sortStudentsByScore();
        List<Student> students  = exam.getStudents();
        LinkedList<ERow> rankingRows = new LinkedList<>();
        for(Student currentStudent : students){
            Logger.info(currentStudent.getName() + " - " + currentStudent.getMaxScore());
            rankingRows.add(new XsRow()
                    .with(new TextCell(currentStudent.getName()))
                    .with(new NumberCell(currentStudent.getMaxScore())));
        }

        XsRow xsRow[] = new XsRow[rankingRows.size()];
        rankingRows.toArray(xsRow);
        new XsWorkbook(
                new XsSheet(xsRow)
        ).saveTo(outFolderPath  + File.separator + "ranking" + ".xlsx");

    }
}
