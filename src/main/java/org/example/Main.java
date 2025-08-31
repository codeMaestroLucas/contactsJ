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

public class Main {
    private static final MyInterfaceUtls interfaceUtls = CompletedFirms.interfaceUtls;

    /**
     * Calculates the execution time of a given block of code.
     * @param taskToRun The code to execute, passed as a Runnable.
     */
    private static void calculateTimeOfExecution(Runnable taskToRun) {
        long startTime = System.currentTimeMillis();

        try {
            taskToRun.run();

        } catch (Exception e) {
            System.err.println("An error occurred during execution: " + e.getMessage());
            e.printStackTrace();

        } finally {
            long endTime = System.currentTimeMillis();

            System.out.println("\n" + "=".repeat(70));

            String formattedTime = interfaceUtls.calculateTime(startTime, endTime);
            System.out.println("\nTotal time: " + formattedTime + "\n\n");
        }
    }

    private static void getRegisteredContacts() {
        System.out.println("Starting: Collecting previously registered lawyers...");
        ContactsAlreadyRegisteredSheet contactsSheet = new ContactsAlreadyRegisteredSheet();
        contactsSheet.collectLawyersRegistered();
        System.out.println("Completed: Filtering and processing complete.");
    }

    private static void serachLawyersInWeb() {
        System.out.println("Starting: Searching for new lawyers...");
        int totalLawyersRegistered = 0;

        List<Site> sites = CompletedFirms.constructFirms(CONFIG.LAWYERS_IN_SHEET);
        Reports reports = Reports.getINSTANCE();

        // Processing Firms
        try {
            // Already making the verification of Site.name registered in the`monthFirms.txt` in the constructor
            for (Site site : sites) {
                long initTimeFirm = System.currentTimeMillis();

                interfaceUtls.header(site.name);
                site.searchForLawyers(false);

                long finalTimeFirm = System.currentTimeMillis();
                String time = interfaceUtls.calculateTime(initTimeFirm, finalTimeFirm);

                System.out.println("\n  - Time: " + time);
                System.out.println("  - Lawyers registered: " + site.lawyersRegistered + "\n");

                reports.createReportRow(site, time);

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
    }


    private static void performCompleteSearch() {
//        calculateTimeOfExecution(Main::getRegisteredContacts);
        calculateTimeOfExecution(Main::serachLawyersInWeb);
        System.out.println("\n\n" + "=".repeat(70));
    }

    public static void main(String[] args) {
        calculateTimeOfExecution(Main::performCompleteSearch);
    }
}
