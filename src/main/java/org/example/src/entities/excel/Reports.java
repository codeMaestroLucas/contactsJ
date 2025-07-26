package org.example.src.entities.excel;

import org.example.src.entities.BaseSites.Site;

import java.util.Objects;

public class Reports extends Excel {
    private static Reports INSTANCE;

    private int currentRow;

    private Reports() {
        super("src/main/resources/baseFiles/excel/Reports.xlsx");
        this.currentRow = 1;
        this.eraseLastSheet();
    }

    public static Reports getINSTANCE() {
        if (Objects.isNull(INSTANCE)) {
            INSTANCE = new Reports();
        }
        return INSTANCE;
    }


    public void createReportRow(Site site, String time) {
        try {
            this.addContentOnRow(currentRow, site.name, time, String.valueOf(site.lawyersRegistered));
            this.currentRow ++;

        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
