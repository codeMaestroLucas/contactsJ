package org.example.src.sites._standingBy.download;

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

public class DMDLaw extends ByNewPage {

    public DMDLaw() {
        super(
                "DMD Law",
                "https://www.dmd.law/people/",
                1
        );
    }

    private final By[] byRoleArray = {
            By.className("partner-details")
    };

    @Override
    protected void accessPage(int index) {
        this.driver.get(this.link);
        MyDriver.waitForPageToLoad();
    }

    @Override
    protected List<WebElement> getLawyersInPage() {
        String[] validRoles = new String[]{"partner", "director", "counsel", "senior associate"};

        try {
            WebDriverWait wait = new WebDriverWait(this.driver, Duration.ofSeconds(10L));
            List<WebElement> lawyers = wait.until(ExpectedConditions.presenceOfAllElementsLocatedBy(By.className("partner-pan")));
            return this.siteUtl.filterLawyersInPage(lawyers, byRoleArray, false, validRoles);
        } catch (Exception e) {
            throw new RuntimeException("Failed to find lawyer elements", e);
        }
    }

    @Override
    public String openNewTab(WebElement lawyer) throws LawyerExceptions {
        By[] byArray = {By.tagName("a")};
        String link = extractor.extractLawyerAttribute(lawyer, byArray, "LINK", "href", LawyerExceptions::linkException);
        MyDriver.openNewTab(link);
        return link;
    }

    private String getName(WebElement div) throws LawyerExceptions {
        By[] byArray = {By.className("widget-title")};
        return extractor.extractLawyerText(div, byArray, "NAME", LawyerExceptions::nameException);
    }

    private String getRole(WebElement div) throws LawyerExceptions {
        By[] byArray = {By.cssSelector(".prof-img-details h4")};
        return extractor.extractLawyerText(div, byArray, "ROLE", LawyerExceptions::roleException);
    }

    private String getPracticeArea(WebElement div) throws LawyerExceptions {
        By[] byArray = {By.className("cont-wrap")};
        return extractor.extractLawyerText(div, byArray, "PRACTICE AREA", LawyerExceptions::practiceAreaException);
    }

    @Override
    public Object getLawyer(WebElement lawyer) throws Exception {
        this.openNewTab(lawyer);
        WebElement div = driver.findElement(By.id("pg-w69a035d58abc4-0"));

        return Map.of(
                "link", Objects.requireNonNull(driver.getCurrentUrl()),
                "name", this.getName(div),
                "role", this.getRole(div),
                "firm", this.name,
                "country", "India",
                "practice_area", this.getPracticeArea(div),
                "email", "", // No explicit email in HTML provided
                "phone", "912243565555"
        );
    }
}
