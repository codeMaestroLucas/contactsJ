package org.example.src.utils;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.InputStream;
import java.text.Normalizer;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;

/**
 * Utility class for treating and formatting Lawyer data.
 */
public final class TreatLawyerParams {

    // ── Practice Area map ────────────────────────────────────────────────────

    private static volatile Map<String, List<String>> PRACTICE_AREA_MAP = null;

    private static Map<String, List<String>> getPracticeAreaMap() {
        if (PRACTICE_AREA_MAP == null) {
            synchronized (TreatLawyerParams.class) {
                if (PRACTICE_AREA_MAP == null) {
                    PRACTICE_AREA_MAP = loadJsonMap("/baseFiles/json/practiceAreas.json");
                }
            }
        }
        return PRACTICE_AREA_MAP;
    }

    private static String matchPracticeArea(String normalizedInput) {
        Map<String, List<String>> map = getPracticeAreaMap();
        String bestMatch = null;
        int bestMatchLength = 0;

        for (Map.Entry<String, List<String>> entry : map.entrySet()) {
            for (String alias : entry.getValue()) {
                if (normalizedInput.contains(alias) && alias.length() > bestMatchLength) {
                    bestMatch = entry.getKey();
                    bestMatchLength = alias.length();
                }
            }
        }
        return bestMatch;
    }

    // ── Country map ──────────────────────────────────────────────────────────

    private static volatile Map<String, List<String>> COUNTRY_MAP = null;

    private static Map<String, List<String>> getCountryMap() {
        if (COUNTRY_MAP == null) {
            synchronized (TreatLawyerParams.class) {
                if (COUNTRY_MAP == null) {
                    COUNTRY_MAP = loadJsonMap("/baseFiles/json/countryAliases.json");
                }
            }
        }
        return COUNTRY_MAP;
    }

    /**
     * Returns the canonical country name for the given alias, or {@code null} if not found.
     * Uses exact match (after normalization) to avoid false positives from short aliases.
     */
    private static String matchCountry(String normalizedInput) {
        Map<String, List<String>> map = getCountryMap();
        for (Map.Entry<String, List<String>> entry : map.entrySet()) {
            for (String alias : entry.getValue()) {
                if (normalizedInput.equals(alias)) {
                    return entry.getKey();
                }
            }
        }
        return null;
    }

    // ── Shared JSON loader ───────────────────────────────────────────────────

    private static Map<String, List<String>> loadJsonMap(String classpathResource) {
        try (InputStream is = TreatLawyerParams.class.getResourceAsStream(classpathResource)) {
            if (is != null) {
                return new ObjectMapper().readValue(is, new TypeReference<Map<String, List<String>>>() {});
            }
            System.err.println("Resource not found: " + classpathResource);
        } catch (Exception e) {
            System.err.println("Failed to load " + classpathResource + ": " + e.getMessage());
        }
        return new HashMap<>();
    }

    private static final Set<String> ABBREVIATIONS = new HashSet<>(Arrays.asList(
            "mr", "ms", "mx", "dr", "prof", "mrs", "miss", "php",
            "master", "sir", "esq", "rev", "att", "llm", "kc",
            "msc", "llb", "nbsp", "dsc", "em", "mag", "mbl",
            "mba", "mbe", "lawyer", "advocate", "advokat",
            "phd", "prof", "univ", "she/her", "he/him",
            "professor", "lord", "dipl -phys", "adv"
    ));

    private static final String[] VALID_ROLES = {
            "Senior Partner", "Senior Associate", "Senior Director", "Senior Advisor", "Senior Counsel",

            "Associate Principal", "Associate Counsel", "Associate Director", "Associate Advisor", "Associate Partner",
            "Principal Associate",

            "Of Counsel", "Special Counsel",

            "Managing Partner", "Managing Director", "Managing Associate", "Managing Principal", "Managing Counsel",

            "Founding Partner", "Co Founder",

            "Partner", "Counsel", "Director", "Founder", "Principal", "Advisor", "Manager", "Shareholder",
            "Head", "Chair", "Legal", "Silk", "Dipl."
    };


    /**
     * Treats a lawyer's practice area.
     * First tries to match against the canonical practice area map (practiceAreas.json).
     * If no match is found, falls back to removing generic terms.
     */
    public static String treatPracticeArea(String practiceArea) {
        if (Objects.isNull(practiceArea) || practiceArea.isBlank()) {
            return Objects.isNull(practiceArea) ? "-----" : practiceArea.trim();
        }

        // Normalize for JSON lookup: lowercase, & → "and", collapse whitespace
        String normalized = practiceArea
                .replace("&amp;", " and ")
                .replaceAll("(?i)\\s*&\\s*", " and ")
                .toLowerCase()
                .replaceAll("\\s+", " ")
                .trim();

        String canonical = matchPracticeArea(normalized);
        if (canonical != null) return canonical;

        // Fallback: remove generic terms
        String cleaned = practiceArea
                .replace("&amp;", "")
                .replaceAll("(?i)law", "")
                .replaceAll("(?i)specialist", "")
                .replaceAll("(?i)department", "")
                .replaceAll("(?i)service(s)?", "")
                .trim();

        return cleaned.isBlank() ? "-----" : cleaned;
    }


    /**
     * Normalizes a country name to its canonical form as defined in {@code countryAliases.json}.
     *
     * <p>Matching is <b>exact</b> (after lowercasing, accent removal, and whitespace collapse)
     * to avoid false positives from short aliases like "us" or "uk".
     * If no alias matches, the original value is returned trimmed.
     *
     * <p>Examples:
     * <pre>
     *   "UK"                     → "England"
     *   "United Kingdom"         → "England"
     *   "United States"          → "USA"
     *   "Russian Federation"     → "Russia"
     *   "Türkiye"                → "Turkey"
     *   "Germany"                → "Germany"  (no alias → unchanged)
     * </pre>
     */
    public static String treatCountry(String country) {
        if (country == null || country.isBlank()) {
            return country == null ? "-----" : country.trim();
        }

        String normalized = removeAccents(country)
                .replaceAll("[\\r\\n\\t]", " ")
                .replaceAll("\\s+", " ")
                .toLowerCase()
                .trim();

        String canonical = matchCountry(normalized);
        return canonical != null ? canonical : country.trim();
    }


    /**
     * Cleans a lawyer's name by removing abbreviations, punctuation, and roles.
     * Fixed: Now correctly capitalizes names with hyphens (e.g., Sainte-Marie).
     */
    public static String treatName(String name) {
        if (name == null || name.isBlank()) return "";

        String processedName = name
                .toLowerCase()
                .replaceAll("[.,;*ˆ:`()\\[\\]{}]", " ")
                .replaceAll("[\"']", " ")
                .replace("ll m", "")
                .replace("k c", "")
                .trim();

        for (String role : VALID_ROLES) {
            processedName = processedName.replace(role.toLowerCase(), " ");
        }

        String[] words = processedName.trim().split("\\s+");
        StringBuilder fullName = new StringBuilder();

        for (String word : words) {
            if (!word.isBlank() && !ABBREVIATIONS.contains(word)) {
                fullName.append(capitalizeWordWithHyphens(word)).append(" ");
            }
        }
        return fullName.toString().trim();
    }


    /**
     * Helper method to capitalize parts of a word separated by hyphens.
     */
    private static String capitalizeWordWithHyphens(String word) {
        if (!word.contains("-")) {
            return Character.toUpperCase(word.charAt(0)) + word.substring(1);
        }

        String[] parts = word.split("-", -1);
        StringBuilder result = new StringBuilder();

        for (int i = 0; i < parts.length; i++) {
            String part = parts[i];
            if (!part.isEmpty()) {
                result.append(Character.toUpperCase(part.charAt(0)))
                        .append(part.substring(1));
            }
            if (i < parts.length - 1) {
                result.append("-");
            }
        }
        return result.toString();
    }


    /**
     * Removes all accent marks (diacritics) from a string.
     */
    public static String removeAccents(String input) {
        if (input == null) {
            return "";
        }
        String normalized = Normalizer.normalize(input, Normalizer.Form.NFD);
        // Remove all diacritical marks
        return normalized.replaceAll("\\p{InCombiningDiacriticalMarks}+", "");
    }

    /**
     * Treats the name for the generation of the email
     * @return name in lowercase and without accents
     */
    public static String treatNameForEmail(String name) {
        name = TreatLawyerParams.treatName(name);
        return TreatLawyerParams.removeAccents(name).toLowerCase().trim();
    }


    /**
     * Cleans an email address.
     * @param email The original email string.
     * @return The treated email.
     */
    public static String treatEmail(String email) {
        if (email == null) return "";
        email = removeAccents(email);
        return email.replaceAll("\\?.*$", "")
                .toLowerCase()
                .replace("mailto", "")
                .replace("email", "")
                .replace("e-mail", "")
                .replace(":", "")
                .trim();
    }


    /**
     * Cleans a phone number, keeping only digits.
     * @param phone The original phone string.
     * @return The treated phone number.
     */
    public static String treatPhone(String phone) {
        if (phone == null) return "";
        return phone.replaceAll("\\D", "")
                .replaceFirst("^0+", "")
                .trim();
    }


    /**
     * Normalizes a lawyer's role based on a predefined list of valid roles.
     * @param role The original role string.
     * @return The normalized role or the original role if no match is found.
     */
    public static String treatRole(String role) {
        if (role == null) return "";
        String lowerCaseRole = role.trim().toLowerCase();
        for (String validRole : VALID_ROLES) {
            if (lowerCaseRole.contains(validRole.toLowerCase())) {
                return validRole;
            }
        }
        return role;
    }


    /**
     * Determines the specialism based on the lawyer's role.
     * @param treatedRole The already treated role.
     * @return "Legal" or "Advisor".
     */
    public static String treatSpecialism(String treatedRole) {
        String roleToCheck = treatedRole.toLowerCase();
        if (roleToCheck.equals("manager") || roleToCheck.contains("advisor")) {
            return "Advisor";
        }
        return "Legal";
    }


    /**
     * Extracts a name from an email address as a fallback.
     * @param email The email address.
     * @return The extracted name, appended with "*****".
     */
    public static String getNameFromEmail(String email) {
        if (email == null || email.isBlank()) {
            return "*****";
        }
        String namePart = email.split("@")[0];
        String name = namePart
                .replace("-", " ")
                .replace(".", " ")
                .trim();
        return name + " *****";
    }
}