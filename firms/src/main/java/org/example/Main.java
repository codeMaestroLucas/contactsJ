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

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.*;


public class Main {
    private static final Reports reports = Reports.getINSTANCE();
    private static final MyInterfaceUtls interfaceUtls = CompletedFirms.interfaceUtls;
    private static final List<Site> failedFirms = new ArrayList<>();

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
    private static int searchLawyersInWeb(int alreadyCollected, boolean collectFailures) throws InterruptedException {
        if (collectFailures) failedFirms.clear();

        int totalLawyersRegistered = 0;
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
                future.cancel(true);
                needsNewExecutor = true;
                if (collectFailures) failedFirms.add(site);

            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();

            } catch (ExecutionException e) {
                future.cancel(true);
                if (e.getCause() != null) {
                    e.getCause().printStackTrace();
                }
                if (collectFailures) failedFirms.add(site);

            } finally {
                siteTimer.stop();
                reports.createReportRow(site, siteTimer.format());
                firmsProcessed++;

                // Clean up browser state between sites (clear cookies, close extra tabs)
                try {
                    MyDriver.cleanUpBetweenSites();
                } catch (Exception ignored) {}

                // Preventive restart every N firms to avoid browser memory/state degradation.
                // Re-execute the same firm on the fresh browser with a 2-minute timeout,
                // since it ran on an accumulated-state browser and may have been affected.
                if (firmsProcessed % DRIVER_RESTART_INTERVAL == 0) {
                    MyDriver.restartDriver();

                    int lawyersBeforeRetry = site.lawyersRegistered;
                    Stopwatch retryTimer = new Stopwatch();
                    retryTimer.start();
                    Future<Void> retryFuture = executor.submit(() -> {
                        site.searchForLawyers(false);
                        return null;
                    });
                    try {
                        retryFuture.get(2, TimeUnit.MINUTES);
                        int newLawyers = site.lawyersRegistered - lawyersBeforeRetry;
                        if (newLawyers > 0) {
                            FirmsOMonth.registerFirmMonth(site.name);
                            totalLawyersRegistered += newLawyers;
                        }
                    } catch (TimeoutException | ExecutionException retryEx) {
                        retryFuture.cancel(true);
                        needsNewExecutor = true;
                    } catch (InterruptedException retryEx) {
                        Thread.currentThread().interrupt();
                    } finally {
                        retryTimer.stop();
                        reports.createReportRow(site, retryTimer.format());
                    }
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

    /**
     * Re-runs firms that failed (timeout or error) during Phase 1 with a fresh browser
     * and a reduced 2-minute timeout. Failures here are not retried again.
     *
     * @param alreadyCollected total lawyers already registered, for shared global cap
     * @return number of lawyers registered during the retry
     */
    private static int retryFailedFirms(int alreadyCollected) throws InterruptedException {
        if (failedFirms.isEmpty()) return 0;

        System.out.println("[Retry] Restarting browser before retry phase...");
        MyDriver.restartDriver();

        int totalLawyersRegistered = 0;
        final int RETRY_TIMEOUT_MINUTES = 2;
        ExecutorService executor = Executors.newSingleThreadExecutor();

        for (Site site : failedFirms) {
            if (Thread.currentThread().isInterrupted()
                    || (alreadyCollected + totalLawyersRegistered) >= (CONFIG.TOTAL_LAWYERS_TO_GET + CONFIG.LAWYERS_IN_FILTER)) {
                break;
            }

            interfaceUtls.header("[RETRY] " + site.name);
            Stopwatch siteTimer = new Stopwatch();
            siteTimer.start();

            Future<Void> future = executor.submit(() -> {
                site.searchForLawyers(false);
                return null;
            });

            boolean needsNewExecutor = false;

            try {
                future.get(RETRY_TIMEOUT_MINUTES, TimeUnit.MINUTES);

                if (site.lawyersRegistered > 0) {
                    FirmsOMonth.registerFirmMonth(site.name);
                    totalLawyersRegistered += site.lawyersRegistered;
                }

            } catch (TimeoutException e) {
                System.err.println("[Retry] Timeout exceeded for site: " + site.name);
                future.cancel(true);
                needsNewExecutor = true;

            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                System.err.println("[Retry] Search interrupted for site: " + site.name);

            } catch (ExecutionException e) {
                System.err.println("[Retry] Error in " + site.name + ": " + e.getMessage());
                future.cancel(true);
                if (e.getCause() != null) {
                    e.getCause().printStackTrace();
                }

            } finally {
                siteTimer.stop();
                reports.createReportRow(site, siteTimer.format());

                try {
                    MyDriver.cleanUpBetweenSites();
                } catch (Exception ignored) {}

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

    /** Returns [contactLawyers, contactFirms, webLawyers1, retryLawyers] */
    @SneakyThrows
    private static int[] performCompleteSearch() throws InterruptedException {
        ContactsAlreadyRegisteredSheet sheet = getRegisteredContacts();

        // Compute BEFORE Phase 2 so the cap is shared with Phase 1's results
        int contactLawyers = sheet.getTotalLawyers();
        int contactFirms   = sheet.getLawFirmsCollectedCount();

        int web1 = searchLawyersInWeb(contactLawyers, true);
        int retryLawyers = retryFailedFirms(contactLawyers + web1);

        return new int[]{ contactLawyers, contactFirms, web1, retryLawyers };
    }

    private static void printExecutionSummary(
            int contactLawyers, int contactFirms,
            int webLawyers1, int retryLawyers, int webLawyers2,
            String totalTime) {

        final String RESET  = "\u001B[0m";
        final String BOLD   = "\u001B[1m";
        final String BLUE   = "\u001B[34m";
        final String YELLOW = "\u001B[33m";
        final String DIM    = "\u001B[2m";

        int total = contactLawyers + webLawyers1 + retryLawyers + webLawyers2;
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

        System.out.printf(" %-28s │ %s%9d%s │  %s%d firms%s%n",
                "3 · Retry (failed firms)",
                BLUE + BOLD, retryLawyers, RESET,
                DIM, failedFirms.size(), RESET);

        System.out.printf(" %-28s │ %s%9d%s │  %s—%s%n",
                "4 · Web Search (2nd pass)",
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

        int contactLawyers = 0, contactFirms = 0, webLawyers1 = 0, retryLawyers = 0, webLawyers2 = 0;

        NoSleep.preventSleep(); // block sleep
        EmailDuplicateChecker.getINSTANCE().login();
        try {
            int[] phase1 = performCompleteSearch();
            contactLawyers = phase1[0];
            contactFirms   = phase1[1];
            webLawyers1    = phase1[2];
            retryLawyers   = phase1[3];

            // Pass what was already collected so the global cap is shared between both phases
            webLawyers2 = searchLawyersInWeb(contactLawyers + webLawyers1 + retryLawyers, false);

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
            printExecutionSummary(contactLawyers, contactFirms, webLawyers1, retryLawyers, webLawyers2, totalTime);
        }
    }
}
