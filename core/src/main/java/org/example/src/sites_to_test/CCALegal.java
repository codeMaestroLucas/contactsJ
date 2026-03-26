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

public class CCALegal extends ByNewPage {

    public CCALegal() {
        super(
                "CCA",
                "https://www.cca.law/en/team/",
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
            WebDriverWait wait = new WebDriverWait(this.driver, Duration.ofSeconds(10L));
            List<WebElement> lawyers = wait.until(ExpectedConditions.presenceOfAllElementsLocatedBy(By.className("card")));
            return this.siteUtl.filterLawyersInPage(lawyers, new By[]{By.className("card-subtitle")}, true);
        } catch (Exception e) {
            return List.of();
        }
    }

    @Override
    public String openNewTab(WebElement lawyer) throws LawyerExceptions {
        String profileLink = lawyer.getAttribute("href");
        MyDriver.openNewTab(profileLink);
        return profileLink;
    }

    @Override
    protected Object getLawyer(WebElement lawyer) throws Exception {
        this.openNewTab(lawyer);
        WebElement container = driver.findElement(By.className("main-content"));

        String name = extractor.extractLawyerText(container, new By[]{By.className("page-title")}, "NAME", LawyerExceptions::nameException);
        String role = extractor.extractLawyerText(container, new By[]{By.className("card-subtitle")}, "ROLE", LawyerExceptions::roleException);

        List<WebElement> socialElements = container.findElements(By.cssSelector(".box-text a"));
        String[] socials = super.getSocials(socialElements, false);

        return Map.of(
                "link", driver.getCurrentUrl(),
                "name", name,
                "role", role,
                "firm", this.name,
                "country", "Portugal",
                "practice_area", "",
                "email", socials[0],
                "phone", socials[1].isEmpty() ? "" : socials[1]
        );
    }
}