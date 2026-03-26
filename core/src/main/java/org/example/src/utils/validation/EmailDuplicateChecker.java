package org.example.src.utils.validation;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.time.Duration;
import java.util.List;
import java.util.Objects;

/**
 * Singleton class to check if an email is already registered on globallawexperts.com
 * Maintains a persistent session to avoid repeated logins.
 */
public final class EmailDuplicateChecker {
    private static EmailDuplicateChecker INSTANCE;
    private WebDriver driver;
    private boolean isLoggedIn = false;

    private static final String LOGIN_URL = "https://globallawexperts.com/login/?redirect_to=https%3A%2F%2Fgloballawexperts.com%2Fdashboard%2F";
    private static final String DUPLICATE_CHECKER_URL = "https://globallawexperts.com/lead-duplicate-checker/";
    private static final String USERNAME = "contact@kfroisconsulting.com";
    private static final String PASSWORD = "Fo5KdZhSNxT!y1bQpkPh)6qg";

    // Locators
    private static final By USERNAME_INPUT = By.name("login_username");
    private static final By PASSWORD_INPUT = By.name("login_password");
    private static final By LOGIN_BUTTON = By.xpath("/html/body/div[1]/div/div/div[2]/div/div/div/form/div[3]/div[4]/button");
    private static final By EMAIL_INPUT = By.id("email-input");
    private static final By RESULT_CONTAINER = By.id("result-container");

    private EmailDuplicateChecker() {
        initializeDriver();
    }

    public static EmailDuplicateChecker getINSTANCE() {
        if (Objects.isNull(INSTANCE)) {
            INSTANCE = new EmailDuplicateChecker();
        }
        return INSTANCE;
    }

    /**
     * Initializes the WebDriver with Chrome options
     */
    private void initializeDriver() {
        ChromeOptions options = new ChromeOptions();
        options.addArguments("--disable-gpu");
        options.addArguments("--ignore-certificate-errors");
        options.addArguments("--disable-web-security");
        options.addArguments("--allow-insecure-localhost");
        options.addArguments("--no-proxy-server");
        options.addArguments("--disable-features=IsolateOrigins,site-per-process");
        options.addArguments("--disable-blink-features=AutomationControlled");
        options.setExperimentalOption("excludeSwitches", List.of("enable-automation"));
        options.setExperimentalOption("useAutomationExtension", false);
        options.addArguments("--user-agent=Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/120.0.0.0 Safari/537.36");

        this.driver = new ChromeDriver(options);
    }

    /**
     * Performs login on globallawexperts.com
     * Only executed once during the first email check
     */
    public void login() {
        try {
            driver.get(LOGIN_URL);
            
            WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(15));

            // Wait for and fill username
            WebElement usernameField = wait.until(ExpectedConditions.presenceOfElementLocated(USERNAME_INPUT));
            usernameField.clear();
            usernameField.sendKeys(USERNAME);

            // Fill password
            WebElement passwordField = driver.findElement(PASSWORD_INPUT);
            passwordField.clear();
            passwordField.sendKeys(PASSWORD);

            // Click login button
            WebElement loginButton = wait.until(ExpectedConditions.elementToBeClickable(LOGIN_BUTTON));
            loginButton.click();

            // Wait for redirect to dashboard (confirming successful login)
            wait.until(ExpectedConditions.urlContains("dashboard"));
            
            isLoggedIn = true;

            Thread.sleep(5000);

            driver.get("https://globallawexperts.com/auth/");
            Thread.sleep(2000);

        } catch (Exception e) {
            System.err.println("EmailDuplicateChecker: Login failed - " + e.getMessage());
            throw new RuntimeException("Failed to login to globallawexperts.com", e);
        }
    }

    /**
     * Verifies if the required elements are present on the page
     * @return true if all required elements are present, false otherwise
     */
    private boolean areRequiredElementsPresent() {
        try {
            WebDriverWait shortWait = new WebDriverWait(driver, Duration.ofSeconds(3));
            shortWait.until(ExpectedConditions.presenceOfElementLocated(EMAIL_INPUT));
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    /**
     * Ensures the duplicate checker page is loaded with all required elements
     * If elements are not present, attempts to re-login
     * If re-login fails, restarts the driver
     */
    private void ensurePageIsReady() throws InterruptedException {
        int maxAttempts = 3;
        int attempt = 0;

        while (attempt < maxAttempts) {
            attempt++;

            // Navigate to duplicate checker page if not already there
            if (!driver.getCurrentUrl().equals(DUPLICATE_CHECKER_URL)) {
                driver.get(DUPLICATE_CHECKER_URL);
                Thread.sleep(2000);
            }

            // Check if we need to authenticate
            try {
                driver.findElement(By.cssSelector("a.ts-action-con[href*='https://globallawexperts.com/auth/']"));
                System.out.println("EmailDuplicateChecker: Auth required, navigating to auth page...");
                driver.get("https://globallawexperts.com/auth/");
                Thread.sleep(2000);
                driver.get(DUPLICATE_CHECKER_URL);
                Thread.sleep(2000);
            } catch (Exception e) {
                // Auth link not present, continue
            }

            // Check if required elements are present
            if (areRequiredElementsPresent()) {
                return; // Success!
            }

            // Elements not present, check if we need to login again

            if (attempt < maxAttempts) {
                // Try to login again
                isLoggedIn = false;
                try {
                    login();
                    Thread.sleep(5000);
                    driver.get(DUPLICATE_CHECKER_URL);
                    Thread.sleep(2000);
                } catch (Exception loginError) {}
            }
        }

        // If we got here, all attempts failed - restart the driver
        restartDriver();
        
        // One final attempt after restart
        login();
        Thread.sleep(5000);
        driver.get(DUPLICATE_CHECKER_URL);
        Thread.sleep(2000);
        
        if (!areRequiredElementsPresent()) {
            throw new RuntimeException("Failed to load duplicate checker page after driver restart");
        }
    }

    /**
     * Closes and reinitializes the WebDriver
     */
    private void restartDriver() {
        try {
            if (driver != null) {
                driver.quit();
            }
        } catch (Exception e) {} finally {
            driver = null;
            isLoggedIn = false;
        }
        
        // Initialize new driver
        initializeDriver();
    }

    /**
     * Checks if an email is already registered (duplicate) on globallawexperts.com
     * 
     * @param email The email to check
     * @return true if email is clean (no duplicates), false if duplicate found
     */
    public boolean isEmailClean(String email) {
        if (email == null || email.trim().isEmpty()) {
            System.err.println("EmailDuplicateChecker: Invalid email provided");
            return false;
        }

        try {
            // Login on first use
            if (!isLoggedIn) {
                login();
            }

            // Ensure page is ready with all required elements
            ensurePageIsReady();

            WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(15));

            // Wait for email input and enter email
            WebElement emailInput = wait.until(ExpectedConditions.presenceOfElementLocated(EMAIL_INPUT));
            emailInput.clear();
            emailInput.sendKeys(email);

            wait.until(ExpectedConditions.elementToBeClickable(By.id("check-button"))).click();

            // Wait for result container to appear
            WebElement resultContainer = wait.until(
                ExpectedConditions.visibilityOfElementLocated(RESULT_CONTAINER)
            );

            // Check if it has the "result-clean" class
            String resultClass = resultContainer.getAttribute("class");
            return resultClass != null && resultClass.contains("result-clean");

        } catch (Exception e) {
            System.err.println("EmailDuplicateChecker: Error checking email '" + email + "' - " + e.getMessage());
            e.printStackTrace();
            
            // Try to restart driver for next attempt
            try {
                restartDriver();
                isLoggedIn = false;
            } catch (Exception restartError) {
                System.err.println("EmailDuplicateChecker: Failed to restart driver - " + restartError.getMessage());
            }
            
            // In case of error, assume email is not clean to be safe
            return false;
        }
    }

    /**
     * Closes the WebDriver session
     * Should be called when the application finishes
     */
    public void close() {
        if (driver != null) {
            try {
                driver.quit();
            } catch (Exception e) {
                System.err.println("EmailDuplicateChecker: Error closing WebDriver - " + e.getMessage());
            } finally {
                driver = null;
                isLoggedIn = false;
            }
        }
    }
}
