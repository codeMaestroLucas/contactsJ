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

public class Roschier extends ByPage {
    private final By[] byRoleArray = {
            By.className("card__body"),
            By.className("card__text")
    };

    public Roschier() {
        super(
            "Roschier",
            "https://www.roschier.com/people",
            1
        );
    }


    @Override
    protected void accessPage(int index) throws InterruptedException {
        driver.get(this.link);
        MyDriver.waitForPageToLoad();
        Thread.sleep(1000);


        MyDriver.clickOnElement(By.id("CybotCookiebotDialogBodyLevelButtonLevelOptinAllowAll"));

        // Actually it has more than 30
        MyDriver.clickOnElementMultipleTimes(
                By.cssSelector(".text-center .wp-block-button .wp-block-button__link"),
                5, 5
        );
    }


    @Override
    protected List<WebElement> getLawyersInPage() {
        String[] validRoles = new String[]{
                "partner",
                "senior associate",
        };

        try {
            WebDriverWait wait = new WebDriverWait(this.driver, Duration.ofSeconds(10L));

            List<WebElement> lawyers = wait.until(
                    ExpectedConditions.presenceOfAllElementsLocatedBy(
                            By.className("card--person")
                    )
            );
            return this.siteUtl.filterLawyersInPage(lawyers, byRoleArray, false, validRoles);

        } catch (Exception e) {
            throw new RuntimeException("Failed to find lawyer elements", e);
        }
    }


    private String getLink(WebElement lawyer) {
        By[] byArray = {
                By.cssSelector("a[href^='https://www.roschier.com/people/']")
        };
        WebElement element = siteUtl.iterateOverBy(byArray, lawyer);
        return element.getAttribute("href");
    }


    private String getName(WebElement lawyer) {
        By[] byArray = {
                By.className("card__body"),
                By.className("card__title")
        };
        WebElement element = siteUtl.iterateOverBy(byArray, lawyer);
        return siteUtl.getContentFromTag(element.getAttribute("outerHTML"));
    }


    private String getRole(WebElement lawyer) {
        WebElement element = siteUtl.iterateOverBy(byRoleArray, lawyer);
        return siteUtl.getContentFromTag(element.getAttribute("outerHTML"));
    }


    private String[] getSocials(WebElement lawyer) {
        try {
            List<WebElement> socials = lawyer
                    .findElement(By.className("card__footer"))
                    .findElement(By.className("card__contact"))
                    .findElements(By.cssSelector("a"));
            return super.getSocials(socials, false);

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
                "country", "Ireland",
                "practice_area", "",
                "email", socials[0],
                "phone", socials[1]
        );
    }
}
