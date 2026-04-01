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

public class Harris extends ByNewPage {

    public Harris() {
        super(
                "Harris",
                "https://harrisco.com/lawyers/",
                1
        );
    }

    @Override
    protected void accessPage(int index) throws InterruptedException {
        this.driver.get(this.link);
        MyDriver.waitForPageToLoad();
        Thread.sleep(1000L);
    }

    @Override
    protected List<WebElement> getLawyersInPage() {
        try {
            WebDriverWait wait = new WebDriverWait(this.driver, Duration.ofSeconds(10L));
            List<WebElement> lawyers = wait.until(ExpectedConditions.presenceOfAllElementsLocatedBy(By.className("ee-post")));
            return this.siteUtl.filterLawyersInPage(lawyers, new By[]{By.className("bde-text")}, true);
        } catch (Exception e) {
            return List.of();
        }
    }

    @Override
    public String openNewTab(WebElement lawyer) throws LawyerExceptions {
        String link = lawyer.findElement(By.className("bde-text-link")).getAttribute("href");
        MyDriver.openNewTab(link);
        return link;
    }

    @Override
    protected Object getLawyer(WebElement lawyer) throws Exception {
        this.openNewTab(lawyer);
        WebElement body = driver.findElement(By.className("section-container"));

        String name = extractor.extractLawyerText(driver.findElement(By.tagName("header")), new By[]{By.tagName("h1")}, "NAME", LawyerExceptions::nameException);
        String role = extractor.extractLawyerText(body, new By[]{By.className("bde-text-4607-228")}, "ROLE", LawyerExceptions::roleException);
        String practice = extractor.extractLawyerText(body, new By[]{By.className("bde-rich-text-4607-266")}, "PRACTICE AREA", LawyerExceptions::practiceAreaException);
        String[] socials = super.getSocials(body.findElements(By.tagName("a")), false);

        return Map.of(
                "link", Objects.requireNonNull(driver.getCurrentUrl()),
                "name", name,
                "role", role,
                "firm", this.name,
                "country", "Canada",
                "practice_area", practice,
                "email", socials[0],
                "phone", socials[1].isEmpty() ? "6048912237" : socials[1]
        );
    }
}
