package org.example.src.sites.americas;

import org.example.exceptions.LawyerExceptions;
import org.example.src.entities.BaseSites.ByNewPage;
import org.example.src.entities.MyDriver;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;

import java.util.List;
import java.util.Map;

public class ARIFA extends ByNewPage {

    public ARIFA() {
        super(
                "ARIFA",
                "https://www.arifa.com/lawyers.html",
                5
        );
    }

    @Override
    protected void accessPage(int index) throws InterruptedException {
        String otherUrl = "https://www.arifa.com/lawyers.html?page=" + (index + 1);
        String url = index == 0 ? this.link : otherUrl;
        this.driver.get(url);
        MyDriver.waitForPageToLoad();
    }

    @Override
    protected List<WebElement> getLawyersInPage() {
        List<WebElement> lawyers = MyDriver.wait.findElements(By.cssSelector(".isotope-item"));
        return this.siteUtl.filterLawyersInPage(lawyers, null, true);
    }

    @Override
    public String openNewTab(WebElement lawyer) throws LawyerExceptions {
        String link = lawyer.findElement(By.cssSelector("h3.ts-name a")).getAttribute("href");
        MyDriver.openNewTab(link);
        return link;
    }

    @Override
    protected Object getLawyer(WebElement lawyer) throws Exception {
        String name = extractor.extractLawyerText(lawyer, new By[]{By.cssSelector("h3.ts-name")}, "NAME", LawyerExceptions::nameException);
        String role = extractor.extractLawyerText(lawyer, new By[]{By.className("ts-designation")}, "ROLE", LawyerExceptions::roleException);

        String profileLink = this.openNewTab(lawyer);
        WebElement container = MyDriver.wait.findElement(By.className("col-lg-6"));

        String[] socials = this.getSocials(container.findElements(By.tagName("a")), false);

        return Map.of(
                "link", profileLink,
                "name", name,
                "role", role,
                "firm", this.name,
                "country", "Panama",
                "practice_area", "",
                "email", socials[0],
                "phone", extractor.extractLawyerText(container, new By[]{By.xpath(".//span[contains(strong, 'T')]")}, "PHONE", LawyerExceptions::phoneException)
        );
    }
}