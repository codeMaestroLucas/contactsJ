package org.example.src.sites.to_test;

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

public class GordonLegal extends ByPage {

    private final By[] byRoleArray = {
            By.className("member-position")
    };

    public GordonLegal() {
        super(
                "Gordon Legal",
                "https://gordonlegal.com.au/people/",
                1
        );
    }

    @Override
    protected void accessPage(int index) throws InterruptedException {
        this.driver.get(this.link);
        MyDriver.waitForPageToLoad();
        Thread.sleep(1000L);
    }

    @Override
    protected List<WebElement> getLawyersInPage() {
        String[] validRoles = {"principal lawyer", "senior associate", "special counsel"};
        try {
            WebDriverWait wait = new WebDriverWait(this.driver, Duration.ofSeconds(10L));
            List<WebElement> lawyers = wait.until(
                    ExpectedConditions.presenceOfAllElementsLocatedBy(By.className("member-wrapper"))
            );
            return this.siteUtl.filterLawyersInPage(lawyers, byRoleArray, true, validRoles);
        } catch (Exception e) {
            throw new RuntimeException("Failed to find lawyer elements", e);
        }
    }

    private String getName(WebElement lawyer) throws LawyerExceptions {
        By[] byArray = {By.className("member-name")};
        return extractor.extractLawyerText(lawyer, byArray, "NAME", LawyerExceptions::nameException);
    }

    private String getRole(WebElement lawyer) throws LawyerExceptions {
        return extractor.extractLawyerText(lawyer, byRoleArray, "ROLE", LawyerExceptions::roleException);
    }

    private String constructEmail(String name) {
        name = TreatLawyerParams.treatName(name);
        String[] parts = TreatLawyerParams.treatNameForEmail(name).split("\\s+");
        if (parts.length < 2) return "";
        String firstLetter = parts[0].substring(0, 1);
        String lastName = parts[parts.length - 1];
        return (firstLetter + lastName + "@gordonlegal.com.au").toLowerCase();
    }

    @Override
    public Object getLawyer(WebElement lawyer) throws Exception {
        String name = this.getName(lawyer);
        return Map.of(
                "link", this.link,
                "name", name,
                "role", this.getRole(lawyer),
                "firm", this.name,
                "country", "Australia",
                "practice_area", "",
                "email", constructEmail(name),
                "phone", "1300722744"
        );
    }
}
