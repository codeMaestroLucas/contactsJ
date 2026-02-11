package org.example.src.utils.myInterface;

import lombok.Getter;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellType;
import org.apache.poi.ss.usermodel.Row;
import org.example.src.entities.BaseSites.Site;
import org.example.src.entities.excel.ContactsAlreadyRegisteredSheet;
import org.example.src.utils.ContinentConfig;
import org.example.src.utils.FirmsOMonth;

import java.util.*;
import java.util.function.Supplier;

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


    // Continent name → getter for each builder
    private static final Map<String, Supplier<Site[]>> BY_PAGE_GETTERS = Map.of(
            "Africa", ByPageFirmsBuilder::getAfrica,
            "Asia", ByPageFirmsBuilder::getAsia,
            "Europe", ByPageFirmsBuilder::getEurope,
            "North America", ByPageFirmsBuilder::getNorthAmerica,
            "Central America", ByPageFirmsBuilder::getCentralAmerica,
            "South America", ByPageFirmsBuilder::getSouthAmerica,
            "Oceania", ByPageFirmsBuilder::getOceania
    );

    private static final Map<String, Supplier<Site[]>> BY_NEW_PAGE_GETTERS = Map.of(
            "Africa", ByNewPageFirmsBuilder::getAfrica,
            "Asia", ByNewPageFirmsBuilder::getAsia,
            "Europe", ByNewPageFirmsBuilder::getEurope,
            "North America", ByNewPageFirmsBuilder::getNorthAmerica,
            "Central America", ByNewPageFirmsBuilder::getCentralAmerica,
            "South America", ByNewPageFirmsBuilder::getSouthAmerica,
            "Oceania", ByNewPageFirmsBuilder::getOceania
    );


    /**
     * Construct all firms ordered by continent weight (highest first), shuffled within each weight group.
     * Firms already registered in monthFirms.txt are excluded.
     * Mundial firms always have weight 0 (lowest priority).
     */
    public static List<Site> constructFirms() {
        // Group enabled continents by weight (descending)
        Map<Integer, List<String>> continentsByWeight = new TreeMap<>(Collections.reverseOrder());

        for (Map.Entry<String, ContinentConfig.ContinentSettings> entry : ContinentConfig.getConfig().entrySet()) {
            if (entry.getValue().isEnabled()) {
                int weight = entry.getValue().getWeight();
                continentsByWeight.computeIfAbsent(weight, k -> new ArrayList<>()).add(entry.getKey());
            }
        }

        List<Site> result = new ArrayList<>();

        // Process each weight group (highest weight first thanks to reverse TreeMap)
        for (Map.Entry<Integer, List<String>> weightGroup : continentsByWeight.entrySet()) {
            List<Site> groupFirms = new ArrayList<>();

            for (String continent : weightGroup.getValue()) {
                collectFirmsFromContinent(continent, groupFirms);
            }

            Collections.shuffle(groupFirms);
            result.addAll(groupFirms);
        }

        // Mundial firms — weight 0, always last
        List<Site> mundialFirms = new ArrayList<>();
        collectFilteredFirms(ByPageFirmsBuilder.getMundial(), mundialFirms);
        collectFilteredFirms(ByNewPageFirmsBuilder.getMundial(), mundialFirms);
        Collections.shuffle(mundialFirms);
        result.addAll(mundialFirms);

        return result;
    }

    private static void collectFirmsFromContinent(String continent, List<Site> dest) {
        Supplier<Site[]> byPage = BY_PAGE_GETTERS.get(continent);
        Supplier<Site[]> byNewPage = BY_NEW_PAGE_GETTERS.get(continent);

        if (byPage != null) collectFilteredFirms(byPage.get(), dest);
        if (byNewPage != null) collectFilteredFirms(byNewPage.get(), dest);
    }

    private static void collectFilteredFirms(Site[] firms, List<Site> dest) {
        for (Site site : firms) {
            if (site != null && !FirmsOMonth.isFirmRegisteredInMonth(site.name)) {
                dest.add(site);
            }
        }
    }


    /**
     * Shows continent configuration with firms breakdown by ByPage and ByNewPage.
     */
    private static void showContinentBreakdown() {
        int lineLength = 100;
        String title = "| CONTINENT CONFIGURATION |";
        int padding = (lineLength - title.length()) / 2;

        System.out.println("\n" + "=".repeat(lineLength));
        System.out.println(" ".repeat(padding) + title);
        System.out.println("=".repeat(lineLength));

        // Header
        System.out.printf(" %-18s │ %6s │ %6s │ %10s │ %10s │ %12s │ %12s%n",
                "Continent", "Status", "Weight", "ByPage", "ByNewPage", "Total Firms", "Max Lawyers");
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
            int weight = ContinentConfig.getContinentWeight(name);
            int totalFirms = byPage.length + byNewPage.length;
            int maxLawyers = countTotalMaxLawyer(byPage) + countTotalMaxLawyer(byNewPage);

            String statusIcon = enabled ? GREEN + "ON " + RESET : RED + "OFF" + RESET;
            String lineColor = enabled ? "" : DIM;
            String endColor = enabled ? "" : RESET;

            System.out.printf("%s %-18s │ %s   │ %6d │ %10d │ %10d │ %12d │ %12d%s%n",
                    lineColor, name, statusIcon, weight, byPage.length, byNewPage.length, totalFirms, maxLawyers, endColor);

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

        // Mundial (always enabled, weight 0)
        Site[] mundialByPage = ByPageFirmsBuilder.getMundial();
        Site[] mundialByNewPage = ByNewPageFirmsBuilder.getMundial();
        int mundialTotal = mundialByPage.length + mundialByNewPage.length;
        int mundialLawyers = countTotalMaxLawyer(mundialByPage) + countTotalMaxLawyer(mundialByNewPage);

        System.out.println("-".repeat(lineLength));
        System.out.printf(" %-18s │ %s%s%s   │ %6d │ %10d │ %10d │ %12d │ %12d%n",
                "Mundial", CYAN, "***", RESET, 0, mundialByPage.length, mundialByNewPage.length, mundialTotal, mundialLawyers);

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
        showAllFirmsCompleted();
    }
}
