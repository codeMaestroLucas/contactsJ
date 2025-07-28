package org.example.src.entities.BaseSites;

import org.example.src.entities.MyDriver;
import org.openqa.selenium.*;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.time.Duration;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class SiteUtils {
    private WebDriver driver;
    private static SiteUtils INSTANCE;

    private SiteUtils() {
        this.driver = MyDriver.getINSTANCE();
    }

    public static SiteUtils getINSTANCE() {
        if (INSTANCE == null) {
            INSTANCE = new SiteUtils();
        }
        return INSTANCE;
    }


    /**
     * Prints an error when lawyer details are invalid.
     */
    public void printInvalidLawyer(Map<String, String> details, int index, int page, String firm) {
        System.out.printf("Error reading %dth lawyer at page %d of firm %s.%nSkipping...%n",
                index + 1, page + 1, firm);
        System.out.println("  Link: " + details.get("link"));
        System.out.println("  Name: " + details.get("name"));
        System.out.println("  Practice Area: " + details.get("practice_area"));
        System.out.println("  Email: " + details.get("email"));
        System.out.println("  Phone: " + details.get("phone"));
        System.out.println("  Country: " + details.get("country"));
    }


    /**
     * Search in the webElement sent by the array of By's
     * @param byArray used to search in the element
     * @param webElement
     */
    public WebElement iterateOverBy(By[] byArray, WebElement webElement) {
        WebElement current = webElement;

        for (By locator : byArray) {
            try {
                WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(3));
                WebElement parent = current;

                // Wait until the child element is found inside the current parent
                current = wait.until(driver -> parent.findElement(locator));
            } catch (Exception e) {
                System.err.println("Failed to find element with locator: " + locator);
                current = null;
            }
        }

        return current;
    }


    /**
     * Filters lawyers from the provided list based on their role, returning only valid lawyers.
     *
     * @param lawyersInPage List of WebElements representing all lawyers on the page.
     * @param webRole       Array of locators for the role element within a lawyer element.
     * @param byText        If true, uses `getText()`; otherwise, uses `getAttribute("outerHTML")`.
     * @return Filtered list of lawyers WebElements.
     */
    public List<WebElement> filterLawyersInPage(List<WebElement> lawyersInPage, By[] webRole,
                                                   boolean byText, String[] validRoles) {
        List<WebElement> validLawyers = new ArrayList<>();

        for (WebElement lawyer : lawyersInPage) {
            try {
                WebElement element = iterateOverBy(webRole, lawyer);

                String role = byText
                        ? element.getText()
                        : element.getAttribute("outerHTML")
                        .replaceAll("[\\n\\t]", "");
                role = role.toLowerCase().trim();

                if (!byText) role = getContentFromTag(role);

                for (String word : validRoles) {
                    if (role.contains(word.toLowerCase().trim())) {
                        validLawyers.add(lawyer);
                        break;
                    }
                }

            } catch (Exception ignored) {
                throw ignored;
            }
        }
        return validLawyers;
    }


    /**
     * Extracts content from inside an HTML tag.
     *
     * @param tag String containing the HTML tag.
     * @return Content inside the tag or null if not found.
     */
    public String getContentFromTag(String tag) {
        Matcher matcher =
                Pattern.compile(">([^<>]+)<").matcher(tag);
        return matcher.find() ? matcher.group(1) : null;
    }


    /**
     * Extracts content from inside an HTML tag.
     *
     * @param element WebElement containing the HTML tag.
     * @return Content inside the tag or null if not found.
     */
    public String getContentFromTag(WebElement element) {
        String tag = element.getAttribute("outerHTML");
        Matcher matcher =
                Pattern.compile(">([^<>]+)<").matcher(tag);
        return matcher.find() ? matcher.group(1) : null;
    }


    /**
     * Waits until 10sec for the presence of the add btn and then click on it.
     * @param by direct path for the addBtn.
     */
    public void clickOnAddBtn(By by) {
        try {
            // Wait up to 10 seconds for elements to be present
            WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(10));
            WebElement btn = wait.until(ExpectedConditions.presenceOfElementLocated(by));
            btn.click();

        } catch (Exception _) {}
    }


    /**
     * Title-cases the string passed.
     */
    public String titleString(String text) {
        if (text == null || text.isEmpty()) {
            return text;
        }

        StringBuilder titleToReturn = new StringBuilder();
        text = text.toLowerCase();

        String[] words = text.split("\\s+");
        for (int i = 0; i < words.length; i++) {
            String word = words[i];
            if (!word.isEmpty()) {
                titleToReturn
                        .append(Character.toUpperCase(word.charAt(0)))
                        .append(word.substring(1));
            }
            if (i < words.length - 1) {
                titleToReturn.append(" ");
            }
        }

        return titleToReturn.toString();
    }
}
