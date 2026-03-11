package org.example.src.entities.excel;

import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFCellStyle;
import org.apache.poi.xssf.usermodel.XSSFColor;
import org.example.src.CONFIG;
import org.example.src.entities.Lawyer;
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
            return 0;
        }

        if (Objects.isNull(firmToCheck) || firmToCheck.trim().isEmpty()) {
            return 0;
        }

        try {
            return Integer.parseInt(firmToCheck.trim()) + 1;
        } catch (NumberFormatException e) {
            return 0;
        }
    }


    /**
     * Write all the firms collected in the file `monthFirms.txt` to prevent duplicates on main execution
     */
    private void registerFirmsCollected() {
        try (BufferedWriter bw = new BufferedWriter(new FileWriter(CONFIG.MONTH_FILE, true))) {
            for (String lawFirm : lawFirmsCollected) {
                bw.write(lawFirm);
                bw.newLine();
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
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
        int lastRowNum = this.getSheet().getLastRowNum();

        System.out.println("\n=== Starting collectLawyersRegistered ===");
        System.out.println("Starting from row: " + this.lastFirmRow);
        System.out.println("Last row in sheet: " + lastRowNum);
        System.out.println("Target lawyers: " + CONFIG.LAWYERS_IN_FILTER);
        System.out.println("==========================================\n");

        for (i = this.lastFirmRow; i <= lastRowNum; i++) {

            if (totalLawyers == CONFIG.LAWYERS_IN_FILTER) break;

            Row row = this.getSheet().getRow(i);
            if (row == null) continue;

            String name = getCellValue(row.getCell(4));
            String firm = getCellValue(row.getCell(13));
            String email = getCellValue(row.getCell(5));
            String country = getCellValue(row.getCell(7));

            if (Validations.isACountryToAvoid(country)) continue;

            if (email.isEmpty() || name.isEmpty()) continue;

            // Reset counters when firm changes
            if (!firm.equals(lastFirm)) {
                totalLawyersPerFirm = 0;
                setOfCountriesCollectPerFirm.clear();
                lastFirm = firm;
            }

            // Skip if we already have 3 lawyers from this firm or country already collected for this firm
            if (totalLawyersPerFirm >= 3 || setOfCountriesCollectPerFirm.contains(country.toLowerCase().trim())) {
                continue;
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
                totalLawyers++;
                addedThisRun++;
                setOfCountriesCollectPerFirm.add(country.toLowerCase().trim());
                totalLawyersPerFirm++;
                lawFirmsCollected.add(firm);
            }
        }

        // re-run if needed
        if (totalLawyers < CONFIG.LAWYERS_IN_FILTER && addedThisRun > 0) {
            this.lastFirm = "";
            this.lastFirmRow = 0;
            if (maxReRuns > 0) {
                maxReRuns--;
                this.collectLawyersRegistered();
            }
        }

        System.out.println("\n=== Collection Complete ===");
        System.out.println("Total lawyers added: " + totalLawyers);
        System.out.println("Firms collected: " + lawFirmsCollected.size());
        System.out.println("Last row processed: " + i);
        System.out.println("===========================\n");

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


    public static void main(String[] args) {
        // Remember to change the LastRow to 0
        ContactsAlreadyRegisteredSheet sheet1 = new ContactsAlreadyRegisteredSheet();
        sheet1.collectLawyersRegistered();
    }
}
