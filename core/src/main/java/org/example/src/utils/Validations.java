package org.example.src.utils;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.Getter;
import org.example.exceptions.ValidationExceptions;
import org.example.src.entities.Lawyer;
import org.example.src.utils.validation.EmailDuplicateChecker;

import java.io.IOException;
import java.nio.file.*;
import java.util.*;
import java.util.stream.Collectors;

/**
 * Utility class containing validation functions for lawyers.
 */
public class Validations {

    /**
     * TEST MODE FLAG
     * When true, all validations will ALWAYS fail (for testing extraction only).
     * Set to true in test classes to prevent actual lawyer registration.
     */
    private static boolean TEST_MODE = false;
    private static int testModeCount = 0;

    /**
     * Enable test mode - all validations will fail.
     * Use this in test classes to visualize site extraction without registering lawyers.
     */
    public static void enableTestMode() {
        TEST_MODE = true;
        System.out.println("🔧 TEST MODE ENABLED - All validations will fail (no registrations will be made)");
    }

    /**
     * Disable test mode - validations will work normally.
     */
    public static void disableTestMode() {
        TEST_MODE = false;
    }

    /**
     * Check if test mode is enabled.
     */
    public static boolean isTestMode() {
        return TEST_MODE;
    }

    public static void resetTestModeCount() { testModeCount = 0; }
    public static int getTestModeCount() { return testModeCount; }

    public static boolean isACountryToAvoid(String country) {
        return isAPermanentCountryToAvoid(country);
    }

    private static boolean isAPermanentCountryToAvoid(String country) {
        Path filePath = Paths.get("core/src/main/resources/baseFiles/json/countriesToAvoidPermanent.json");
        ObjectMapper mapper = new ObjectMapper();

        try {
            String jsonContent = Files.readString(filePath);

            // Read the structure: Map<Continent, List<CountryData>>
            Map<String, List<CountryData>> countryDataByContinent = mapper.readValue(
                    jsonContent,
                    new TypeReference<Map<String, List<CountryData>>>() {}
            );

            // Flatten all countries from all continents
            List<String> allCountries = countryDataByContinent.values().stream()
                    .flatMap(List::stream)
                    .map(CountryData::getCountry)
                    .filter(Objects::nonNull)
                    .collect(Collectors.toList());

            // Check if the country is in the list
            return allCountries.stream()
                    .anyMatch(c -> c.trim().equalsIgnoreCase(country.trim()));

        } catch (IOException e) {
            System.err.println("Error reading permanent countries data: " + e.getMessage());
            return false;
        }
    }

    /**
     * Checks if a given firm is in the "firmToAvoid.json" file.
     */
    public static boolean isAFirmToAVoid(String firm) {
        Path filePath = Paths.get("core/src/main/resources/baseFiles/json/firmsToAvoid.json");
        ObjectMapper mapper = new ObjectMapper();

        try {
            String jsonContent = Files.readString(filePath);

            List<CountryData> countriesToAvoid = mapper.readValue(
                    jsonContent,
                    new TypeReference<List<CountryData>>() {}
            );

            return countriesToAvoid.stream()
                    .map(CountryData::getCountry)
                    .filter(java.util.Objects::nonNull)  // Remove valores nulos
                    .anyMatch(c -> c.trim().equalsIgnoreCase(firm.trim()));

        } catch (IOException e) {
            System.err.println("Error reading country data: " + e.getMessage());
            return false;
        }
    }

    /**
     * Checks if the given email was already registered in the current month or in contacts.xlsx.
     */
    private static boolean isEmailAlreadyRegistered(String email, String emailsOfMonthPath) {
        return EmailOfMonth.isEmailRegisteredInMonth(email, emailsOfMonthPath);
    }

    /**
     * Checks if the email is listed in the "emailsToAvoid" file.
     */
    private static boolean isAEmailToAvoid(String email, String emailsToAvoidPath) {
        return EmailOfMonth.isEmailRegisteredInMonth(email, emailsToAvoidPath);
    }

    /**
     * Validates if the operation of registering a lawyer can proceed.
     * 
     * IN TEST MODE: Always throws ValidationExceptions.testModeActive() to prevent registration.
     */
    public static void makeValidations(
            Lawyer lawyer,
            Set<String> setOfLastCountries,
            String emailsOfMonthPath,
            String emailsToAvoidPath
    ) throws ValidationExceptions {

        // ✅ TEST MODE: Always fail validation to prevent registration
        if (TEST_MODE) {
            testModeCount++;
            throw ValidationExceptions.testModeActive();
        }

        if (lawyer.getEmail() == null || lawyer.getEmail().isEmpty()) {
            throw ValidationExceptions.emailValidation();
        }

        String email = lawyer.getEmail();
        String country = lawyer.getCountry();
        String firm = lawyer.getFirm();

        if (isAFirmToAVoid(firm)) {
            throw ValidationExceptions.firmToAvoid();
        }

        if (!"-----".equals(country) && isACountryToAvoid(country)) {
            throw ValidationExceptions.countryToAvoid();
        }

        if (isAEmailToAvoid(email, emailsToAvoidPath)) {
            throw ValidationExceptions.emailToAvoid();
        }

        if (FirmsOMonth.isFirmRegisteredInMonth(lawyer.getFirm())) {
            throw ValidationExceptions.firmAlreadyRegisteredInMonth();
        }

        if (isEmailAlreadyRegistered(email, emailsOfMonthPath)) {
            throw ValidationExceptions.emailAlreadyRegistered();
        }

        if (setOfLastCountries.contains(country)) {
            throw ValidationExceptions.countryInSetOfCountries();
        }

        // Check if email is duplicate on GlobalLawExperts using persistent session
        if (!isEmailCleanOnGlobalLawExperts(email)) {
            throw ValidationExceptions.emailDuplicateOnGlobalLawExperts();
        }
    }

    /**
     * Checks if email is duplicate on GlobalLawExperts website using the singleton checker.
     * This method uses a persistent session, so login only happens once during the entire execution.
     * 
     * @param email Email to check
     * @return true if email is clean (not duplicate), false if duplicate
     */
    private static boolean isEmailCleanOnGlobalLawExperts(String email) {
        return EmailDuplicateChecker.getINSTANCE().isEmailClean(email);
    }

    @Getter
    @com.fasterxml.jackson.annotation.JsonIgnoreProperties(ignoreUnknown = true)
    public static class CountryData {
        @JsonProperty("Country")
        private String Country;
    }
}
