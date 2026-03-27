package org.example.src.sites.byNewPage;

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

public class CNPLaw extends ByNewPage {

    public CNPLaw() {
        super(
                "CNP Law",
                "https://www.cnplaw.com/our-lawyers/",
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
            List<WebElement> lawyers = wait.until(ExpectedConditions.presenceOfAllElementsLocatedBy(By.cssSelector("article.wpgb-card")));
            return this.siteUtl.filterLawyersInPage(lawyers, new By[]{By.className("wpgb-card-body")}, true);
        } catch (Exception e) {
            return List.of();
        }
    }

    @Override
    public String openNewTab(WebElement lawyer) throws LawyerExceptions {
        String link = lawyer.findElement(By.className("wpgb-card-layer-link")).getAttribute("href");
        MyDriver.openNewTab(link);
        return link;
    }

    private String[] getSocials(WebElement lawyer) {
        try {
            List<WebElement> socials = lawyer.findElements(By.cssSelector(".lawyer-template-contact-container > a"));
            return super.getSocials(socials, false);
        } catch (Exception e) {
            return new String[]{"", ""};
        }
    }

    @Override
    protected Object getLawyer(WebElement lawyer) throws Exception {
        this.openNewTab(lawyer);
        WebElement container = driver.findElement(By.className("lawyer-template-hero-content"));

        String[] socials = this.getSocials(container);

        return Map.of(
                "link", Objects.requireNonNull(driver.getCurrentUrl()),
                "name", extractor.extractLawyerText(container, new By[]{By.className("lawyer-template-hero-title")}, "NAME", LawyerExceptions::nameException),
                "role", extractor.extractLawyerText(container, new By[]{By.className("lawyer-template-title")}, "ROLE", LawyerExceptions::roleException),
                "firm", this.name,
                "country", "Singapore",
                "practice_area", "",
                "email", socials[0].split("\\?")[0],
                "phone", socials[1].isEmpty() ? "6563238383" : socials[1]
        );
    }
}
