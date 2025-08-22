package org.example.src.entities.excel;

import org.example.src.CONFIG;
import org.example.src.entities.Lawyer;

import java.util.Objects;

public class Sheet extends Excel {
    private static Sheet INSTANCE;

    private String lastCountry;
    private String lastFirm;
    private int currentRow;

    private Sheet() {
        super(CONFIG.SHEET_FILE);
        this.currentRow = 1;
        this.lastFirm = "";
        this.lastCountry = "";

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
     */
    public void addLawyer(Lawyer lawyer) {
        String firm = lawyer.getFirm();
        String country = lawyer.getCountry();

        // Just a fallBack, should never happen
        if (this.lastCountry.equalsIgnoreCase(country) && this.lastFirm.equalsIgnoreCase(firm)) return;

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

        if (!Objects.isNull(country)) {
            this.lastCountry = country;
        }

        this.lastFirm = firm;
        this.currentRow ++;

        System.out.println("Lawyer added successfully!");
    }
}
