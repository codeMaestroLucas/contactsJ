package org.example.src.sites.byPage;

import org.example.exceptions.LawyerExceptions;
import org.example.src.entities.BaseSites.ByPage;
import org.example.src.entities.MyDriver;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.time.Duration;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class SIRIUS extends ByPage {
    private final By[] byRoleArray = {
            By.cssSelector("p.wixui-rich-text__text")
    };


    public SIRIUS() {
        super(
                "SIRIUS",
                "https://www.siriusadvokater.dk/en/personer",
                1
        );
    }


    protected void accessPage(int index) throws InterruptedException {
        this.driver.get(this.link);
        MyDriver.waitForPageToLoad();
        Thread.sleep(1000L);

        if (index > 0) return;
        MyDriver.clickOnAddBtn(By.cssSelector("button[data-hook='consent-banner-apply-button']"));
    }


    protected List<WebElement> getLawyersInPage() {
        String[] validRoles = new String[]{
                "partner"
        };

        try {
            WebDriverWait wait = new WebDriverWait(this.driver, Duration.ofSeconds(10L));
            List<WebElement> lawyers = wait.until(ExpectedConditions.presenceOfAllElementsLocatedBy(By.cssSelector("div[role='listitem']")));

            // Filter out the first 10 elements and any that are not valid lawyers
            List<WebElement> filteredLawyers = lawyers.stream()
                    .filter(e -> e.findElements(By.cssSelector("h2")).size() > 0) // Basic check for a lawyer element
                    .collect(Collectors.toList());

            return this.siteUtl.filterLawyersInPage(filteredLawyers, byRoleArray, false, validRoles);
        } catch (Exception e) {
            throw new RuntimeException("Failed to find lawyer elements", e);
        }
    }


    public String getLink(WebElement lawyer) throws LawyerExceptions {
        By[] byArray = new By[]{
                By.className("mGoGm2")
        };
        return extractor.extractLawyerAttribute(lawyer, byArray, "LINK", "href", LawyerExceptions::linkException);
    }


    private String getName(WebElement lawyer) throws LawyerExceptions {
        By[] byArray = new By[]{
                By.cssSelector("h2")
        };
        return extractor.extractLawyerText(lawyer, byArray, "NAME", LawyerExceptions::nameException);
    }


    private String getRole(WebElement lawyer) throws LawyerExceptions {
        return extractor.extractLawyerAttribute(lawyer, byRoleArray, "ROLE", "textContent", LawyerExceptions::roleException);
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
                "country", "Denmark",
                "practice_area", "",
                "email", this.getEmail(lawyer),
                "phone", "4588888585"
        );
    }
}