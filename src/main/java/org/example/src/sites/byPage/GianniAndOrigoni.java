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

import static java.util.Map.entry;

public class GianniAndOrigoni extends ByPage {

    public static final Map<String, String> OFFICE_TO_COUNTRY = Map.ofEntries(
            entry("Rome", "Italy"),
            entry("Milan", "Italy"),
            entry("Bologna", "Italy"),
            entry("Padua", "Italy"),
            entry("Turin", "Italy"),
            entry("Abu Dhabi", "the UAE"),
            entry("Brussels", "Belgium"),
            entry("London", "England"),
            entry("New York", "USA"),
            entry("Hong Kong", "Hong Kong"),
            entry("Shanghai", "China")
    );

    public GianniAndOrigoni() {
        super(
                "Gianni And Origoni",
                "https://www.gop.it/people.php?lang=eng",
                1,
                3
        );
    }

    @Override
    protected void accessPage(int index) throws InterruptedException {
        driver.get(this.link);
        MyDriver.waitForPageToLoad();
        Thread.sleep(1000);

        // Click on search -> loads the lawyers
        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(10));
        wait.until(
                ExpectedConditions.elementToBeClickable(By.className("bottone_people"))
        ).click();

        MyDriver.waitForPageToLoad();
    }

    @Override
    protected List<WebElement> getLawyersInPage() {
        By[] webRole = {
                By.className("campotab2")
        };

        String[] validRoles = {
                "partner",
                "counsel",
                "senior associate"
        };

        try {
            WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(10));
            List<WebElement> lawyers = wait.until(
                    ExpectedConditions.presenceOfAllElementsLocatedBy(By.className("tabella_risu"))
            );
            return siteUtl.filterLawyersInPage(lawyers, webRole, true, validRoles);
        } catch (Exception e) {
            throw new RuntimeException("Failed to find lawyer elements", e);
        }
    }

    private String getLink(WebElement lawyer) {
        By[] byArray = {
                By.className("campotab6"),
                By.cssSelector("a")
        };
        WebElement element = siteUtl.iterateOverBy(byArray, lawyer);
        return element.getAttribute("href");
    }

    private String getName(WebElement lawyer) {
        By[] byArray = {
                By.className("campotab1")
        };
        WebElement element = siteUtl.iterateOverBy(byArray, lawyer);
        return element.getText();
    }

    private String getRole(WebElement lawyer) {
        By[] byArray = {
                By.className("campotab2")
        };
        WebElement element = siteUtl.iterateOverBy(byArray, lawyer);
        return element.getText();
    }

    private String getCountry(WebElement lawyer) {
        By[] byArray = {
                By.className("campotab5")
        };
        WebElement element = siteUtl.iterateOverBy(byArray, lawyer);
        return OFFICE_TO_COUNTRY.getOrDefault(element.getText().trim(), "Unknown");
    }

    private String getPracticeArea(WebElement lawyer) {
        By[] byArray = {
                By.className("campotab3")
        };
        WebElement element = siteUtl.iterateOverBy(byArray, lawyer);
        return element.getText();
    }

    private String[] getSocials(WebElement lawyer) {
        try {
            String phone = lawyer.findElement(By.className("campotab4")).getText();
            String email = lawyer.findElement(By.className("campotab7"))
                    .findElement(By.cssSelector("a"))
                    .getAttribute("href")
                    .replaceAll("\\?.*$", "");

            return new String[] { email, phone };
        } catch (Exception e) {
            System.err.println("Error getting socials for " + getName(lawyer) + ": " + e.getMessage());
            return new String[] { "", "" };
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
                "practice_area", this.getPracticeArea(lawyer),
                "email", socials[0],
                "phone", socials[1]
        );
    }
}
