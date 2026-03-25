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

public class BDGSAssociates extends ByNewPage {

    public BDGSAssociates() {
        super(
                "BDGS Associates",
                "https://www.bdgs-associes.com/en/our-lawyers/",
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
            List<WebElement> lawyers = wait.until(ExpectedConditions.presenceOfAllElementsLocatedBy(By.cssSelector(".team-container")));
            return this.siteUtl.filterLawyersInPage(lawyers, new By[]{By.cssSelector(".team-author-name")}, true);
        } catch (Exception e) {
            return List.of();
        }
    }

    @Override
    public String openNewTab(WebElement lawyer) throws LawyerExceptions {
        String link = lawyer.findElement(By.cssSelector("a")).getAttribute("href");
        MyDriver.openNewTab(link);
        return link;
    }

    @Override
    protected Object getLawyer(WebElement lawyer) throws Exception {
        this.openNewTab(lawyer);
        WebElement container = driver.findElement(By.xpath("//div/div[3]/div/div/div"));

        By[] nameBy = {By.xpath("//h1")};
        By[] roleBy = {By.xpath("//div[contains(@class, 'wpb_text_column')]//h6[contains(., 'PARTNER')]")};

        String[] socials = super.getSocials(container.findElements(By.cssSelector("a[href^='mailto:']")), false);

        return Map.of(
                "link", Objects.requireNonNull(driver.getCurrentUrl()),
                "name", extractor.extractLawyerText(driver.findElement(By.tagName("body")), nameBy, "NAME", LawyerExceptions::nameException),
                "role", extractor.extractLawyerText(container, roleBy, "ROLE", LawyerExceptions::roleException),
                "firm", this.name,
                "country", "France",
                "practice_area", "",
                "email", socials[0],
                "phone", socials[1].isEmpty() ? "+33 1 42 99 22 22" : socials[1]
        );
    }
}
