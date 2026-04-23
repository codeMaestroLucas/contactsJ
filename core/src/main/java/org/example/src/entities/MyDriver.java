package org.example.src.entities;

import org.openqa.selenium.*;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.interactions.Actions;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.Wait;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.time.Duration;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public final class MyDriver {
    private static WebDriver driver;
    private static boolean headless = false;

    private MyDriver() {}

    // ── Setup & lifecycle ─────────────────────────────────────────────────────

    public static void setHeadless(boolean value) {
        headless = value;
    }

    public static synchronized WebDriver getINSTANCE() {
        if (driver == null) {
            ChromeOptions options = new ChromeOptions();
            if (headless) options.addArguments("--headless");
            options.addArguments("--disable-gpu");
            options.addArguments("--ignore-certificate-errors");
            options.addArguments("--disable-web-security");
            options.addArguments("--allow-insecure-localhost");
            options.addArguments("--no-proxy-server");
            options.addArguments("--disable-features=IsolateOrigins,site-per-process");
            options.addArguments("--disable-blink-features=AutomationControlled");
            options.setExperimentalOption("excludeSwitches", List.of("enable-automation"));
            options.setExperimentalOption("useAutomationExtension", false);

            driver = new ChromeDriver(options);
            ((JavascriptExecutor) driver).executeScript(
                    "Object.defineProperty(navigator, 'webdriver', {get: () => undefined})"
            );
        }
        return driver;
    }

    /**
     * Waits for the current page to fully load by checking the `document.readyState`.
     * Waits up to 3min. If a page hasn't loaded by then, it's likely broken or
     * unresponsive — waiting longer just delays the entire execution for no benefit.
     */
    public static void waitForPageToLoad() {
        driver.manage().timeouts().pageLoadTimeout(Duration.ofMinutes(3));
        new WebDriverWait(driver, Duration.ofMinutes(3))
                .until(webDriver -> ((JavascriptExecutor) webDriver)
                        .executeScript("return document.readyState")
                        .equals("complete"));
    }

    /**
     * Quits the current browser and resets the driver to null.
     * The next call to getINSTANCE() will start a fresh Chrome process.
     */
    public static synchronized void restartDriver() {
        if (driver != null) {
            try { driver.quit(); } catch (Exception ignored) {}
            driver = null;
        }
    }

    public static void cleanUpBetweenSites() { TabHelper.cleanUp(); }

    // ── Scroll (delegates) ────────────────────────────────────────────────────

    public static void rollDown(int timesToScroll, double sleepTime) throws InterruptedException {
        ScrollHelper.rollDown(timesToScroll, sleepTime);
    }

    public static void rollDownToBottom(double sleepTime) throws InterruptedException {
        ScrollHelper.rollDownToBottom(sleepTime);
    }

    public static void scrollToBottom(double sleepTime) throws InterruptedException {
        ScrollHelper.scrollToBottom(sleepTime);
    }

    // ── Click (delegates) ─────────────────────────────────────────────────────

    public static void cmdClickOnElement(By by) { ClickHelper.cmdClick(by); }

    public static void cmdClickOnElement(WebElement element) { ClickHelper.cmdClick(element); }

    public static void clickOnElement(By by) { ClickHelper.click(by); }

    public static void clickOnElement(WebElement element) { ClickHelper.click(element); }

    public static void clickOnAddBtn(Object buttonToClick) { ClickHelper.clickAdd(buttonToClick); }

    public static void clickOnElementMultipleTimes(Object element, int numberOfIterations, double sleepTime) {
        ClickHelper.clickMultiple(element, numberOfIterations, sleepTime);
    }

    // ── Tab (delegates) ───────────────────────────────────────────────────────

    public static void openNewTab(String url) { TabHelper.openNewTab(url); }

    public static void openNewTabWithJS(String url) { TabHelper.openNewTabWithJS(url); }

    public static void switchToTab(int index) { TabHelper.switchToTab(index); }

    public static void closeCurrentTab() { TabHelper.closeCurrentTab(); }

    // ── WaitWrapper ───────────────────────────────────────────────────────────

    /** Waits up to 10 seconds before looking up elements. Usage: {@code MyDriver.wait.findElements(by)} */
    public static final WaitWrapper wait = new WaitWrapper(10);

    public static final class WaitWrapper {
        private final int seconds;

        private WaitWrapper(int seconds) {
            this.seconds = seconds;
        }

        public WebElement findElement(By by) {
            return new WebDriverWait(driver, Duration.ofSeconds(seconds))
                    .until(ExpectedConditions.presenceOfElementLocated(by));
        }

        public List<WebElement> findElements(By by) {
            return new WebDriverWait(driver, Duration.ofSeconds(seconds))
                    .until(ExpectedConditions.presenceOfAllElementsLocatedBy(by));
        }
    }

    // ── ScrollHelper ──────────────────────────────────────────────────────────

    private static final class ScrollHelper {

        /**
         * Scrolls down the page to load more elements.
         *
         * @param timesToScroll Number of scroll attempts
         * @param sleepTime     Delay in seconds between scrolls
         */
        static void rollDown(int timesToScroll, double sleepTime) throws InterruptedException {
            JavascriptExecutor js = (JavascriptExecutor) driver;
            for (int i = 0; i < timesToScroll; i++) {
                js.executeScript("window.scrollBy(0, window.innerHeight);");
                Thread.sleep((long) (sleepTime * 1000L));
            }
            Thread.sleep(1500);
        }

        /**
         * Scrolls down incrementally until the page stops growing.
         * Ideal for lazy-loaded pages where the total scroll count is unknown.
         *
         * @param sleepTime delay in seconds between each scroll
         */
        static void rollDownToBottom(double sleepTime) throws InterruptedException {
            JavascriptExecutor js = (JavascriptExecutor) driver;
            long previousHeight = (long) js.executeScript("return document.body.scrollHeight");
            while (true) {
                js.executeScript("window.scrollBy(0, window.innerHeight);");
                Thread.sleep((long) (sleepTime * 1000L));
                long currentHeight = (long) js.executeScript("return document.body.scrollHeight");
                if (currentHeight == previousHeight) break;
                previousHeight = currentHeight;
            }
            Thread.sleep(1500);
        }

        /**
         * Scrolls to the bottom of the page by jump-scrolling until no more content loads.
         *
         * @param sleepTime Delay in seconds between each scroll
         */
        static void scrollToBottom(double sleepTime) throws InterruptedException {
            JavascriptExecutor js = (JavascriptExecutor) driver;
            long previousHeight = (long) js.executeScript("return document.body.scrollHeight");
            while (true) {
                js.executeScript("window.scrollTo(0, document.body.scrollHeight);");
                Thread.sleep((long) (sleepTime * 1000L));
                long currentHeight = (long) js.executeScript("return document.body.scrollHeight");
                if (currentHeight == previousHeight) break;
                previousHeight = currentHeight;
            }
            Thread.sleep(1500);
        }
    }

    // ── ClickHelper ───────────────────────────────────────────────────────────

    private static final class ClickHelper {

        /**
         * Tries click → hover+JS → JS fallback.
         */
        private static void performUniqueClick(WebElement elementToClick) {
            try {
                elementToClick.click();
            } catch (Exception e) {
                try {
                    Actions actions = new Actions(driver);
                    actions.moveToElement(elementToClick).perform();
                    Thread.sleep(500);
                    ((JavascriptExecutor) driver).executeScript("arguments[0].click();", elementToClick);
                } catch (Exception hoverException) {
                    ((JavascriptExecutor) driver).executeScript("arguments[0].click();", elementToClick);
                }
            }
        }

        /**
         * CMD+Clicks (Mac) / CTRL+Clicks (Windows/Linux) the element and switches to the new tab.
         * If the page ignores the modifier and navigates in the same tab, captures the URL,
         * goes back, and opens it in a new tab manually.
         */
        static void cmdClick(WebElement element) {
            WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(10));
            WebElement elementToClick = wait.until(ExpectedConditions.elementToBeClickable(element));
            String previousLink = driver.getCurrentUrl();

            Keys modifier = System.getProperty("os.name").toLowerCase().contains("mac")
                    ? Keys.COMMAND
                    : Keys.CONTROL;

            java.util.Set<String> handlesBefore = driver.getWindowHandles();

            try {
                new Actions(driver)
                        .keyDown(modifier)
                        .click(elementToClick)
                        .keyUp(modifier)
                        .perform();
            } catch (Exception e) {
                try {
                    new Actions(driver)
                            .moveToElement(elementToClick)
                            .pause(Duration.ofMillis(500))
                            .keyDown(modifier)
                            .click(elementToClick)
                            .keyUp(modifier)
                            .perform();
                } catch (Exception hoverException) {
                    ((JavascriptExecutor) driver).executeScript(
                            "arguments[0].dispatchEvent(new MouseEvent('click', " +
                            "{bubbles:true, cancelable:true, metaKey:true, ctrlKey:true}));",
                            elementToClick
                    );
                }
            }

            boolean newTabOpened = false;
            try {
                new WebDriverWait(driver, Duration.ofSeconds(5))
                        .until(d -> d.getWindowHandles().size() > handlesBefore.size());
                newTabOpened = true;
            } catch (Exception ignored) {}

            if (newTabOpened) {
                String newHandle = driver.getWindowHandles().stream()
                        .filter(h -> !handlesBefore.contains(h))
                        .findFirst()
                        .orElseThrow(() -> new RuntimeException("CMD+Click did not open a new tab"));
                driver.switchTo().window(newHandle);
                waitForPageToLoad();
            } else {
                waitForPageToLoad();
                String navigatedUrl = driver.getCurrentUrl();
                if (!navigatedUrl.equals(previousLink)) {
                    driver.navigate().back();
                    waitForPageToLoad();
                    TabHelper.openNewTab(navigatedUrl);
                }
            }
        }

        static void cmdClick(By by) {
            WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(10));
            WebElement element = wait.until(ExpectedConditions.presenceOfElementLocated(by));
            cmdClick(element);
        }

        static void click(By by) {
            WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(10));
            WebElement elementToClick = wait.until(ExpectedConditions.presenceOfElementLocated(by));
            performUniqueClick(elementToClick);
        }

        static void click(WebElement element) {
            WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(10));
            WebElement elementToClick = wait.until(ExpectedConditions.elementToBeClickable(element));
            performUniqueClick(elementToClick);
        }

        static void clickAdd(Object buttonToClick) {
            try {
                WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(10));
                WebElement elementToClick = wait.until(
                        ExpectedConditions.elementToBeClickable((By) buttonToClick)
                );
                performUniqueClick(elementToClick);
            } catch (Exception ignored) {}
        }

        static void clickMultiple(Object element, int numberOfIterations, double sleepTime) {
            int i = 0;
            try {
                for (i = 0; i < numberOfIterations; i++) {
                    if (element instanceof By) {
                        click((By) element);
                    } else if (element instanceof WebElement) {
                        click((WebElement) element);
                    } else {
                        throw new IllegalArgumentException("Element must be either a By or WebElement");
                    }
                    Thread.sleep((long) (1000L * sleepTime));
                    ScrollHelper.rollDown(1, 0.1);
                }
            } catch (Exception e) {
                System.out.printf("Stopped before completing all the %d clicks.%n", numberOfIterations);
                System.out.printf("Performed only %d clicks.%n", i);
            }
        }
    }

    // ── TabHelper ─────────────────────────────────────────────────────────────

    private static final class TabHelper {

        static void openNewTab(String url) {
            driver.switchTo().newWindow(WindowType.TAB).get(url);
            waitForPageToLoad();
        }

        /**
         * Opens a new tab via JavaScript's {@code window.open} — more reliable for
         * JS-heavy / headless-Chrome environments where the Selenium-native call may be ignored.
         */
        static void openNewTabWithJS(String url) {
            java.util.Set<String> handlesBefore = driver.getWindowHandles();
            ((JavascriptExecutor) driver).executeScript("window.open(arguments[0], '_blank');", url);

            new WebDriverWait(driver, Duration.ofSeconds(10))
                    .until(d -> d.getWindowHandles().size() > handlesBefore.size());

            String newHandle = driver.getWindowHandles().stream()
                    .filter(h -> !handlesBefore.contains(h))
                    .findFirst()
                    .orElseThrow(() -> new RuntimeException("window.open did not create a new tab"));

            driver.switchTo().window(newHandle);
            waitForPageToLoad();
        }

        static void switchToTab(int index) {
            List<String> tabs = new ArrayList<>(driver.getWindowHandles());
            driver.switchTo().window(tabs.get(index));
            waitForPageToLoad();
        }

        static void closeCurrentTab() {
            driver.close();
            switchToTab(0);
            waitForPageToLoad();
        }

        /**
         * Cleans up browser state between site executions without closing the browser.
         * Closes extra tabs, clears cookies, and navigates to about:blank.
         */
        static void cleanUp() {
            if (driver == null) return;
            try {
                List<String> handles = new ArrayList<>(driver.getWindowHandles());
                if (handles.size() > 1) {
                    String firstHandle = handles.get(0);
                    for (int i = handles.size() - 1; i >= 1; i--) {
                        driver.switchTo().window(handles.get(i));
                        driver.close();
                    }
                    driver.switchTo().window(firstHandle);
                }
                driver.manage().deleteAllCookies();
                driver.get("about:blank");
            } catch (Exception e) {
                System.err.println("Warning: Could not fully clean up browser between sites: " + e.getMessage());
            }
        }
    }
}
