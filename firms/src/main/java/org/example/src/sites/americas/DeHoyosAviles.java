package org.example.src.sites.americas;

import org.example.exceptions.LawyerExceptions;
import org.example.src.entities.BaseSites.ByNewPage;
import org.example.src.entities.MyDriver;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;

import java.util.List;
import java.util.Map;

public class DeHoyosAviles extends ByNewPage {

    public DeHoyosAviles() {
        super(
                "De Hoyos Aviles",
                "https://www.dha.mx/team-dha",
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
            List<WebElement> lawyers = MyDriver.wait.findElements(By.cssSelector("p > span > a[href*='https://www.dha.mx/']"));
            // Removendo o segundo
            lawyers.remove(1);
            return lawyers;
        } catch (Exception e) {
            return List.of();
        }
    }

    @Override
    public String openNewTab(WebElement lawyer) throws LawyerExceptions {
        String link = lawyer.getAttribute("href");
        MyDriver.openNewTab(link);
        return link;
    }

    @Override
    protected Object getLawyer(WebElement lawyer) throws Exception {

        String link = this.openNewTab(lawyer);
        WebElement container = driver.findElement(By.cssSelector("div[data-mesh-id^='comp-'][data-mesh-id$='inlineContent']"));
        String name = extractor.extractLawyerText(driver.findElement(By.tagName("body")), new By[]{By.tagName("h1")}, "NAME", LawyerExceptions::nameException);
        String[] socials = super.getSocials(container.findElements(By.tagName("a")), false);
        return Map.of(
                "link", link,
                "name", name,
                "role", "Partner",
                "firm", this.name,
                "country", "Mexico",
                "practice_area", "",
                "email", socials[0].replace("?subject=contact%20from%20website", ""),
                "phone", socials[1].isEmpty() ? "526646864848" : socials[1]
        );
    }
}
