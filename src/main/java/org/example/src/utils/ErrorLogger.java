package org.example.src.utils;

import org.example.exceptions.LawyerExceptions;
import org.example.exceptions.ValidationExceptions;
import org.openqa.selenium.WebDriverException;

import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.concurrent.TimeoutException;

/**
 * Singleton class to handle error logging.
 * Accumulates errors by firm and writes them in a clean, grouped format.
 */
public class ErrorLogger {
    private static ErrorLogger INSTANCE;
    private final Map<String, Map<String, Integer>> errorCountsByFirm = new HashMap<>();
    private static final String LOG_FILE_PATH = "log.txt";
    private static final String SEPARATOR = "=".repeat(100);

    /**
     * Private constructor to prevent instantiation.
     * Initializes the log file at the beginning of a new session.
     */
    private ErrorLogger() {
        try (FileWriter fw = new FileWriter(LOG_FILE_PATH, false);
             PrintWriter pw = new PrintWriter(fw)) {
            pw.printf("LOG SESSION STARTED - %s%n%n", new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date()));
        } catch (IOException e) {
            System.err.println("FATAL: Could not initialize log file.");
            e.printStackTrace(System.err);
        }
    }

    public static synchronized ErrorLogger getINSTANCE() {
        if (INSTANCE == null) {
            INSTANCE = new ErrorLogger();
        }
        return INSTANCE;
    }

    /**
     * Logs an error by accumulating it in memory.
     * @param firmName The name of the firm where the error occurred.
     * @param e The exception to log.
     * @param showLogs If true, prints to the console. If false, accumulates for file logging.
     */
    public void log(String firmName, Exception e, boolean showLogs) {
        if (showLogs) {
            System.err.printf("Error while processing firm %s:%n", firmName);
            e.printStackTrace(System.err);
            return;
        }

        // Do not log validation exceptions to the file
        if (e instanceof ValidationExceptions) return;

        String errorDescription = getErrorDescription(e, null);
        accumulateError(firmName, errorDescription);
    }

    /**
     * Overloaded method to log errors with additional context information.
     * @param firmName The name of the firm where the error occurred.
     * @param e The exception to log.
     * @param showLogs If true, prints to the console. If false, accumulates for file logging.
     * @param context Additional context information (e.g., "Error accessing page 3")
     */
    public void log(String firmName, Exception e, boolean showLogs, String context) {
        if (showLogs) {
            System.err.printf("Error while processing firm %s: %s%n", firmName, context);
            e.printStackTrace(System.err);
            return;
        }

        // Do not log validation exceptions to the file
        if (e instanceof ValidationExceptions) return;

        String errorDescription = getErrorDescription(e, context);
        accumulateError(firmName, errorDescription);
    }

    /**
     * Accumulates an error for a specific firm.
     * @param firmName The firm name.
     * @param errorDescription The error description.
     */
    private void accumulateError(String firmName, String errorDescription) {
        errorCountsByFirm.putIfAbsent(firmName, new HashMap<>());
        Map<String, Integer> firmErrors = errorCountsByFirm.get(firmName);
        firmErrors.put(errorDescription, firmErrors.getOrDefault(errorDescription, 0) + 1);
    }

    /**
     * Writes accumulated logs for a specific firm to the log file.
     * @param firmName The name of the firm to write logs for.
     */
    public void flushFirmLogs(String firmName) {
        Map<String, Integer> firmErrors = errorCountsByFirm.get(firmName);
        
        if (firmErrors == null || firmErrors.isEmpty()) {
            return;
        }

        try (FileWriter fw = new FileWriter(LOG_FILE_PATH, true);
             PrintWriter pw = new PrintWriter(fw)) {

            pw.println(SEPARATOR);
            pw.println(firmName);
            pw.println();
            pw.println("Errors:");

            // Sort errors alphabetically for consistent output
            List<Map.Entry<String, Integer>> sortedErrors = new ArrayList<>(firmErrors.entrySet());
            sortedErrors.sort(Map.Entry.comparingByKey());

            for (Map.Entry<String, Integer> entry : sortedErrors) {
                pw.printf("    - %s %dx%n", entry.getKey(), entry.getValue());
            }

            pw.println(SEPARATOR);
            pw.println();

            // Clear the errors for this firm after writing
            errorCountsByFirm.remove(firmName);

        } catch (IOException e) {
            System.err.println("FATAL: Could not write to log file.");
            e.printStackTrace(System.err);
        }
    }

    /**
     * Writes all accumulated logs to the file and clears the buffer.
     * Useful for flushing all pending logs at the end of the session.
     */
    public void flushAllLogs() {
        List<String> firmNames = new ArrayList<>(errorCountsByFirm.keySet());
        firmNames.sort(String::compareTo);
        
        for (String firmName : firmNames) {
            flushFirmLogs(firmName);
        }
    }

    /**
     * Generates a clean, human-readable description of an error.
     * @param e The exception.
     * @param context Optional context information.
     * @return A formatted error description string.
     */
    private String getErrorDescription(Exception e, String context) {
        StringBuilder description = new StringBuilder();

        // Determine error type
        if (e instanceof LawyerExceptions) {
            // LawyerExceptions already have clean messages like "Invalid NAME: John"
            description.append("Lawyer Error: ").append(e.getMessage());
        } else if (e instanceof WebDriverException) {
            description.append("WebDriver Error: ");
            String message = e.getMessage();
            if (message != null) {
                // Extract first meaningful line or first 60 characters
                String[] lines = message.split("\n");
                String firstLine = lines[0].trim();
                if (firstLine.length() > 60) {
                    firstLine = firstLine.substring(0, 60);
                }
                description.append(firstLine);
            } else {
                description.append("Unknown error");
            }
        } else if (e instanceof TimeoutException) {
            description.append("Timeout Error");
            if (e.getMessage() != null) {
                description.append(": ").append(e.getMessage());
            }
        } else if (e instanceof RuntimeException) {
            description.append("Runtime Error: ");
            String message = e.getMessage();
            if (message != null && !message.isEmpty()) {
                // Use first 60 characters of message
                description.append(message.length() > 60 ? message.substring(0, 60) : message);
            } else {
                description.append(e.getClass().getSimpleName());
            }
        } else {
            // Generic exception handling
            description.append(e.getClass().getSimpleName());
            if (e.getMessage() != null) {
                description.append(": ").append(
                    e.getMessage().length() > 60 ? e.getMessage().substring(0, 60) : e.getMessage()
                );
            }
        }

        // Add context if provided
        if (context != null && !context.isEmpty()) {
            description.append(" (").append(context).append(")");
        }

        return description.toString();
    }
}
