package org.example.src.sites.americas;

import org.example.exceptions.LawyerExceptions;
import org.example.src.entities.BaseSites.ByNewPage;
import org.example.src.entities.MyDriver;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import java.util.List;
import java.util.Map;

public class BronsSalas extends ByNewPage {

    public BronsSalas() {
        super(
                "Brons & Salas",
                "https://brons.com.ar/en/our-team/",
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
        List<WebElement> lawyers = MyDriver.wait.findElements(By.cssSelector("li.eg-profesionales-wrapper"));
        return this.siteUtl.filterLawyersInPage(lawyers, new By[]{By.className("eg-profesionales-element-5")}, true);
    }

    @Override
    public String openNewTab(WebElement lawyer) throws LawyerExceptions {
        String link = extractor.extractLawyerAttribute(lawyer, new By[]{By.tagName("a")}, "LINK", "href", LawyerExceptions::linkException);
        MyDriver.openNewTab(link);
        return link;
    }

    @Override
    public Object getLawyer(WebElement lawyer) throws Exception {
        String name = extractor.extractLawyerText(lawyer, new By[]{By.className("eg-profesionales-element-1")}, "NAME", LawyerExceptions::nameException);
        String role = extractor.extractLawyerText(lawyer, new By[]{By.className("eg-profesionales-element-5")}, "ROLE", LawyerExceptions::roleException);

        String link = this.openNewTab(lawyer);
        WebElement container = MyDriver.wait.findElement(By.xpath("//div/div/div/div/div[1]/div/div/div[2]/div/div[3]/div"));
        String[] socials = super.getSocials(container.findElements(By.cssSelector("a")), false);

        return Map.of(
                "link", link,
                "name", name,
                "role", role,
                "firm", this.name,
                "country", "Argentina",
                "practice_area", "",
                "email", socials[0],
                "phone", socials[1].isEmpty() ? "5443197100" : socials[1]
        );
    }
}
