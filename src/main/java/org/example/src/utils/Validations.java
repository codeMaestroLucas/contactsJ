package org.example.src.utils;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import org.example.src.entities.Lawyer;

import java.io.IOException;
import java.nio.file.*;
import java.util.Set;
import java.util.List;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.core.type.TypeReference;
import org.example.src.entities.excel.Contacts;

/**
 * Utility class containing validation functions for lawyers.
 */
public class Validations {

    /**
     * Checks if a given country is in the "countriesToAvoid.json" file.
     *
     * @param country The country to be checked.
     * @return true if the country is listed as a country to avoid; false otherwise.
     */
    private static boolean isACountryToAvoid(String country) {
        Path filePath = Paths.get("src/main/resources/baseFiles/json/countriesToAvoid.json");
        ObjectMapper mapper = new ObjectMapper();

        try {
            // Read JSON file content
            String jsonContent = Files.readString(filePath);

            // Parse JSON into List<CountryData>
            List<CountryData> countriesToAvoid = mapper.readValue(
                    jsonContent,
                    new TypeReference<List<CountryData>>() {}
            );

            return countriesToAvoid.stream()
                    .map(CountryData::getCountry)
                    .anyMatch(c -> c.trim().equalsIgnoreCase(country.trim()));


        } catch (IOException e) {
            System.err.println("Error reading country data: " + e.getMessage());
            return false;
        }


    }

    /**
     * Checks if the given email was already registered in the current month or in contacts.xlsx.
     *
     * @param email            The email to verify.
     * @param emailsOfMonthPath Path to the emails-of-the-month file.
     * @return true if the email is found in the current month or contacts; false otherwise.
     */
    private static boolean isEmailAlreadyRegistered(String email, String emailsOfMonthPath) {
        Contacts contacts = Contacts.getINSTANCE();

        // Check in the file for the current month
        if (EmailOfMonth.isEmailRegisteredInMonth(email, emailsOfMonthPath)) return true;

        // Check in contacts.xlsx
        return contacts.isEmailRegistered(email);
    }

    /**
     * Checks if the email is listed in the "emailsToAvoid" file.
     *
     * @param email             The email to check.
     * @param emailsToAvoidPath Path to the file with emails to avoid.
     * @return true if the email is in the file; false otherwise.
     */
    private static boolean isAEmailToAvoid(String email, String emailsToAvoidPath) {
        return EmailOfMonth.isEmailRegisteredInMonth(email, emailsToAvoidPath);
    }

    /**
     * Validates if the operation of registering a lawyer can proceed.
     *
     * @param lawyer            The lawyer to validate.
     * @param setOfLastCountries The set of last countries already processed.
     * @param emailsOfMonthPath Path to the emails-of-the-month file.
     * @param emailsToAvoidPath Path to the emails-to-avoid file.
     * @return true if the lawyer can be registered; false otherwise.
     */
    public static boolean makeValidations(
            Lawyer lawyer, Set<String> setOfLastCountries,
            String emailsOfMonthPath, String emailsToAvoidPath
    ) {

        if (lawyer.email == null || lawyer.email.isEmpty()) {
            System.out.println("Incomplete lawyer data, skipping...\n");
            return false;
        }

        String email = lawyer.email;
        String country = lawyer.country;
        if (!country.equals("-----")) {
            if (isACountryToAvoid(country)) return false;
        }

        if (isAEmailToAvoid(email, emailsToAvoidPath)) return false;

        if (FirmsOMonth.isFirmRegisteredInMonth(lawyer.firm)) return false;

        if (isEmailAlreadyRegistered(email, emailsOfMonthPath)) return false;

        if (setOfLastCountries.contains(country)) return false;

        return true;
    }

    /**
     * Helper class to represent country data from JSON.
     */
    @Getter
    public static class CountryData {
        @JsonProperty("Country")
        private String Country;
    }
}
