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
import java.util.Objects;

public class DNP extends ByNewPage {

    public DNP() {
        super(
                "D&P",
                "https://dnplawgroup.com/we-are-david-and-paulian",
                1
        );
    }

    @Override
    protected void accessPage(int index) throws InterruptedException {
        this.driver.get(this.link);
        MyDriver.waitForPageToLoad();
        Thread.sleep(1500L);
    }

    @Override
    protected List<WebElement> getLawyersInPage() {
        try {
            WebDriverWait wait = new WebDriverWait(this.driver, Duration.ofSeconds(10));
            // The shapes are specific to an image map, targetting the polygon elements
            return wait.until(ExpectedConditions.presenceOfAllElementsLocatedBy(By.cssSelector("polygon.imp-shape")));
        } catch (Exception e) {
            return List.of();
        }
    }

    @Override
    public String openNewTab(WebElement lawyer) throws LawyerExceptions {
        // Since polygons usually trigger JS, we try to click or extract the title if used for routing
        MyDriver.clickOnElement(lawyer);
        MyDriver.waitForPageToLoad();
        return driver.getCurrentUrl();
    }

    @Override
    protected Object getLawyer(WebElement lawyer) throws Exception {
        this.openNewTab(lawyer);
        WebElement container = driver.findElement(By.className("single-member-content"));

        String name = extractor.extractLawyerText(container, new By[]{By.className("title")}, "NAME", LawyerExceptions::nameException);
        String role = extractor.extractLawyerText(container, new By[]{By.className("subtitle")}, "ROLE", LawyerExceptions::roleException);
        String practice = extractor.extractLawyerText(container, new By[]{By.xpath("//h3[contains(.,'Key Practice')]/following-sibling::ul/li")}, "PRACTICE AREA", LawyerExceptions::practiceAreaException);

        String[] socials = super.getSocials(container.findElements(By.tagName("a")), false);

        return Map.of(
                "link", Objects.requireNonNull(driver.getCurrentUrl()),
                "name", name,
                "role", role,
                "firm", this.name,
                "country", "Malaysia",
                "practice_area", practice,
                "email", socials[0],
                "phone", socials[1].isEmpty() ? "60327111000" : socials[1]
        );
    }
}
