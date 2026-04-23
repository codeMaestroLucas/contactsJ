package org.example.src.entities.excel;
import lombok.Getter;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.example.src.entities.Lawyer;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

@Getter
public class Excel {
    private final String filePath;
    protected Workbook workbook;
    protected Sheet sheet;
    protected Integer rowsToFill;


    public Excel(String filePath, Integer rowsToFill) {
        this.filePath = filePath;
        this.rowsToFill = rowsToFill;

        try {
            FileInputStream file = new FileInputStream(filePath);
            this.workbook = new XSSFWorkbook(file);
            this.sheet = this.workbook.getSheetAt(0);
            file.close(); // Close input stream after loading workbook into memory
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    public Excel(String filePath) {
        this.filePath = filePath;

        try {
            FileInputStream file = new FileInputStream(filePath);
            this.workbook = new XSSFWorkbook(file);
            this.sheet = this.workbook.getSheetAt(0);
            file.close(); // Close input stream after loading workbook into memory
        } catch (IOException e) {
            e.printStackTrace();
        }

    }


    /**
     * Save the changes back to the file.
     */
    public void saveSheet() {
        try (FileOutputStream file = new FileOutputStream(filePath)) {
            workbook.write(file);
            file.flush();

        } catch (IOException e) {
            throw new RuntimeException("Error saving Excel file: " + filePath, e);
        }
    }

    /**
     * Close the workbook to free resources.
     * Should be called when done with all operations.
     */
    public void closeWorkbook() {
        try {
            if (workbook != null) {
                workbook.close();
            }
        } catch (IOException e) {
            throw new RuntimeException("Error closing Excel workbook: " + filePath, e);
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
     * Sorts by a single column.
     * Empty/phantom rows are excluded from the result and removed from the sheet.
     */
    public int sortRows(int colIndex, int numColumns, int startRow) {
        return sortRowsCore(Comparator.comparing(r -> r[colIndex]), numColumns, startRow);
    }

    public int sortRows(int colIndex, int numColumns) {
        return sortRows(colIndex, numColumns, 1);
    }

    /**
     * Sorts by multiple columns in priority order (nested sort).
     * Empty/phantom rows are excluded from the result and removed from the sheet.
     *
     * @param colIndexes  columns to sort by, in descending priority order
     */
    public int sortRows(int[] colIndexes, int numColumns, int startRow) {
        if (colIndexes == null || colIndexes.length == 0) return 0;
        Comparator<String[]> comparator = Comparator.comparing((String[] r) -> r[colIndexes[0]]);
        for (int i = 1; i < colIndexes.length; i++) {
            final int idx = colIndexes[i];
            comparator = comparator.thenComparing(r -> r[idx]);
        }
        return sortRowsCore(comparator, numColumns, startRow);
    }

    public int sortRows(int[] colIndexes, int numColumns) {
        return sortRows(colIndexes, numColumns, 1);
    }

    /** Kept for callers that build their own Comparator (e.g. Reports, LastCheck). */
    public int sortRows(Comparator<String[]> comparator, int numColumns, int startRow) {
        return sortRowsCore(comparator, numColumns, startRow);
    }

    public int sortRows(Comparator<String[]> comparator, int numColumns) {
        return sortRowsCore(comparator, numColumns, 1);
    }

    /** Core sort: reads, filters phantom rows, sorts, removes, rewrites. */
    private int sortRowsCore(Comparator<String[]> comparator, int numColumns, int startRow) {
        int lastRow = this.sheet.getLastRowNum();
        if (lastRow < startRow) return 0;

        List<String[]> rows = new ArrayList<>();
        for (int i = startRow; i <= lastRow; i++) {
            Row row = this.sheet.getRow(i);
            if (row == null) continue;

            String[] cells = new String[numColumns];
            boolean hasData = false;
            for (int j = 0; j < numColumns; j++) {
                cells[j] = readCell(row.getCell(j));
                if (!cells[j].isBlank()) hasData = true;
            }
            if (!hasData) continue;

            rows.add(cells);
        }

        rows.sort(comparator);

        for (int i = lastRow; i >= startRow; i--) {
            Row row = this.sheet.getRow(i);
            if (row != null) this.sheet.removeRow(row);
        }

        for (int i = 0; i < rows.size(); i++) {
            Row newRow = this.sheet.createRow(startRow + i);
            String[] cells = rows.get(i);
            for (int j = 0; j < cells.length; j++) {
                newRow.createCell(j).setCellValue(cells[j]);
            }
        }

        this.saveSheet();
        return rows.size();
    }

    private static String readCell(Cell cell) {
        if (cell == null) return "";
        return switch (cell.getCellType()) {
            case STRING -> cell.getStringCellValue().trim();
            case NUMERIC -> {
                if (DateUtil.isCellDateFormatted(cell)) yield cell.getDateCellValue().toString();
                yield Double.toString(cell.getNumericCellValue());
            }
            case BOOLEAN -> Boolean.toString(cell.getBooleanCellValue());
            case FORMULA -> cell.getCellFormula();
            default -> "";
        };
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
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
