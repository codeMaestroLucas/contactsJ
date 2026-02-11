package org.example.src.utils;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.Getter;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;

/**
 * Central configuration class for continent settings.
 * Reads from continentsConfig.json to determine which continents are enabled/disabled.
 *
 * This configuration affects:
 * 1. Validations - countries from disabled continents are avoided
 * 2. _CompletedFirmsData - firms from disabled continents are not included
 */
public class ContinentConfig {

    private static final Path CONFIG_PATH = Paths.get("src/main/resources/baseFiles/json/continentsConfig.json");
    private static final ObjectMapper mapper = new ObjectMapper();

    // Cache for continent configuration
    private static Map<String, ContinentSettings> cachedConfig = null;

    /**
     * Gets the configuration for all continents.
     * Uses caching to avoid repeated file reads.
     *
     * @return Map of continent name to its settings
     */
    public static Map<String, ContinentSettings> getConfig() {
        if (cachedConfig == null) {
            cachedConfig = loadConfig();
        }
        return cachedConfig;
    }

    /**
     * Clears the cache, forcing a reload on next access.
     * Useful if the configuration file is modified at runtime.
     */
    public static void clearCache() {
        cachedConfig = null;
    }

    /**
     * Checks if a continent is enabled.
     *
     * @param continent The continent name
     * @return true if enabled, false if disabled or not found
     */
    public static boolean isContinentEnabled(String continent) {
        Map<String, ContinentSettings> config = getConfig();
        ContinentSettings settings = config.get(continent);
        return settings != null && settings.isEnabled();
    }

    /**
     * Gets a set of all enabled continent names.
     *
     * @return Set of enabled continent names
     */
    public static Set<String> getEnabledContinents() {
        Set<String> enabled = new HashSet<>();
        for (Map.Entry<String, ContinentSettings> entry : getConfig().entrySet()) {
            if (entry.getValue().isEnabled()) {
                enabled.add(entry.getKey());
            }
        }
        return enabled;
    }

    /**
     * Gets a set of all disabled continent names.
     *
     * @return Set of disabled continent names
     */
    public static Set<String> getDisabledContinents() {
        Set<String> disabled = new HashSet<>();
        for (Map.Entry<String, ContinentSettings> entry : getConfig().entrySet()) {
            if (!entry.getValue().isEnabled()) {
                disabled.add(entry.getKey());
            }
        }
        return disabled;
    }

    /**
     * Loads the configuration from the JSON file.
     */
    private static Map<String, ContinentSettings> loadConfig() {
        try {
            String jsonContent = Files.readString(CONFIG_PATH);
            return mapper.readValue(jsonContent, new TypeReference<Map<String, ContinentSettings>>() {});
        } catch (IOException e) {
            System.err.println("Error reading continent configuration: " + e.getMessage());
            // Return empty map as fallback (all continents would be considered disabled)
            return new HashMap<>();
        }
    }

    /**
     * Gets the weight of a continent.
     *
     * @param continent The continent name
     * @return the weight (default 1 if not found)
     */
    public static int getContinentWeight(String continent) {
        Map<String, ContinentSettings> config = getConfig();
        ContinentSettings settings = config.get(continent);
        return settings != null ? settings.getWeight() : 1;
    }

    @Getter
    public static class ContinentSettings {
        @JsonProperty("enabled")
        private boolean enabled = true;

        @JsonProperty("weight")
        private int weight = 1;
    }
}
