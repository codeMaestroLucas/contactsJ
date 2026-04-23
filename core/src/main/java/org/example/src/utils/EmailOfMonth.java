package org.example.src.utils;

import java.io.*;
import java.nio.file.*;
import java.time.LocalDate;
import java.util.*;

public class EmailOfMonth {
    private static final String currentFormattedDate = String.format(
            "%d/%d\n", LocalDate.now().getMonthValue(), LocalDate.now().getYear()
    );
    private static final Map<String, Set<String>> cache = new HashMap<>();

    private static String generateStringToFile(String email) {
        int emptySpaces = 70 - email.length();
        return email + " ".repeat(emptySpaces) + currentFormattedDate;
    }

    private static Set<String> getOrLoad(String emailFilePath) {
        return cache.computeIfAbsent(emailFilePath, path -> {
            Set<String> emails = new HashSet<>();
            try {
                String content = Files.readString(Path.of(path));
                for (String line : content.split("\n")) {
                    String trimmed = line.trim();
                    if (!trimmed.isEmpty()) {
                        String[] parts = trimmed.split("\\s+");
                        if (parts.length > 0 && !parts[0].isEmpty()) {
                            emails.add(parts[0].toLowerCase());
                        }
                    }
                }
            } catch (IOException ignored) {}
            return emails;
        });
    }

    public static void registerEmailOfMonth(String email, String emailFilePath) {
        getOrLoad(emailFilePath).add(email.toLowerCase().trim());
        String line = generateStringToFile(email);
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(emailFilePath, true))) {
            writer.write(line);
        } catch (IOException e) {
            System.err.println("Error writing to file: " + e.getMessage());
        }
    }

    public static boolean isEmailRegisteredInMonth(String email, String emailFilePath) {
        return getOrLoad(emailFilePath).contains(email.toLowerCase().trim());
    }
}
