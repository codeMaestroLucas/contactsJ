package org.example;

import org.example.src.CONFIG;
import org.example.src.entities.BaseSites.Site;
import org.example.src.entities.MyDriver;
import org.example.src.entities.excel.ContactsAlreadyRegisteredSheet;
import org.example.src.entities.excel.Reports;
import org.example.src.utils.FirmsOMonth;
import org.example.src.utils.myInterface.CompletedFirms;
import org.example.src.utils.myInterface.MyInterfaceUtls;

import java.util.List;
import java.util.concurrent.*;

import static org.example.src.utils.TimeCalculator.calculateTimeOfExecution;
import static org.example.src.utils.TimeCalculator.calculateTimeOfExecutionWithTimeout;

public class Main {
    private static final MyInterfaceUtls interfaceUtls = CompletedFirms.interfaceUtls;

    private static void getRegisteredContacts() {
        System.out.println("Starting: Collecting previously registered lawyers...");
        ContactsAlreadyRegisteredSheet contactsSheet = new ContactsAlreadyRegisteredSheet();
        contactsSheet.collectLawyersRegistered();
        System.out.println("Completed: Filtering and processing complete.");
    }

    private static void searchLawyersInWeb() {
        System.out.println("Starting: Searching for new lawyers...");
        int totalLawyersRegistered = 0;

        List<Site> sites = CompletedFirms.constructFirms(CONFIG.LAWYERS_IN_SHEET + 70);

        try {
            for (Site site : sites) {
                if (Thread.currentThread().isInterrupted()) {
                    System.out.println("Execution interrupted - stopping lawyer search.");
                    break;
                }

                interfaceUtls.header(site.name);

                // Measure time + stop if > 10 minutes
                calculateTimeOfExecutionWithTimeout(() -> {
                    try {
                        site.searchForLawyers(false);
                    } catch (Exception e) {
                        throw new RuntimeException(e);
                    }
                }, site);


                if (site.lawyersRegistered > 0) {
                    FirmsOMonth.registerFirmMonth(site.name);
                    totalLawyersRegistered += site.lawyersRegistered;
                }


                if (totalLawyersRegistered >= CONFIG.LAWYERS_IN_SHEET) break;
            }

        } catch (Exception e) {
            System.err.println("An error occurred while searching for lawyers: " + e.getMessage());
            e.printStackTrace();

        } finally {
            MyDriver.quitDriver();
        }

        System.out.println("Completed: Lawyer search finished.");
        System.out.println("Total lawyers collected in web:" + totalLawyersRegistered);
        System.out.println();
    }


    private static void performCompleteSearch() {
        calculateTimeOfExecution(Main::getRegisteredContacts);
        calculateTimeOfExecution(Main::searchLawyersInWeb);

        System.out.println("\n\n" + "=".repeat(70));
    }

    public static void main(String[] args) {
        performCompleteSearch();
    }
}
