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

public class ARQUIS extends ByPage {

    public ARQUIS() {
        super(
                "ARQUIS",
                "https://www.arqis.com/en/team/",
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
            WebDriverWait wait = new WebDriverWait(this.driver, Duration.ofSeconds(10));
            List<WebElement> lawyers = wait.until(ExpectedConditions.presenceOfAllElementsLocatedBy(By.className("archive-item")));
            return this.siteUtl.filterLawyersInPage(lawyers, new By[]{By.className("member-job_title")}, true);
        } catch (Exception e) {
            return List.of();
        }
    }

    private String[] getSocials(WebElement lawyer) throws LawyerExceptions {
        By[] byEmail = {By.cssSelector(".member-email a")};
        String email = extractor.extractLawyerAttribute(lawyer, byEmail, "EMAIL", "href", LawyerExceptions::emailException);
        return new String[]{email, ""};
    }

    @Override
    protected Object getLawyer(WebElement lawyer) throws Exception {
        String name = extractor.extractLawyerText(lawyer, new By[]{By.className("member-name")}, "NAME", LawyerExceptions::nameException);
        String role = extractor.extractLawyerText(lawyer, new By[]{By.className("member-job_title")}, "ROLE", LawyerExceptions::roleException);
        String[] socials = this.getSocials(lawyer);

        return Map.of(
                "link", extractor.extractLawyerAttribute(lawyer, new By[]{By.cssSelector("a")}, "LINK", "href", LawyerExceptions::linkException),
                "name", name,
                "role", role,
                "firm", this.name,
                "country", role.toLowerCase().contains("munich") ? "Germany" : "Germany",
                "practice_area", "",
                "email", socials[0],
                "phone", "49211130690"
        );
    }
}
