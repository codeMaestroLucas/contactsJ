package org.example.src.utils;

import org.example.src.CONFIG;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

public class FirmsExhausted {

    private static final String basePath = CONFIG.EXHAUSTED_FIRMS_FILE;

    /**
     * Registers the given firm as exhausted (found lawyers but none passed validation).
     */
    public static void register(String firm) {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(basePath, true))) {
            writer.write(firm + System.lineSeparator());
        } catch (IOException e) {
            System.err.println("Error writing to exhausted firms file: " + e.getMessage());
        }
    }

    /**
     * Checks if the firm is already registered in exhaustedFirms.txt.
     */
    public static boolean isFirmExhausted(String firm) {
        Path path = Path.of(basePath);
        try {
            if (!Files.exists(path)) {
                Files.createDirectories(path.getParent());
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
            System.err.println("Error reading exhausted firms file: " + e.getMessage());
        }

        return false;
    }
}
