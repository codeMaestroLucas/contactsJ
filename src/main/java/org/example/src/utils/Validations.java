package org.example.src.utils;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.Getter;
import org.example.exceptions.ValidationExceptions;
import org.example.src.entities.Lawyer;
import org.example.src.entities.excel.Contacts;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.io.IOException;
import java.nio.file.*;
import java.time.Duration;
import java.util.List;
import java.util.Set;

/**
 * Utility class containing validation functions for lawyers.
 */
public class Validations {

    /**
     * Checks if a given country is in the "countriesToAvoid.json" file.
     */
    public static boolean isACountryToAvoid(String country) {
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
                    .filter(java.util.Objects::nonNull)  // ✅ Remove valores nulos
                    .anyMatch(c -> c.trim().equalsIgnoreCase(country.trim()));

        } catch (IOException e) {
            System.err.println("Error reading country data: " + e.getMessage());
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

        // Check if email is duplicate on GlobalLawExperts (last validation)
        if (!isEmailCleanOnGlobalLawExperts(email)) {
            throw ValidationExceptions.emailDuplicateOnGlobalLawExperts();
        }
    }


    /**
     * Checks if email is duplicate on GlobalLawExperts website.
     * Returns true if email is clean (not duplicate), false if duplicate.
     */
    private static boolean isEmailCleanOnGlobalLawExperts(String email) {
        WebDriver driver = null;
        try {
            driver = new ChromeDriver();
            WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(20));
            
            // Step 1: Access login page
            driver.get("https://globallawexperts.com/login/?redirect_to=https%3A%2F%2Fgloballawexperts.com%2Fdashboard%2F");
            
            // Step 2: Fill login credentials
            WebElement usernameField = wait.until(ExpectedConditions.presenceOfElementLocated(By.name("login_username")));
            usernameField.sendKeys("contact@kfroisconsulting.com");
            
            WebElement passwordField = driver.findElement(By.name("login_password"));
            passwordField.sendKeys("Fo5KdZhSNxT!y1bQpkPh)6qg");
            
            // Step 3: Click login button
            WebElement loginButton = driver.findElement(By.xpath("/html/body/div[1]/div/div/div[2]/div/div/div/form/div[3]/div[4]/button"));
            loginButton.click();
            
            // Wait for login to complete
            Thread.sleep(3000);
            
            // Step 4: Navigate to duplicate checker
            driver.get("https://globallawexperts.com/lead-duplicate-checker/");
            
            // Step 5: Enter email to check
            WebElement emailInput = wait.until(ExpectedConditions.presenceOfElementLocated(By.xpath("//*[@id='email-input']")));
            emailInput.clear();
            emailInput.sendKeys(email);
            
            // Wait for result to appear
            Thread.sleep(2000);
            
            // Step 6: Check result
            WebElement resultContainer = wait.until(ExpectedConditions.presenceOfElementLocated(By.id("result-container")));
            String resultClass = resultContainer.getAttribute("class");
            
            // If contains "result-clean" class, email is clean (no duplicates)
            return resultClass != null && resultClass.contains("result-clean");
            
        } catch (Exception e) {
            System.err.println("Error checking email on GlobalLawExperts: " + e.getMessage());
            e.printStackTrace();
            return false; // Consider as duplicate if error occurs (safer approach)
        } finally {
            if (driver != null) {
                driver.quit();
            }
        }
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
