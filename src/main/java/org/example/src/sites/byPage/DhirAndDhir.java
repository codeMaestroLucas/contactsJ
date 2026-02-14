package org.example.src.sites.byPage;

import org.example.exceptions.LawyerExceptions;
import org.example.src.entities.BaseSites.ByPage;
import org.example.src.entities.MyDriver;
import org.example.src.utils.TreatLawyerParams;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.time.Duration;
import java.util.List;
import java.util.Map;

public class DhirAndDhir extends ByPage {

    public DhirAndDhir() {
        super(
                "Dhir & Dhir",
                "https://www.dhirassociates.com/the-team/",
                1
        );
    }

    @Override
    protected void accessPage(int index) throws InterruptedException {
        this.driver.get(this.link);
        MyDriver.waitForPageToLoad();
    }

    @Override
    protected List<WebElement> getLawyersInPage() {
        try {
            WebDriverWait wait = new WebDriverWait(this.driver, Duration.ofSeconds(10L));
            return wait.until(ExpectedConditions.presenceOfAllElementsLocatedBy(By.className("gdlr-core-personnel-list")));
        } catch (Exception e) {
            throw new RuntimeException("Failed to find lawyer elements", e);
        }
    }

    private String getLink(WebElement lawyer) throws LawyerExceptions {
        By[] byArray = {By.cssSelector("h3.gdlr-core-personnel-list-title a")};
        return extractor.extractLawyerAttribute(lawyer, byArray, "LINK", "href", LawyerExceptions::linkException);
    }

    private String getName(WebElement lawyer) throws LawyerExceptions {
        By[] byArray = {By.cssSelector("h3.gdlr-core-personnel-list-title a")};
        return extractor.extractLawyerText(lawyer, byArray, "NAME", LawyerExceptions::nameException);
    }

    private String getRole(WebElement lawyer) throws LawyerExceptions {
        By[] byArray = {By.className("gdlr-core-personnel-list-position")};
        return extractor.extractLawyerText(lawyer, byArray, "ROLE", LawyerExceptions::roleException);
    }

    private String getPracticeArea(WebElement lawyer) throws LawyerExceptions {
        By[] byArray = {By.className("pr-are")};
        return extractor.extractLawyerText(lawyer, byArray, "PRACTICE AREA", LawyerExceptions::practiceAreaException);
    }

    private String constructEmail(String name) {
        String[] parts = TreatLawyerParams.treatNameForEmail(name).split(" ");
        if (parts.length >= 2) {
            return parts[0] + "." + parts[parts.length - 1] + "@dhirassociates.com";
        }
        return parts[0] + "@dhirassociates.com";
    }

    @Override
    public Object getLawyer(WebElement lawyer) throws Exception {
        String name = this.getName(lawyer);
        String email = this.constructEmail(name);

        return Map.of(
                "link", this.getLink(lawyer),
                "name", name,
                "role", this.getRole(lawyer),
                "firm", this.name,
                "country", "India",
                "practice_area", this.getPracticeArea(lawyer),
                "email", email,
                "phone", "911142410000"
        );
    }
}
