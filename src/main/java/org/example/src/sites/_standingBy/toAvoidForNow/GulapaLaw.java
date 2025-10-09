package org.example.src.sites._standingBy.toAvoidForNow;

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

public class GulapaLaw extends ByPage {

    public GulapaLaw() {
        super(
                "Gulapa Law",
                "https://gulapalaw.com/lawyers/",
                1
        );
    }

    @Override
    protected void accessPage(int index) {
        this.driver.get(this.link);
        MyDriver.waitForPageToLoad();
    }

    @Override
    protected List<WebElement> getLawyersInPage() {
        try {
            WebDriverWait wait = new WebDriverWait(this.driver, Duration.ofSeconds(10L));
            return wait.until(
                    ExpectedConditions.presenceOfAllElementsLocatedBy(
                            By.cssSelector("a[href*='/lawyers/']")
                    )
            );
        } catch (Exception e) {
            throw new RuntimeException("Failed to find lawyer elements", e);
        }
    }

    private String getLink(WebElement lawyer) throws LawyerExceptions {
        String href = lawyer.getAttribute("href");
        if (href == null || href.isEmpty()) throw LawyerExceptions.linkException("Link not found");
        return href.startsWith("http") ? href : "https://gulapalaw.com" + href;
    }

    private String getName(WebElement lawyer) throws LawyerExceptions {
        By[] byArray = {By.cssSelector("h3.text-secondary")};
        return extractor.extractLawyerText(lawyer, byArray, "NAME", LawyerExceptions::nameException);
    }

    private String getCountry(WebElement lawyer) throws LawyerExceptions {
        By[] byArray = {By.cssSelector("p.text-primary")};
        return extractor.extractLawyerText(lawyer, byArray, "COUNTRY", LawyerExceptions::countryException);
    }

    private String getEmail(WebElement lawyer) throws LawyerExceptions {
        By[] byArray = {By.cssSelector("div.text-white p")};
        return extractor.extractLawyerText(lawyer, byArray, "EMAIL", LawyerExceptions::emailException);
    }

    @Override
    public Object getLawyer(WebElement lawyer) throws Exception {
        return Map.of(
                "link", this.getLink(lawyer),
                "name", this.getName(lawyer),
                "role", "Partner",
                "firm", this.name,
                "country", this.getCountry(lawyer),
                "practice_area", "",
                "email", this.getEmail(lawyer),
                "phone", ""
        );
    }
}