package org.example.src.sites.byNewPage;

import org.example.exceptions.LawyerExceptions;
import org.example.src.entities.BaseSites.ByNewPage;
import org.example.src.entities.MyDriver;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.time.Duration;
import java.util.List;
import java.util.Map;
import java.util.Objects;

public class Holmes extends ByNewPage {
    private final By[] byRoleArray = {
            By.className("people-card__top"),
            By.className("people-card__subtitle")
    };

    public Holmes() {
        super(
            "Holmes",
            "https://holmeslaw.ie/people",
            1
        );
    }


    @Override
    protected void accessPage(int index) throws InterruptedException {
        this.driver.get(this.link);
        MyDriver.waitForPageToLoad();
        Thread.sleep(1000L);

        MyDriver.clickOnAddBtn(By.id("onetrust-accept-btn-handler"));

        // Load more btn
        MyDriver.clickOnElement(By.className("people-card-container__button"));
    }


    @Override
    protected List<WebElement> getLawyersInPage() {
        String[] validRoles = new String[]{
                "partner",
                "director",
                "counsel",
                "senior associate"
        };

        try {
            WebDriverWait wait = new WebDriverWait(this.driver, Duration.ofSeconds(10L));

            List<WebElement> lawyers = wait.until(
                    ExpectedConditions.presenceOfAllElementsLocatedBy(
                            By.cssSelector("ul.people-card-list > li.people-card > a")
                    )
            );
            return this.siteUtl.filterLawyersInPage(lawyers, byRoleArray, true, validRoles);

        } catch (Exception e) {
            throw new RuntimeException("Failed to find lawyer elements", e);
        }
    }


    public void openNewTab(WebElement lawyer) throws LawyerExceptions {
        MyDriver.openNewTab(lawyer.getAttribute("href"));
    }


    private String getName(WebElement lawyer) throws LawyerExceptions {
        By[] byArray = new By[]{
                By.className("banner__title")
        };
        return extractor.extractLawyerText(lawyer, byArray, "NAME", LawyerExceptions::nameException);
    }


    private String getRole(WebElement lawyer) throws LawyerExceptions {
        By[] byArray = new By[]{
                By.className("banner__subheading")
        };
        return extractor.extractLawyerText(lawyer, byArray, "ROLE", LawyerExceptions::roleException);
    }


    private String getPracticeArea(WebElement lawyer) throws LawyerExceptions {
        By[] byArray = new By[]{
                By.className("banner__content"),
                By.className("banner__text")
        };
        return extractor.extractLawyerText(lawyer, byArray, "PRACTICE AREA", LawyerExceptions::practiceAreaException);
    }


    private String[] getSocials() {
        WebElement lawyer = driver.findElement(By.className("banner-frame"));

        try {
            List<WebElement> socials = lawyer
                    .findElements(By.cssSelector("a"));
            return super.getSocials(socials, false);

        } catch (Exception e) {
            System.err.println("Error getting socials: " + e.getMessage());
            return new String[]{"", ""};
        }
    }


    @Override
    public Object getLawyer(WebElement lawyer) throws Exception {
        this.openNewTab(lawyer);

        WebElement div = driver.findElement(By.className("banner__glyph-container"));

        String[] socials = this.getSocials();

        return Map.of(
                "link", Objects.requireNonNull(driver.getCurrentUrl()),
                "name", this.getName(div),
                "role", this.getRole(div),
                "firm", this.name,
                "country", "Ireland",
                "practice_area", this.getPracticeArea(div),
                "email", socials[0],
                "phone", "35316768928"
        );
    }
}
