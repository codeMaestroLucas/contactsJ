package org.example.src.entities;

import org.openqa.selenium.*;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.interactions.Actions;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.time.Duration;

public class MyDriver {
    private static WebDriver driver;

    private MyDriver() {
        // Private constructor to prevent instantiation
    }

    public static WebDriver getINSTANCE() {
        if (driver == null) {
            ChromeOptions options = new ChromeOptions();
//            options.addArguments("--headless"); // Run Chrome in headless mode
            options.addArguments("--disable-gpu");
            options.addArguments("--ignore-certificate-errors");
            options.addArguments("--disable-web-security");
            options.addArguments("--allow-insecure-localhost");
            options.addArguments("--no-proxy-server");
            options.addArguments("--disable-features=IsolateOrigins,site-per-process");

            driver = new ChromeDriver(options);
        }

        return driver;
    }

// HOW SHOULD I CALL THIS FUNCTIONS IN OTHER CLASSES

    /**
     * Waits for the current page to fully load by checking the `document.readyState`.
     * Waits up to 6 minutes.
     */
    public static void waitForPageToLoad() {
        driver.manage().timeouts().pageLoadTimeout(Duration.ofMinutes(6));

        new WebDriverWait(driver, Duration.ofMinutes(6))
                .until(webDriver -> ((JavascriptExecutor) webDriver)
                        .executeScript("return document.readyState")
                        .equals("complete"));
    }

    /**
     * Scroll down the page to load more elements.
     *
     * @param timesToRollDown Number of times to scroll.
     * @param sleepTime       Sleep time in seconds between scrolls.
     */
    public static void rollDown(int timesToRollDown, double sleepTime) throws InterruptedException {
        Actions actions = new Actions(driver);

        for (int i = 0; i < timesToRollDown; i++) {
            Thread.sleep((long) (sleepTime * 1000L));
            actions.keyDown(Keys.CONTROL)
                    .sendKeys(Keys.END)
                    .keyUp(Keys.CONTROL)
                    .perform();
        }
        Thread.sleep(1500);
    }

    /**
     * Waits until 10sec to find a element and then perform a click.
     * @param by locator for the element
     */
    public static void clickOnElement(By by) {
        // Find element
        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(10));
        WebElement elementToClick = wait.until(ExpectedConditions.presenceOfElementLocated(by));

        // Click on element
        try {
            elementToClick.click();

        } catch (Exception e) {
            ((JavascriptExecutor) driver).executeScript("arguments[0].click();", elementToClick);
        }
    }


    /**
     * Perform a click on a element passed
     * @param element to click
     */
    public static void clickOnElement(WebElement element) {
        // Find element
        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(10));
        WebElement elementToClick = wait.until(ExpectedConditions.elementToBeClickable(element));

        // Click on element
        try {
            elementToClick.click();

        } catch (Exception e) {
            ((JavascriptExecutor) driver).executeScript("arguments[0].click();", elementToClick);
        }
    }


    /**
     * Clicks on the given element multiple times with a delay between each click.
     *
     * @param element            The element to click. Can be either a {@link By} locator or a {@link WebElement}.
     * @param numberOfIterations Number of times the element should be clicked.
     * @param sleepTime          Time in seconds to wait between clicks.
     * @throws InterruptedException if the thread is interrupted during sleep.
     * @throws IllegalArgumentException if the element is neither a By nor a WebElement.
     */
    public static void clickOnElementMultipleTimes(
            Object element, int numberOfIterations, int sleepTime
    ) throws InterruptedException {


        int i = 0;
        try {
            for (i = 0; i < numberOfIterations; i++) {
                if (element instanceof By) {
                    clickOnElement((By) element);

                } else if (element instanceof WebElement) {
                    clickOnElement((WebElement) element);

                } else {
                    throw new IllegalArgumentException("Element must be either a By or WebElement");
                }

                Thread.sleep(1000L * sleepTime);
            }
        } catch (Exception e) {
            System.out.printf("Stopped before completing all the %d clicks.\n", numberOfIterations);
            System.out.printf("Performed only %d clicks.\n", i);
        }
    }


    public static void quitDriver() {
        if (driver != null) {
            driver.quit();
            driver = null;
        }
    }
}