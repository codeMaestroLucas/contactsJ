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

public class ArochiLindner extends ByNewPage {

    public ArochiLindner() {
        super(
                "Arochi & Lindner",
                "https://www.arochilindner.com/en/mexico-en/",
                2
        );
    }

    String currentCountry = "";
    String currentPhone = "";

    @Override
    protected void accessPage(int index) throws InterruptedException {
        String otherUrl = "https://www.arochilindner.com/en/spain-en/";
        String url = index == 0 ? this.link : otherUrl;

        currentCountry = index == 0 ? "Mexico" : "Spain";
        currentPhone = index == 0 ? "525550952050" : "34965135918";

        this.driver.get(url);
        MyDriver.waitForPageToLoad();
        Thread.sleep(1000L);
    }

    @Override
    protected List<WebElement> getLawyersInPage() {
        try {
            WebDriverWait wait = new WebDriverWait(this.driver, Duration.ofSeconds(10L));
            List<WebElement> lawyers = wait.until(ExpectedConditions.presenceOfAllElementsLocatedBy(By.className("team-container")));
            return this.siteUtl.filterLawyersInPage(lawyers, new By[]{By.className("team-author")}, true);
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
        WebElement container = driver.findElement(By.id("content"));

        String name = extractor.extractLawyerText(container, new By[]{By.tagName("h2")}, "NAME", LawyerExceptions::nameException);
        String role = extractor.extractLawyerText(container, new By[]{By.className("h4div")}, "ROLE", LawyerExceptions::roleException);
        String[] socials = super.getSocials(container.findElements(By.tagName("a")), false);

        return Map.of(
                "link", Objects.requireNonNull(driver.getCurrentUrl()),
                "name", name,
                "role", role.split(",")[0].trim(),
                "firm", this.name,
                "country", currentCountry,
                "practice_area", "",
                "email", socials[0],
                "phone", currentPhone
        );
    }
}
