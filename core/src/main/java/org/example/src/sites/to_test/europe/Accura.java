package org.example.src.sites.to_test.europe;

import org.example.exceptions.LawyerExceptions;
import org.example.src.entities.BaseSites.ByPage;
import org.example.src.entities.MyDriver;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;

import java.util.List;
import java.util.Map;

public class Accura extends ByPage {

    public Accura() {
        super(
                "Accura",
                "https://accura.dk/en/professionals/",
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
        List<WebElement> lawyers = MyDriver.wait.findElements(By.className("employee-card"));
        return siteUtl.filterLawyersInPage(lawyers, new By[]{By.className("card__label")}, true, validRoles);
    }

    @Override
    public Object getLawyer(WebElement lawyer) throws Exception {
        return Map.of(
                "link", extractor.extractLawyerAttribute(lawyer, new By[]{By.tagName("a")}, "LINK", "href", LawyerExceptions::linkException),
                "name", extractor.extractLawyerText(lawyer, new By[]{By.className("card__title")}, "NAME", LawyerExceptions::nameException),
                "role", extractor.extractLawyerText(lawyer, new By[]{By.className("card__label")}, "ROLE", LawyerExceptions::roleException),
                "firm", this.name,
                "country", "Denmark",
                "practice_area", "",
                "email", extractor.extractLawyerText(lawyer, new By[]{By.className("card__email")}, "EMAIL", LawyerExceptions::emailException),
                "phone", extractor.extractLawyerText(lawyer, new By[]{By.className("card__phone")}, "PHONE", LawyerExceptions::phoneException)
        );
    }
}
