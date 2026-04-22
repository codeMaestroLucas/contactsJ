package org.example.src.sites.europe;

import org.example.exceptions.LawyerExceptions;
import org.example.src.entities.BaseSites.ByNewPage;
import org.example.src.entities.MyDriver;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import java.util.List;
import java.util.Map;

public class Cambrian extends ByNewPage {

    public Cambrian() {
        super(
                "Cambrian",
                "https://www.cambrian.be/team/",
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
        List<WebElement> lawyers = MyDriver.wait.findElements(By.className("gc-team__member"));
        return this.siteUtl.filterLawyersInPage(lawyers, new By[]{By.className("gc-team__position")}, true);
    }

    @Override
    public String openNewTab(WebElement lawyer) throws LawyerExceptions {
        String link = extractor.extractLawyerAttribute(lawyer, new By[]{By.cssSelector("a.gc-team__name")}, "LINK", "href", LawyerExceptions::linkException);
        MyDriver.openNewTab(link);
        return link;
    }

    @Override
    public Object getLawyer(WebElement lawyer) throws Exception {
        String name = MyDriver.wait.findElement(By.tagName("img")).getAttribute("alt");
        String role = extractor.extractLawyerText(lawyer, new By[]{By.className("gc-team__position")}, "ROLE", LawyerExceptions::roleException);

        String link = this.openNewTab(lawyer);
        WebElement container = driver.findElement(By.className("gp-team__left"));
        String[] socials = super.getSocials(container.findElements(By.tagName("a")), false);

        return Map.of(
                "link", link,
                "name", name,
                "role", role,
                "firm", this.name,
                "country", "Belgium",
                "practice_area", "",
                "email", socials[0],
                "phone", "3223011400"
        );
    }
}
