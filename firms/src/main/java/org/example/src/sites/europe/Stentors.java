package org.example.src.sites.europe;

import org.example.exceptions.LawyerExceptions;
import org.example.src.entities.BaseSites.ByPage;
import org.example.src.entities.MyDriver;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;

import java.util.List;
import java.util.Map;

public class Stentors extends ByPage {

    public Stentors() {
        super(
                "Stentors",
                "https://stentors.eu/",
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
        List<WebElement> lawyers = MyDriver.wait.findElements(By.className("single-team-area"));
        return this.siteUtl.filterLawyersInPage(lawyers, new By[]{By.className("tlp-position")}, true);
    }

    @Override
    protected Object getLawyer(WebElement lawyer) throws Exception {
        String[] socials = super.getSocialsFromText(lawyer.getText());
        if (socials[0].isEmpty()) socials[0] = super.getSocials(lawyer.findElements(By.tagName("a")), true)[0];


        return Map.of(
                "link", extractor.extractLawyerAttribute(lawyer, new By[]{By.cssSelector("h3 a")}, "LINK", "href", LawyerExceptions::linkException),
                "name", extractor.extractLawyerText(lawyer, new By[]{By.className("tlp-name")}, "NAME", LawyerExceptions::nameException),
                "role", extractor.extractLawyerText(lawyer, new By[]{By.className("tlp-position")}, "ROLE", LawyerExceptions::roleException),
                "firm", this.name,
                "country", "Slovakia",
                "practice_area", "",
                "email", socials[0],
                "phone", socials[1].isEmpty() ? "421233527750" : socials[1]
        );
    }
}
