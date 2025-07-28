package org.example.src.sites.byNewPage;

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

/**
 * Transform in NewPage
 */
public class GideLoyretteNouel extends ByPage {
    public static final Map<String, String> OFFICE_TO_COUNTRY = Map.ofEntries(
            entry("Algiers", "Algeria"),
            entry("Brussels", "Belgium"),
            entry("Casablanca", "Morocco"),
            entry("Istanbul", "Turkey"),
            entry("London", "England"),
            entry("New York City", "EUA"),
            entry("Paris", "France"),
            entry("Shanghai", "China"),
            entry("Tunis", "Tunisia"),
            entry("Warsaw", "Poland")
    );

    public GideLoyretteNouel() {
        super(
            "Gide Loyrette Nouel",
            "https://www.gide.com/en/lawyers/?",
            1,
            1
        );
    }


    @Override
    protected void accessPage(int index) throws InterruptedException {
        driver.get(this.link);
        MyDriver.waitForPageToLoad();
        Thread.sleep(1000);

        // Click on filter to load lawyers
        if (index == 0) {
            siteUtl.clickOnAddBtn(By.id("onetrust-accept-btn-handler"));

            driver.findElement(By.className("filters"))
                  .findElement(By.className("list"))
                  .findElement(By.cssSelector("li:last-child"))
                  .click();

            Thread.sleep(2000);

            driver.findElement(By.id("filter-offices"))
                  .findElement(By.className("inner"))
                  .findElement(By.cssSelector("ul > li"))
                  .click();

            Thread.sleep(5000);

        } else { // Click on next page
            driver.findElement(By.className("pagination"))
                  .findElement(By.className("next"))
                  .click();
            Thread.sleep(5000);
        }

    }


    @Override
    protected List<WebElement> getLawyersInPage() {
        By[] webRole = {
                By.cssSelector("div")
        };

        String[] validRoles = {
                "partner",
                "counsel"
        };

        try {
            // Wait up to 10 seconds for elements to be present
            WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(10));
            List<WebElement> lawyers = wait.until(
                    ExpectedConditions.presenceOfAllElementsLocatedBy(By.className("content"))
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
                By.className("title-4")
        };
        WebElement element = siteUtl.iterateOverBy(byArray, lawyer);
        return element.getText();
    }


    private String getRole(WebElement lawyer) {
        By[] byArray = {
                By.cssSelector("div")
        };
        WebElement element = siteUtl.iterateOverBy(byArray, lawyer);
        return element.getText();
    }


    private String getCountry(WebElement lawyer) {
        By[] byArray = {
                By.className("bureau")
        };
        WebElement element = siteUtl.iterateOverBy(byArray, lawyer);
        return OFFICE_TO_COUNTRY.get(element.getText());
    }


    private String getPracticeArea(WebElement lawyer) {
        By[] byArray = {
                By.className("expertises"),
                By.cssSelector("li")
        };
        WebElement element = siteUtl.iterateOverBy(byArray, lawyer);
        return element.getText();
    }


    private String[] getSocials(WebElement lawyer) {
        try {
            List<WebElement> socials = lawyer
                    .findElement(By.className(""))
                    .findElements(By.cssSelector(""));
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
                "practice_area", this.getPracticeArea(lawyer),
                "email", socials[0],
                "phone", socials[1]
        );
    }


    public static void main(String[] args) {
        GideLoyretteNouel x = new GideLoyretteNouel();
        x.searchForLawyers();
    }
}
