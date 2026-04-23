package org.example;

import lombok.SneakyThrows;
import org.example.src.CONFIG;
import org.example.src.entities.BaseSites.Site;
import org.example.src.entities.excel.ContactsAlreadyRegisteredSheet;
import org.example.src.entities.excel.Reports;
import org.example.src.entities.excel.Sheet;
import org.example.src.utils.ErrorLogger;
import org.example.src.utils.FirmsExhausted;
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
    private static final int DRIVER_RESTART_INTERVAL = 35;

    private static ContactsAlreadyRegisteredSheet getRegisteredContacts() {
        ContactsAlreadyRegisteredSheet contactsSheet = new ContactsAlreadyRegisteredSheet();
        contactsSheet.collectLawyersRegistered();
        return contactsSheet;
    }

    /**
     * Core execution loop: runs a list of sites with the given timeout and options.
     *
     * @param sites                 sites to run
     * @param timeoutMinutes        per-site timeout
     * @param collectFailures       if true, timed-out/errored sites go into failedFirms
     * @param enableRestartInterval if true, restarts the browser every DRIVER_RESTART_INTERVAL firms
     * @param alreadyCollected      lawyers already registered before this call (for the global cap)
     * @param headerPrefix          prefix for the console header (e.g. "[RETRY] ")
     */
    private static int runSites(
            List<Site> sites,
            int timeoutMinutes,
            boolean collectFailures,
            boolean enableRestartInterval,
            int alreadyCollected,
            String headerPrefix
    ) throws InterruptedException {
        if (collectFailures) failedFirms.clear();

        int totalLawyersRegistered = 0;
        int firmsProcessed = 0;
        ExecutorService executor = Executors.newSingleThreadExecutor();

        for (Site site : sites) {
            if (Thread.currentThread().isInterrupted()
                    || (alreadyCollected + totalLawyersRegistered) >= (CONFIG.TOTAL_LAWYERS_TO_GET + CONFIG.LAWYERS_IN_FILTER)) {
                break;
            }

            interfaceUtls.header(headerPrefix + site.name);
            Stopwatch siteTimer = new Stopwatch();
            siteTimer.start();

            Future<Void> future = executor.submit(() -> {
                site.searchForLawyers(false);
                return null;
            });

            boolean needsNewExecutor = false;

            try {
                future.get(timeoutMinutes, TimeUnit.MINUTES);

                if (site.lawyersRegistered > 0) {
                    FirmsOMonth.registerFirmMonth(site.name);
                    totalLawyersRegistered += site.lawyersRegistered;
                } else if (site.lawyersAttempted > 0) {
                    FirmsExhausted.register(site.name);
                }

            } catch (TimeoutException e) {
                future.cancel(true);
                needsNewExecutor = true;
                if (collectFailures) failedFirms.add(site);

            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();

            } catch (ExecutionException e) {
                future.cancel(true);
                if (e.getCause() != null) e.getCause().printStackTrace();
                if (collectFailures) failedFirms.add(site);

            } finally {
                siteTimer.stop();
                reports.createReportRow(site, siteTimer.format());
                firmsProcessed++;

                try {
                    MyDriver.cleanUpBetweenSites();
                } catch (Exception ignored) {}

                if (enableRestartInterval && firmsProcessed % DRIVER_RESTART_INTERVAL == 0) {
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
                        } else if (site.lawyersAttempted > 0 && site.lawyersRegistered == 0) {
                            FirmsExhausted.register(site.name);
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
     * Searches for lawyers across all active firms, stopping once the global cap is reached.
     */
    private static int searchLawyersInWeb(int alreadyCollected, boolean collectFailures) throws InterruptedException {
        List<Site> sites = CompletedFirms.constructFirms();
        return runSites(sites, CONFIG.TIMEOUT_MINUTES, collectFailures, true, alreadyCollected, "");
    }

    /**
     * Re-runs firms that failed during Phase 1 with a fresh browser and a 2-minute timeout.
     */
    private static int retryFailedFirms(int alreadyCollected) throws InterruptedException {
        if (failedFirms.isEmpty()) return 0;
        System.out.println("[Retry] Restarting browser before retry phase...");
        MyDriver.restartDriver();
        return runSites(new ArrayList<>(failedFirms), 2, false, false, alreadyCollected, "[RETRY] ");
    }

    /** Returns [contactLawyers, contactFirms, webLawyers1, retryLawyers] */
    @SneakyThrows
    private static int[] performCompleteSearch() throws InterruptedException {
        ContactsAlreadyRegisteredSheet sheet = getRegisteredContacts();

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

        NoSleep.preventSleep();
        EmailDuplicateChecker.getINSTANCE().login();
        try {
            int[] phase1 = performCompleteSearch();
            contactLawyers = phase1[0];
            contactFirms   = phase1[1];
            webLawyers1    = phase1[2];
            retryLawyers   = phase1[3];

            webLawyers2 = searchLawyersInWeb(contactLawyers + webLawyers1 + retryLawyers, false);

        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        } finally {
            ErrorLogger.getINSTANCE().flushAllLogs();
            Sheet.getINSTANCE().sortRows();
            Sheet.getINSTANCE().closeWorkbook();
            reports.sortRows();
            reports.closeWorkbook();
            EmailDuplicateChecker.getINSTANCE().close();
            NoSleep.allowSleep();

            String totalTime = globalTimer.format();
            printExecutionSummary(contactLawyers, contactFirms, webLawyers1, retryLawyers, webLawyers2, totalTime);
        }
    }
}
