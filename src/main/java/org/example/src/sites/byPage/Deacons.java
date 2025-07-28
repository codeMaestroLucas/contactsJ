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

public class Deacons extends ByPage {
    public Deacons() {
        super(
            "Deacons",
            "https://www.deacons.com/people",
            1,
            1
        );
    }


    @Override
    protected void accessPage(int index) throws InterruptedException {
        driver.get(this.link);
        MyDriver.waitForPageToLoad();
        Thread.sleep(1000);

        if (index > 0) return;

        siteUtl.clickOnAddBtn(By.id("wt-cli-accept-all-btn"));
    }


    @Override
    protected List<WebElement> getLawyersInPage() {
        By[] webRole = {
                By.className("title")
        };

        String[] validRoles = {
                "partner"
        };

        try {
            // Wait up to 10 seconds for elements to be present
            WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(10));
            List<WebElement> lawyers = wait.until(
                    ExpectedConditions.presenceOfAllElementsLocatedBy(By.className("details"))
            );

            return siteUtl.filterLawyersInPage(lawyers, webRole, true, validRoles);

        } catch (Exception e) {
            throw new RuntimeException("Failed to find lawyer elements", e);
        }
    }


    private String getLink(WebElement lawyer) {
        By[] byArray = {
            By.className("name"),
            By.cssSelector("a")
        };
        WebElement element = siteUtl.iterateOverBy(byArray, lawyer);
        return element.getAttribute("href");
    }


    private String getName(WebElement lawyer) {
        By[] byArray = {
                By.className("name"),
                By.cssSelector("a")
        };
        WebElement element = siteUtl.iterateOverBy(byArray, lawyer);
        return element.getText();
    }


    private String getRole(WebElement lawyer) {
        By[] byArray = {
                By.className("title")
        };
        WebElement element = siteUtl.iterateOverBy(byArray, lawyer);
        return element.getText();
    }


    private String getPracticeArea(WebElement lawyer) {
        By[] byArray = {
                By.className("title")
        };
        WebElement element = siteUtl.iterateOverBy(byArray, lawyer);
        // Split by " | "
        return element.getText().split(" \\| ")[1];
    }


    private String[] getSocials(WebElement lawyer) {
        try {
            List<WebElement> socials = lawyer
                    .findElement(By.className("contact"))
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
                "country", "Hong Kong",
                "practice_area", this.getPracticeArea(lawyer),
                "email", socials[0],
                "phone", socials[1]
        );
    }
}
