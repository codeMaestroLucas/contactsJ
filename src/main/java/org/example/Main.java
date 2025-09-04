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

public class Main {
    private static final MyInterfaceUtls interfaceUtls = CompletedFirms.interfaceUtls;
    private static final long TIMEOUT_MINUTES = 10;
    private static final Reports reports = Reports.getINSTANCE();


    /**
     * Calculates the execution time of a given block of code (no timeout).
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
            String formattedTime = interfaceUtls.calculateTime(startTime, endTime);

            System.out.println("\n" + "=".repeat(70));
            System.out.println("\nTotal time: " + formattedTime);
            System.out.println();
        }
    }

    /**
     * Calculates the execution time of a given block of code with a 10-minute timeout.
     */
    private static void calculateTimeOfExecutionWithTimeout(Runnable taskToRun, Site site) {
        long startTime = System.currentTimeMillis();
        ExecutorService executor = Executors.newSingleThreadExecutor();

        try {
            Future<?> future = executor.submit(taskToRun);
            future.get(TIMEOUT_MINUTES, TimeUnit.MINUTES);

        } catch (TimeoutException e) {
            System.err.println("Execution timed out after " + TIMEOUT_MINUTES + " minutes. Stopping execution.");
            executor.shutdownNow();

        } catch (ExecutionException e) {
            System.err.println("An error occurred during execution: " + e.getCause().getMessage());
            e.getCause().printStackTrace();

        } catch (InterruptedException e) {
            System.err.println("Execution was interrupted: " + e.getMessage());
            Thread.currentThread().interrupt();

        } finally {
            if (!executor.isShutdown()) {
                executor.shutdown();
            }

            long endTime = System.currentTimeMillis();
            long elapsedTime   = endTime - startTime;
            System.out.println("\n" + "=".repeat(70));

            String time = interfaceUtls.calculateTime(startTime, endTime);

            System.out.println("\n  - Time: " + time);
            System.out.println("  - Lawyers registered: " + site.lawyersRegistered + "\n");


            reports.createReportRow(site, time);


            if (elapsedTime >= TimeUnit.MINUTES.toMillis(TIMEOUT_MINUTES)) {
                System.out.println("\nExecution stopped due to timeout after " + TIMEOUT_MINUTES + " minutes");
            } else {
                String formattedTime = interfaceUtls.calculateTime(startTime, endTime);
                System.out.println("\nTotal time: " + formattedTime);
            }
            System.out.println();
        }
    }

    private static void getRegisteredContacts() {
        System.out.println("Starting: Collecting previously registered lawyers...");
        ContactsAlreadyRegisteredSheet contactsSheet = new ContactsAlreadyRegisteredSheet();
        contactsSheet.collectLawyersRegistered();
        System.out.println("Completed: Filtering and processing complete.");
    }

    private static void searchLawyersInWeb() {
        System.out.println("Starting: Searching for new lawyers...");
        int totalLawyersRegistered = 0;

        List<Site> sites = CompletedFirms.constructFirms(CONFIG.LAWYERS_IN_SHEET + 20);

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
