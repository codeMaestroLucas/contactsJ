package org.example.src.sites.to_test;

import org.example.exceptions.LawyerExceptions;
import org.example.src.entities.BaseSites.ByPage;
import org.example.src.entities.MyDriver;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.time.Duration;
import java.util.List;
import java.util.Map;

public class CliffeDekkerHofmeyr extends ByPage {

    public CliffeDekkerHofmeyr() {
        super(
                "Cliffe Dekker Hofmeyr",
                "https://www.cliffedekkerhofmeyr.com/en/people/",
                1
        );
    }

    @Override
    protected void accessPage(int index) throws InterruptedException {
        this.driver.get(this.link);
        MyDriver.waitForPageToLoad();
        MyDriver.rollDownToBottom(1.0);
    }

    @Override
    protected List<WebElement> getLawyersInPage() {
        try {
            WebDriverWait wait = new WebDriverWait(this.driver, Duration.ofSeconds(10L));
            List<WebElement> lawyers = wait.until(ExpectedConditions.presenceOfAllElementsLocatedBy(By.className("people-single")));
            return this.siteUtl.filterLawyersInPage(lawyers, new By[]{By.className("people-single__position")}, true);
        } catch (Exception e) {
            return List.of();
        }
    }

    @Override
    protected Object getLawyer(WebElement lawyer) throws Exception {
        String name = extractor.extractLawyerText(lawyer, new By[]{By.className("people-single__title")}, "NAME", LawyerExceptions::nameException);
        String role = extractor.extractLawyerText(lawyer, new By[]{By.className("people-single__position")}, "ROLE", LawyerExceptions::roleException);
        String practice = extractor.extractLawyerText(lawyer, new By[]{By.className("people-single__details")}, "PRACTICE AREA", LawyerExceptions::practiceAreaException);

        List<WebElement> socialElements = lawyer.findElements(By.cssSelector("a[href^='mailto:']"));
        String[] socials = super.getSocials(socialElements, false);

        return Map.of(
                "link", extractor.extractLawyerAttribute(lawyer, new By[]{By.cssSelector(".people-single__title a")}, "LINK", "href", LawyerExceptions::linkException),
                "name", name,
                "role", role,
                "firm", this.name,
                "country", "South Africa",
                "practice_area", practice,
                "email", socials[0],
                "phone", socials[1]
        );
    }
}
