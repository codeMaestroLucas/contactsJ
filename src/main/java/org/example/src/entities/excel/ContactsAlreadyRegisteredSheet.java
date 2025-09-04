package org.example.src.entities.excel;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.DateUtil;
import org.apache.poi.ss.usermodel.Row;
import org.example.src.CONFIG;
import org.example.src.entities.Lawyer;

import java.io.*;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;


public final class ContactsAlreadyRegisteredSheet extends Excel{
    private String lastFirm = "";
    private int totalLawyersPerFirm = 3;
    private int totalLawyers = 0;
    private int lastFirmRow;
    private int maxReRuns = 1;
    private Set<String> lawFirmsCollected = new HashSet<>();

    // Sheet to write data
    private final Sheet destinationSheet = Sheet.getINSTANCE();

    // Contacts sheet to check for existing emails
    private final Contacts contacts = Contacts.getINSTANCE();

    public ContactsAlreadyRegisteredSheet() {
        super(CONFIG.FILTERED_ACTIVE_CONTACTS_FILE, CONFIG.LAWYERS_IN_FILTER);
        this.lastFirmRow = this.getLastFirmCollectedRow();
    }


    /**
     * Returns the (last row collected + 1) in the previous execution of the function
     * in the file `lastRowRegisteredInContacts.txt` to start the iteration.
     * @return row of the last firm
     */
    private int getLastFirmCollectedRow() {
        String firmToCheck;
        try (BufferedReader br = new BufferedReader(new FileReader(CONFIG.LAST_FIRM_REGISTERED_FILE))) {
            firmToCheck = br.readLine();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        return Objects.isNull(firmToCheck) ?  0 : Integer.parseInt(firmToCheck) + 1;
    }


    /**
     * Write all the firms collected in the file `monthFirms.txt` to prevent duplicates on main execution
     */
    private void registerFirmsCollected() {
        for (String lawFirm :lawFirmsCollected) {

            try (BufferedWriter br = new BufferedWriter(new FileWriter(CONFIG.MONTH_FILE))){
                br.write(String.valueOf(lawFirm));
            } catch (IOException e) {
                throw new RuntimeException(e);
            }

        }
    }


    /**
     * Register the row of the last firm collected in the file `lastRowRegisteredInContacts.txt`.
     */
    private void registerLastFirmCollectedRow(int rowOfLastFirm) {
        try (BufferedWriter br = new BufferedWriter(new FileWriter(CONFIG.LAST_FIRM_REGISTERED_FILE))){
            br.write(String.valueOf(rowOfLastFirm));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }


    /**
     * Collect the lawyers registered in the file `filteredCollectedContacts.xlsx`.
     * If it runs and don't collect all the necessary lawyer it clears the file
     * `lastRowRegisteredInContacts.txt` and re-run it-self.
     */
    public void collectLawyersRegistered() {
        int i;
        int addedThisRun = 0; // Track how many lawyers were actually registered this run

        // Start from the row of the last row registered + 1
        for (i = this.lastFirmRow; i <= this.getSheet().getLastRowNum(); i++) {

            if (totalLawyers == CONFIG.LAWYERS_IN_FILTER) break;

            Row row = this.getSheet().getRow(i);
            if (row == null) continue; // Skip empty rows

            String name = getCellValue(row.getCell(4));
            String firm = getCellValue(row.getCell(13));
            String email = getCellValue(row.getCell(5));

            // Skip empty rows
            if (email.isEmpty() || name.isEmpty()) {
                sheet.removeRow(row);
                sheet.shiftRows(i + 1, this.getSheet().getLastRowNum(), -1); // Compact rows
                i--; // Stay on same index since rows shifted
                continue;
            }

            // Cap per-firm lawyers
            if (firm.equals(lastFirm) && totalLawyersPerFirm >= 3) continue;

            if (contacts.isEmailRegistered(email)) {
                System.out.println("Email '" + email + "' is already registered. Cleaning up.");
                sheet.removeRow(row);
                sheet.shiftRows(i + 1, this.getSheet().getLastRowNum(), -1);
                i--;
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

            // Bool to avoid repeated countries
            boolean successfullyRegistered = destinationSheet.addLawyer(lawyer, false);
            if (successfullyRegistered) {
                sheet.removeRow(row);
                sheet.shiftRows(i + 1, this.getSheet().getLastRowNum(), -1);
                i--;

                totalLawyers++;
                addedThisRun++;
            }

            // Reset per-firm counter when firm changes
            if (!firm.equals(lastFirm)) {
                totalLawyersPerFirm = 0;
            }
            totalLawyersPerFirm++;

            lastFirm = firm;
        }

        // Re-run the function only if we made progress
        if (totalLawyers < CONFIG.LAWYERS_IN_FILTER && addedThisRun > 0) {
            this.lastFirm = "";
            this.lastFirmRow = 0;

            if (maxReRuns > 0) {
                maxReRuns--;
                System.out.println("\n\u001B[32mRe-running registered Lawyers filtering\u001B[0m.\n");
                this.collectLawyersRegistered();
            }
        }

        this.registerLastFirmCollectedRow(i);
        this.saveSheet();
        this.registerFirmsCollected();
    }


    /**
     * Helper method to safely get a cell's string value as a string.
     *
     * @param cell The cell to read from.
     * @return The cell value as a trimmed string, or an empty string if the cell is null.
     */
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
}