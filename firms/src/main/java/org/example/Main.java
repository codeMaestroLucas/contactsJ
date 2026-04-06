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
import org.example.src.utils.Stopwatch;
import org.example.src.utils.myInterface.CompletedFirms;
import org.example.src.utils.myInterface.MyInterfaceUtls;
import org.example.src.utils.validation.EmailDuplicateChecker;

import org.example.src.entities.MyDriver;

import java.util.List;
import java.util.concurrent.*;


public class Main {
    private static final Reports reports = Reports.getINSTANCE();
    private static final MyInterfaceUtls interfaceUtls = CompletedFirms.interfaceUtls;

    private static ContactsAlreadyRegisteredSheet getRegisteredContacts() {
        ContactsAlreadyRegisteredSheet contactsSheet = new ContactsAlreadyRegisteredSheet();
        contactsSheet.collectLawyersRegistered();
        return contactsSheet;
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
        int firmsProcessed = 0;
        final int DRIVER_RESTART_INTERVAL = 35;

        List<Site> sites = CompletedFirms.constructFirms();
        ExecutorService executor = Executors.newSingleThreadExecutor();

        for (Site site : sites) {
            if (Thread.currentThread().isInterrupted()
                    || (alreadyCollected + totalLawyersRegistered) >= (CONFIG.TOTAL_LAWYERS_TO_GET + CONFIG.LAWYERS_IN_FILTER)) {
                break;
            }

            interfaceUtls.header(site.name);
            Stopwatch siteTimer = new Stopwatch();
            siteTimer.start();

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
                siteTimer.stop();
                reports.createReportRow(site, siteTimer.format());
                redo = 0;
                firmsProcessed++;

                // Clean up browser state between sites (clear cookies, close extra tabs)
                try {
                    MyDriver.cleanUpBetweenSites();
                } catch (Exception ignored) {}

                // Preventive restart every N firms to avoid browser memory/state degradation
                if (firmsProcessed % DRIVER_RESTART_INTERVAL == 0) {
                    System.out.println("[Driver] Restarting browser after " + DRIVER_RESTART_INTERVAL + " firms...");
                    MyDriver.restartDriver();
                }

                // If the previous site timed out or was interrupted, the executor's thread
                // may still be running or have a stale interrupt flag.
                // Recreate the executor and browser so the next site gets a fresh, clean state.
                if (needsNewExecutor) {
                    executor.shutdownNow();
                    executor = Executors.newSingleThreadExecutor();
                    MyDriver.restartDriver();
                }

                Thread.sleep(2500);
            }
        }

        executor.shutdownNow();
        return totalLawyersRegistered;
    }

    /** Returns [contactLawyers, contactFirms, webLawyers1] */
    @SneakyThrows
    private static int[] performCompleteSearch() throws InterruptedException {
        ContactsAlreadyRegisteredSheet sheet = getRegisteredContacts();

        // Compute BEFORE Phase 2 so the cap is shared with Phase 1's results
        int contactLawyers = sheet.getTotalLawyers();
        int contactFirms   = sheet.getLawFirmsCollectedCount();

        int web1 = searchLawyersInWeb(contactLawyers);

        return new int[]{ contactLawyers, contactFirms, web1 };
    }

    private static void printExecutionSummary(
            int contactLawyers, int contactFirms,
            int webLawyers1, int webLawyers2,
            String totalTime) {

        final String RESET  = "\u001B[0m";
        final String BOLD   = "\u001B[1m";
        final String BLUE   = "\u001B[34m";
        final String YELLOW = "\u001B[33m";
        final String DIM    = "\u001B[2m";

        int total = contactLawyers + webLawyers1 + webLawyers2;
        int lineLen = 70;
        String title = "EXECUTION SUMMARY";
        int pad = (lineLen - title.length()) / 2;

        System.out.println("\n" + "═".repeat(lineLen));
        System.out.println(" ".repeat(pad) + BOLD + title + RESET);
        System.out.println("═".repeat(lineLen));
        System.out.printf(" %-28s │ %9s │  %s%n", "Phase", "Lawyers", "Details");
        System.out.println("─".repeat(lineLen));

        System.out.printf(" %-28s │ %s%9d%s │  %s%d firms processed%s%n",
                "1 · Filtered Contacts",
                BLUE + BOLD, contactLawyers, RESET,
                DIM, contactFirms, RESET);

        System.out.printf(" %-28s │ %s%9d%s │  %s—%s%n",
                "2 · Web Search",
                BLUE + BOLD, webLawyers1, RESET,
                DIM, RESET);

        System.out.printf(" %-28s │ %s%9d%s │  %s—%s%n",
                "3 · Web Search (2nd pass)",
                BLUE + BOLD, webLawyers2, RESET,
                DIM, RESET);

        System.out.println("─".repeat(lineLen));
        System.out.printf(" %s%-28s │ %9d%s │%n", BOLD, "TOTAL", total, RESET);
        System.out.println("═".repeat(lineLen));
        System.out.printf("  Total time : %s%s%s%n", YELLOW + BOLD, totalTime, RESET);
        System.out.println("═".repeat(lineLen));
    }

    public static void main(String[] args) {
        Stopwatch globalTimer = new Stopwatch();
        globalTimer.start();

        int contactLawyers = 0, contactFirms = 0, webLawyers1 = 0, webLawyers2 = 0;

        NoSleep.preventSleep(); // block sleep
        EmailDuplicateChecker.getINSTANCE().login();
        try {
            int[] phase1 = performCompleteSearch();
            contactLawyers = phase1[0];
            contactFirms   = phase1[1];
            webLawyers1    = phase1[2];

            // Pass what was already collected so the global cap is shared between both phases
            webLawyers2 = searchLawyersInWeb(contactLawyers + webLawyers1);

        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        } finally {
            // Write any remaining logs that weren't flushed
            ErrorLogger.getINSTANCE().flushAllLogs();
            // Sort Sheet.xlsx rows: D → J → E → F → C
            Sheet.getINSTANCE().sortRows();
            // Sort Reports.xlsx: lawyersRegistered ASC, then time DESC
            reports.sortRows();
            // Close the reports workbook to ensure all data is saved
            reports.closeWorkbook();
            // Close the email duplicate checker session
            EmailDuplicateChecker.getINSTANCE().close();
            NoSleep.allowSleep(); // allow sleep again when finished

            String totalTime = globalTimer.format();
            printExecutionSummary(contactLawyers, contactFirms, webLawyers1, webLawyers2, totalTime);
        }
    }
}
