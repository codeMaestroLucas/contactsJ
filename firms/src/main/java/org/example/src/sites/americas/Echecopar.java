package org.example.src.sites.americas;

import org.example.exceptions.LawyerExceptions;
import org.example.src.entities.BaseSites.ByPage;
import org.example.src.entities.MyDriver;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import java.util.List;
import java.util.Map;

public class Echecopar extends ByPage {

    public Echecopar() {
        super(
                "Echecopar",
                "https://echecopar.com/equipo/",
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
        WebElement div = MyDriver.wait.findElement(By.xpath("//*[@id=\"associates-container\"]"));
        List<WebElement> lawyers = div.findElements(By.tagName("article"));
        return this.siteUtl.filterLawyersInPage(lawyers, new By[]{By.cssSelector("span.font-normal")}, true, validRoles);
    }

    @Override
    protected Object getLawyer(WebElement lawyer) throws Exception {
        String[] socials = this.getSocialsFromText(lawyer.getAttribute("innerText"));

        return Map.of(
                "link", extractor.extractLawyerAttribute(lawyer, new By[]{By.tagName("a")}, "LINK", "href", LawyerExceptions::linkException),
                "name", extractor.extractLawyerText(lawyer, new By[]{By.cssSelector("span.font-medium")}, "NAME", LawyerExceptions::nameException),
                "role", extractor.extractLawyerText(lawyer, new By[]{By.cssSelector("span.font-normal")}, "ROLE", LawyerExceptions::roleException),
                "firm", this.name,
                "country", "Peru",
                "practice_area", "",
                "email", socials[0],
                "phone", socials[1].isEmpty() ? "5116188500" : socials[1]
        );
    }
}
