package org.example.src.utils;

import org.example.src.entities.BaseSites.Site;
import org.example.src.entities.excel.Reports;

import java.util.concurrent.*;

import static org.example.src.CONFIG.TIMEOUT_MINUTES;
import static org.example.src.utils.myInterface.CompletedFirms.interfaceUtls;

public class TimeCalculator {
    private static final Reports reports = Reports.getINSTANCE();

    /**
     * Calculates the execution time of a given block of code (no timeout).
     */
    public static void calculateTimeOfExecution(Runnable taskToRun) {
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
    public static void calculateTimeOfExecutionWithTimeout(Runnable taskToRun, Site site) {
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
        }
    }
}
