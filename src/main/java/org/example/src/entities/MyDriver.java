package org.example.src.entities;

import org.openqa.selenium.*;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.interactions.Actions;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.time.Duration;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public final class MyDriver {
    private static WebDriver driver;

    // Private constructor to prevent instantiation
    private MyDriver() {}

    public static WebDriver getINSTANCE() {
        if (driver == null) {
            ChromeOptions options = new ChromeOptions();
            options.addArguments("--headless"); // Run Chrome in headless mode
            options.addArguments("--disable-gpu");
            options.addArguments("--ignore-certificate-errors");
            options.addArguments("--disable-web-security");
            options.addArguments("--allow-insecure-localhost");
            options.addArguments("--no-proxy-server");
            options.addArguments("--disable-features=IsolateOrigins,site-per-process");

            // Make browser appear more human-like
            options.addArguments("--disable-blink-features=AutomationControlled");
            options.setExperimentalOption("excludeSwitches", List.of("enable-automation"));
            options.setExperimentalOption("useAutomationExtension", false);

            // Add realistic user agent
            options.addArguments("--user-agent=Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/120.0.0.0 Safari/537.36");

            driver = new ChromeDriver(options);
        }

        return driver;
    }

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
        JavascriptExecutor js = (JavascriptExecutor) driver;

        for (int i = 0; i < timesToRollDown; i++) {
            Thread.sleep((long) (sleepTime * 1000L));
            js.executeScript("window.scrollTo(0, document.body.scrollHeight);");
        }
        Thread.sleep(1500);
    }



    /**
     * Perform a unique click on a element.
     * 1st it tryes to click on the element.
     * If it fails it hovers it for 0.5 seconds and then try to click on it.
     * If it fails again perform a last try click
     */
    private static void performUniqueClick(WebElement elementToClick) {
        // Click on element
        try {
            elementToClick.click();
        } catch (Exception e) {
            try {
                // Hover over the element first
                Actions actions = new Actions(driver);
                actions.moveToElement(elementToClick).perform();

                // Small pause to let hover effects take place
                Thread.sleep(500);

                // Then try JavaScript click
                ((JavascriptExecutor) driver).executeScript("arguments[0].click();", elementToClick);
            } catch (Exception hoverException) {
                // If hover + JS click fails, try direct JS click as final fallback
                ((JavascriptExecutor) driver).executeScript("arguments[0].click();", elementToClick);
//                System.out.println("Used direct JS click as final fallback");
            }
        }
    }


    /**
     * Waits until 10sec to find an element and then perform a click.
     * @param by locator for the element
     */
    public static void clickOnElement(By by) {
        // Find element
        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(10));
        WebElement elementToClick = wait.until(ExpectedConditions.presenceOfElementLocated(by));
        performUniqueClick(elementToClick);
    }


    /**
     * Perform a click on an element passed
     * @param element to click
     */
    public static void clickOnElement(WebElement element) {
        // Find element
        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(10));
        WebElement elementToClick = wait.until(ExpectedConditions.elementToBeClickable(element));
        performUniqueClick(elementToClick);
    }


    /**
     * Perform a click on an element passed
     * @param buttonToClick to click
     */
    public static void clickOnAddBtn(Object buttonToClick) {
        // Find element
        try {
            WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(10));
            WebElement elementToClick = wait.until(
                    ExpectedConditions.elementToBeClickable(
                            (By) buttonToClick
                    )
            );
            performUniqueClick(elementToClick);
        } catch (Exception _) {} // Ignore exceptions
    }


    /**
     * Clicks on the given element multiple times with a delay between each click.
     *
     * @param element            The element to click. Can be either a {@link By} locator or a {@link WebElement}.
     * @param numberOfIterations Number of times the element should be clicked.
     * @param sleepTime          Time in seconds to wait between clicks.
     * @throws IllegalArgumentException if the element is neither a By nor a WebElement.
     */
    public static void clickOnElementMultipleTimes(
            Object element,
            int numberOfIterations,
            double sleepTime)
    {
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

                Thread.sleep((long) (1000L * sleepTime));
                MyDriver.rollDown(1, 0.1);
            }
        } catch (Exception e) {
            System.out.printf("Stopped before completing all the %d clicks.\n", numberOfIterations);
            System.out.printf("Performed only %d clicks.\n", i);
        }
    }

    /**
     * Oppen a new tab with the passing url
     */
    public static void openNewTab(String url) {
        driver.switchTo().newWindow(WindowType.TAB).get(url);
        waitForPageToLoad();
    }

    /**
     * Swtich to a new tab in the index passen
     */
    public static void switchToTab(int index) {
        List<String> tabs = new ArrayList<>(driver.getWindowHandles());
        driver.switchTo().window(tabs.get(index));
        waitForPageToLoad();
    }

    /**
     * Close the current tab
     */
    public static void closeCurrentTab() {
        driver.close();
        switchToTab(0); // default to first tab
        waitForPageToLoad();
    }

    
    public static void quitDriver() {
        if (driver != null) {
            driver.quit();
            driver = null;
        }
    }
}