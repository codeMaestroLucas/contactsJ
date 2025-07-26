package org.example.src.entities;

import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.Keys;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.interactions.Actions;

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
     * Waits up to 4 minutes.
     */
    public static void waitForPageToLoad() {
        driver.manage().timeouts().pageLoadTimeout(Duration.ofMinutes(4));

        new org.openqa.selenium.support.ui.WebDriverWait(driver, Duration.ofMinutes(4))
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


    public static void quitDriver() {
        if (driver != null) {
            driver.quit();
            driver = null;
        }
    }
}