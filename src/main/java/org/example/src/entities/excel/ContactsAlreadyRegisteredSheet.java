package org.example.src.entities.excel;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.DateUtil;
import org.apache.poi.ss.usermodel.Row;
import org.example.src.CONFIG;
import org.example.src.entities.Lawyer;
import org.example.src.entities.MyDriver;
import org.example.src.utils.Validations;

import java.io.*;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;


public final class ContactsAlreadyRegisteredSheet extends Excel{
    private String lastFirm = "";
    private int totalLawyersPerFirm = 0;
    private int totalLawyers = 0;
    private int lastFirmRow;
    private int maxReRuns = 1;
    private Set<String> lawFirmsCollected = new HashSet<>();
    private Set<String> setOfCountriesCollectPerFirm = new HashSet<>();

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
     * Collect and register lawyers into the destination sheet.
     * Removes duplicates, enforces per-firm limits, and re-runs if necessary.
     */
    public void collectLawyersRegistered() {
        int i;
        int addedThisRun = 0;

        for (i = this.lastFirmRow; i <= this.getSheet().getLastRowNum(); i++) {

            if (totalLawyers == CONFIG.LAWYERS_IN_FILTER) break;

            Row row = this.getSheet().getRow(i);
            if (row == null) continue;

            String name = getCellValue(row.getCell(4));
            String firm = getCellValue(row.getCell(13));
            String email = getCellValue(row.getCell(5));
            String country = getCellValue(row.getCell(7));

            if (Validations.isACountryToAvoid(country)) continue;

            if (email.isEmpty() || name.isEmpty()) {
                sheet.removeRow(row);
                continue;
            }

            if (contacts.isEmailRegistered(email)) {
                System.out.println("Email '" + email + "' is already registered. Cleaning up.");
                sheet.removeRow(row);
                continue;
            }

            if (firm.equals(lastFirm)) {
                if (totalLawyersPerFirm == 3 || setOfCountriesCollectPerFirm.contains(country.toLowerCase().trim())
                ) continue;
            }

            // collect full lawyer info
            String phone = getCellValue(row.getCell(6));
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

            boolean successfullyRegistered = destinationSheet.addLawyer(lawyer, false);
            if (successfullyRegistered) {
                sheet.removeRow(row);

                totalLawyers++;
                addedThisRun++;
                setOfCountriesCollectPerFirm.add(country.toLowerCase().trim());
                totalLawyersPerFirm++;
                lastFirm = firm;
            }

            if (!firm.equals(lastFirm)) {
                totalLawyersPerFirm = 0;
                setOfCountriesCollectPerFirm.clear();
            }
        }

        // re-run if needed
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
     * Only filters the contacts in the sheet (removes invalid or duplicate rows).
     */
    public void filterContactsOnly() {
        //change the number if needed
        for (int i = 0; i <= 300; i++) {
            Row row = this.getSheet().getRow(i);
            if (row == null) continue;

            String email = getCellValue(row.getCell(5));

            if (contacts.isEmailRegistered(email)) {
                System.out.println("Email '" + email + "' is already registered. Cleaning up.");
                sheet.removeRow(row);
            }
        }
        this.saveSheet();
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


    public static void main(String[] args) {
        // Remember to change the LastRow to 0
        ContactsAlreadyRegisteredSheet sheet1 = new ContactsAlreadyRegisteredSheet();
        sheet1.collectLawyersRegistered();
    }
}
