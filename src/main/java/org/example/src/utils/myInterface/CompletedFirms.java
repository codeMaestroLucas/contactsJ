package org.example.src.utils.myInterface;

import lombok.Getter;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellType;
import org.apache.poi.ss.usermodel.Row;
import org.example.src.entities.BaseSites.Site;
import org.example.src.entities.MyDriver;
import org.example.src.entities.excel.ContactsAlreadyRegisteredSheet;
import org.example.src.utils.ContinentConfig;
import org.example.src.utils.FirmsOMonth;

import java.util.*;

@Getter
public class CompletedFirms {
    private static Site[] getByPage() { return ByPageFirmsBuilder.build(); }
    private static Site[] getByNewPage() { return ByNewPageFirmsBuilder.build(); }

    public final static MyInterfaceUtls interfaceUtls = MyInterfaceUtls.getINSTANCE();

    // ANSI color codes
    private static final String RESET = "\u001B[0m";
    private static final String GREEN = "\u001B[32m";
    private static final String RED = "\u001B[31m";
    private static final String YELLOW = "\u001B[33m";
    private static final String BLUE = "\u001B[34m";
    private static final String CYAN = "\u001B[36m";
    private static final String BOLD = "\u001B[1m";
    private static final String DIM = "\u001B[2m";


    /**
     * Construct all firms that are not inserted in the monthFirms.txt file then insert them in an array and shuffle it.
     * Respects continent configuration - only includes firms from enabled continents.
     */
    public static List<Site> constructFirms() {
        Site[][] sites = new Site[][] {getByPage(), getByNewPage()};
        List<Site> filteredSites = new ArrayList<>();

        // Filter all sites that weren't registered in the `monthsFirms.txt` file
        for (Site[] category : sites) {
            for (Site site : category) if (site != null && !FirmsOMonth.isFirmRegisteredInMonth(site.name)) filteredSites.add(site);
        }

        Collections.shuffle(filteredSites);
        return filteredSites;
    }


    /**
     * Shows continent configuration with firms breakdown by ByPage and ByNewPage.
     */
    private static void showContinentBreakdown() {
        int lineLength = 90;
        String title = "| CONTINENT CONFIGURATION |";
        int padding = (lineLength - title.length()) / 2;

        System.out.println("\n" + "=".repeat(lineLength));
        System.out.println(" ".repeat(padding) + title);
        System.out.println("=".repeat(lineLength));

        // Header
        System.out.printf(" %-18s │ %6s │ %10s │ %10s │ %12s │ %12s%n",
                "Continent", "Status", "ByPage", "ByNewPage", "Total Firms", "Max Lawyers");
        System.out.println("-".repeat(lineLength));

        // Continent data structure: name, byPageGetter, byNewPageGetter
        Object[][] continents = {
                {"Africa",          ByPageFirmsBuilder.getAfrica(),         ByNewPageFirmsBuilder.getAfrica()},
                {"Asia",            ByPageFirmsBuilder.getAsia(),           ByNewPageFirmsBuilder.getAsia()},
                {"Europe",          ByPageFirmsBuilder.getEurope(),         ByNewPageFirmsBuilder.getEurope()},
                {"North America",   ByPageFirmsBuilder.getNorthAmerica(),   ByNewPageFirmsBuilder.getNorthAmerica()},
                {"Central America", ByPageFirmsBuilder.getCentralAmerica(), ByNewPageFirmsBuilder.getCentralAmerica()},
                {"South America",   ByPageFirmsBuilder.getSouthAmerica(),   ByNewPageFirmsBuilder.getSouthAmerica()},
                {"Oceania",         ByPageFirmsBuilder.getOceania(),        ByNewPageFirmsBuilder.getOceania()},
        };

        int totalEnabled = 0, totalDisabled = 0;
        int firmsEnabled = 0, firmsDisabled = 0;
        int lawyersEnabled = 0, lawyersDisabled = 0;

        for (Object[] continent : continents) {
            String name = (String) continent[0];
            Site[] byPage = (Site[]) continent[1];
            Site[] byNewPage = (Site[]) continent[2];

            boolean enabled = ContinentConfig.isContinentEnabled(name);
            int totalFirms = byPage.length + byNewPage.length;
            int maxLawyers = countTotalMaxLawyer(byPage) + countTotalMaxLawyer(byNewPage);

            String statusIcon = enabled ? GREEN + "ON " + RESET : RED + "OFF" + RESET;
            String lineColor = enabled ? "" : DIM;
            String endColor = enabled ? "" : RESET;

            System.out.printf("%s %-18s │ %s   │ %10d │ %10d │ %12d │ %12d%s%n",
                    lineColor, name, statusIcon, byPage.length, byNewPage.length, totalFirms, maxLawyers, endColor);

            if (enabled) {
                totalEnabled++;
                firmsEnabled += totalFirms;
                lawyersEnabled += maxLawyers;
            } else {
                totalDisabled++;
                firmsDisabled += totalFirms;
                lawyersDisabled += maxLawyers;
            }
        }

        // Mundial (always enabled)
        Site[] mundialByPage = ByPageFirmsBuilder.getMundial();
        Site[] mundialByNewPage = ByNewPageFirmsBuilder.getMundial();
        int mundialTotal = mundialByPage.length + mundialByNewPage.length;
        int mundialLawyers = countTotalMaxLawyer(mundialByPage) + countTotalMaxLawyer(mundialByNewPage);

        System.out.println("-".repeat(lineLength));
        System.out.printf(" %-18s │ %s%s%s   │ %10d │ %10d │ %12d │ %12d%n",
                "Mundial", CYAN, "***", RESET, mundialByPage.length, mundialByNewPage.length, mundialTotal, mundialLawyers);

        // Summary
        System.out.println("=".repeat(lineLength));

        int grandTotalFirms = firmsEnabled + firmsDisabled + mundialTotal;
        int grandTotalLawyers = lawyersEnabled + lawyersDisabled + mundialLawyers;
        int activeFirms = firmsEnabled + mundialTotal;
        int activeLawyers = lawyersEnabled + mundialLawyers;

        System.out.printf("%n %sSUMMARY:%s%n", BOLD, RESET);
        System.out.printf("   Continents: %s%d enabled%s / %s%d disabled%s%n",
                GREEN, totalEnabled, RESET, RED, totalDisabled, RESET);
        System.out.printf("   Active Firms:    %s%d%s / %d total  (%s%.1f%%%s)%n",
                GREEN, activeFirms, RESET, grandTotalFirms, YELLOW, (activeFirms * 100.0 / grandTotalFirms), RESET);
        System.out.printf("   Active Lawyers:  %s%d%s / %d total  (%s%.1f%%%s)%n",
                GREEN, activeLawyers, RESET, grandTotalLawyers, YELLOW, (activeLawyers * 100.0 / grandTotalLawyers), RESET);

        System.out.println("=".repeat(lineLength));
    }


    /**
     * A log print to count all sites completed (only enabled continents)
     */
    private static int showSitesCompleted() {
        int lineLength = 90;
        String title = "| ACTIVE SITES |";
        int padding = (lineLength - title.length()) / 2;

        System.out.println("\n" + "-".repeat(padding) + title + "-".repeat(padding));

        Object[][] categories = {
                { "ByPage",    getByPage()    },
                { "ByNewPage", getByNewPage() },
        };

        int grandTotal = 0;
        int totalFirmsRegistered = 0;

        for (Object[] category : categories) {
            String label = (String) category[0];
            Site[] firms = (Site[]) category[1];

            int totalToRegister = countTotalMaxLawyer(firms);
            grandTotal += totalToRegister;
            totalFirmsRegistered += firms.length;

            System.out.printf(" - %-10s %s%-30s%s To Register: %s%d%s%n",
                    label + ":", YELLOW, firms.length + " firms", RESET, BLUE, totalToRegister, RESET);
        }

        System.out.println("-".repeat(lineLength));
        System.out.printf("  %sTotal Active Firms:%s %s%-15d%s %sMax Lawyers:%s %s%d%s%n",
                BOLD, RESET, YELLOW, totalFirmsRegistered, RESET, BOLD, RESET, BLUE, grandTotal, RESET);
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
            if (row == null) continue;

            for (Cell cell : row) {
                if (cell.getCellType() != CellType.BLANK && cell.getCellType() != CellType._NONE) {
                    nonEmptyRows++;
                    break;
                }
            }
        }

        int lineLength = 90;
        String title = "| FILTERED LAWYERS |";
        int padding = Math.max(0, (lineLength - title.length()) / 2);

        System.out.println("-".repeat(padding) + title + "-".repeat(lineLength - padding - title.length()));
        System.out.printf(" - Filtered Lawyers: %s%d%s%n", BLUE, nonEmptyRows, RESET);
        System.out.println("-".repeat(lineLength));

        return nonEmptyRows;
    }


    /**
     * Perform a log of the total of lawyers registered by the Search in Web and by the Filtered Contacts file.
     * Then it shows the total amount of lawyers registered
     */
    private static void showAllFirmsCompleted() {
        System.out.println("\n");

        // Show continent breakdown first
        showContinentBreakdown();

        // Then show active sites and filtered contacts
        int totalMaxLawyers = showFilteredContacts();
        totalMaxLawyers += showSitesCompleted();

        int lineLength = 90;
        String title = "| GRAND TOTAL |";
        int padding = Math.max(0, (lineLength - title.length()) / 2);

        System.out.println("=".repeat(padding) + title + "=".repeat(lineLength - padding - title.length()));
        System.out.printf(" %sTotal Lawyers Available:%s %s%d%s%n", BOLD, RESET, BLUE, totalMaxLawyers, RESET);
        System.out.println("=".repeat(lineLength));
    }


    public static void main(String[] args) {
        MyDriver.quitDriver();
        showAllFirmsCompleted();
    }
}
