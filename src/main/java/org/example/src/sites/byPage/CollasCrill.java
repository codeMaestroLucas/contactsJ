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
import java.util.Objects;

public class CollasCrill extends ByPage {
    private final Map<String, String> countries = Map.of(
            "bvi", "the British Virgin Islands",
            "cayman", "the Cayman Islands",
            "london", "England",
            "guernsey", "Guernsey",
            "jersey", "Jersey"
    );


    public CollasCrill() {
        super(
            "Collas Crill",
            "https://www.collascrill.com/people/all-locations/partners/all-practices/",
            1,
            3
        );
    }


    @Override
    protected void accessPage(int index) throws InterruptedException {
        driver.get(this.link);
        MyDriver.waitForPageToLoad();
        Thread.sleep(1000);

        if (index > 0) return;

        siteUtl.clickOnAddBtn(By.className("cky-btn-accept"));
    }


    @Override
    protected List<WebElement> getLawyersInPage() {
        By[] webRole = {
                By.className("title")
        };

        String[] validRoles = {
                "partner",
                "counsel",
                "senior associate",
                "chairman"
        };

        try {
            // Wait up to 10 seconds for elements to be present
            WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(10));
            List<WebElement> lawyers = wait.until(
                    ExpectedConditions.presenceOfAllElementsLocatedBy(By.className("overlay-contents"))
            );

            return siteUtl.filterLawyersInPage(lawyers, webRole, false, validRoles);

        } catch (Exception e) {
            throw new RuntimeException("Failed to find lawyer elements", e);
        }
    }


    private String getLink(WebElement lawyer) {
        By[] byArray = {
            By.cssSelector("a")
        };
        WebElement element = siteUtl.iterateOverBy(byArray, lawyer);
        return element.getAttribute("href");
    }


    private String getName(WebElement lawyer) {
        By[] byArray = {
                By.className("name-desktop"),
        };
        WebElement element = siteUtl.iterateOverBy(byArray, lawyer);
        String textToReturn = element.getText();

        if (textToReturn.isEmpty()) {
            return siteUtl.getContentFromTag(element.getAttribute("outerHTML"));
        }

        return textToReturn;
    }


    private String getRole(WebElement lawyer) {
        By[] byArray = {
                By.className("title")
        };
        WebElement element = siteUtl.iterateOverBy(byArray, lawyer);
        String textToReturn = element.getText();

        if (textToReturn.isEmpty()) {
            return siteUtl.getContentFromTag(element.getAttribute("outerHTML"));
        }

        return textToReturn;
    }


    private String getCountry(WebElement lawyer) {
        By[] byArray = {
                By.className("location")
        };
        WebElement element = siteUtl.iterateOverBy(byArray, lawyer);
        String country = element.getText();

        if (country.isEmpty()) {
            country = siteUtl.getContentFromTag(element.getAttribute("outerHTML")).toLowerCase().trim();
        }

        return countries.getOrDefault(country.toLowerCase(), "-----");
    }


    private String[] getSocials(WebElement lawyer) {
        try {
            List<WebElement> socials = lawyer
                    .findElement(By.className("icons"))
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
