package org.example.src.sites.to_test.americas;

import org.example.exceptions.LawyerExceptions;
import org.example.src.entities.BaseSites.ByNewPage;
import org.example.src.entities.MyDriver;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;

import java.util.List;
import java.util.Map;

public class DaviesWardPhillipsVineberg extends ByNewPage {

    public DaviesWardPhillipsVineberg() {
        super(
                "Davies Ward Phillips & Vineberg",
                "https://www.dwpv.com/our-people",
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
        List<WebElement> lawyers = MyDriver.wait.findElements(By.className("people-detail-card-large"));
        return this.siteUtl.filterLawyersInPage(lawyers, new By[]{By.className("people-detail-card-large__info__second")}, true);
    }

    @Override
    public String openNewTab(WebElement lawyer) throws LawyerExceptions, InterruptedException {
        String url = extractor.extractLawyerAttribute(lawyer, new By[]{By.className("people-detail-card-large__anchor-wrapper")}, "LINK", "href", LawyerExceptions::linkException);
        MyDriver.openNewTab(url);
        return url;
    }

    @Override
    protected Object getLawyer(WebElement lawyer) throws Exception {
        String name = extractor.extractLawyerText(lawyer, new By[]{By.className("people-detail-card-large__info__first")}, "NAME", LawyerExceptions::nameException);
        String role = extractor.extractLawyerText(lawyer, new By[]{By.className("people-detail-card-large__info__second")}, "ROLE", LawyerExceptions::roleException);

        String link = this.openNewTab(lawyer);
        WebElement container = MyDriver.wait.findElement(By.className("people-detail-masthead__main-container"));

        String[] socials = this.getSocials(container.findElements(By.tagName("a")), false);

        return Map.of(
                "link", link,
                "name", name,
                "role", role,
                "firm", this.name,
                "country", "Canada",
                "practice_area", "",
                "email", socials[0],
                "phone", socials[1].isEmpty() ? "14168630900" : socials[1]
        );
    }
}
