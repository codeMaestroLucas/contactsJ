package org.example.src.sites._standingBy.otherIsuues;

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

public class Macfarlanes extends ByPage {

    private final By[] byRoleArray = {
            By.className("secundary-info")
    };

    public Macfarlanes() {
        super(
                "Macfarlanes",
                "https://www.macfarlanes.com/who-we-are/people/?serviceId=0&page=1&searchText=",
                15
        );
    }

    @Override
    protected void accessPage(int index) throws InterruptedException {
        //todo: change page
        String url = "https://www.macfarlanes.com/who-we-are/people/?serviceId=0&page=" + (index + 1) + "&searchText=";
        this.driver.get(url);
        MyDriver.waitForPageToLoad();
        Thread.sleep(1000L);
        if (index == 0) {
            MyDriver.clickOnElement(By.cssSelector("button[data-cookie-close=\"accept\"]"));
        } else {
            MyDriver.clickOnElement(By.className("js-next"));
            Thread.sleep(10000L);

        }

    }

    @Override
    protected List<WebElement> getLawyersInPage() {
        String[] validRoles = new String[]{
                "partner", "counsel"
        };

        try {
            WebDriverWait wait = new WebDriverWait(this.driver, Duration.ofSeconds(10L));
            List<WebElement> lawyers = wait.until(
                    ExpectedConditions.presenceOfAllElementsLocatedBy(
                            By.cssSelector("div.people-listing-wrap > div.people-listing")
                    )
            );
            return this.siteUtl.filterLawyersInPage(lawyers, byRoleArray, true, validRoles);
        } catch (Exception e) {
            throw new RuntimeException("Failed to find lawyer elements", e);
        }
    }

    private String getLink(WebElement lawyer) throws LawyerExceptions {
        By[] byArray = new By[]{By.className("people-listing__link")};
        return extractor.extractLawyerAttribute(lawyer, byArray, "LINK", "href", LawyerExceptions::linkException);
    }

    private String getName(WebElement lawyer) throws LawyerExceptions {
        By[] byArray = new By[]{
                By.className("people-listing__name")
        };
        return extractor.extractLawyerText(lawyer, byArray, "NAME", LawyerExceptions::nameException);
    }

    private String getRole(WebElement lawyer) throws LawyerExceptions {
        return extractor.extractLawyerText(lawyer, byRoleArray, "ROLE", LawyerExceptions::roleException);
    }

    private String[] getSocials(WebElement lawyer) {
        try {
            List<WebElement> socials = lawyer.findElement(By.className("people-listing__contact")).findElements(By.tagName("a"));
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
                "country", "England",
                "practice_area", "",
                "email", socials[0],
                "phone", socials[1].isEmpty() ? "xxxxxx" : socials[1]
        );
    }
}