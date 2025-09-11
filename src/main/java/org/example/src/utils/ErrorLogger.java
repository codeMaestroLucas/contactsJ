package org.example.src.utils;

import org.example.exceptions.ValidationExceptions;
import org.openqa.selenium.WebDriverException;

import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.TimeoutException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Singleton class to handle error logging.
 * It prevents duplicate error logging for the same firm within a session
 * and directs output to a file when not in debug mode.
 */
public class ErrorLogger {

    private static ErrorLogger INSTANCE;
    private final Map<String, Set<String>> loggedErrorsByFirm = new HashMap<>();
    private static final String LOG_FILE_PATH = "log.txt";

    /**
     * Private constructor to prevent instantiation.
     * It clears the log file at the beginning of a new session by opening it in overwrite mode.
     */
    private ErrorLogger() {
        // Initialize the log file for the new session. This will overwrite the old file.
        try (FileWriter fw = new FileWriter(LOG_FILE_PATH, false); // 'false' for overwrite mode
             PrintWriter pw = new PrintWriter(fw)) {
            pw.println("===== LOG SESSION STARTED =====");
            pw.println("====================================================\n");
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
     * Logs an error, either to the console or to a file, avoiding duplicates for the same firm.
     * @param firmName The name of the firm where the error occurred.
     * @param e The exception to log.
     * @param showLogs If true, prints the full stack trace to the console. If false, logs a summary to the file.
     */
    public void log(String firmName, Exception e, boolean showLogs) {
        if (showLogs) {
            System.err.printf("Error while processing firm %s:%n", firmName);
            e.printStackTrace(System.err);
            return;
        }

        // Do not log validation exceptions to the file.
        if (e instanceof ValidationExceptions) return;

        String errorIdentifier = getExceptionIdentifier(e);

        // Initialize the set for the firm if it doesn't exist
        loggedErrorsByFirm.putIfAbsent(firmName, new HashSet<>());

        // If this specific error type has already been logged for this firm, do nothing.
        if (loggedErrorsByFirm.get(firmName).contains(errorIdentifier)) return;

        // Add the identifier to the set to prevent future duplicate logs for this session
        loggedErrorsByFirm.get(firmName).add(errorIdentifier);

        // Write the unique error to the log file in append mode.
        try (FileWriter fw = new FileWriter(LOG_FILE_PATH, true);
             PrintWriter pw = new PrintWriter(fw)) {
            pw.println("------------------------------------------------------------");
            pw.printf("Firm: %s%n", firmName);
            pw.printf("Error Type: %s%n", e.getClass().getName());
            pw.printf("Message: %s%n", e.getMessage());
            pw.println("Stack trace:");
            e.printStackTrace(pw);
            pw.println("------------------------------------------------------------\n");
        } catch (IOException ioException) {
            System.err.println("FATAL: Could not write to log file.");
            ioException.printStackTrace(System.err);
        }
    }

    /**
     * Generates a unique identifier for an exception to prevent duplicate logging.
     * For WebDriverExceptions, it tries to extract a specific error code.
     * @param e The exception.
     * @return A string identifier for the error type.
     */
    private String getExceptionIdentifier(Exception e) {
        if (e instanceof WebDriverException) {
            String message = e.getMessage();
            if (message != null) {
                // Extracts specific, recurring error patterns to treat them as one type.
                Pattern pattern = Pattern.compile("(net::[A-Z_]+)|(no such window)|(target window already closed)");
                Matcher matcher = pattern.matcher(message);
                if (matcher.find()) {
                    return "WebDriverException: " + matcher.group(0).trim();
                }
            }
        }
        if (e instanceof TimeoutException) {
            return "TimeoutException";
        }
        // For other exceptions, use the class name as the identifier.
        return e.getClass().getSimpleName();
    }
}

