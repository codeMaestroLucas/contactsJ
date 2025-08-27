package org.example.src.utils.myInterface;

import lombok.Getter;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellType;
import org.apache.poi.ss.usermodel.Row;
import org.example.src.CONFIG;
import org.example.src.entities.BaseSites.Site;
import org.example.src.entities.MyDriver;
import org.example.src.entities.excel.ContactsAlreadyRegisteredSheet;
import org.example.src.utils.FirmsOfWeek;

import java.util.*;

@Getter
public class CompletedFirms {
    private final static Site[] byPage = _CompletedFirmsData.byPage;
    private final static Site[] byNewPage = _CompletedFirmsData.byNewPage;
    private final static Site[] byFilter = _CompletedFirmsData.byFilter;
    private final static Site[] byClick = _CompletedFirmsData.byClick;

    public final static MyInterfaceUtls interfaceUtls = MyInterfaceUtls.getINSTANCE();


    /**
     * Construct all firms that are not inserted in the week file then insert the filter firms in the in an array and
     * then shuffle it.
     */
    public static List<Site> constructFirms(int maxFirmsToGet) {
        Site[][] sites = new Site[][] {byPage, byNewPage, byFilter, byClick};
        List<Site> filteredSites = new ArrayList<>();

        // Filter all sites that weren't registered in the `week` file
        for (Site[] category : sites) {
            for (Site site : category) {
                if (site != null && !FirmsOfWeek.isRegisteredInFirmWeek(site.name)) filteredSites.add(site);            }
        }

        Collections.shuffle(filteredSites);
        return filteredSites.subList(0, maxFirmsToGet);
    }


    /**
     * A log print to count all sites completed
     */
    private static int showSitesCompleted() {
        String title = "| SITES COMPLETED |";
        int lineLength = 70;
        int padding = (lineLength - title.length()) / 2;

        System.out.println("-".repeat(padding) + title + "-".repeat(padding));

        Object[][] categories = {
                { "ByPage",    byPage },
                { "ByNewPage", byNewPage },
                { "ByFilter",  byFilter },
                { "ByClick",   byClick }
        };

        int grandTotal = 0;
        int totalFirmsRegistered =0;

        for (Object[] category : categories) {
            String label = (String) category[0];
            Site[] firms = (Site[]) category[1];

            int totalToRegister = countTotalMaxLawyer(firms);
            grandTotal += totalToRegister;
            totalFirmsRegistered += firms.length;

            System.out.printf(" - %-10s \u001B[33m%-30s\u001B[0m To Register: \u001B[34m%d\u001B[0m%n", label + ":", firms.length, totalToRegister);
        }

        System.out.println("-".repeat(lineLength));
        System.out.printf("  Total Firms: \u001B[1;33m%-20d\u001B[0m Total Lawyers to Register: \u001B[1;34m%d\u001B[0m%n", totalFirmsRegistered, grandTotal);
        System.out.println("-".repeat(lineLength));

        return grandTotal;
    }


    private static int countTotalMaxLawyer(Site[] firms) {
        int total = 0;
        for (Site firm : firms) {
            total += firm.maxLawyersForSite;
        }
        return total;
    }

    /**
     * A log print to count all filtered lawyers
     */
    private static int showFilteredContacts() {
        ContactsAlreadyRegisteredSheet sheet = new ContactsAlreadyRegisteredSheet();
        int lastRow = sheet.getSheet().getLastRowNum();
        int nonEmptyRows = 0;

        for (int i = 0; i <= lastRow; i++) {
            Row row = sheet.getSheet().getRow(i);
            if (row == null) continue; // skip empty rows

            for (Cell cell : row) {
                if (cell.getCellType() != CellType.BLANK && cell.getCellType() != CellType._NONE) {
                    nonEmptyRows++;
                    break;
                }
            }
        }

        String title = "| FILTERED LAWYERS |";
        int lineLength = 70;
        int padding = Math.max(0, (lineLength - title.length()) / 2);

        System.out.println("-".repeat(padding) + title + "-".repeat(lineLength - padding - title.length()));
        System.out.println(" - Filtered Lawyers:" + " ".repeat(38)  + "\u001B[34m" + nonEmptyRows + "\u001B[0m");
        System.out.println("-".repeat(lineLength));

        return nonEmptyRows;
    }


    /**
     * Perform a log of the total of lawyers registered by the Search in Web and by the Filtered Contacts file.
     * Then it shows the total amount of lawyers registered
     */
    private static void showAllFirmsCompleted() {
        System.out.println("\n\n");
        int totalMaxLawyers = showFilteredContacts();
        totalMaxLawyers += showSitesCompleted();

        String title = "| TOTAL |";
        int lineLength = 70;
        int padding = Math.max(0, (lineLength - title.length()) / 2);

        System.out.println("=".repeat(padding) + title + "=".repeat(lineLength - padding - title.length()));
        System.out.println(" - Total Lawyers:" + " ".repeat(41)  + "\u001B[34m" + totalMaxLawyers + "\u001B[0m");
        System.out.println("=".repeat(lineLength));
    }


    public static void main(String[] args) {
        MyDriver.quitDriver();
        showAllFirmsCompleted();
    }}
