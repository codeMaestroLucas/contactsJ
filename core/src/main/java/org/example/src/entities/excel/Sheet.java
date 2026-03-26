package org.example.src.entities.excel;

import org.apache.commons.lang3.Validate;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.example.src.CONFIG;
import org.example.src.entities.Lawyer;
import org.example.src.utils.Validations;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;

public final class Sheet extends Excel {
    private static Sheet INSTANCE;

    private Set<String> lastCountries = new HashSet<>();
    private String lastFirm = "";
    private int currentRow = 1;

    private Sheet() {
        super(CONFIG.SHEET_FILE);
        this.eraseLastSheet();
    }

    public static Sheet getINSTANCE() {
        if (Objects.isNull(INSTANCE)) {
            INSTANCE = new Sheet();
        }
        return INSTANCE;
    }


    /**
     * Adds the lawyer in the sheet
     * @param lawyer to be registered
     * @param showMsg parameter to check if want to show the msg of "Lawyer added successfully!" when registering lawyers
     */
    public boolean addLawyer(Lawyer lawyer, boolean showMsg) {
        String firm = lawyer.getFirm();
        String country = lawyer.getCountry();

        // Just a fallBack, should never happen
        if (
            this.lastCountries.contains((country.toLowerCase())) &&
            this.lastFirm.equalsIgnoreCase(firm) &&
            Validations.isACountryToAvoid(country)
        ) return false;

        this.addContentOnRow(
                this.currentRow,
                lawyer.getName(),
                lawyer.getEmail(),
                lawyer.getPhone(),
                country,
                lawyer.getPracticeArea(),
                lawyer.getLink(),
                "Karine Frois",               // Manager Column (G)
                lawyer.getSpecialism(),       // Specialism Column (H)
                lawyer.getRole(),
                firm
        );

        this.lastCountries.add((country.toLowerCase()));
        this.lastFirm = firm;
        this.currentRow ++;

        if (showMsg) System.out.println("Lawyer added successfully!");
        return true;
    }

    /**
     * Sorts all data rows (skipping the header at row 0) by the following
     * cumulative priority — equivalent to Excel's multi-level sort:
     * D (Country) → J (Firm) → E (Practice Area) → F (Link) → C (Phone)
     */
    public void sortRows() {
        int lastRow = this.sheet.getLastRowNum();
        if (lastRow < 2) return;

        // 1. Collect all data rows as String[]
        List<String[]> rows = new ArrayList<>();
        for (int i = 1; i <= lastRow; i++) {
            Row row = this.sheet.getRow(i);
            if (row == null) continue;
            String[] cells = new String[10];
            for (int j = 0; j < 10; j++) {
                Cell cell = row.getCell(j);
                cells[j] = (cell != null) ? cell.getStringCellValue() : "";
            }
            rows.add(cells);
        }

        // 2. Multi-level sort: D(3) → J(9) → E(4) → F(5) → C(2)
        rows.sort(Comparator.comparing((String[] r) -> r[3])
                .thenComparing(r -> r[9])
                .thenComparing(r -> r[4])
                .thenComparing(r -> r[5])
                .thenComparing(r -> r[2]));

        // 3. Remove all data rows (descending to avoid index gaps)
        for (int i = lastRow; i >= 1; i--) {
            Row row = this.sheet.getRow(i);
            if (row != null) this.sheet.removeRow(row);
        }

        // 4. Re-write in sorted order
        for (int i = 0; i < rows.size(); i++) {
            Row newRow = this.sheet.createRow(i + 1);
            String[] cells = rows.get(i);
            for (int j = 0; j < cells.length; j++) {
                newRow.createCell(j).setCellValue(cells[j]);
            }
        }

        this.currentRow = rows.size() + 1;
        this.saveSheet();
    }
}
