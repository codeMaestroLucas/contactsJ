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

public class Pitblado extends ByNewPage {

    public Pitblado() {
        super(
                "Pitblado",
                "https://www.pitblado.com/our-people",
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
            List<WebElement> lawyers = wait.until(ExpectedConditions.presenceOfAllElementsLocatedBy(
                    By.cssSelector(".lawyer-tile")
            ));
            return this.siteUtl.filterLawyersInPage(lawyers, new By[]{By.cssSelector(".title")}, true, validRoles);
        } catch (Exception e) {
            return List.of();
        }
    }

    @Override
    public String openNewTab(WebElement lawyer) throws LawyerExceptions {
        String profileUrl = extractor.extractLawyerAttribute(lawyer, new By[]{By.cssSelector("h3.name a")}, "LINK", "href", LawyerExceptions::linkException);
        MyDriver.openNewTab(profileUrl);
        return profileUrl;
    }

    @Override
    protected Object getLawyer(WebElement lawyer) throws Exception {
        this.openNewTab(lawyer);
        WebElement content = driver.findElement(By.cssSelector(".main"));
        String[] socials = super.getSocials(content.findElements(By.tagName("a")), false);

        return Map.of(
                "link", Objects.requireNonNull(driver.getCurrentUrl()),
                "name", extractor.extractLawyerText(content, new By[]{By.cssSelector("h1.name")}, "NAME", LawyerExceptions::nameException),
                "role", extractor.extractLawyerText(content, new By[]{By.cssSelector("p.title")}, "ROLE", LawyerExceptions::roleException),
                "firm", this.name,
                "country", "Canada",
                "practice_area", "",
                "email", socials[0],
                "phone", socials[1].isEmpty() ? "2049560560" : socials[1]
        );
    }
}