package org.example;

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
        // Executor para gerenciar a thread e o timeout
        ExecutorService executor = Executors.newSingleThreadExecutor();

        for (Site site : sites) {
            if (Thread.currentThread().isInterrupted() || totalLawyersRegistered >= CONFIG.LAWYERS_IN_SHEET) {
                System.out.println("Stopping lawyer search.");
                break;
            }

            interfaceUtls.header(site.name);

            long initTime = System.currentTimeMillis();

            // A tarefa que será executada com timeout
            Future<Void> future = executor.submit(() -> {
                site.searchForLawyers(false);
                return null; // Callable precisa retornar um valor
            });

            try {
                // Define o timeout. Aqui, estou usando 10 minutos.
                future.get(1, TimeUnit.MINUTES);

                if (site.lawyersRegistered > 0) {
                    FirmsOMonth.registerFirmMonth(site.name);
                    totalLawyersRegistered += site.lawyersRegistered;
                }

            } catch (TimeoutException e) {
                System.err.println("Timeout exceeded for site: " + site.name);
                future.cancel(true); // Tenta interromper a thread em execução

            } catch (InterruptedException | ExecutionException e) {
                System.err.println("An error occurred while searching in " + site.name + ": " + e.getMessage());
                future.cancel(true); // Tenta interromper a thread em execução
                // Se a causa for uma exceção da automação, imprima o stack trace dela
                if (e.getCause() != null) {
                    e.getCause().printStackTrace();
                }

            } finally {
                long endTime = System.currentTimeMillis();
                reports.createReportRow(site, instance.calculateTime(initTime, endTime));
            }
        }

        executor.shutdownNow(); // Encerra o executor service
        System.out.println("Completed: Lawyer search finished.");
        System.out.println("Total lawyers collected in web: " + totalLawyersRegistered);
        System.out.println();
    }


    private static void performCompleteSearch() {
        calculateTimeOfExecution(Main::getRegisteredContacts);
        calculateTimeOfExecution(Main::searchLawyersInWeb);

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
