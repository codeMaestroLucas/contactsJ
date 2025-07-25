package org.example.src.entities.excel;

import org.example.src.entities.Lawyer;

import java.util.Objects;

public class Sheet extends Excel {
    private static Sheet INSTANCE;

    private String lastCountry;
    private String lastFirm;
    private int currentRow;

    private Sheet() {
        super("src/main/resources/baseFiles/excel/Sheet.xlsx");
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
        String firm = lawyer.firm;

        String country = lawyer.country;
        String practiceArea = lawyer.practiceArea.isEmpty() ? "-----" : lawyer.practiceArea;

        if (this.lastCountry.equalsIgnoreCase(country) && this.lastFirm.equalsIgnoreCase(firm)) {
            System.out.printf(
                    "The firm %s already has a lawyer in the country %s registered in the sheet.\n",
                    this.lastFirm, this.lastCountry);
            return;
        }

        this.addContentOnRow(
                this.currentRow,
                lawyer.name,
                lawyer.role,
                firm,
                country,
                "-----",                 // Empty for nationality
                practiceArea,
                lawyer.email,
                lawyer.phone
        );

        if (!Objects.isNull(country)) {
            this.lastCountry = country;
        }

        this.lastFirm = firm;
        this.currentRow ++;

        System.out.println("Lawyer added successfully!");
    }
}
