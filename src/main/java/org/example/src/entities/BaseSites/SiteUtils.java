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
        System.out.println("  Role: " + details.get("role"));
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
     * Checks if a determinate role is inside the valid roles Array
     * @param role string to be verified
     * @param validRoles array of valid roles
     * @return true if exists, false if not
     */
    public boolean isValidPosition(String role, String[] validRoles) {
        role = role.toLowerCase().trim();

        for (String validRole : validRoles) {
            if (role.contains(validRole)) {
                return true;
            }
        }

        return false;
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
                        : element.getAttribute("outerHTML");


                role = role
                        .replaceAll("[\\n\\t]", "") // Remove all "\n\t"
                        .replace("-", " ")       // Remove all "-"
                        .replaceAll("\\s+", " ")   // Remove all empty duplicated spaces between words
                        .toLowerCase().trim();

                for (String word : validRoles) {
                    if (role.contains(word.toLowerCase().trim())) {
                        validLawyers.add(lawyer);
                        break;
                    }
                }

            } catch (Exception ignored) {
//                throw ignored;
                System.out.println(ignored.getMessage());
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

    /**
     * Returns the country name based on the given office location.
     * This method cleans the input office name by removing all non-alphabetic characters,
     * converting it to lowercase, and then uses it to look up the corresponding country
     * in the provided {@code OFFICE_TO_COUNTRY} map.
     *
     * @param OFFICE_TO_COUNTRY a map that links cleaned office names to country names
     * @param element element to collect the text by text or HTML
     * @return the country name if found in the map; otherwise, the cleaned office name itself
     */
    public String getCountryBasedInOffice(Map<String, String> OFFICE_TO_COUNTRY, WebElement element) {
        String officeToCheck = element.getText();
        if (officeToCheck.isEmpty() || officeToCheck.equals(" ")) officeToCheck = getContentFromTag(element);

        officeToCheck = officeToCheck.replaceAll("[^A-Za-z]", " ").toLowerCase().trim();
        return OFFICE_TO_COUNTRY.getOrDefault(officeToCheck, officeToCheck);
    }

    /**
     * Returns the country name based on the given office location.
     * @param officeToCountry a map that links normalized office names to country names
     * @param officeToCheck   the office name to check
     * @return the country name if found in the map; otherwise, the normalized office name itself
     */
    public String getCountryBasedInOffice(Map<String, String> officeToCountry, String officeToCheck, String defaultValue) {
        if (officeToCheck == null || officeToCheck.isBlank()) {
            return "";
        }

        String normalizedOffice = officeToCheck
                .replaceAll("[^A-Za-z]", " ")
                .replace("nbsp", " ")
                .toLowerCase()
                .replaceAll("\\s+", " ") // collapse multiple spaces
                .trim();

        // 1. Try full match
        if (officeToCountry.containsKey(normalizedOffice)) {
            return officeToCountry.get(normalizedOffice);
        }

        // 2. Try word-by-word
        String[] words = normalizedOffice.split(" ");
        for (String word : words) {
            if (officeToCountry.containsKey(word)) {
                return officeToCountry.get(word);
            }
        }

        // 3. Nothing found, return normalized string or the default value
        return defaultValue.isEmpty() ? normalizedOffice : defaultValue;
    }


    /**
     * Returns the country name based on the prefix of a phone number.
     * After normalization, the method checks if the phone number starts with any
     * of the prefixes defined in {@code officeToCountry}. If a match is found,
     * the corresponding country is returned. Otherwise, the {@code defaultValue}
     * is returned (if provided), or the cleaned phone number itself.
     *
     * @param officeToCountry a map that links phone number prefixes to country names
     * @param phone           the phone number to check
     * @param defaultValue    a fallback value to return when no match is found;
     *                        often used for the most common country to avoid
     *                        overpopulating the map
     * @return the country name if a prefix match is found; otherwise {@code defaultValue}
     *         (if not empty), or the cleaned phone number itself
     */
    public String getCountryBasedInOfficeByPhone(Map<String, String> officeToCountry, String phone, String defaultValue) {
        // Remove all non-digit characters and leading zeros
        phone = phone.replaceAll("\\D", "").replaceFirst("^0+", "");

        String toReturn = defaultValue.isEmpty() ? phone : defaultValue;

        for (String key : officeToCountry.keySet()) {
            if (phone.startsWith(key)) {
                toReturn = officeToCountry.get(key);
                break;
            }
        }
        return toReturn;
    }

}
