package org.example.src.sites.to_test;

import org.example.exceptions.LawyerExceptions;
import org.example.src.entities.BaseSites.ByPage;
import org.example.src.entities.MyDriver;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;

import java.util.List;
import java.util.Map;

public class MKK extends ByPage {

    public MKK() {
        super(
                "MKK",
                "http://www.mkklaw.net/attorneys_main_eng.asp",
                1
        );
    }

    @Override
    protected void accessPage(int index) {
        this.driver.get(this.link);
        MyDriver.waitForPageToLoad();
    }

    @Override
    protected List<WebElement> getLawyersInPage() {
        List<WebElement> lawyers = driver.findElements(By.cssSelector("a[href*='attorneys/']"));
        return this.siteUtl.filterLawyersInPage(lawyers, new By[]{By.cssSelector("h2:first-of-type")}, true);
    }

    @Override
    protected Object getLawyer(WebElement lawyer) throws Exception {
        return Map.of(
                "link", extractor.extractLawyerAttribute(lawyer, new By[]{}, "LINK", "href", LawyerExceptions::linkException),
                "name", extractor.extractLawyerText(lawyer, new By[]{By.tagName("h1")}, "NAME", LawyerExceptions::nameException),
                "role", extractor.extractLawyerText(lawyer, new By[]{By.cssSelector("h2:first-of-type")}, "ROLE", LawyerExceptions::roleException),
                "firm", this.name,
                "country", "Indonesia",
                "practice_area", "",
                "email", extractor.extractLawyerText(lawyer, new By[]{By.cssSelector("h2:last-of-type")}, "EMAIL", LawyerExceptions::emailException),
                "phone", "62215155555"
        );
    }
}
