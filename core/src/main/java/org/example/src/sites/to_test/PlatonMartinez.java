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

public class PlatonMartinez extends ByNewPage {

    public PlatonMartinez() {
        super(
                "Platon Martinez Flores San Pedro & Leaño",
                "https://platonmartinez.com/team",
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
            return wait.until(ExpectedConditions.presenceOfAllElementsLocatedBy(By.className("lawyers-portrait__item")));
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
        String name = extractor.extractLawyerText(lawyer, new By[]{By.tagName("h2")}, "NAME", LawyerExceptions::nameException);

        String link = this.openNewTab(lawyer);
        WebElement container = driver.findElement(By.className("lawyer__container"));

        String role = extractor.extractLawyerText(container, new By[]{By.className("lawyer__position")}, "ROLE", LawyerExceptions::roleException);
        String email = extractor.extractLawyerText(container, new By[]{By.className("lawyer__contact"), By.tagName("a")}, "EMAIL", LawyerExceptions::emailException);
        String practiceArea = extractor.extractLawyerText(container, new By[]{By.className("lawyer__practice-areas")}, "PRACTICE AREA", LawyerExceptions::practiceAreaException);

        return Map.of(
                "link", link,
                "name", name,
                "role", role,
                "firm", this.name,
                "country", "the Philippines",
                "practice_area", practiceArea,
                "email", email,
                "phone", "63288124587"
        );
    }
}
