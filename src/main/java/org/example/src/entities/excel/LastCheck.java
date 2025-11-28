package org.example.src.entities.excel;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.DateUtil;
import org.apache.poi.ss.usermodel.Row;
import org.example.src.CONFIG;
import org.example.src.utils.NoSleep;
import org.example.src.utils.validation.EmailDuplicateChecker;

/**
 * Class to validate emails in a spreadsheet using EmailDuplicateChecker.
 * Iterates through all rows, checks if emails are registered on the site,
 * and removes rows with registered emails while keeping count.
 */
public final class LastCheck extends Excel {
    
    private int registeredEmailsCount = 0;
    private final EmailDuplicateChecker emailChecker = EmailDuplicateChecker.getINSTANCE();

    public LastCheck() {
        super(CONFIG.LAST_CHECK_SHEET_FILE);
    }

    /**
     * Process all rows in the sheet, checking each email against the duplicate checker.
     * If an email is registered (not clean), removes the row and increments counter.
     */
    public void checkAndCleanEmails() {
        System.out.println("\n=== Starting Email Validation Process ===\n");
        
        int totalRowsProcessed = 0;
        int lastRowNum = this.getSheet().getLastRowNum();

        // Start from row 1 (skip header at row 0)
        for (int i = 1; i <= lastRowNum; i++) {
            Row row = this.getSheet().getRow(i);
            
            if (row == null) continue;

            String email = getCellValue(row.getCell(1));

            if (email.isEmpty()) {
                System.out.println("Row " + i + ": Empty email, skipping...");
                continue;
            }

            totalRowsProcessed++;

            // Check if email is clean (not registered)
            boolean isClean = emailChecker.isEmailClean(email);

            if (!isClean) {
                // Email is registered on the site - remove row and count it
                System.err.println(email + "  →  IS REGISTERED. Removing row...");
                sheet.removeRow(row);
                registeredEmailsCount++;
            }
        }

        // Save the changes to the sheet
        this.saveSheet();

        // Print final summary
        System.out.println("\n=== Email Validation Complete ===");
        System.out.println("Total emails processed: " + totalRowsProcessed);
        System.out.println("Emails that were registered on the site: " + registeredEmailsCount);
        System.out.println("Emails that are clean (not registered): " + (totalRowsProcessed - registeredEmailsCount));
        System.out.println("\nSheet has been updated and saved.");
        System.out.println("===================================\n");
    }

    /**
     * Helper method to safely get a cell's value as a string.
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

    /**
     * Cleanup method to close resources
     */
    public void cleanup() {
        emailChecker.close();
        this.closeWorkbook();
    }

    public static void main(String[] args) {
        LastCheck checker = new LastCheck();

        NoSleep.preventSleep(); // block sleep
        try {
            checker.checkAndCleanEmails();
        } finally {
            // Always cleanup resources
            checker.cleanup();
            NoSleep.allowSleep(); // allow sleep again when finished
        }
    }
}
