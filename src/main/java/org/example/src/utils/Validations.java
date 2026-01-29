package org.example.src.utils;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.Getter;
import org.example.exceptions.ValidationExceptions;
import org.example.src.entities.Lawyer;
import org.example.src.entities.excel.Contacts;
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
     * Checks if a given country should be avoided.
     * Combines two sources:
     * 1. PERMANENT countries - ALWAYS avoided (countriesToAvoidPermanent.json)
     * 2. TEMPORARY countries - Only avoided when enabled (countriesToAvoidTemporary.json)
     * 
     * @param country The country to check
     * @return true if the country should be avoided, false otherwise
     */
    public static boolean isACountryToAvoid(String country) {
        // Check permanent countries first (always avoided)
        if (isAPermanentCountryToAvoid(country)) {
            return true;
        }
        
        // Check temporary countries (only if enabled)
        return isATemporaryCountryToAvoid(country);
    }

    /**
     * Checks if a country is in the PERMANENT avoid list.
     * These countries are ALWAYS avoided, regardless of any enabled flag.
     * 
     * File: countriesToAvoidPermanent.json
     */
    private static boolean isAPermanentCountryToAvoid(String country) {
        Path filePath = Paths.get("src/main/resources/baseFiles/json/countriesToAvoidPermanent.json");
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
     * Checks if a country is in the TEMPORARY avoid list AND the continent is enabled.
     * These countries are only avoided when their continent has "enabled": true.
     * 
     * File: countriesToAvoidTemporary.json
     */
    private static boolean isATemporaryCountryToAvoid(String country) {
        Path filePath = Paths.get("src/main/resources/baseFiles/json/countriesToAvoidTemporary.json");
        ObjectMapper mapper = new ObjectMapper();

        try {
            String jsonContent = Files.readString(filePath);

            // Read the structure: Map<Continent, ContinentData>
            Map<String, ContinentData> continentDataMap = mapper.readValue(
                    jsonContent,
                    new TypeReference<Map<String, ContinentData>>() {}
            );

            // Flatten all countries from ENABLED continents only
            List<String> enabledCountries = continentDataMap.entrySet().stream()
                    .filter(entry -> entry.getValue().isEnabled()) // ✅ Only process enabled continents
                    .flatMap(entry -> entry.getValue().getCountries().stream())
                    .map(CountryData::getCountry)
                    .filter(Objects::nonNull)
                    .collect(Collectors.toList());

            // Check if the country is in the list
            return enabledCountries.stream()
                    .anyMatch(c -> c.trim().equalsIgnoreCase(country.trim()));

        } catch (IOException e) {
            System.err.println("Error reading temporary countries data: " + e.getMessage());
            return false;
        }
    }

    /**
     * Checks if a given firm is in the "firmToAvoid.json" file.
     */
    public static boolean isAFirmToAVoid(String firm) {
        Path filePath = Paths.get("src/main/resources/baseFiles/json/firmsToAvoid.json");
        ObjectMapper mapper = new ObjectMapper();

        try {
            String jsonContent = Files.readString(filePath);

            List<CountryData> countriesToAvoid = mapper.readValue(
                    jsonContent,
                    new TypeReference<List<CountryData>>() {}
            );

            return countriesToAvoid.stream()
                    .map(CountryData::getCountry)
                    .filter(java.util.Objects::nonNull)  // ✅ Remove valores nulos
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
        Contacts contacts = Contacts.getINSTANCE();

        if (EmailOfMonth.isEmailRegisteredInMonth(email, emailsOfMonthPath)) return true;

        return contacts.isEmailRegistered(email);
    }

    /**
     * Checks if the email is listed in the "emailsToAvoid" file.
     */
    private static boolean isAEmailToAvoid(String email, String emailsToAvoidPath) {
        return EmailOfMonth.isEmailRegisteredInMonth(email, emailsToAvoidPath);
    }

    /**
     * Validates if the operation of registering a lawyer can proceed.
     */
    public static void makeValidations(
            Lawyer lawyer,
            Set<String> setOfLastCountries,
            String emailsOfMonthPath,
            String emailsToAvoidPath
    ) throws ValidationExceptions {

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

    /**
     * Helper class to represent continent data from JSON.
     * Contains an "enabled" flag to toggle entire continents on/off.
     */
    @Getter
    @com.fasterxml.jackson.annotation.JsonIgnoreProperties(ignoreUnknown = true)
    public static class ContinentData {
        @JsonProperty("enabled")
        private boolean enabled = true; // Default to enabled if not specified
        
        @JsonProperty("countries")
        private List<CountryData> countries = new ArrayList<>();
    }

    /**
     * Helper class to represent country data from JSON.
     */
    @Getter
    @com.fasterxml.jackson.annotation.JsonIgnoreProperties(ignoreUnknown = true)
    public static class CountryData {
        @JsonProperty("Country")
        private String Country;
    }
}
