package org.example.src.utils;

import org.example.exceptions.LawyerExceptions;
import org.example.exceptions.ValidationExceptions;

import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * Singleton class for tracking and logging extraction errors.
 * Designed to help identify which scraper classes need maintenance.
 *
 * Features:
 * - Tracks errors by firm and error type (linkException, nameException, etc.)
 * - Tolerance of 10 errors per firm (firms with ≤10 errors are considered normal and omitted from the log)
 * - Generates a summary with most common errors
 * - Outputs clean, actionable logs for maintenance
 */
public class ErrorLogger {
    private static ErrorLogger INSTANCE;

    private static final String LOG_FILE_PATH = "log.txt";
    private static final int ERROR_TOLERANCE_PER_FIRM = 10;

    // Error tracking: Firm -> (ErrorType -> Count)
    private final Map<String, Map<String, Integer>> errorsByFirm = new LinkedHashMap<>();

    // Track lawyers registered per firm
    private final Map<String, Integer> lawyersRegisteredByFirm = new LinkedHashMap<>();

    // Track all firms that were processed
    private final Set<String> processedFirms = new LinkedHashSet<>();

    // Global error counts for summary
    private final Map<String, Integer> globalErrorCounts = new HashMap<>();

    // Session start time
    private final String sessionStartTime;

    private ErrorLogger() {
        this.sessionStartTime = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date());
    }

    public static synchronized ErrorLogger getINSTANCE() {
        if (INSTANCE == null) {
            INSTANCE = new ErrorLogger();
        }
        return INSTANCE;
    }

    /**
     * Resets the logger for a new session.
     * Call this at the start of a new execution if needed.
     */
    public void reset() {
        errorsByFirm.clear();
        lawyersRegisteredByFirm.clear();
        processedFirms.clear();
        globalErrorCounts.clear();
    }

    /**
     * Marks a firm as processed (started scraping).
     * Call this at the beginning of processing each firm.
     */
    public void startFirm(String firmName) {
        processedFirms.add(firmName);
        lawyersRegisteredByFirm.putIfAbsent(firmName, 0);
    }

    /**
     * Records that a lawyer was successfully registered for a firm.
     */
    public void recordLawyerRegistered(String firmName) {
        lawyersRegisteredByFirm.merge(firmName, 1, Integer::sum);
    }

    /**
     * Logs an extraction error.
     * Automatically categorizes the error by type.
     *
     * @param firmName The firm where the error occurred
     * @param e        The exception
     * @param showLogs If true, also prints to console
     */
    public void log(String firmName, Exception e, boolean showLogs) {
        // Skip validation exceptions - they're not extraction errors
        if (e instanceof ValidationExceptions) return;

        // Silently ignore practice area errors
        if (e instanceof LawyerExceptions && e.getMessage() != null && e.getMessage().contains("PRACTICE")) return;

        String errorType = categorizeError(e);

        // Track by firm
        errorsByFirm.putIfAbsent(firmName, new LinkedHashMap<>());
        errorsByFirm.get(firmName).merge(errorType, 1, Integer::sum);

        // Track globally
        globalErrorCounts.merge(errorType, 1, Integer::sum);

        if (showLogs) {
            System.err.printf("[%s] %s: %s%n", firmName, errorType, e.getMessage());
        }
    }

    /**
     * Logs an error with additional context.
     * Note: Context is used for console output only, not for aggregation.
     * This ensures errors are grouped by type only (e.g., "getSocialsError" not "getSocialsError (Error reading 36th lawyer at page 1)")
     */
    public void log(String firmName, Exception e, boolean showLogs, String context) {
        if (e instanceof ValidationExceptions) return;

        // Silently ignore practice area errors
        if (e instanceof LawyerExceptions && e.getMessage() != null && e.getMessage().contains("PRACTICE")) return;

        String errorType = categorizeError(e);

        // Only use errorType for aggregation (no context)
        errorsByFirm.putIfAbsent(firmName, new LinkedHashMap<>());
        errorsByFirm.get(firmName).merge(errorType, 1, Integer::sum);
        globalErrorCounts.merge(errorType, 1, Integer::sum);

        if (showLogs) {
            // Context is only shown in console output, not stored for aggregation
            String displayType = (context != null && !context.isEmpty())
                    ? errorType + " (" + context + ")"
                    : errorType;
            System.err.printf("[%s] %s: %s%n", firmName, displayType, e.getMessage());
        }
    }

    /**
     * Categorizes an exception into a simple, actionable error type.
     */
    private String categorizeError(Exception e) {
        if (e instanceof LawyerExceptions) {
            String msg = e.getMessage();
            if (msg == null) return "lawyerException";

            if (msg.contains("LINK")) return "linkException";
            if (msg.contains("NAME")) return "nameException";
            if (msg.contains("EMAIL")) return "emailException";
            if (msg.contains("PHONE")) return "phoneException";
            if (msg.contains("ROLE")) return "roleException";
            if (msg.contains("COUNTRY")) return "countryException";
            if (msg.contains("PRACTICE")) return "practiceAreaException";

            return "lawyerException";
        }

        // Extract method name from stack trace for other exceptions
        String methodName = extractMethodFromStack(e);
        if (methodName != null) {
            return methodName + "Error";
        }

        return e.getClass().getSimpleName();
    }

    /**
     * Extracts the relevant method name from the stack trace.
     */
    private String extractMethodFromStack(Exception e) {
        for (StackTraceElement element : e.getStackTrace()) {
            String className = element.getClassName();
            String method = element.getMethodName();

            // Look for site class methods
            if (className.contains(".sites.") && !method.equals("searchForLawyers")) {
                return method;
            }
        }
        return null;
    }

    /**
     * Writes all accumulated logs to the log file.
     * Call this at the end of the session.
     */
    public void flushAllLogs() {
        try (PrintWriter pw = new PrintWriter(new FileWriter(LOG_FILE_PATH, false))) {
            writeHeader(pw);
            writeFirmErrors(pw);
            writeErrorSummary(pw);
            writeFooter(pw);

        } catch (IOException e) {
            System.err.println("ERROR: Could not write to log file: " + e.getMessage());
        }
    }

    private void writeHeader(PrintWriter pw) {
        pw.println("=====================================================================================================");
        pw.println("                                    ERROR LOG - " + sessionStartTime);
        pw.println("=====================================================================================================");
        pw.println();
    }

    private void writeFirmErrors(PrintWriter pw) {
        if (errorsByFirm.isEmpty()) {
            pw.println("[OK] No extraction errors recorded!");
            pw.println();
            return;
        }

        // Filter out firms within tolerance (≤ ERROR_TOLERANCE_PER_FIRM errors)
        List<Map.Entry<String, Map<String, Integer>>> firmsAboveTolerance = new ArrayList<>();
        int firmsWithinTolerance = 0;

        for (Map.Entry<String, Map<String, Integer>> entry : errorsByFirm.entrySet()) {
            int totalErrors = entry.getValue().values().stream().mapToInt(Integer::intValue).sum();
            if (totalErrors > ERROR_TOLERANCE_PER_FIRM) {
                firmsAboveTolerance.add(entry);
            } else {
                firmsWithinTolerance++;
            }
        }

        if (firmsAboveTolerance.isEmpty()) {
            pw.printf("[OK] All firms are within error tolerance (≤%d errors each). %d firm(s) had minor errors.%n",
                    ERROR_TOLERANCE_PER_FIRM, firmsWithinTolerance);
            pw.println();
            return;
        }

        pw.println("----------------------------------------------------------------------------------------------------");
        pw.println("                                      ERRORS BY FIRM");
        pw.println("----------------------------------------------------------------------------------------------------");
        pw.println();

        if (firmsWithinTolerance > 0) {
            pw.printf("(%d firm(s) omitted — within tolerance)%n%n", firmsWithinTolerance);
        }

        // Sort firms by total error count (descending)
        firmsAboveTolerance.sort((a, b) -> {
            int totalA = a.getValue().values().stream().mapToInt(Integer::intValue).sum();
            int totalB = b.getValue().values().stream().mapToInt(Integer::intValue).sum();
            return Integer.compare(totalB, totalA);
        });

        for (Map.Entry<String, Map<String, Integer>> entry : firmsAboveTolerance) {
            String firmName = entry.getKey();
            Map<String, Integer> errors = entry.getValue();
            int totalErrors = errors.values().stream().mapToInt(Integer::intValue).sum();
            int lawyersRegistered = lawyersRegisteredByFirm.getOrDefault(firmName, 0);

            pw.printf("Class %s [%d lawyers registered, %d errors]%n", firmName, lawyersRegistered, totalErrors);

            // Sort errors by count (descending)
            List<Map.Entry<String, Integer>> sortedErrors = new ArrayList<>(errors.entrySet());
            sortedErrors.sort((a, b) -> Integer.compare(b.getValue(), a.getValue()));

            for (Map.Entry<String, Integer> error : sortedErrors) {
                pw.printf("   - %-30s occurred %d times%n", error.getKey(), error.getValue());
            }
            pw.println();
        }
    }

    private void writeErrorSummary(PrintWriter pw) {
        if (globalErrorCounts.isEmpty()) {
            return;
        }

        pw.println("----------------------------------------------------------------------------------------------------");
        pw.println("                                      ERROR SUMMARY");
        pw.println("----------------------------------------------------------------------------------------------------");
        pw.println();

        int totalErrors = globalErrorCounts.values().stream().mapToInt(Integer::intValue).sum();
        pw.printf("Total errors: %d%n%n", totalErrors);

        // Sort by count (descending)
        List<Map.Entry<String, Integer>> sorted = new ArrayList<>(globalErrorCounts.entrySet());
        sorted.sort((a, b) -> Integer.compare(b.getValue(), a.getValue()));

        pw.println("Most common errors:");
        for (Map.Entry<String, Integer> entry : sorted) {
            double percentage = (entry.getValue() * 100.0) / totalErrors;
            pw.printf("   %-35s %5d  (%5.1f%%)%n", entry.getKey(), entry.getValue(), percentage);
        }
        pw.println();
    }

    private void writeFooter(PrintWriter pw) {
        long firmsAboveTolerance = errorsByFirm.entrySet().stream()
                .filter(e -> e.getValue().values().stream().mapToInt(Integer::intValue).sum() > ERROR_TOLERANCE_PER_FIRM)
                .count();

        pw.println("=====================================================================================================");
        pw.printf("Firms processed: %d | Firms above tolerance (>%d errors): %d%n",
                processedFirms.size(), ERROR_TOLERANCE_PER_FIRM, firmsAboveTolerance);
        pw.println("=====================================================================================================");
    }

    /**
     * For backward compatibility - flushes logs for a specific firm.
     * In this new implementation, all logs are flushed together at the end.
     */
    public void flushFirmLogs(String firmName) {
        // No-op in new implementation - logs are flushed all together at the end
    }
}
