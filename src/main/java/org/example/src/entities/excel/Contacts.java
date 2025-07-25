package org.example.src.entities.excel;

import org.apache.poi.ss.usermodel.Row;

import java.util.Objects;


public class Contacts extends Excel{
    private static Contacts INSTANCE;

    private Contacts() {
        super("src//main//resources//baseFiles//excel//Contacts.xlsx");
    }

    public static Contacts getINSTANCE() {
        if (Objects.isNull(INSTANCE)) {
            INSTANCE = new Contacts();
        }
        return INSTANCE;
    }

    public Boolean isEmailRegistered(String emailToSearch) {
        // Skipping the header (start from index 1)
        for (Row row : this.sheet) {
            String cellValue = row.cellIterator().next().toString().trim().toLowerCase();
            if (cellValue.equals(emailToSearch)) {
                    return true;
                }
        }

        return false;
    }
}
