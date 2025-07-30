package org.example.src.sites._standingBy;

import org.example.src.entities.BaseSites.ByPage;
import org.example.src.entities.MyDriver;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.time.Duration;
import java.util.List;
import java.util.Map;

public class Fasken extends ByPage {
    public Fasken() {
        super(
            "Fasken",
            "https://www.fasken.com/en/people",
            5,
            1
        );
    }


    @Override
    protected void accessPage(int index) throws InterruptedException {
        String otherUrl = "https://www.fasken.com/en/people#firstResult=" + (15 * index);
        String url = (index == 0) ? this.link : otherUrl;
        driver.get(url);
        MyDriver.waitForPageToLoad();
        Thread.sleep(1000);

        if (index > 0) return;

        siteUtl.clickOnAddBtn(By.id("onetrust-accept-btn-handler"));
    }


    @Override
    protected List<WebElement> getLawyersInPage() {
        By[] webRole = {
                By.className("atomic-result-section-details"),
                By.className("title-metadata-people-role"),
                By.cssSelector("ul > li > slot")
        };

        String[] validRoles = {
                "partner",
                "counsel",
                "senior associate"
        };

        try {
            // Wait up to 10 seconds for elements to be present
            WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(10));
            List<WebElement> lawyers = wait.until(
                    ExpectedConditions.presenceOfAllElementsLocatedBy(By.cssSelector("#layout-wrapper > atomic-layout-section:nth-of-type(4) > atomic-layout-section:nth-of-type(1) > atomic-result-list div div div:nth-of-type(1) atomic-result div:nth-of-type(1) div div div div"))
            );

            return siteUtl.filterLawyersInPage(lawyers, webRole, true, validRoles);

        } catch (Exception e) {
            throw new RuntimeException("Failed to find lawyer elements", e);
        }
    }


    private String getLink(WebElement lawyer) {
        By[] byArray = {
            By.className("atomic-result-tile-link")
        };
        WebElement element = siteUtl.iterateOverBy(byArray, lawyer);
        return element.getAttribute("href");
    }


    private String getName(WebElement lawyer) {
        By[] byArray = {
                By.className("atomic-result-section-details"),
                By.className("notranslate")
        };
        WebElement element = siteUtl.iterateOverBy(byArray, lawyer);
        return element.getText();
    }


    private String getRole(WebElement lawyer) {
        By[] byArray = {
                By.className("atomic-result-section-details"),
                By.className("title-metadata-people-role"),
                By.cssSelector("ul > li > slot")
        };
        WebElement element = siteUtl.iterateOverBy(byArray, lawyer);
        return element.getText();
    }


    private String getCountry(WebElement lawyer) {
        By[] byArray = {
                By.className("hydrated"),
                By.className("bottom-metadata-location"),
                By.cssSelector("ul > li > slot")
        };
        WebElement element = siteUtl.iterateOverBy(byArray, lawyer);
        return element.getText();
    }

    private String getPracticeArea(WebElement lawyer) {
        By[] byArray = {
                By.className("atomic-result-section-details"),
                By.className("title-metadata-people-role"),
                By.cssSelector("ul > li > slot")
        };
        WebElement element = siteUtl.iterateOverBy(byArray, lawyer);
        return element.getText().split("\\|")[1];
    }


    private String[] getSocials(WebElement lawyer) {
        try {
            List<WebElement> socials = lawyer
                    .findElement(By.className("atomic-result-section-details"))
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
                "practice_area", this.getPracticeArea(lawyer),
                "email", socials[0],
                "phone", socials[1]
        );
    }


    public static void main(String[] args) {
        Fasken x = new Fasken();
        x.searchForLawyers();
    }
}
