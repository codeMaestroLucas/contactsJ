package org.example.src.entities;

import lombok.Data;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

@Data
public class Lawyer {
    public String link;
    public String name;
    public String role;
    public String firm;
    public String country;
    public String practiceArea;
    public String email;
    public String phone;

    private final String[] validRoles = {
            "Senior Associate",
            "Of Counsel",
            "Managing Partner",
            "Managing Director",
            "Founding Partner",
            "Partner",
            "Shareholder",
            "Counsel",
            "Director",
            "Founder",
            "Advisor",
            "Principal",
//                "Head",
    };


    // Full constructor
    public Lawyer(String link, String name, String role, String firm, String country, String practiceArea, @org.jetbrains.annotations.NotNull String email, String phone) {
        this.link = link;
        this.role = this.treatRole(role.trim());
        this.firm = firm;
        this.country = country.trim();
        this.practiceArea = Objects.isNull(practiceArea) ? "-----" : practiceArea;
        this.email = treatEmail(email);
        this.phone = treatPhone(phone);

        // Move down so the email be treated and then used for the function `getNameFromEmail`
        this.name = treatName(name);
    }


    /**
     * This function is used to treat a Lawyer name by removing abbreviations
     * and returning the cleaned name.
     * Also, if the name is empty, it calls
     * the function `getNameFromEmail`.
     *
     *
     * @param name original name
     * @return treated name
     */
    private String treatName(String name) {
        if (name == null || name.isEmpty()) {
            return this.getNameFromEmail(this.email);
        }

        // Remove punctuation and convert to lowercase
        name = name.replace("\n", " ")
                .replaceAll("[.,]", " ")
                .replaceAll("[\"']", " ")
                // .replaceAll("\\*", " ") // Uncomment if needed
                .toLowerCase();

        // Remove common legal role terms
        for (String role : validRoles) {
            name = name.replace(role.toLowerCase(), " ");
        }

        // Abbreviations to remove
        Set<String> abbreviations = new HashSet<>(Arrays.asList(
                "mr", "ms", "mx", "dr", "prof", "mrs", "miss",
                "master", "sir", "esq", "rev", "att", "llm", "kc"
        ));

        // Split and filter
        String[] words = name.trim().split("\\s+");
        StringBuilder fullName = new StringBuilder();

        for (String word : words) {
            if (!word.isBlank() && !abbreviations.contains(word)) {
                fullName.append(Character.toUpperCase(word.charAt(0)))
                        .append(word.substring(1))
                        .append(" ");
            }
        }

        return fullName.toString().trim();
    }


    /**
     * Treat lawyer email
     * @param email email to be treated
     * @return email formatted
     */
    private String treatEmail(String email) {
        return email.toLowerCase()
                .replace("mailto", "")
                .replace(":", "")
                .trim();
    }


    /**
     * Treat lawyer phone
     * @param phone phone to be treated
     * @return phone formatted
     */
    private String treatPhone(String phone) {
        // Remove all non-digit characters
        String treatedPhone = phone.replaceAll("\\D", "");

        // Remove leading zeros
        treatedPhone = treatedPhone.replaceFirst("^0+", "");

        return treatedPhone;
    }


    /**
     * Treat lawyer role
     * @param role role to be treated
     * @return role formatted
     */
    private String treatRole(String role) {
        for (String validRole : validRoles) {
            if (role.toLowerCase().contains(validRole.toLowerCase())) {
                return validRole;
            }
        }

        return role;
    }


    /**
     * Extracts a name from the email address if the name wasn't found.
     *
     * @param email Email string.
     * @return Extracted name followed by *****.
     */
    protected String getNameFromEmail(String email) {
        String sanitizedEmail = email
                .replaceAll("(?i)mailto", "")
                .replaceAll("(?i):", "")
                .trim().toLowerCase();
        String name = "";

        try {
            java.util.regex.Matcher matcher =
                    java.util.regex.Pattern.compile("^([\\w.-]+)@").matcher(sanitizedEmail);

            if (matcher.find()) {
                name = matcher.group(1)
                        .replace("-", " ")
                        .replace(".", " ")
                        .trim();

            } else {
                throw new IllegalArgumentException("Invalid email format or no match found.");
            }

        } catch (Exception e) {
            java.util.regex.Matcher fallback =
                    java.util.regex.Pattern.compile("^([^@]+)")
                            .matcher(sanitizedEmail);

            if (fallback.find()) {
                name = fallback.group(1)
                        .replace("-", " ")
                        .replace(".", " ")
                        .trim();
            }
        }

        return name + " *****";
    }
}
