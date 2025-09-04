package org.example.src.entities;

import lombok.Builder;
import lombok.Data;
import org.apache.poi.hssf.record.LabelRecord;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

@Data
public final class Lawyer {
    public String link;
    public String name;
    public String role;
    public String firm;
    public String country;
    public String practiceArea;
    public String email;
    public String phone;
    public String specialism;

    // Abbreviations to remove
    final Set<String> abbreviations = new HashSet<>(Arrays.asList(
            "mr", "ms", "mx", "dr", "prof", "mrs", "miss",
            "master", "sir", "esq", "rev", "att", "llm", "kc"
    ));

    private final String[] validRoles = {
        "Senior Partner", "Senior Associate", "Senior Director", "Senior Advisor",

        "Associate Principal", "Associate Counsel",

        "Of Counsel", "Special Counsel",

        "Managing Partner", "Managing Director", "Managing Associate", "Managing Principal", "Managing Counsel",
        "Founding Partner",

        "Partner", "Counsel", "Director", "Founder", "Principal", "Advisor", "Manager", "Shareholder",
        "Head", "Chair", "Legal"
    };

    @Builder
    public Lawyer(String link, String name, String role, String firm, String country, String practiceArea, @org.jetbrains.annotations.NotNull String email, String phone) {
        this.link =         link;
        this.role =         treatRole(role.trim().toLowerCase());
        this.firm =         firm;
        this.country =      country.trim();
        this.practiceArea = treatPracticeArea(practiceArea);
        this.email =        treatEmail(email.trim());
        this.phone =        treatPhone(phone.trim());
        this.specialism =   treatSpecialism();

        // Move down so the email be treated and then used for the function `getNameFromEmail`
        this.name =         treatName(name.trim());
    }


    /**
     * Treats lawyers practice area
     *
     * @param practiceArea original practice area
     * @return practice area treated
     */
    private String treatPracticeArea(String practiceArea) {
        if (Objects.isNull(practiceArea)) return "-----";

        return practiceArea
                .replace("&amp", "")
                .replace("law", "").replace("Law", "")
                .replace("specialist", "").replace("Specialist", "")
                .replace("department", "").replace("Department", "")
                .replace("service", "").replace("Service", "")
                .replace("services", "").replace("Services", "")
                .trim();
    }


    /**
     * This function is used to treat a Lawyer name by removing abbreviations
     * and returning the cleaned name.
     * Also, if the name is empty, it calls
     * the function `getNameFromEmail`.
     *
     * @param name original name
     * @return treated name
     */
    private String treatName(String name) {
        if (name == null || name.isEmpty()) {
            return this.getNameFromEmail(this.email);
        }

        // Remove punctuation and convert to lowercase
        name = name
                .replaceAll("[.,]", " ")
                .replaceAll("[\"']", " ")
                .trim().toLowerCase();

        // Remove common legal role terms
        for (String role : validRoles) {
            name = name.replace(role.toLowerCase(), " ");
        }


        // Split and filter & Remove common abbreviation
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
     *
     * @param email email to be treated
     * @return email formatted
     */
    private String treatEmail(String email) {
        email = email.replaceAll("\\?.*$", ""); // remove everything from "?" onwards

        return email.toLowerCase()
                .replace("mailto", "")
                .replace(":", "")
                .trim();
    }


    /**
     * Treat lawyer phone
     *
     * @param phone phone to be treated
     * @return phone formatted
     */
    private String treatPhone(String phone) {
        // Remove all non-digit characters and leading zeros
        return phone.replaceAll("\\D", "")
                    .replaceFirst("^0+", "");
    }


    /**
     * Treat lawyer role
     *
     * @param role role to be treated
     * @return role formatted
     */
    private String treatRole(String role) {
        for (String validRole : validRoles) {
            if (role.contains(validRole.toLowerCase())) {
                return validRole;
            }
        }
        return role;
    }


    private String treatSpecialism() {
        String specialism = "Advisor";
        String roleToCheck = this.role.toLowerCase();

        if (!roleToCheck.equals("manager") || !roleToCheck.contains("advisor")) specialism = "Legal";

        return specialism;
    }


    /**
     * Extracts a name from the email address if the name wasn't found.
     *
     * @param email Email string.
     * @return Extracted name followed by *****.
     */
    private String getNameFromEmail(String email) {
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
