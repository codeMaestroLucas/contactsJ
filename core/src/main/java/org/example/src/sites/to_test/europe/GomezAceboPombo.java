package org.example.src.sites.to_test.europe;

import org.example.exceptions.LawyerExceptions;
import org.example.src.entities.BaseSites.ByPage;
import org.example.src.entities.MyDriver;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;

import java.util.List;
import java.util.Map;

public class GomezAceboPombo extends ByPage {

    public GomezAceboPombo() {
        super(
                "Gómez-Acebo & Pombo",
                "https://ga-p.com/en/people/",
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
        return MyDriver.wait.findElements(By.className("personaCard"));
    }

    @Override
    protected Object getLawyer(WebElement lawyer) throws Exception {
        return Map.of(
                "link", extractor.extractLawyerAttribute(lawyer, new By[]{By.tagName("a")}, "LINK", "href", LawyerExceptions::linkException),
                "name", extractor.extractLawyerText(lawyer, new By[]{By.className("name")}, "NAME", LawyerExceptions::nameException),
                "role", extractor.extractLawyerText(lawyer, new By[]{By.className("cargo")}, "ROLE", LawyerExceptions::roleException),
                "firm", this.name,
                "country", "Spain",
                "practice_area", extractor.extractLawyerText(lawyer, new By[]{By.className("areas")}, "PRACTICE AREA", LawyerExceptions::practiceAreaException),
                "email", extractor.extractLawyerText(lawyer, new By[]{By.cssSelector("a[href^='mailto:']")}, "EMAIL", LawyerExceptions::emailException),
                "phone", extractor.extractLawyerText(lawyer, new By[]{By.cssSelector("a[href^='tel:']")}, "PHONE", LawyerExceptions::phoneException)
        );
    }
}
