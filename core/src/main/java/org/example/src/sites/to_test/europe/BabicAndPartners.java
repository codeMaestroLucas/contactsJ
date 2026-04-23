package org.example.src.sites.to_test.europe;

import org.example.exceptions.LawyerExceptions;
import org.example.src.entities.BaseSites.ByPage;
import org.example.src.entities.MyDriver;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import java.util.List;
import java.util.Map;

public class BabicAndPartners extends ByPage {

    public BabicAndPartners() {
        super(
                "Babic & Partners",
                "https://www.babic-partners.hr/legal-team/marija-gregoric/",
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
        return MyDriver.wait.findElements(By.cssSelector("div.article_content_left"));
    }

    @Override
    public Object getLawyer(WebElement lawyer) throws Exception {
        String[] socials = super.getSocials(lawyer.findElements(By.tagName("p")), true);

        return Map.of(
                "link", this.link,
                "name", extractor.extractLawyerText(lawyer, new By[]{By.className("entry-title")}, "NAME", LawyerExceptions::nameException),
                "role", extractor.extractLawyerText(lawyer, new By[]{By.xpath(".//p[contains(text(), 'Partner')]")}, "ROLE", LawyerExceptions::roleException),
                "firm", this.name,
                "country", "Croatia",
                "practice_area", "",
                "email", socials[0],
                "phone", "38514821211"
        );
    }
}
