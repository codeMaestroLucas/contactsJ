package org.example.src.sites.europe;

import org.example.exceptions.LawyerExceptions;
import org.example.src.entities.BaseSites.ByNewPage;
import org.example.src.entities.MyDriver;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import java.util.List;
import java.util.Map;

public class EHRechtsanwalte extends ByNewPage {

    public EHRechtsanwalte() {
        super(
                "E+H Rechtsanwälte",
                "https://www.eh.at/en/team/",
                1
        );
    }

    @Override
    protected void accessPage(int index) throws InterruptedException {
        this.driver.get(this.link);
        MyDriver.waitForPageToLoad();
        MyDriver.clickOnElementMultipleTimes(
                By.xpath("//*[@id=\"filter-results\"]/div/nav[2]/a"),
                5, 0.5
        );
    }

    @Override
    protected List<WebElement> getLawyersInPage() {
        List<WebElement> lawyers = MyDriver.wait.findElements(By.cssSelector("article.team"));
        return this.siteUtl.filterLawyersInPage(lawyers, new By[]{By.className("team-card__position")}, true);
    }

    @Override
    public String openNewTab(WebElement lawyer) throws LawyerExceptions {
        String link = extractor.extractLawyerAttribute(lawyer, new By[]{By.cssSelector("a")}, "LINK", "href", LawyerExceptions::linkException);
        MyDriver.openNewTab(link);
        return link;
    }

    @Override
    public Object getLawyer(WebElement lawyer) throws Exception {
        String name = extractor.extractLawyerText(lawyer, new By[]{By.className("team-card__title")}, "NAME", LawyerExceptions::nameException);
        String role = extractor.extractLawyerText(lawyer, new By[]{By.className("team-card__position")}, "ROLE", LawyerExceptions::roleException);
        String pa = extractor.extractLawyerText(lawyer, new By[]{By.cssSelector("div.post-terms.post-terms-group")}, "PRACTICE AREA", LawyerExceptions::practiceAreaException);

        String link = this.openNewTab(lawyer);
        WebElement container = driver.findElement(By.xpath("/html/body/div[2]/section[1]/div/div[1]/div/section[2]/div/div/div"));

        String[] socials = super.getSocials(container.findElements(By.tagName("a")), false);
        String country = extractor.extractLawyerText(driver.findElement(By.tagName("body")), new By[]{By.xpath("/html/body/div[2]/section[1]/div/div[1]/div/section[2]/div/div/div/div[1]/div/div/div/ul/li")}, "COUNTRY", LawyerExceptions::countryException);
        country = country.contains("brussels") ? "Belgium" : "Austria";

        return Map.of(
                "link", link,
                "name", name,
                "role", role,
                "firm", this.name,
                "country", country,
                "practice_area", pa,
                "email", socials[0],
                "phone", socials[1].isEmpty() ? "43160636470" : socials[1]
        );
    }
}
