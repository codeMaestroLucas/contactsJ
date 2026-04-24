package org.example.src.sites.europe;

import org.example.exceptions.LawyerExceptions;
import org.example.src.entities.BaseSites.ByNewPage;
import org.example.src.entities.MyDriver;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;

import java.util.List;
import java.util.Map;

public class PoelmannVanDenBroek extends ByNewPage {

    public PoelmannVanDenBroek() {
        super(
                "Poelmann van den Broek",
                "https://www.poelmannvandenbroek.nl/mensen",
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
        List<WebElement> lawyers = MyDriver.wait.findElements(By.cssSelector("a.panel--teammember"));
        return this.siteUtl.filterLawyersInPage(lawyers, new By[]{By.cssSelector("h3.primary")}, true, validRoles);
    }

    @Override
    public String openNewTab(WebElement lawyer) throws LawyerExceptions {
        String url = lawyer.getAttribute("href");
        MyDriver.openNewTab(url);
        return url;
    }

    @Override
    protected Object getLawyer(WebElement lawyer) throws Exception {
        String name = extractor.extractLawyerText(lawyer, new By[]{By.tagName("h3")}, "NAME", LawyerExceptions::nameException);
        String role = extractor.extractLawyerText(lawyer, new By[]{By.cssSelector("h3.primary")}, "ROLE", LawyerExceptions::roleException);

        String link = this.openNewTab(lawyer);
        WebElement container = MyDriver.wait.findElement(By.xpath("/html/body/section[1]/div[2]/div/div[2]/div/div"));

        String practiceArea = extractor.extractLawyerText(container, new By[]{By.tagName("h4")}, "PRACTICE AREA", LawyerExceptions::practiceAreaException);
        String[] socials = this.getSocials(container.findElements(By.tagName("a")), false);

        return Map.of(
                "link", link,
                "name", name,
                "role", role,
                "firm", this.name,
                "country", "the Netherlands",
                "practice_area", practiceArea.replace(role + ":", "").trim(),
                "email", socials[0],
                "phone", socials[1].isEmpty() ? "31243811411" : socials[1]
        );
    }
}
