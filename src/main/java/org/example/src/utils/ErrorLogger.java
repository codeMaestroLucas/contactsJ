package org.example.src.utils;

import org.example.exceptions.ValidationExceptions;
import org.openqa.selenium.WebDriverException;

import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeoutException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Singleton class to handle error logging with grouped output per firm.
 * Collects errors during processing and outputs them in a clean format at the end of each firm.
 */
public class ErrorLogger {
    private static ErrorLogger INSTANCE;
    
    // Map<FirmName, Map<ErrorDescription, Count>>
    private final Map<String, Map<String, Integer>> firmErrors = new ConcurrentHashMap<>();
    
    private static final String LOG_FILE_PATH = "log.txt";
    private static final String SEPARATOR = "=".repeat(100);

    /**
     * Private constructor to prevent instantiation.
     * Initializes the log file for a new session.
     */
    private ErrorLogger() {
        initializeLogFile();
    }

    public static synchronized ErrorLogger getINSTANCE() {
        if (INSTANCE == null) {
            INSTANCE = new ErrorLogger();
        }
        return INSTANCE;
    }

    /**
     * Initializes the log file, overwriting any existing content.
     */
    private void initializeLogFile() {
        try (FileWriter fw = new FileWriter(LOG_FILE_PATH, false);
             PrintWriter pw = new PrintWriter(fw)) {
            pw.printf("LOG SESSION STARTED - %s%n%n", new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date()));
        } catch (IOException e) {
            System.err.println("FATAL: Could not initialize log file.");
            e.printStackTrace(System.err);
        }
    }

    /**
     * Logs an error for a specific firm. Errors are collected and will be written 
     * when flushErrorsForFirm() is called.
     * 
     * @param firmName The name of the firm where the error occurred
     * @param e The exception to log
     * @param showLogs If true, prints to console instead of collecting for file
     */
    public void log(String firmName, Exception e, boolean showLogs) {
        if (showLogs) {
            System.err.printf("Error while processing firm %s: %s%n", firmName, e.getMessage());
            return;
        }

        // Skip validation exceptions
        if (e instanceof ValidationExceptions) {
            return;
        }

        String errorDescription = getErrorDescription(e);
        
        // Get or create the error map for this firm
        firmErrors.putIfAbsent(firmName, new HashMap<>());
        Map<String, Integer> errors = firmErrors.get(firmName);
        
        // Increment the count for this error type
        errors.put(errorDescription, errors.getOrDefault(errorDescription, 0) + 1);
    }

    /**
     * Overloaded method that ignores the context parameter for backwards compatibility.
     */
    public void log(String firmName, Exception e, boolean showLogs, String context) {
        log(firmName, e, showLogs);
    }

    /**
     * Writes all collected errors for a firm to the log file in the specified format
     * and clears the errors for that firm from memory.
     * 
     * @param firmName The name of the firm to flush errors for
     */
    public void flushErrorsForFirm(String firmName) {
        Map<String, Integer> errors = firmErrors.get(firmName);
        
        // If no errors for this firm, don't write anything
        if (errors == null || errors.isEmpty()) {
            return;
        }

        try (FileWriter fw = new FileWriter(LOG_FILE_PATH, true);
             PrintWriter pw = new PrintWriter(fw)) {
            
            // Write the formatted output
            pw.println(SEPARATOR);
            pw.println(firmName);
            pw.println();
            pw.println("Errors:");
            
            // Write each error with its count
            for (Map.Entry<String, Integer> entry : errors.entrySet()) {
                String errorDescription = entry.getKey();
                Integer count = entry.getValue();
                pw.printf("    - %s %dx%n", errorDescription, count);
            }
            
            pw.println(SEPARATOR);
            pw.println();

        } catch (IOException ioException) {
            System.err.println("FATAL: Could not write to log file.");
            ioException.printStackTrace(System.err);
        }

        // Clear the errors for this firm
        firmErrors.remove(firmName);
    }

    /**
     * Flushes errors for all firms that still have pending errors.
     * Useful for cleanup at the end of the session.
     */
    public void flushAllPendingErrors() {
        for (String firmName : firmErrors.keySet()) {
            flushErrorsForFirm(firmName);
        }
    }

    /**
     * Generates a short, readable error description from an exception.
     * 
     * @param e The exception
     * @return A short description of the error
     */
    private String getErrorDescription(Exception e) {
        if (e instanceof WebDriverException) {
            String message = e.getMessage();
            if (message != null) {
                // Try to extract specific WebDriver error patterns
                Pattern pattern = Pattern.compile("(net::[A-Z_]+)|(no such window)|(target window already closed)|(element not found)|(timeout)");
                Matcher matcher = pattern.matcher(message.toLowerCase());
                if (matcher.find()) {
                    String match = matcher.group(0);
                    return "WebDriver Error: " + formatErrorText(match);
                }
                
                // Extract first meaningful part of the message
                String[] parts = message.split("\\n")[0].split("\\.");
                if (parts.length > 0) {
                    String firstPart = parts[0].trim();
                    if (firstPart.length() > 50) {
                        firstPart = firstPart.substring(0, 47) + "...";
                    }
                    return "WebDriver Error: " + firstPart;
                }
            }
            return "WebDriver Error: Unknown issue";
        }
        
        if (e instanceof TimeoutException) {
            return "Timeout: Element or page took too long to load";
        }
        
        if (e instanceof RuntimeException) {
            String message = e.getMessage();
            if (message != null && !message.isEmpty()) {
                if (message.length() > 60) {
                    message = message.substring(0, 57) + "...";
                }
                return "Runtime Error: " + message;
            }
            return "Runtime Error: " + e.getClass().getSimpleName();
        }
        
        // For other exceptions, use class name and short message
        String className = e.getClass().getSimpleName();
        String message = e.getMessage();
        
        if (message != null && !message.isEmpty()) {
            if (message.length() > 40) {
                message = message.substring(0, 37) + "...";
            }
            return className + ": " + message;
        }
        
        return className;
    }

    /**
     * Formats error text to be more readable.
     */
    private String formatErrorText(String text) {
        return text.replace("_", " ").toLowerCase().trim();
    }

    /**
     * Gets the current count of pending firms with errors.
     * Useful for debugging or monitoring.
     */
    public int getPendingFirmsCount() {
        return firmErrors.size();
    }

    /**
     * Checks if a specific firm has any pending errors.
     */
    public boolean hasPendingErrors(String firmName) {
        Map<String, Integer> errors = firmErrors.get(firmName);
        return errors != null && !errors.isEmpty();
    }
}
