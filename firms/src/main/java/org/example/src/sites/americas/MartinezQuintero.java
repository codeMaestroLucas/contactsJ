package org.example.src.sites.americas;

import org.example.exceptions.LawyerExceptions;
import org.example.src.entities.BaseSites.ByPage;
import org.example.src.entities.MyDriver;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;

import java.util.List;
import java.util.Map;

public class MartinezQuintero extends ByPage {

    public MartinezQuintero() {
        super(
                "Martínez Quintero Mendoza González Laguado & De La Rosa",
                "https://www.mqmgld.com/en/team",
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
        List<WebElement> lawyers = MyDriver.wait.findElements(By.cssSelector(".w-dyn-item"));
        return this.siteUtl.filterLawyersInPage(lawyers, new By[]{By.className("cargo")}, true);
    }

    @Override
    protected Object getLawyer(WebElement lawyer) throws Exception {
        return Map.of(
                "link", extractor.extractLawyerAttribute(lawyer, new By[]{By.tagName("a")}, "LINK", "href", LawyerExceptions::linkException),
                "name", extractor.extractLawyerText(lawyer, new By[]{By.tagName("h4")}, "NAME", LawyerExceptions::nameException),
                "role", extractor.extractLawyerText(lawyer, new By[]{By.className("cargo")}, "ROLE", LawyerExceptions::roleException),
                "firm", this.name,
                "country", "Colombia",
                "practice_area", "",
                "email", extractor.extractLawyerText(lawyer, new By[]{By.className("correo")}, "EMAIL", LawyerExceptions::emailException),
                "phone", "576013268600"
        );
    }
}
