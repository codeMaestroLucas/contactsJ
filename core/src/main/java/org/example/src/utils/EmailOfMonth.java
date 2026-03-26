package org.example.src.utils;

import java.io.*;
import java.nio.file.*;
import java.time.LocalDate;

public class EmailOfMonth {
    private static final String currentFormattedDate = String.format(
            "%d/%d\n",LocalDate.now().getMonthValue(), LocalDate.now().getYear()
    );

    /**
     * Formats the string line to be inserted in the file.
     */
    private static String generateStringToFile(String email) {
        int emptySpaces = 70 - email.length();
        return email + " ".repeat(emptySpaces) + currentFormattedDate;
    }


    /**
     * Registers the given email in the file with the current month and year.
     */
    public static void registerEmailOfMonth(String email, String emailFilePath) {
        String line = generateStringToFile(email);

        try (BufferedWriter writer = new BufferedWriter(new FileWriter(emailFilePath, true))) {
            writer.write(line);
        } catch (IOException e) {
            System.err.println("Error writing to file: " + e.getMessage());
        }
    }


    /**
     * Checks if the email is already registered in the file for the current month.
     */
    public static boolean isEmailRegisteredInMonth(String email, String emailFilePath) {
        try {
            String content = Files.readString(Path.of(emailFilePath));
            return content.toLowerCase().contains(email);

        } catch (IOException e) {
            System.err.println("Error reading file: " + e.getMessage());
            return false;
        }
    }
}

