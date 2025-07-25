package org.example.src.entities.excel;
import lombok.Getter;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;


import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;

@Getter
public class Excel {
    private final String filePath;
    protected Workbook workbook;
    protected Sheet sheet;
    protected Integer rowsToFill;


    public Excel(String filePath, Integer rowsToFill) {
        this.filePath = filePath;
        this.rowsToFill = rowsToFill;

        try (FileInputStream file = new FileInputStream(filePath)) {
            this.workbook = new XSSFWorkbook(file);
            this.sheet = this.workbook.getSheetAt(0);
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    public Excel(String filePath) {
        this.filePath = filePath;

        try (FileInputStream file = new FileInputStream(filePath)) {
            this.workbook = new XSSFWorkbook(file);
            this.sheet = this.workbook.getSheetAt(0);
        } catch (IOException e) {
            e.printStackTrace();
        }

    }


    /**
     * Save the changes back to the file.
     */
    protected void saveSheet() {
        try (FileOutputStream file = new FileOutputStream(filePath)) {
            workbook.write(file);
            file.flush();

        } catch (IOException e) {
            throw new RuntimeException("Error saving Excel file: " + filePath, e);
        }
    }


    /**
     * Erases all data from the rows in the Excel sheet,
     * starting from the second row (skipping the header).
     *
     */
    protected void eraseLastSheet() {
        // Get the last row index (0-based)
        int lastRowIndex = sheet.getLastRowNum();

        // Start from row 1 (skip header at row 0)
        for (int rowNum = 1; rowNum <= lastRowIndex; rowNum++) {
            Row row = this.sheet.getRow(rowNum);
            if (row != null) {
                this.sheet.removeRow(row);
            }
        }

        this.saveSheet();
    }


    /**
     * Creates or Gets a row, and then insert all the content given
     * @param rowIndex index of the row
     * @param args to be inserted in the row
     */
    protected void addContentOnRow(int rowIndex, String... args) {
        try {
            Row row = this.sheet.getRow(rowIndex);
            if (row == null) {
                row = this.sheet.createRow(rowIndex);
            }

            for (int i = 0; i < args.length; i++) {
                row.createCell(i).setCellValue(args[i]);
            }

            this.saveSheet();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
