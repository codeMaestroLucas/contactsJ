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

public class BWB extends ByNewPage {

    public BWB() {
        super(
                "BWB",
                "https://bwb.legal/en/team",
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
            List<WebElement> lawyers = wait.until(ExpectedConditions.presenceOfAllElementsLocatedBy(By.cssSelector(".cell.small-6")));
            return this.siteUtl.filterLawyersInPage(lawyers, new By[]{By.className("bf-texts__style-5")}, true);
        } catch (Exception e) {
            return List.of();
        }
    }

    @Override
    public String openNewTab(WebElement lawyer) throws LawyerExceptions {
        String link = lawyer.findElement(By.tagName("a")).getAttribute("href");
        MyDriver.openNewTab(link);
        return link;
    }

    @Override
    protected Object getLawyer(WebElement lawyer) throws Exception {
        this.openNewTab(lawyer);
        WebElement container = driver.findElement(By.cssSelector("div[style*='z-index: 1']"));

        By[] nameBy = {By.tagName("h1")};
        By[] roleBy = {By.className("bf-person-role__wrapper")};

        String[] socials = super.getSocials(container.findElements(By.tagName("a")), false);

        return Map.of(
                "link", Objects.requireNonNull(driver.getCurrentUrl()),
                "name", extractor.extractLawyerText(container, nameBy, "NAME", LawyerExceptions::nameException),
                "role", extractor.extractLawyerText(container, roleBy, "ROLE", LawyerExceptions::roleException),
                "firm", this.name,
                "country", "Liechtenstein",
                "practice_area", "",
                "email", socials[0],
                "phone", socials[1].isEmpty() ? "4232360404" : socials[1]
        );
    }
}
