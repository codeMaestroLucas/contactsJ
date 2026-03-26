package org.example.src.sites_to_test;

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

public class Cooley extends ByNewPage {

    public Cooley() {
        super(
                "Cooley",
                "https://www.cooley.com/people#t=cooley-coveo-tab-people-listing",
                1
        );
    }

    @Override
    protected void accessPage(int index) throws InterruptedException {
        this.driver.get(this.link);
        MyDriver.waitForPageToLoad();
        Thread.sleep(2000);
    }

    @Override
    protected List<WebElement> getLawyersInPage() {
        try {
            WebDriverWait wait = new WebDriverWait(this.driver, Duration.ofSeconds(10));
            List<WebElement> lawyers = wait.until(ExpectedConditions.presenceOfAllElementsLocatedBy(By.cssSelector("article.teaser")));
            return this.siteUtl.filterLawyersInPage(lawyers, new By[]{By.className("teaser-position")}, true);
        } catch (Exception e) {
            return List.of();
        }
    }

    @Override
    public String openNewTab(WebElement lawyer) throws LawyerExceptions {
        By[] byArray = {By.className("CoveoResultLink")};
        String link = extractor.extractLawyerAttribute(lawyer, byArray, "LINK", "href", LawyerExceptions::linkException);
        MyDriver.openNewTab(link);
        return link;
    }

    @Override
    protected Object getLawyer(WebElement lawyer) throws Exception {
        String countryText = extractor.extractLawyerText(lawyer, new By[]{By.xpath(".//div[contains(@class,'teaser-position')][2]")}, "COUNTRY", LawyerExceptions::countryException);

        this.openNewTab(lawyer);
        WebElement hero = driver.findElement(By.className("hero-person"));

        String name = extractor.extractLawyerText(hero, new By[]{By.className("name")}, "NAME", LawyerExceptions::nameException);
        String role = extractor.extractLawyerText(hero, new By[]{By.className("eyebrow")}, "ROLE", LawyerExceptions::roleException);
        String email = extractor.extractLawyerAttribute(hero, new By[]{By.cssSelector("a[href^='mailto:']")}, "EMAIL", "textContent", LawyerExceptions::emailException);

        return Map.of(
                "link", Objects.requireNonNull(driver.getCurrentUrl()),
                "name", name,
                "role", role,
                "firm", this.name,
                "country", countryText,
                "practice_area", "",
                "email", email,
                "phone", "xxxxxx"
        );
    }
}
