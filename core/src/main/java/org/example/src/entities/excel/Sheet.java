package org.example.src.entities.excel;

import org.example.src.CONFIG;
import org.example.src.entities.Lawyer;
import org.example.src.utils.Validations;

import java.util.HashSet;
import java.util.Set;

public final class Sheet extends Excel {

    private Set<String> lastCountries = new HashSet<>();
    private String lastFirm = "";
    private int currentRow = 1;

    private Sheet() {
        super(CONFIG.SHEET_FILE);
        this.eraseLastSheet();
    }

    private static final class Holder {
        static final Sheet INSTANCE = new Sheet();
    }

    public static Sheet getINSTANCE() {
        return Holder.INSTANCE;
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
        this.currentRow = super.sortRows(new int[]{3, 9, 4, 5, 2}, 10) + 1;
    }
}
