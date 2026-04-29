package org.example.src.sites.to_test.americas;

import org.example.exceptions.LawyerExceptions;
import org.example.src.entities.BaseSites.ByPage;
import org.example.src.entities.MyDriver;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;

import java.util.List;
import java.util.Map;

public class Torys extends ByPage {

    public Torys() {
        super(
                "Torys",
                "https://www.torys.com/people",
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
        List<WebElement> lawyers = MyDriver.wait.findElements(By.cssSelector("li.zn-search-results__list-item"));
        return this.siteUtl.filterLawyersInPage(lawyers, new By[]{By.className("zn-item-with-image__content-eyebrow")}, true);
    }

    @Override
    protected Object getLawyer(WebElement lawyer) throws Exception {
        String[] socials = this.getSocials(lawyer.findElements(By.cssSelector("a.zn-item-with-image__text-icon")), true);

        return Map.of(
                "link", extractor.extractLawyerAttribute(lawyer, new By[]{By.className("zn-item-with-image__content-link")}, "LINK", "href", LawyerExceptions::linkException),
                "name", extractor.extractLawyerText(lawyer, new By[]{By.className("zn-item-with-image__content-title")}, "NAME", LawyerExceptions::nameException),
                "role", extractor.extractLawyerText(lawyer, new By[]{By.className("zn-item-with-image__content-eyebrow")}, "ROLE", LawyerExceptions::roleException),
                "firm", this.name,
                "country", "Canada",
                "practice_area", "",
                "email", socials[0],
                "phone", socials[1].isEmpty() ? "4168657347" : socials[1]
        );
    }
}
