package org.example.src.utils;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.Getter;
import org.example.exceptions.ValidationExceptions;
import org.example.src.entities.Lawyer;
import org.example.src.entities.excel.Contacts;

import java.io.IOException;
import java.nio.file.*;
import java.util.List;
import java.util.Set;

/**
 * Utility class containing validation functions for lawyers.
 */
public class Validations {

    /**
     * Checks if a given country is in the "countriesToAvoid.json" file.
     */
    private static boolean isACountryToAvoid(String country) {
        Path filePath = Paths.get("src/main/resources/baseFiles/json/countriesToAvoid.json");
        ObjectMapper mapper = new ObjectMapper();

        try {
            String jsonContent = Files.readString(filePath);

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
