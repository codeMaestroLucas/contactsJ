package org.example.src.utils;

import org.example.src.CONFIG;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

public class FirmsOMonth {

    private static final String basePath = CONFIG.MONTH_FILE;

    /**
     * Registers the given firm in the file.
     */
    public static void registerFirmMonth(String firm) {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(basePath, true))) {
            writer.write(firm + System.lineSeparator());
        } catch (IOException e) {
            System.err.println("Error writing to file: " + e.getMessage());
        }
    }

    /**
     * Checks if the firm is already registered in the file `monthFirms.txt`.
     */
    public static boolean isFirmRegisteredInMonth(String firm) {
        Path path = Path.of(basePath);
        try {
            // Create the file if it doesn't exist
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
