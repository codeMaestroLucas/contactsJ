package org.example.src.utils;

import org.example.exceptions.LawyerExceptions;
import org.example.src.entities.BaseSites.SiteUtils;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;

import java.util.Objects;

public class Extractor {
    private final SiteUtils siteUtl;
    private static Extractor INSTANCE;


    private Extractor() {
        this.siteUtl = SiteUtils.getINSTANCE();
    }

    public static Extractor getINSTANCE() {
        if (INSTANCE == null) {
            INSTANCE = new Extractor();
        }
        return INSTANCE;
    }


    /**
     * Generic method to extract data from a WebElement with proper exception handling
     * @param lawyer The WebElement containing the lawyer data
     * @param locators Array of By locators to find the element
     * @param fieldName Name of the field for exception messages
     * @param useAttribute If true, gets attribute value; if false, gets text content
     * @param attributeName Name of attribute to get (only used if useAttribute is true)
     * @param exceptionSupplier Function that creates the appropriate exception
     * @return The extracted string value
     */
    private String extractLawyerField(
            WebElement lawyer, By[] locators, String fieldName,
            boolean useAttribute, String attributeName,
            java.util.function.Function<String, LawyerExceptions> exceptionSupplier)
            throws LawyerExceptions
    {
        try {
            WebElement element = this.siteUtl.iterateOverBy(locators, lawyer);
            if (element == null) {
                throw exceptionSupplier.apply("Element not found for " + fieldName);
            }

            // Extract the data
            String value;
            if (!Objects.isNull(attributeName) && attributeName.equals("outerHTML")) {
                value = siteUtl.getContentFromTag(element);
            } else {
                value = useAttribute ? element.getAttribute(attributeName) : element.getText();
            }

            if (value == null || value.isBlank()) {
                throw exceptionSupplier.apply(value);
            }


            return value.replaceAll("[\\t\\n]", " ").trim();

        } catch (LawyerExceptions e) {
            if ((fieldName.equals("EMAIL"))) {
                throw e;
            } else {
                if (!fieldName.equals("PRACTICE AREA")) System.err.println(e.getMessage());
                return "";
            }

        } catch (Exception e) {
            throw exceptionSupplier.apply("Element not found for " + fieldName);
        }
    }


    /**
     * Convenience method for getting text content
     */
    public String extractLawyerText(
            WebElement lawyer, By[] locators, String fieldName,
            java.util.function.Function<String, LawyerExceptions> exceptionSupplier)
            throws LawyerExceptions
    {
        String value = extractLawyerField(lawyer, locators, fieldName, false, null, exceptionSupplier);
        if (value.isBlank()) {
            System.out.println("\t$$ Could not extract lawyer text for " + fieldName);
            value = extractLawyerAttribute(lawyer, locators, fieldName, "textContent", exceptionSupplier);
        }
        return value;
    }


    /**
     * Convenience method for getting attribute content
     */
    public String extractLawyerAttribute(
            WebElement lawyer, By[] locators, String fieldName, String attributeName,
            java.util.function.Function<String, LawyerExceptions> exceptionSupplier)
            throws LawyerExceptions
    {
        return extractLawyerField(lawyer, locators, fieldName, true, attributeName, exceptionSupplier);
    }
}
