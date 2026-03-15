package org.example;

import lombok.SneakyThrows;
import org.example.src.CONFIG;
import org.example.src.entities.BaseSites.Site;
import org.example.src.entities.excel.ContactsAlreadyRegisteredSheet;
import org.example.src.entities.excel.Reports;
import org.example.src.entities.excel.Sheet;
import org.example.src.utils.ErrorLogger;
import org.example.src.utils.FirmsOMonth;
import org.example.src.utils.NoSleep;
import org.example.src.utils.myInterface.CompletedFirms;
import org.example.src.utils.myInterface.MyInterfaceUtls;
import org.example.src.utils.validation.EmailDuplicateChecker;

import org.example.src.entities.MyDriver;

import java.util.List;
import java.util.concurrent.*;

import static org.example.src.utils.TimeCalculator.calculateTimeOfExecution;

public class Main {
    private static final Reports reports = Reports.getINSTANCE();
    private static final MyInterfaceUtls instance = MyInterfaceUtls.getINSTANCE();
    private static final MyInterfaceUtls interfaceUtls = CompletedFirms.interfaceUtls;

    private static void getRegisteredContacts() {
        ContactsAlreadyRegisteredSheet contactsSheet = new ContactsAlreadyRegisteredSheet();
        contactsSheet.collectLawyersRegistered();
    }

    /**
     * Searches for lawyers across all active firms, stopping once the global cap is reached.
     *
     * @param alreadyCollected number of lawyers already registered in previous phases,
     *                         so that the global limit is shared across all calls.
     */
    private static int searchLawyersInWeb(int alreadyCollected) throws InterruptedException {
        int totalLawyersRegistered = 0;
        int redo = 0;

        List<Site> sites = CompletedFirms.constructFirms();
        ExecutorService executor = Executors.newSingleThreadExecutor();

        for (Site site : sites) {
            if (Thread.currentThread().isInterrupted()
                    || (alreadyCollected + totalLawyersRegistered) >= (CONFIG.TOTAL_LAWYERS_TO_GET + CONFIG.LAWYERS_IN_FILTER)) {
                break;
            }

            interfaceUtls.header(site.name);
            long initTime = System.currentTimeMillis();

            Future<Void> future = executor.submit(() -> {
                site.searchForLawyers(false);
                return null;
            });

            boolean needsNewExecutor = false;

            try {
                future.get(CONFIG.TIMEOUT_MINUTES, TimeUnit.MINUTES);

                if (site.lawyersRegistered > 0) {
                    FirmsOMonth.registerFirmMonth(site.name);
                    totalLawyersRegistered += site.lawyersRegistered;
                }

            } catch (TimeoutException e) {
                System.err.println("Timeout exceeded for site: " + site.name);
                future.cancel(true);
                needsNewExecutor = true;

            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                System.err.println("Search interrupted for site: " + site.name);

            } catch (ExecutionException e) {
                System.err.println("An error occurred while searching in " + site.name + ": " + e.getMessage());
                future.cancel(true);
                if (e.getCause() != null) {
                    e.getCause().printStackTrace();
                }

                if (totalLawyersRegistered == 0 && redo == 0) {
                    System.out.println("REDOING FIRM IN THE FUTURE");
                    sites.add(site);
                    redo = 1;
                }

            } finally {
                long endTime = System.currentTimeMillis();
                reports.createReportRow(site, instance.calculateTime(initTime, endTime));
                redo = 0;

                // Clean up browser state between sites (clear cookies, close extra tabs)
                try {
                    MyDriver.cleanUpBetweenSites();
                } catch (Exception ignored) {}

                // If the previous site timed out or was interrupted, the executor's thread
                // may still be running or have a stale interrupt flag.
                // Recreate the executor so the next site gets a fresh, clean thread.
                if (needsNewExecutor) {
                    executor.shutdownNow();
                    executor = Executors.newSingleThreadExecutor();
                }

                Thread.sleep(2500);
            }
        }

        executor.shutdownNow();
        return totalLawyersRegistered;
    }

    @SneakyThrows
    private static int performCompleteSearch() {
//        Few contacts were
        calculateTimeOfExecution(Main::getRegisteredContacts);

        final int[] result = {0};
        calculateTimeOfExecution(() -> {
            try {
                result[0] = searchLawyersInWeb(0);
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        });

        return result[0];
    }

    public static void main(String[] args) {
        long globalStart = System.currentTimeMillis();
        int totalLawyers = 0;

        NoSleep.preventSleep(); // block sleep
        EmailDuplicateChecker.getINSTANCE().login();
        try {
            totalLawyers += performCompleteSearch();
            // Pass what was already collected so the global cap is shared between both phases
            totalLawyers += searchLawyersInWeb(totalLawyers);

        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        } finally {
            // Write any remaining logs that weren't flushed
            ErrorLogger.getINSTANCE().flushAllLogs();
            // Sort Sheet.xlsx rows: D → J → E → F → C
            Sheet.getINSTANCE().sortRows();
            // Close the reports workbook to ensure all data is saved
            reports.closeWorkbook();
            // Close the email duplicate checker session
            EmailDuplicateChecker.getINSTANCE().close();
            NoSleep.allowSleep(); // allow sleep again when finished

            // Final summary
            String totalTime = instance.calculateTime(globalStart, System.currentTimeMillis());
            System.out.println("\n" + "=".repeat(70));
            System.out.printf("  Total time: \u001B[1;33m%s\u001B[0m%n", totalTime);
            System.out.printf("  Total lawyers registered: \u001B[1;31m%d\u001B[0m%n", totalLawyers);
            System.out.println("=".repeat(70));
        }
    }
}
