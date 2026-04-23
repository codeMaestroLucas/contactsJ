package org.example.src.utils;

import org.example.src.CONFIG;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashSet;
import java.util.Set;

public class FirmsExhausted {

    private static final String basePath = CONFIG.EXHAUSTED_FIRMS_FILE;
    private static final Set<String> exhaustedSet = loadFromFile();

    private static Set<String> loadFromFile() {
        Set<String> set = new HashSet<>();
        Path path = Path.of(basePath);
        try {
            if (!Files.exists(path)) {
                Files.createDirectories(path.getParent());
                Files.createFile(path);
            } else {
                for (String line : Files.readAllLines(path)) {
                    if (!line.isBlank()) set.add(line.trim().toLowerCase());
                }
            }
        } catch (IOException e) {
            System.err.println("Error loading exhausted firms file: " + e.getMessage());
        }
        return set;
    }

    public static void register(String firm) {
        if (exhaustedSet.add(firm.trim().toLowerCase())) {
            try (BufferedWriter writer = new BufferedWriter(new FileWriter(basePath, true))) {
                writer.write(firm + System.lineSeparator());
            } catch (IOException e) {
                System.err.println("Error writing to exhausted firms file: " + e.getMessage());
            }
        }
    }

    public static boolean isFirmExhausted(String firm) {
        return exhaustedSet.contains(firm.trim().toLowerCase());
    }
}
