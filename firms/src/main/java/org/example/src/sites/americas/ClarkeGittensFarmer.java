package org.example.src.sites.americas;

import org.example.exceptions.LawyerExceptions;
import org.example.src.entities.BaseSites.ByNewPage;
import org.example.src.entities.MyDriver;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;

import java.util.List;
import java.util.Map;

public class ClarkeGittensFarmer extends ByNewPage {

    public ClarkeGittensFarmer() {
        super(
                "Clarke Gittens Farmer",
                "https://cgf.law/partners/",
                2
        );
    }

    @Override
    protected void accessPage(int index) throws InterruptedException {
        String otherUrl = "https://cgf.law/associates/";
        String url = index == 0 ? this.link : otherUrl;
        this.driver.get(url);
        MyDriver.waitForPageToLoad();
    }

    @Override
    protected List<WebElement> getLawyersInPage() {
        return MyDriver.wait.findElements(By.className("team-member-card-item"));
    }

    @Override
    public String openNewTab(WebElement lawyer) throws LawyerExceptions {
        String url = extractor.extractLawyerAttribute(lawyer, new By[]{}, "LINK", "href", LawyerExceptions::linkException);
        MyDriver.openNewTab(url);
        return url;
    }

    @Override
    protected Object getLawyer(WebElement lawyer) throws Exception {
        String name = extractor.extractLawyerText(lawyer, new By[]{By.tagName("h3")}, "NAME", LawyerExceptions::nameException);

        String link = this.openNewTab(lawyer);
        WebElement container = MyDriver.wait.findElement(By.id("content"));

        String[] socials = this.getSocials(MyDriver.wait.findElements(By.cssSelector(".contact-info a")), false);

        return Map.of(
                "link", link,
                "name", name,
                "role", "",
                "firm", this.name,
                "country", "Barbados",
                "practice_area", "",
                "email", socials[0],
                "phone", socials[1].isEmpty() ? "2464366287" : socials[1]
        );
    }
}
