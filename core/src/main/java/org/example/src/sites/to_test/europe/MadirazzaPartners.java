package org.example.src.sites.to_test.europe;

import org.example.exceptions.LawyerExceptions;
import org.example.src.entities.BaseSites.ByNewPage;
import org.example.src.entities.MyDriver;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;

import java.util.List;
import java.util.Map;

public class MadirazzaPartners extends ByNewPage {

    public MadirazzaPartners() {
        super(
                "Madirazza & Partners",
                "https://madirazza.hr/team/",
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
        try {
            List<WebElement> lawyers = MyDriver.wait.findElements(By.cssSelector("article.post"));
            return this.siteUtl.filterLawyersInPage(lawyers, new By[]{By.className("entry-content-col")}, true);
        } catch (Exception e) {
            return List.of();
        }
    }

    @Override
    public String openNewTab(WebElement lawyer) throws LawyerExceptions {
        String link = extractor.extractLawyerAttribute(lawyer, new By[]{By.cssSelector(".entry-title a")}, "LINK", "href", LawyerExceptions::linkException);
        MyDriver.openNewTab(link);
        return link;
    }

    @Override
    protected Object getLawyer(WebElement lawyer) throws Exception {
        String name = extractor.extractLawyerText(lawyer, new By[]{By.className("entry-title")}, "NAME", LawyerExceptions::nameException);
        String role = extractor.extractLawyerText(lawyer, new By[]{By.className("entry-content-col")}, "ROLE", LawyerExceptions::roleException).replace(name, "").trim();

        String link = this.openNewTab(lawyer);

        WebElement container = driver.findElement(By.className("details"));
        String[] socials = super.getSocialsFromText(container.getText());
        String practice = extractor.extractLawyerText(container, new By[]{By.xpath(".//h5[contains(text(),'Practice areas')]/following-sibling::p")}, "PRACTICE AREA", LawyerExceptions::practiceAreaException);

        return Map.of(
                "link", link,
                "name", name,
                "role", role,
                "firm", this.name,
                "country", "Croatia",
                "practice_area", practice,
                "email", socials[0],
                "phone", socials[1].isEmpty() ? "38514877280" : socials[1]
        );
    }
}
