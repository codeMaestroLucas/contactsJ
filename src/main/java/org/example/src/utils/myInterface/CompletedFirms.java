package org.example.src.utils.myInterface;

import lombok.Getter;
import org.example.src.entities.BaseSites.Site;
import org.example.src.utils.FirmsOfWeek;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

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
                if (site != null
                    && !FirmsOfWeek.isRegisteredInFirmWeek(site.name)) {
                        filteredSites.add(site);
                }
            }
        }

        Collections.shuffle(filteredSites);
        return filteredSites.subList(0, maxFirmsToGet);
    }


    /**
     * A log print to count all firms completed
     */
    public static void showCompletedFirmsPrint() {
        String title = "| COMPLETED |";
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

            System.out.printf(" -  %-10s %-30s To Register: %d%n", label + ":", firms.length, totalToRegister);
        }

        System.out.println("-".repeat(lineLength));
        System.out.printf("  Total Firms: %-20d Total Lawyers to Register: %d%n", totalFirmsRegistered, grandTotal);
        System.out.println("-".repeat(lineLength));
    }


    private static int countTotalMaxLawyer(Site[] firms) {
        int total = 0;
        for (Site firm : firms) {
            total += firm.maxLawyersForSite;
        }
        return total;
    }


    public static void main(String[] args) { showCompletedFirmsPrint(); }
}
