package org.example.src.entities.excel;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.DateUtil;
import org.apache.poi.ss.usermodel.Row;
import org.example.src.CONFIG;
import org.example.src.utils.TreatLawyerParams;

public final class FilteredContactsNormalizer extends Excel {

    public FilteredContactsNormalizer(String filePath) {
        super(filePath);
    }

    public void normalize() {
        int lastRowNum = this.getSheet().getLastRowNum();

        for (int i = 1; i <= lastRowNum; i++) {
            Row row = this.getSheet().getRow(i);
            if (row == null) continue;

            setCell(row, 4,  TreatLawyerParams.treatName(getCellValue(row.getCell(4))));
            setCell(row, 5,  TreatLawyerParams.treatEmail(getCellValue(row.getCell(5))));
            setCell(row, 6,  TreatLawyerParams.treatPhone(getCellValue(row.getCell(6))));
            setCell(row, 7,  TreatLawyerParams.treatCountry(getCellValue(row.getCell(7))));
            setCell(row, 8,  TreatLawyerParams.treatPracticeArea(getCellValue(row.getCell(8))));
            setCell(row, 9,  getCellValue(row.getCell(9)).trim());
            setCell(row, 12, TreatLawyerParams.treatRole(getCellValue(row.getCell(12))));
            setCell(row, 13, getCellValue(row.getCell(13)).trim());
        }

        this.saveSheet();
        this.sort();
    }

    // N (Firm, col 13) → H (Country, col 7)
    private void sort() {
        super.sortRows(new int[]{13, 7}, 14);
    }

    private static void setCell(Row row, int colIndex, String value) {
        Cell cell = row.getCell(colIndex);
        if (cell == null) cell = row.createCell(colIndex);
        cell.setCellValue(value);
    }

    private static String getCellValue(Cell cell) {
        if (cell == null) return "";

        return switch (cell.getCellType()) {
            case STRING -> cell.getStringCellValue().trim();

            case NUMERIC -> {
                if (DateUtil.isCellDateFormatted(cell)) {
                    yield cell.getDateCellValue().toString();
                }
                yield Double.toString(cell.getNumericCellValue());
            }

            case BOOLEAN -> Boolean.toString(cell.getBooleanCellValue());

            case FORMULA -> cell.getCellFormula();

            default -> "";
        };
    }

    public static void main(String[] args) {
        FilteredContactsNormalizer normalizer = new FilteredContactsNormalizer(CONFIG.FILTERED_ACTIVE_CONTACTS_FILE);
        normalizer.normalize();
        System.out.println("Normalization complete.");
    }
}
