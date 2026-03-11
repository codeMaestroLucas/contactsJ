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

public class SokolNovakTrojanDolecek extends ByPage {

    private final By[] byRoleArray = {By.className("profile-tile__meta--job-title")};

    public SokolNovakTrojanDolecek() {
        super(
                "Sokol, Novák, Trojan, Doleček",
                "https://sntd.eu/en/people",
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
        String[] validRoles = {"partner", "counsel", "senior associate"};
        try {
            WebDriverWait wait = new WebDriverWait(this.driver, Duration.ofSeconds(10));
            List<WebElement> lawyers = wait.until(ExpectedConditions.presenceOfAllElementsLocatedBy(By.className("profile-tile")));
            return this.siteUtl.filterLawyersInPage(lawyers, byRoleArray, true, validRoles);
        } catch (Exception e) {
            throw new RuntimeException("Error finding lawyers", e);
        }
    }

    private String getLink(WebElement lawyer) throws LawyerExceptions {
        By[] byArray = {By.cssSelector("h3.profile-tile__title > a"), By.xpath("./..")};
        // The link is in the header which is a child of the content div, or the img wrapper
        By[] altBy = {By.className("profile-tile__img-wrapper")};
        return extractor.extractLawyerAttribute(lawyer, altBy, "LINK", "href", LawyerExceptions::linkException);
    }

    private String getName(WebElement lawyer) throws LawyerExceptions {
        By[] byArray = {By.className("profile-tile__title")};
        return extractor.extractLawyerText(lawyer, byArray, "NAME", LawyerExceptions::nameException);
    }

    private String getRole(WebElement lawyer) throws LawyerExceptions {
        return extractor.extractLawyerText(lawyer, byRoleArray, "ROLE", LawyerExceptions::roleException);
    }

    private String getEmail(WebElement lawyer) throws LawyerExceptions {
        By[] byArray = {By.cssSelector("a[href^='mailto:']")};
        try {
            return extractor.extractLawyerText(lawyer, byArray, "EMAIL", LawyerExceptions::emailException);
        } catch (LawyerExceptions e) {
            return null;
        }
    }

    @Override
    public Object getLawyer(WebElement lawyer) throws Exception {
        String email = this.getEmail(lawyer);
        if (email == null) return "Invalid Role";

        return Map.of(
                "link", getLink(lawyer),
                "name", getName(lawyer),
                "role", getRole(lawyer),
                "firm", this.name,
                "country", "the Czech Republic",
                "practice_area", "",
                "email", email,
                "phone", "420270005533"
        );
    }
}
