package org.example.src.sites.byNewPage;

import org.example.exceptions.LawyerExceptions;
import org.example.src.entities.BaseSites.ByNewPage;
import org.example.src.entities.MyDriver;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import java.util.List;
import java.util.Map;

public class TEGOS extends ByNewPage {

    public TEGOS() {
        super(
                "TEGOS",
                "https://tegos.legal/people/",
                1,
                2
        );
    }

    @Override
    protected void accessPage(int index) throws InterruptedException {
        this.driver.get(this.link);
        MyDriver.waitForPageToLoad();
        MyDriver.clickOnElementMultipleTimes(
                By.xpath("/html/body/main/section[2]/div/div[2]/div[1]/div[2]/span"),
                8, 0.5
        );
    }

    @Override
    protected List<WebElement> getLawyersInPage() {
        List<WebElement> lawyers = MyDriver.wait.findElements(By.cssSelector("li.item-corner-base"));
        return this.siteUtl.filterLawyersInPage(lawyers, new By[]{By.className("people-list-tags")}, true);
    }

    @Override
    public String openNewTab(WebElement lawyer) throws LawyerExceptions {
        String link = extractor.extractLawyerAttribute(lawyer, new By[]{By.tagName("a")}, "LINK", "href", LawyerExceptions::linkException);
        MyDriver.openNewTab(link);
        return link;
    }

    @Override
    public Object getLawyer(WebElement lawyer) throws Exception {
        String name = extractor.extractLawyerText(lawyer, new By[]{By.className("people-list-title")}, "NAME", LawyerExceptions::nameException);
        String role = extractor.extractLawyerText(lawyer, new By[]{By.className("people-list-tags")}, "ROLE", LawyerExceptions::roleException);
        String country = extractor.extractLawyerText(lawyer, new By[]{By.className("people-list-location")}, "COUNTRY", LawyerExceptions::countryException);

        String link = this.openNewTab(lawyer);

        WebElement container = driver.findElement(By.className("single-people-intro-right-info"));
        String[] socials = super.getSocials(container.findElements(By.tagName("a")), false);

        return Map.of(
                "link", link,
                "name", name,
                "role", role,
                "firm", this.name,
                "country", country,
                "practice_area", "",
                "email", socials[0],
                "phone", socials[1].isEmpty() ? "3726257700" : socials[1]
        );
    }
}