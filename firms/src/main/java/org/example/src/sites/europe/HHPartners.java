package org.example.src.sites.europe;

import org.example.exceptions.LawyerExceptions;
import org.example.src.entities.BaseSites.ByPage;
import org.example.src.entities.MyDriver;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;

import java.util.List;
import java.util.Map;

public class HHPartners extends ByPage {

    public HHPartners() {
        super(
                "HH Partners",
                "https://www.hhpartners.fi/en/staff/",
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
        List<WebElement> lawyers = MyDriver.wait.findElements(By.className("staff-gallery-element"));
        return this.siteUtl.filterLawyersInPage(lawyers, new By[] {By.className("staff-gallery-job-title")}, true);
    }

    @Override
    protected Object getLawyer(WebElement lawyer) throws Exception {
        String[] socials = super.getSocials(lawyer.findElements(By.tagName("a")), false);

        return Map.of(
                "link", extractor.extractLawyerAttribute(lawyer, new By[]{By.className("staff-gallery-name"), By.tagName("a")}, "LINK", "href", LawyerExceptions::linkException),
                "name", extractor.extractLawyerText(lawyer, new By[]{By.className("staff-gallery-name")}, "NAME", LawyerExceptions::nameException),
                "role", extractor.extractLawyerText(lawyer, new By[]{By.className("staff-gallery-job-title")}, "ROLE", LawyerExceptions::roleException),
                "firm", this.name,
                "country", "Finland",
                "practice_area", "",
                "email", socials[0],
                "phone", socials[1].isEmpty() ? "3589177613" : socials[1]
        );
    }
}
