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

public class Bener extends ByNewPage {

    public Bener() {
        super(
                "Bener",
                "https://bener.com/the-team/",
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
            List<WebElement> lawyers = wait.until(ExpectedConditions.presenceOfAllElementsLocatedBy(By.className("vc_grid-item")));
            return this.siteUtl.filterLawyersInPage(lawyers, new By[]{By.className("team-grid-position")}, true);
        } catch (Exception e) {
            return List.of();
        }
    }

    @Override
    public String openNewTab(WebElement lawyer) throws LawyerExceptions {
        String profileLink = extractor.extractLawyerAttribute(lawyer, new By[]{By.cssSelector("a.vc_gitem-link")}, "LINK", "href", LawyerExceptions::linkException);
        MyDriver.openNewTab(profileLink);
        return profileLink;
    }

    @Override
    protected Object getLawyer(WebElement lawyer) throws Exception {
        this.openNewTab(lawyer);
        WebElement container = driver.findElement(By.xpath("//div/div[1]"));

        List<WebElement> socialElements = container.findElements(By.cssSelector("a"));
        String[] socials = super.getSocials(socialElements, false);

        return Map.of(
                "link", Objects.requireNonNull(driver.getCurrentUrl()),
                "name", extractor.extractLawyerText(container, new By[]{By.className("post-title")}, "NAME", LawyerExceptions::nameException),
                "role", extractor.extractLawyerText(container, new By[]{By.className("team-grid-position")}, "ROLE", LawyerExceptions::roleException),
                "firm", this.name,
                "country", "Turkey",
                "practice_area", "",
                "email", socials[0],
                "phone", socials[1].isEmpty() ? "902122707050" : socials[1]
        );
    }
}
