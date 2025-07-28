package org.example.src.sites.byPage;

import org.example.src.entities.BaseSites.ByPage;
import org.example.src.entities.MyDriver;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.time.Duration;
import java.util.List;
import java.util.Map;

public class Conyers extends ByPage {
    public Conyers() {
        super(
            "Conyers",
            "https://www.conyers.com/people/?keyword&practicearea&location&position&languages&search",
            15,
            3
        );
    }


    @Override
    protected void accessPage(int index) throws InterruptedException {
        String otherUrl = String.format("https://www.conyers.com/people/page/%d/?keyword&practicearea&location&position&languages&search", index + 1);
        String url = (index == 0) ? this.link : otherUrl;
        driver.get(url);
        MyDriver.waitForPageToLoad();
        Thread.sleep(1000);

        if (index > 0) return;

        siteUtl.clickOnAddBtn(By.id("CybotCookiebotDialogBodyLevelButtonLevelOptinAllowAll"));
    }


    @Override
    protected List<WebElement> getLawyersInPage() {
        By[] webRole = {
                By.className("person-title")
        };

        String[] validRoles = {
                "partner",
                "director",
                "counsel",
                "senior associate"
        };

        try {
            // Wait up to 10 seconds for elements to be present
            WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(10));
            List<WebElement> lawyers = wait.until(
                    ExpectedConditions.presenceOfAllElementsLocatedBy(By.className("person-result-info"))
            );

            return siteUtl.filterLawyersInPage(lawyers, webRole, true, validRoles);

        } catch (Exception e) {
            throw new RuntimeException("Failed to find lawyer elements", e);
        }
    }


    private String getLink(WebElement lawyer) {
        By[] byArray = {
            By.className("title"),
                By.cssSelector("a")
        };
        WebElement element = siteUtl.iterateOverBy(byArray, lawyer);
        return element.getAttribute("href");
    }


    private String getName(WebElement lawyer) {
        By[] byArray = {
                By.className("title"),
                By.cssSelector("a")
        };
        WebElement element = siteUtl.iterateOverBy(byArray, lawyer);
        return element.getText();
    }


    private String getRole(WebElement lawyer) {
        By[] byArray = {
                By.className("person-title")
        };
        WebElement element = siteUtl.iterateOverBy(byArray, lawyer);
        return element.getText();
    }


    private String getCountry(WebElement lawyer) {
        By[] byArray = {
                By.className("light")
        };
        WebElement element = siteUtl.iterateOverBy(byArray, lawyer);
        String country = element.getText();

        if (country.equalsIgnoreCase("london")) return "England";
        if (country.equalsIgnoreCase("toronto")) return "Canada";

        country = siteUtl.titleString(country);
        if (country.contains("Cayman Islands") || country.contains("British Virgin Islands")) {
            country = "the " + country;
        }

        return country;
    }


    private String[] getSocials(WebElement lawyer) {
        try {
            List<WebElement> socials = lawyer
                    .findElement(By.className("person-contact-details"))
                    .findElements(By.cssSelector("a"));
            return super.getSocials(socials);

        } catch (Exception e) {
            System.err.println("Error getting socials: " + e.getMessage());
            return new String[]{"", ""};
        }
    }


    @Override
    public Object getLawyer(WebElement lawyer) throws Exception {
        String[] socials = this.getSocials(lawyer);

        return Map.of(
                "link", this.getLink(lawyer),
                "name", this.getName(lawyer),
                "role", this.getRole(lawyer),
                "firm", this.name,
                "country", this.getCountry(lawyer),
                "practice_area", "",
                "email", socials[0],
                "phone", socials[1]
        );
    }
}
