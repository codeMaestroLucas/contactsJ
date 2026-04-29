package org.example.src.sites.europe;

import org.example.src.entities.BaseSites.ByNewPage;
import org.example.src.entities.MyDriver;
import org.example.exceptions.LawyerExceptions;
import org.example.src.utils.VCard;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;

import java.util.List;
import java.util.Map;

public class BansSzaboRauschPartners extends ByNewPage {

    public BansSzaboRauschPartners() {
        super(
                "Bán, S. Szabó, Rausch & Partners",
                "https://www.bansszabo.hu/en/our-team/",
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
        List<WebElement> lawyers = MyDriver.wait.findElements(By.cssSelector(".cards a"));

        List<WebElement> filteredLawyersInPage = this.siteUtl.filterLawyersInPage(lawyers, new By[]{By.tagName("p")}, true, validRoles);
        filteredLawyersInPage.removeFirst();
        return filteredLawyersInPage;
    }

    @Override
    public String openNewTab(WebElement lawyer) throws LawyerExceptions {
        String url = lawyer.getAttribute("href");
        MyDriver.openNewTab(url);
        return url;
    }

    @Override
    protected Object getLawyer(WebElement lawyer) throws Exception {
        String name = extractor.extractLawyerText(lawyer, new By[]{By.tagName("h4")}, "NAME", LawyerExceptions::nameException);
        String role = extractor.extractLawyerText(lawyer, new By[]{By.tagName("p")}, "ROLE", LawyerExceptions::roleException);

        String link = this.openNewTab(lawyer);
        WebElement container = MyDriver.wait.findElement(By.className("data"));

        String practiceArea = extractor.extractLawyerText(container, new By[]{By.tagName("h6")}, "PRACTICE AREA", LawyerExceptions::practiceAreaException);
        
        String[] socials = super.getSocialsFromText(container.getText());

        return Map.of(
                "link", link,
                "name", name,
                "role", role,
                "firm", this.name,
                "country", "Hungary",
                "practice_area", practiceArea,
                "email", socials[0],
                "phone", socials[1]
        );
    }
}
