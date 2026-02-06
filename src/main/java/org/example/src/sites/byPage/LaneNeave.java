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

public class LaneNeave extends ByPage {
    private final By[] byRoleArray = {By.cssSelector("strong > a")};

    public LaneNeave() {
        super(
                "Lane Neave",
                "https://www.laneneave.co.nz/our-people/",
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
        String[] validRoles = {"partner", "counsel", "senior associate", "senior solicitor"};
        try {
            WebDriverWait wait = new WebDriverWait(this.driver, Duration.ofSeconds(10));
            List<WebElement> lawyers = wait.until(ExpectedConditions.presenceOfAllElementsLocatedBy(By.className("team-member")));
            return this.siteUtl.filterLawyersInPage(lawyers, byRoleArray, true, validRoles);
        } catch (Exception e) {
            throw new RuntimeException("Failed to find lawyer elements", e);
        }
    }

    private String getLink(WebElement lawyer) throws LawyerExceptions {
        By[] byArray = {By.cssSelector("strong > a")};
        return extractor.extractLawyerAttribute(lawyer, byArray, "LINK", "href", LawyerExceptions::linkException);
    }

    private String getName(WebElement lawyer) throws LawyerExceptions {
        By[] byArray = {By.cssSelector("strong > a")};
        String fullText = extractor.extractLawyerAttribute(lawyer, byArray, "NAME", "innerHTML", LawyerExceptions::nameException);
        return fullText.split("<br>")[0].trim();
    }

    private String getRole(WebElement lawyer) throws LawyerExceptions {
        String fullText = extractor.extractLawyerAttribute(lawyer, byRoleArray, "ROLE", "innerHTML", LawyerExceptions::roleException);
        return fullText.split("<br>")[1].trim();
    }

    private String[] getSocials(WebElement lawyer) {
        try {
            String email = lawyer.findElement(By.cssSelector("a[href^='mailto:']")).getText().trim();
            String phone = lawyer.findElement(By.cssSelector("a[href^='tel:']")).getText().trim();
            return new String[]{email, phone};
        } catch (Exception e) {
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
                "country", "New Zealand",
                "practice_area", "",
                "email", socials[0],
                "phone", socials[1].isEmpty() ? "6493006263" : socials[1]
        );
    }
}
