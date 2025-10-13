package org.example.src.sites._standingBy.otherIssues;

import org.example.exceptions.LawyerExceptions;
import org.example.src.entities.BaseSites.ByPage;
import org.example.src.entities.MyDriver;
import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.time.Duration;
import java.util.List;
import java.util.Map;

public class Cederquist extends ByPage {

    private final By[] byRoleArray = {
            By.cssSelector("div.text-xs > div:first-child")
    };

    public Cederquist() {
        super(
                "Cederquist",
                "https://www.cederquist.se/en/employees/",
                1
        );
    }

    @Override
    protected void accessPage(int index) throws InterruptedException {
        this.driver.get(this.link);
        MyDriver.waitForPageToLoad();
        Thread.sleep(1000L);
        MyDriver.clickOnAddBtn(By.id("CybotCookiebotDialogBodyLevelButtonLevelOptinAllowAll"));

        // Scroll until footer is visible or max attempts reached
        By footer = By.xpath("//*[@id='__next']/div[2]/footer");


        // Cant roll down to load more lawyers
        int maxTries = 20;
        for (int i = 0; i < maxTries; i++) {
            ((JavascriptExecutor) driver).executeScript("arguments[0].scrollIntoView(true);", footer);
            Thread.sleep(500L);
        }

    }

    @Override
    protected List<WebElement> getLawyersInPage() {
        String[] validRoles = new String[]{
                "partner", "counsel", "senior associate"
        };

        try {
            WebDriverWait wait = new WebDriverWait(this.driver, Duration.ofSeconds(10L));
            List<WebElement> lawyers = wait.until(
                    ExpectedConditions.presenceOfAllElementsLocatedBy(
                            By.cssSelector("a.flex-1")
                    )
            );
            return this.siteUtl.filterLawyersInPage(lawyers, byRoleArray, false, validRoles);
        } catch (Exception e) {
            throw new RuntimeException("Failed to find lawyer elements", e);
        }
    }

    private String getLink(WebElement lawyer) {
        return lawyer.getAttribute("href");
    }

    private String getName(WebElement lawyer) throws LawyerExceptions {
        By[] byArray = new By[]{
                By.className("title-article")
        };
        return extractor.extractLawyerAttribute(lawyer, byArray, "NAME", "textContent", LawyerExceptions::nameException);
    }

    private String getRole(WebElement lawyer) throws LawyerExceptions {
        return extractor.extractLawyerAttribute(lawyer, byRoleArray, "ROLE", "textContent", LawyerExceptions::roleException);
    }

    private String[] getSocials(WebElement lawyer) {
        try {
            List<WebElement> socials = lawyer.findElement(By.className("wysiwyg")).findElements(By.tagName("a"));
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
                "country", "Sweden",
                "practice_area", "",
                "email", socials[0],
                "phone", socials[1].isEmpty() ? "46852206500" : socials[1]
        );
    }
}