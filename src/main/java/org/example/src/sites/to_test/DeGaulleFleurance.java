package org.example.src.sites.to_test;

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

public class DeGaulleFleurance extends ByNewPage {

    public DeGaulleFleurance() {
        super(
                "De Gaulle Fleurance",
                "https://www.degaullefleurance.com/en/annuaire/",
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
        By[] byRoleArray = {By.className("fonction")};
        try {
            WebDriverWait wait = new WebDriverWait(this.driver, Duration.ofSeconds(10));
            List<WebElement> lawyers = wait.until(ExpectedConditions.presenceOfAllElementsLocatedBy(By.className("item")));
            return this.siteUtl.filterLawyersInPage(lawyers, byRoleArray, true);
        } catch (Exception e) {
            return List.of();
        }
    }

    @Override
    public String openNewTab(WebElement lawyer) throws LawyerExceptions {
        By[] byArray = {By.className("link")};
        String link = extractor.extractLawyerAttribute(lawyer, byArray, "LINK", "href", LawyerExceptions::linkException);
        MyDriver.openNewTab(link);
        return link;
    }

    @Override
    public Object getLawyer(WebElement lawyer) throws Exception {
        this.openNewTab(lawyer);
        WebElement bio = driver.findElement(By.className("info_bio"));

        By[] byName = {By.className("nom")};
        By[] byRole = {By.className("id"), By.tagName("span")};

        String[] socials = super.getSocials(driver.findElements(By.cssSelector(".details a")), false);

        return Map.of(
                "link", Objects.requireNonNull(driver.getCurrentUrl()),
                "name", extractor.extractLawyerText(bio, byName, "NAME", LawyerExceptions::nameException),
                "role", extractor.extractLawyerText(bio, byRole, "ROLE", LawyerExceptions::roleException),
                "firm", this.name,
                "country", "France",
                "practice_area", "",
                "email", socials[0],
                "phone", socials[1].isEmpty() ? "33184753451" : socials[1]
        );
    }
}
