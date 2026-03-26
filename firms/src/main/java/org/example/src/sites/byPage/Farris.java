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

public class Farris extends ByPage {

    public Farris() {
        super(
                "Farris",
                "https://farris.com/our-people/",
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
            List<WebElement> lawyers = wait.until(ExpectedConditions.presenceOfAllElementsLocatedBy(By.className("profile-card")));
            return this.siteUtl.filterLawyersInPage(lawyers, new By[]{By.className("profile-card__details")}, true);
        } catch (Exception e) {
            return List.of();
        }
    }

    @Override
    protected Object getLawyer(WebElement lawyer) throws Exception {

        String emailPrefix = lawyer.findElement(By.className("js-hidden-email")).getAttribute("data-mail-name");
        String emailDomain = lawyer.findElement(By.className("js-hidden-email")).getAttribute("data-mail-domain");
        String email = emailPrefix + "@" + emailDomain;

        String phone = extractor.extractLawyerText(lawyer, new By[]{By.cssSelector("a[href*='tel:']")}, "PHONE", LawyerExceptions::phoneException);

        return Map.of(
                "link", extractor.extractLawyerAttribute(lawyer, new By[]{By.cssSelector("a.profile-card__photo")}, "LINK", "href", LawyerExceptions::linkException),
                "name", extractor.extractLawyerText(lawyer, new By[]{By.className("profile-card__title")}, "NAME", LawyerExceptions::nameException),
                "role", extractor.extractLawyerText(lawyer, new By[]{By.className("profile-card__details")}, "ROLE", LawyerExceptions::roleException),
                "firm", this.name,
                "country", "Canada",
                "practice_area", "",
                "email", email,
                "phone", phone.isEmpty() ? "6046849151" : phone
        );
    }
}
