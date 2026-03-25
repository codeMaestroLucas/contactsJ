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
import java.util.stream.Collectors;

public class BoyneClarke extends ByPage {

    public BoyneClarke() {
        super(
                "BoyneClarke",
                "https://boyneclarke.com/lawyers/",
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
            return wait.until(ExpectedConditions.presenceOfAllElementsLocatedBy(By.className("lawyer-profile")));
        } catch (Exception e) {
            return List.of();
        }
    }

    @Override
    public Object getLawyer(WebElement lawyer) throws Exception {
        By[] nameBy = {By.tagName("h3")};
        By[] linkBy = {By.cssSelector("h3 a")};

        List<WebElement> practiceElements = lawyer.findElements(By.cssSelector(".services-provided span"));
        String practiceArea = practiceElements.stream().map(WebElement::getText).collect(Collectors.joining(", "));

        String[] socials = super.getSocials(lawyer.findElements(By.tagName("a")), false);

        return Map.of(
                "link", extractor.extractLawyerAttribute(lawyer, linkBy, "LINK", "href", LawyerExceptions::linkException),
                "name", extractor.extractLawyerText(lawyer, nameBy, "NAME", LawyerExceptions::nameException),
                "role", "Lawyer",
                "firm", this.name,
                "country", "Canada",
                "practice_area", practiceArea,
                "email", socials[0],
                "phone", socials[1].isEmpty() ? "9024699500" : socials[1]
        );
    }
}
