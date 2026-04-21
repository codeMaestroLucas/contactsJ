package org.example.src.sites.to_test.europe;

import org.example.exceptions.LawyerExceptions;
import org.example.src.entities.BaseSites.ByPage;
import org.example.src.entities.MyDriver;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import java.util.List;
import java.util.Map;

public class Dorda extends ByPage {

    public Dorda() {
        super(
                "DORDA",
                "https://www.dorda.at/en/team",
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
        List<WebElement> lawyers = MyDriver.wait.findElements(By.cssSelector(".node--type-persons.node--view-mode-teaser"));
        return this.siteUtl.filterLawyersInPage(lawyers, new By[]{By.tagName("strong")}, true);
    }

    @Override
    public Object getLawyer(WebElement lawyer) throws Exception {
        String name = extractor.extractLawyerText(lawyer, new By[]{By.className("card-title")}, "NAME", LawyerExceptions::nameException);
        String role = extractor.extractLawyerText(lawyer, new By[]{By.tagName("strong")}, "ROLE", LawyerExceptions::roleException);
        String[] socials = super.getSocials(lawyer.findElements(By.className("field--name-field-email")), true);

        return Map.of(
                "link", extractor.extractLawyerAttribute(lawyer, new By[]{By.className("profile")}, "LINK", "href", LawyerExceptions::linkException),
                "name", name,
                "role", role,
                "firm", this.name,
                "country", "Austria",
                "practice_area", "",
                "email", socials[0],
                "phone", "4315334700"
        );
    }
}
