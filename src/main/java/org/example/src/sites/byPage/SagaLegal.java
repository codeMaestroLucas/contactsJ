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

public class SagaLegal extends ByPage {
    private final By[] byRoleArray = {By.cssSelector(".team__title span")};

    public SagaLegal() {
        super(
                "Saga Legal",
                "https://sagalegal.in/pages/Team.html",
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
        String[] validRoles = {"partner", "founder", "senior associate", "principal associate"};
        try {
            WebDriverWait wait = new WebDriverWait(this.driver, Duration.ofSeconds(10));
            List<WebElement> lawyers = wait.until(ExpectedConditions.presenceOfAllElementsLocatedBy(By.cssSelector("div.team__item")));
            return this.siteUtl.filterLawyersInPage(lawyers, byRoleArray, true, validRoles);
        } catch (Exception e) {
            throw new RuntimeException("Failed to find lawyer elements", e);
        }
    }

    private String getName(WebElement lawyer) throws LawyerExceptions {
        By[] byArray = {By.tagName("h3")};
        return extractor.extractLawyerText(lawyer, byArray, "NAME", LawyerExceptions::nameException);
    }

    private String getRole(WebElement lawyer) throws LawyerExceptions {
        String text = extractor.extractLawyerText(lawyer, byRoleArray, "ROLE", LawyerExceptions::roleException);
        return text.split(",")[0].trim();
    }

    private String getCountry(WebElement lawyer) throws LawyerExceptions {
        String text = extractor.extractLawyerText(lawyer, byRoleArray, "COUNTRY", LawyerExceptions::countryException);
        return text.split(",")[1].trim();
    }

    @Override
    public Object getLawyer(WebElement lawyer) throws Exception {
        String email = lawyer.findElement(By.cssSelector("a[href^='mailto:']")).getAttribute("href").replace("mailto:", "");
        return Map.of(
                "link", this.link,
                "name", this.getName(lawyer),
                "role", this.getRole(lawyer),
                "firm", this.name,
                "country", this.getCountry(lawyer),
                "practice_area", "",
                "email", email,
                "phone", "9111410669699"
        );
    }
}
