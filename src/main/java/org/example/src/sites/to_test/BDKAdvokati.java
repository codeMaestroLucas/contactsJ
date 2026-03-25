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

public class BDKAdvokati extends ByNewPage {

    public BDKAdvokati() {
        super(
                "BDK Advokati",
                "https://bdkadvokati.com/people",
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
            List<WebElement> lawyers = wait.until(ExpectedConditions.presenceOfAllElementsLocatedBy(By.tagName("article")));
            return this.siteUtl.filterLawyersInPage(lawyers, new By[]{By.cssSelector("p")}, true);
        } catch (Exception e) {
            return List.of();
        }
    }

    @Override
    public String openNewTab(WebElement lawyer) throws LawyerExceptions {
        String link = lawyer.findElement(By.cssSelector("a.block")).getAttribute("href");
        MyDriver.openNewTab(link);
        return link;
    }

    @Override
    protected Object getLawyer(WebElement lawyer) throws Exception {
        this.openNewTab(lawyer);
        WebElement container = driver.findElement(By.cssSelector("div.sm\\:pb-12"));

        By[] nameBy = {By.tagName("h1")};
        By[] roleBy = {By.cssSelector("p.text-dark-blue")};

        String[] socials = super.getSocials(container.findElements(By.tagName("a")), false);

        return Map.of(
                "link", Objects.requireNonNull(driver.getCurrentUrl()),
                "name", extractor.extractLawyerText(container, nameBy, "NAME", LawyerExceptions::nameException),
                "role", extractor.extractLawyerText(container, roleBy, "ROLE", LawyerExceptions::roleException),
                "firm", this.name,
                "country", "Serbia",
                "practice_area", "",
                "email", socials[0],
                "phone", socials[1].isEmpty() ? "+381 11 3284 212" : socials[1]
        );
    }
}
