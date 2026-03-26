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

public class OonBazul extends ByNewPage {

    public OonBazul() {
        super(
                "Oon & Bazul",
                "https://oonbazul.com/our-lawyers/",
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
            List<WebElement> lawyers = wait.until(ExpectedConditions.presenceOfAllElementsLocatedBy(By.className("fusion-portfolio-post")));
            return this.siteUtl.filterLawyersInPage(lawyers, new By[]{By.cssSelector(".fusion-post-content p")}, true);
        } catch (Exception e) {
            return List.of();
        }
    }

    @Override
    public String openNewTab(WebElement lawyer) throws LawyerExceptions {
        String link = extractor.extractLawyerAttribute(lawyer, new By[]{By.className("fusion-portfolio-post-title"), By.tagName("a")}, "LINK", "href", LawyerExceptions::linkException);
        MyDriver.openNewTab(link);
        return link;
    }

    @Override
    protected Object getLawyer(WebElement lawyer) throws Exception {
        this.openNewTab(lawyer);
        WebElement container = driver.findElement(By.className("white-portfolio-box"));

        String name = extractor.extractLawyerText(driver.findElement(By.tagName("body")), new By[]{By.className("fusion-title-heading")}, "NAME", LawyerExceptions::nameException);
        String role = extractor.extractLawyerText(driver.findElement(By.tagName("body")), new By[]{By.className("fusion-person-content-sep")}, "ROLE", LawyerExceptions::roleException);

        String contactInfo = extractor.extractLawyerText(container, new By[]{By.className("content-container")}, "CONTACT INFO", (e) -> null);
        String[] socials = super.getSocialsFromText(contactInfo);

        return Map.of(
                "link", Objects.requireNonNull(driver.getCurrentUrl()),
                "name", name,
                "role", role,
                "firm", this.name,
                "country", "Singapore",
                "practice_area", "",
                "email", socials[0],
                "phone", socials[1].isEmpty() ? "6562233893" : socials[1]
        );
    }
}
