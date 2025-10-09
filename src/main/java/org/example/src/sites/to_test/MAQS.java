package org.example.src.sites.to_test;

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

public class MAQS extends ByPage {
    private final By[] webRole = {
            By.className("person__title")
    };

    public MAQS() {
        super(
                "MAQS",
                "https://maqs.com/en/people?f%5B0%5D=title%3A33",
                7
        );
    }

    @Override
    protected void accessPage(int index) throws InterruptedException {
        String otherUrl = "https://maqs.com/en/people?f%5B0%5D=title%3A33&page=" + index;
        String url = index == 0 ? this.link : otherUrl;
        this.driver.get(url);
        MyDriver.waitForPageToLoad();
        Thread.sleep(1000L);
    }

    @Override
    protected List<WebElement> getLawyersInPage() {
        String[] validRoles = {"partner", "director", "counsel","senior associate"};
        try {
            WebDriverWait wait = new WebDriverWait(this.driver, Duration.ofSeconds(10L));
            List<WebElement> lawyers = wait.until(
                    ExpectedConditions.presenceOfAllElementsLocatedBy(
                            By.className("person-teaser__inner")
                    )
            );
            return siteUtl.filterLawyersInPage(lawyers, webRole, false, validRoles);
        } catch (Exception e) {
            throw new RuntimeException("Failed to find lawyer elements", e);
        }
    }

    private String getLink(WebElement lawyer) throws LawyerExceptions {
        By[] byArray = { By.cssSelector("a[href*='/en/person/']") };
        return extractor.extractLawyerAttribute(lawyer, byArray, "NAME", "href", LawyerExceptions::nameException);
    }

    private String getName(WebElement lawyer) throws LawyerExceptions {
        By[] byArray = { By.className("person__name") };
        return extractor.extractLawyerAttribute(lawyer, byArray, "NAME", "textContent", LawyerExceptions::nameException);
    }

    private String getRole(WebElement lawyer) throws LawyerExceptions {
        return extractor.extractLawyerAttribute(lawyer, webRole, "ROLE", "textContent", LawyerExceptions::roleException);
    }

    private String[] getSocials(WebElement lawyer) {
        try {
            List<WebElement> socials = lawyer.findElements(By.tagName("a"));
            return super.getSocials(socials, false);
        } catch (Exception e) {
            return new String[] { "", "" };
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
                "phone", socials[1].isEmpty() ? "46102657300" : socials[1]
        );
    }
}