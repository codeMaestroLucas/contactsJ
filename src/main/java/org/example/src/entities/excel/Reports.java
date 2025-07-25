package org.example.src.entities.excel;

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


    public void createReportRow(String firm, String time, Integer lawyersRegistered) {
        try {
            this.addContentOnRow(currentRow, firm, time, lawyersRegistered.toString());
            this.currentRow ++;

        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
