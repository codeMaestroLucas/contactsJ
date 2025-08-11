package org.example.src.sites.byPage;

import org.example.src.entities.BaseSites.ByPage;
import org.example.src.entities.MyDriver;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.time.Duration;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class NelliganLaw extends ByPage {
    private final By[] byRoleArray = {
            By.className("jet-listing-dynamic-field__content")
    };


    public NelliganLaw() {
        super(
            "Nelligan Law",
            "https://nelliganlaw.ca/team/",
            1
        );
    }


    protected void accessPage(int index) throws InterruptedException {
        this.driver.get(this.link);
        MyDriver.waitForPageToLoad();
        Thread.sleep(1000L);

        // Click on add btn
        MyDriver.clickOnElement(By.className("cky-btn-accept"));
        Thread.sleep(2000L);

        MyDriver.clickOnElementMultipleTimes(By.id("load_more"), 4, 2);
    }


    protected List<WebElement> getLawyersInPage() {
        String[] validRoles = new String[]{
                "partner",
                "counsel",
        };

        try {
            WebDriverWait wait = new WebDriverWait(this.driver, Duration.ofSeconds(10L));

            List<WebElement> lawyers = wait.until(
                    ExpectedConditions.presenceOfAllElementsLocatedBy(
                        By.cssSelector("div.jet-listing-grid__item")
                    )
            );

            // Filter out elements that don't contain the desired <a> element
            List<WebElement> filtered = new ArrayList<>();
            for (WebElement lawyer : lawyers) {
                try {
                    lawyer.findElement(byRoleArray[0]);
                    filtered.add(lawyer);
                } catch (Exception ignored) {
                    // Skip
                }
            }

            return this.siteUtl.filterLawyersInPage(filtered, byRoleArray, false, validRoles);

        } catch (Exception e) {
            throw new RuntimeException("Failed to find lawyer elements", e);
        }
    }


    private String getLink(WebElement lawyer) {
        By[] byArray = new By[]{
                By.cssSelector("h2 > a[href^='https://nelliganlaw.ca/team/']")
        };
        WebElement element = this.siteUtl.iterateOverBy(byArray, lawyer);
        return element.getAttribute("href");
    }


    private String getName(WebElement lawyer) {
        By[] byArray = new By[]{
                By.cssSelector("h2 > a[href^='https://nelliganlaw.ca/team/']")
        };
        WebElement element = this.siteUtl.iterateOverBy(byArray, lawyer);
        return element.getText();
    }


    private String getRole(WebElement lawyer) {
        WebElement element = this.siteUtl.iterateOverBy(byRoleArray, lawyer);
        return element.getText();
    }


    private String getEmail(WebElement lawyer) {
        By[] byArray = new By[]{
                By.cssSelector("a[href^='mailto:']")
        };
        WebElement element = this.siteUtl.iterateOverBy(byArray, lawyer);
        return element.getAttribute("href");
    }


    public Object getLawyer(WebElement lawyer) throws Exception {
        return Map.of(
            "link", this.getLink(lawyer),
            "name", this.getName(lawyer),
            "role", this.getRole(lawyer),
            "firm", this.name,
            "country", "Canada",
            "practice_area", "",
            "email", this.getEmail(lawyer),
            "phone", "6132388080"
        );
    }
}
