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

public class NitschneiderAndPartners extends ByNewPage {

    public NitschneiderAndPartners() {
        super(
                "Nitschneider & Partners",
                "https://www.nitschneider.com/tim?lang=en",
                1
        );
    }

    @Override
    protected void accessPage(int index) throws InterruptedException {
        this.driver.get(this.link);
        MyDriver.waitForPageToLoad();
        Thread.sleep(2000L);
    }

    @Override
    protected List<WebElement> getLawyersInPage() {
        try {
            WebDriverWait wait = new WebDriverWait(this.driver, Duration.ofSeconds(10));
            List<WebElement> lawyers = wait.until(ExpectedConditions.presenceOfAllElementsLocatedBy(By.className("item-link-wrapper")));
            return this.siteUtl.filterLawyersInPage(lawyers, new By[]{By.className("info-element-description")}, true);
        } catch (Exception e) {
            return List.of();
        }
    }

    @Override
    public String openNewTab(WebElement lawyer) throws LawyerExceptions {
        MyDriver.clickOnElement(lawyer);
        Thread.sleep(1000L);
        return driver.getCurrentUrl();
    }

    @Override
    protected Object getLawyer(WebElement lawyer) throws Exception {
        // Note: Wix galleries often open a modal or dynamic view, treat as ByNewPage logic for context
        this.openNewTab(lawyer);

        String name = extractor.extractLawyerText(null, new By[]{By.cssSelector("[data-testid='richTextElement'] span")}, "NAME", LawyerExceptions::nameException);
        String role = extractor.extractLawyerText(null, new By[]{By.xpath("//span[contains(.,'PARTNER')]")}, "ROLE", LawyerExceptions::roleException);

        String[] socials = super.getSocialsFromText(driver.findElement(By.id("comp-jyook400")).getText());

        return Map.of(
                "link", Objects.requireNonNull(driver.getCurrentUrl()),
                "name", name,
                "role", role,
                "firm", this.name,
                "country", "Slovakia",
                "practice_area", "",
                "email", socials[0],
                "phone", socials[1].isEmpty() ? "421220921210" : socials[1]
        );
    }
}
