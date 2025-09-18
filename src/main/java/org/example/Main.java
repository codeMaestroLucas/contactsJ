package org.example;

import lombok.SneakyThrows;
import org.example.src.CONFIG;
import org.example.src.entities.BaseSites.Site;
import org.example.src.entities.excel.ContactsAlreadyRegisteredSheet;
import org.example.src.entities.excel.Reports;
import org.example.src.utils.FirmsOMonth;
import org.example.src.utils.NoSleep;
import org.example.src.utils.myInterface.CompletedFirms;
import org.example.src.utils.myInterface.MyInterfaceUtls;

import java.util.List;
import java.util.concurrent.*;

import static org.example.src.utils.TimeCalculator.calculateTimeOfExecution;

public class Main {
    private static final Reports reports = Reports.getINSTANCE();
    private static final MyInterfaceUtls instance = MyInterfaceUtls.getINSTANCE();
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
        ExecutorService executor = Executors.newSingleThreadExecutor();

        for (Site site : sites) {
            if (Thread.currentThread().isInterrupted() || totalLawyersRegistered >= CONFIG.LAWYERS_IN_SHEET) {
                System.out.println("Stopping lawyer search.");
                break;
            }

            interfaceUtls.header(site.name);
            long initTime = System.currentTimeMillis();

            Future<Void> future = executor.submit(() -> {
                site.searchForLawyers(false);
                return null;
            });

            try {
                future.get(CONFIG.TIMEOUT_MINUTES, TimeUnit.MINUTES);

                if (site.lawyersRegistered > 0) {
                    FirmsOMonth.registerFirmMonth(site.name);
                    totalLawyersRegistered += site.lawyersRegistered;
                }

            } catch (TimeoutException e) {
                System.err.println("Timeout exceeded for site: " + site.name);
                future.cancel(true);

            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                System.err.println("Search interrupted for site: " + site.name);

            } catch (ExecutionException e) {
                System.err.println("An error occurred while searching in " + site.name + ": " + e.getMessage());
                future.cancel(true);
                if (e.getCause() != null) {
                    e.getCause().printStackTrace();
                }

            } finally {
                long endTime = System.currentTimeMillis();
                reports.createReportRow(site, instance.calculateTime(initTime, endTime));
            }

            try {
                Thread.sleep(200);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                System.err.println("Sleep interrupted between sites.");
            }
        }

        executor.shutdownNow();
        System.out.println("Completed: Lawyer search finished.");
        System.out.println("Total lawyers collected in web: " + totalLawyersRegistered);
        System.out.println();
    }

    @SneakyThrows
    private static void performCompleteSearch() {
        // Wrap methods that throw InterruptedException in lambdas
        calculateTimeOfExecution(() -> getRegisteredContacts());
        calculateTimeOfExecution(() -> {
            try {
                searchLawyersInWeb();
            } catch (Exception e) {
                throw new RuntimeException(e); // wrap checked exception
            }
        });

        System.out.println("\n\n" + "=".repeat(70));
    }

    public static void main(String[] args) {
        NoSleep.preventSleep(); // block sleep
        try {
            performCompleteSearch();
        } finally {
            NoSleep.allowSleep(); // allow sleep again when finished
        }
    }
}
