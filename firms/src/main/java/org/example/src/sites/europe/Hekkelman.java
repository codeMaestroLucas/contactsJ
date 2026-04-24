package org.example.src.sites.europe;

import org.example.exceptions.LawyerExceptions;
import org.example.src.entities.BaseSites.ByNewPage;
import org.example.src.entities.MyDriver;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;

import java.util.List;
import java.util.Map;

public class Hekkelman extends ByNewPage {

    public Hekkelman() {
        super(
                "HEKKELMAN",
                "https://www.hekkelman.nl/onze-mensen/",
                1
        );
    }

    @Override
    protected void accessPage(int index) throws InterruptedException {
        this.driver.get(this.link);
        MyDriver.waitForPageToLoad();
        MyDriver.clickOnElementMultipleTimes(
                By.xpath("//*[@id=\"main\"]/div[2]/div/button"),
                4, 0.5
        );
    }

    @Override
    protected List<WebElement> getLawyersInPage() {
        String[] validRoles = {"notaris"};

        List<WebElement> lawyers = MyDriver.wait.findElements(By.cssSelector("a.c-employee-card"));
        return this.siteUtl.filterLawyersInPage(lawyers, new By[]{By.className("c-employee-card__function")}, true, validRoles);
    }

    @Override
    public String openNewTab(WebElement lawyer) throws LawyerExceptions {
        String url = lawyer.getAttribute("href");
        MyDriver.openNewTab(url);
        return url;
    }

    @Override
    protected Object getLawyer(WebElement lawyer) throws Exception {
        String name = extractor.extractLawyerText(lawyer, new By[]{By.className("c-employee-card__name")}, "NAME", LawyerExceptions::nameException);
        String role = extractor.extractLawyerText(lawyer, new By[]{By.className("c-employee-card__function")}, "ROLE", LawyerExceptions::roleException);

        String link = this.openNewTab(lawyer);
        WebElement container = MyDriver.wait.findElement(By.className("c-hero-person__content"));

        String[] socials = this.getSocials(container.findElements(By.cssSelector(".c-hero-person__contact a")), false);

        return Map.of(
                "link", link,
                "name", name,
                "role", "Notary",
                "firm", this.name,
                "country", "Netherlands",
                "practice_area", "",
                "email", socials[0],
                "phone", socials[1].isEmpty() ? "31243828319" : socials[1]
        );
    }
}
