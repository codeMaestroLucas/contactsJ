package org.example.src.entities.excel;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellType;
import org.apache.poi.ss.usermodel.Row;
import org.example.src.CONFIG;
import org.example.src.entities.Lawyer;


public final class ContactsAlreadyRegisteredSheet extends Excel{
    private String lastFirm = "";
    private int totalLawyersRegisteredPerFirm = 3;

    // Sheet to write data
    private Sheet destinationSheet = Sheet.getINSTANCE();

    // Contacts sheet to check for existing emails
    private Contacts contacts = Contacts.getINSTANCE();

    public ContactsAlreadyRegisteredSheet() {
        super(CONFIG.FILTERED_ACTIVE_CONTACTS_FILE, CONFIG.LAWYERS_IN_FILTER);
    }


    public void filterLawyersRegistered() {
        // Start from the second row to skip the header
        for (int i = 1; i <= this.getSheet().getLastRowNum(); i++) {
            Row row = this.getSheet().getRow(i);
            if (row == null) continue; // Skip empty rows

            String name = getCellValue(row.getCell(4));
            String firm = getCellValue(row.getCell(13));
            String email = getCellValue(row.getCell(5));

            if (email.isEmpty() || name.isEmpty()) continue; // Skip empty rows

            if (firm.equals(lastFirm) && totalLawyersRegisteredPerFirm >= 2) continue;

            if (contacts.isEmailRegistered(email)) {
                System.out.println("Email '" + email + "' is already registered. Cleaning up.");
                sheet.removeRow(row);
                continue;
            }

            String phone = getCellValue(row.getCell(6));
            String country = getCellValue(row.getCell(7));
            String practiceArea = getCellValue(row.getCell(8));
            String link = getCellValue(row.getCell(9));
            String role = getCellValue(row.getCell(12));


            Lawyer lawyer = Lawyer.builder()
                    .link(link)
                    .name(name)
                    .email(email)
                    .phone(phone)
                    .country(country)
                    .role(role)
                    .firm(firm)
                    .practiceArea(practiceArea)
                    .build();
            destinationSheet.addLawyer(lawyer);

            if (!firm.equals(lastFirm)) totalLawyersRegisteredPerFirm = 0;
            totalLawyersRegisteredPerFirm ++;
            lastFirm = firm;

        }
    }


    /**
     * Helper method to safely get a cell's string value.
     *
     * @param cell The cell to read from.
     * @return The cell value as a trimmed string, or an empty string if the cell is null.
     */
    private static String getCellValue(Cell cell) {
        if (cell == null) {
            return "";
        }
        // Set cell type to String to avoid issues with numeric/date cells
        cell.setCellType(CellType.STRING);
        return cell.getStringCellValue().trim();
    }
}