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

public class TSMP extends ByNewPage {

    public TSMP() {
        super(
                "TSMP",
                "https://www.tsmplaw.com/our-people/",
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
            List<WebElement> lawyers = wait.until(ExpectedConditions.presenceOfAllElementsLocatedBy(By.className("globalThumbnailStyling-people")));
            return this.siteUtl.filterLawyersInPage(lawyers, new By[]{By.cssSelector("p.subText")}, true);
        } catch (Exception e) {
            return List.of();
        }
    }

    @Override
    public String openNewTab(WebElement lawyer) throws LawyerExceptions {
        String link = extractor.extractLawyerAttribute(lawyer, new By[]{By.tagName("a")}, "LINK", "href", LawyerExceptions::linkException);
        MyDriver.openNewTab(link);
        return link;
    }

    @Override
    protected Object getLawyer(WebElement lawyer) throws Exception {
        this.openNewTab(lawyer);
        WebElement container = driver.findElement(By.className("peopleDetailsWrap"));

        String name = extractor.extractLawyerText(driver.findElement(By.tagName("body")), new By[]{By.tagName("h1")}, "NAME", LawyerExceptions::nameException).replace("\n", " ").trim();
        String role = extractor.extractLawyerText(container, new By[]{By.xpath(".//p[contains(.,'Partner')]")}, "ROLE", LawyerExceptions::roleException);
        String email = extractor.extractLawyerText(container, new By[]{By.tagName("a")}, "EMAIL", LawyerExceptions::emailException);

        return Map.of(
                "link", Objects.requireNonNull(driver.getCurrentUrl()),
                "name", name,
                "role", role,
                "firm", this.name,
                "country", "Singapore",
                "practice_area", "",
                "email", email,
                "phone", "6565344877"
        );
    }
}
