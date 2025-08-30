package org.example.src.sites.byPage;

import org.example.exceptions.LawyerExceptions;
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
        MyDriver.clickOnElement(By.className("cky-btn-accept"));
        Thread.sleep(2000L);
        MyDriver.clickOnElementMultipleTimes(By.id("load_more"), 4, 2);
    }


    protected List<WebElement> getLawyersInPage() {
        String[] validRoles = new String[]{"partner", "counsel"};
        try {
            WebDriverWait wait = new WebDriverWait(this.driver, Duration.ofSeconds(10L));
            List<WebElement> lawyers = wait.until(ExpectedConditions.presenceOfAllElementsLocatedBy(By.cssSelector("div.jet-listing-grid__item")));
            List<WebElement> filtered = new ArrayList<>();
            for (WebElement lawyer : lawyers) {
                try {
                    lawyer.findElement(byRoleArray[0]);
                    filtered.add(lawyer);
                } catch (Exception ignored) {
                }
            }
            return this.siteUtl.filterLawyersInPage(filtered, byRoleArray, false, validRoles);
        } catch (Exception e) {
            throw new RuntimeException("Failed to find lawyer elements", e);
        }
    }


    public String getLink(WebElement lawyer) throws LawyerExceptions {
        By[] byArray = new By[]{
                By.cssSelector("h2 > a[href^='https://nelliganlaw.ca/team/']")
        };
        return extractor.extractLawyerAttribute(lawyer, byArray, "LINK", "href", LawyerExceptions::linkException);
    }


    private String getName(WebElement lawyer) throws LawyerExceptions {
        By[] byArray = new By[]{
                By.cssSelector("h2 > a[href^='https://nelliganlaw.ca/team/']")
        };
        return extractor.extractLawyerText(lawyer, byArray, "NAME", LawyerExceptions::nameException);
    }


    private String getRole(WebElement lawyer) throws LawyerExceptions {
        return extractor.extractLawyerText(lawyer, byRoleArray, "ROLE", LawyerExceptions::roleException);
    }


    private String getEmail(WebElement lawyer) throws LawyerExceptions {
        By[] byArray = new By[]{
                By.cssSelector("a[href^='mailto:']")
        };
        return extractor.extractLawyerAttribute(lawyer, byArray, "EMAIL", "href", LawyerExceptions::emailException);
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