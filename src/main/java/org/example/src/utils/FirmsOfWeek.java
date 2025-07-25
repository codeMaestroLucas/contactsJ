package org.example.src.utils;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

public class FirmsOfWeek {

    private static final String basePath = "data/weekFirms.txt";

    /**
     * Registers the given firm in the file.
     */
    public static void registerFirmWeek(String firm) {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(basePath, true))) {
            writer.write(firm + System.lineSeparator());
        } catch (IOException e) {
            System.err.println("Error writing to file: " + e.getMessage());
        }
    }

    /**
     * Checks if the firm is already registered in the file for the current week.
     */
    public static boolean getRegisteredFirmWeek(String firm) {
        Path path = Path.of(basePath);
        try {
            // Create the file if doesn't exist
            if (!Files.exists(path)) {
                Files.createDirectories(path.getParent()); // ensure directory exists
                Files.createFile(path);
                return false;
            }

            List<String> lines = Files.readAllLines(path);
            for (String line : lines) {
                if (line.trim().equalsIgnoreCase(firm.trim())) {
                    return true;
                }
            }

        } catch (IOException e) {
            System.err.println("Error reading file: " + e.getMessage());
        }

        return false;
    }
}
