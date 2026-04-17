package org.example.src.sites.americas;

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

public class Leaf extends ByNewPage {

    public Leaf() {
        super(
                "Leaf",
                "https://www.leaf-legal.com/team/",
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
                    By.cssSelector(".partner-card")
            ));
            return this.siteUtl.filterLawyersInPage(lawyers, new By[]{By.cssSelector("span.h4")}, true, validRoles);
        } catch (Exception e) {
            return List.of();
        }
    }

    @Override
    public String openNewTab(WebElement lawyer) throws LawyerExceptions {
        String profileUrl = extractor.extractLawyerAttribute(lawyer, new By[]{By.tagName("a")}, "LINK", "href", LawyerExceptions::linkException);
        MyDriver.openNewTab(profileUrl);
        return profileUrl;
    }

    @Override
    protected Object getLawyer(WebElement lawyer) throws Exception {
        this.openNewTab(lawyer);
        WebElement content = driver.findElement(By.cssSelector(".col-sm-6"));

        String[] socials = super.getSocials(content.findElements(By.tagName("a")), false);

        return Map.of(
                "link", Objects.requireNonNull(driver.getCurrentUrl()),
                "name", extractor.extractLawyerText(content, new By[]{By.tagName("h1")}, "NAME", LawyerExceptions::nameException),
                "role", extractor.extractLawyerText(content, new By[]{By.xpath("//td[span[contains(text(),'Position')]]/following-sibling::td")}, "ROLE", LawyerExceptions::roleException),
                "firm", this.name,
                "country", "China",
                "practice_area", extractor.extractLawyerText(content, new By[]{By.xpath("//td[span[contains(text(),'Specialties')]]/following-sibling::td")}, "PRACTICE AREA", LawyerExceptions::practiceAreaException),
                "email", socials[0],
                "phone", socials[1].isEmpty() ? "862162337226" : socials[1]
        );
    }
}
