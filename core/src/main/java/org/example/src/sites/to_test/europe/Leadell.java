package org.example.src.sites.to_test.europe;

import org.example.exceptions.LawyerExceptions;
import org.example.src.entities.BaseSites.ByNewPage;
import org.example.src.entities.MyDriver;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;

import java.util.List;
import java.util.Map;

public class Leadell extends ByNewPage {

    public Leadell() {
        super(
                "Leadell Balčiūnas & Grajauskas",
                "https://leadell.lt/en/people/",
                1
        );
    }

    @Override
    protected void accessPage(int index) throws InterruptedException {
        this.driver.get(this.link);
        MyDriver.waitForPageToLoad();
        Thread.sleep(1000L);
    }

    @Override
    protected List<WebElement> getLawyersInPage() {
        List<WebElement> lawyers = MyDriver.wait.findElements(By.cssSelector("li.team-member"));
        return this.siteUtl.filterLawyersInPage(lawyers, new By[] {By.className("position")}, true);
    }

    @Override
    public String openNewTab(WebElement lawyer) throws LawyerExceptions {
        String link = extractor.extractLawyerAttribute(lawyer, new By[]{By.tagName("a")}, "LINK", "href", LawyerExceptions::linkException);
        MyDriver.openNewTab(link);
        return link;
    }

    @Override
    protected Object getLawyer(WebElement lawyer) throws Exception {
        String name = extractor.extractLawyerText(lawyer, new By[]{By.className("name")}, "NAME", LawyerExceptions::nameException);
        String role = extractor.extractLawyerText(lawyer, new By[]{By.className("position")}, "ROLE", LawyerExceptions::roleException);

        String link = this.openNewTab(lawyer);

        WebElement container = MyDriver.wait.findElement(By.className("people-main-info"));
        String[] socials = super.getSocials(container.findElements(By.cssSelector(".person-info span")), true);

        return Map.of(
                "link", link,
                "name", name,
                "role", role,
                "firm", this.name,
                "country", "Lithuania",
                "practice_area", "",
                "email", socials[0],
                "phone", socials[1].isEmpty() ? "37052487467" : socials[1]
        );
    }
}
