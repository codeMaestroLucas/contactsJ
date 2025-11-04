package org.example.src.utils;

import java.text.Normalizer;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;


/**
 * Utility class for treating and formatting Lawyer data.
 */
public final class TreatLawyerParams {

    private static final Set<String> ABBREVIATIONS = new HashSet<>(Arrays.asList(
            "mr", "ms", "mx", "dr", "prof", "mrs", "miss", "php",
            "master", "sir", "esq", "rev", "att", "llm", "kc",
            "msc", "llb", "nbsp", "dsc"
    ));

    private static final String[] VALID_ROLES = {
            "Senior Partner", "Senior Associate", "Senior Director", "Senior Advisor", "Senior Counsel",

            "Associate Principal", "Associate Counsel", "Associate Director", "Associate Advisor", "Associate Partner",

            "Of Counsel", "Special Counsel",

            "Managing Partner", "Managing Director", "Managing Associate", "Managing Principal", "Managing Counsel",

            "Founding Partner", "Co Founder",

            "Partner", "Counsel", "Director", "Founder", "Principal", "Advisor", "Manager", "Shareholder",
            "Head", "Chair", "Legal", "Silk", "Dipl."
    };

    /**
     * Treats a lawyer's practice area by removing common, generic terms.
     * @param practiceArea The original practice area string.
     * @return The treated practice area.
     */
    public static String treatPracticeArea(String practiceArea) {
        if (Objects.isNull(practiceArea)) {
            return "-----";
        }
        return practiceArea
                .replace("&amp;", "")
                .replaceAll("(?i)law", "")
                .replaceAll("(?i)specialist", "")
                .replaceAll("(?i)department", "")
                .replaceAll("(?i)service(s)?", "")
                .trim();
    }


    /**
     * Cleans a lawyer's name by removing abbreviations, punctuation, and roles.
     * If the name is empty, it derives a name from the email.
     * @param name The original name string.
     * @return The treated name.
     */
    public static String treatName(String name) {
        String processedName = name
                .replaceAll("[.,;*ˆ:`]", " ")
                .replaceAll("[\"']", " ")
                .toLowerCase()
                .replace("ll m", "")
                .replace("k c", "")
                .trim();

        for (String role : VALID_ROLES) {
            processedName = processedName.replace(role, " ");
        }

        String[] words = processedName.trim().split("\\s+");
        StringBuilder fullName = new StringBuilder();

        for (String word : words) {
            if (!word.isBlank() && !ABBREVIATIONS.contains(word)) {
                fullName.append(Character.toUpperCase(word.charAt(0)))
                        .append(word.substring(1))
                        .append(" ");
            }
        }
        return fullName.toString().trim();
    }


    /**
     * Removes all accent marks (diacritics) from a string.
     * Example: "martin.römermann" -> "martin.romermann"
     * @param input The original string.
     * @return The normalized string without accents.
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