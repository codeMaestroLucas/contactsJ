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

public class AFMpanga extends ByPage {

    public AFMpanga() {
        super(
                "AF Mpanga",
                "https://afmpanga.com/people/",
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
            List<WebElement> lawyers = wait.until(ExpectedConditions.presenceOfAllElementsLocatedBy(By.className("profile-square-v")));
            return this.siteUtl.filterLawyersInPage(lawyers, new By[]{By.className("profile-designation")}, true, validRoles);
        } catch (Exception e) {
            return List.of();
        }
    }

    @Override
    protected Object getLawyer(WebElement lawyer) throws Exception {
        String link = extractor.extractLawyerAttribute(lawyer, new By[]{By.tagName("a")}, "LINK", "href", LawyerExceptions::linkException);
        String name = extractor.extractLawyerText(lawyer, new By[]{By.className("profile-title")}, "NAME", LawyerExceptions::nameException);
        String role = extractor.extractLawyerText(lawyer, new By[]{By.className("profile-designation")}, "ROLE", LawyerExceptions::roleException);

        String[] socials = super.getSocials(lawyer.findElements(By.cssSelector(".ekit-team-social-list a")), false);

        return Map.of(
                "link", link,
                "name", name,
                "role", role,
                "firm", this.name,
                "country", "Uganda",
                "practice_area", "",
                "email", socials[0],
                "phone", socials[1].isEmpty() ? "256414254540" : socials[1]
        );
    }
}
