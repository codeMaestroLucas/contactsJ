package org.example.src.sites.to_test.europe;

import org.example.exceptions.LawyerExceptions;
import org.example.src.entities.BaseSites.ByPage;
import org.example.src.entities.MyDriver;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;

import java.util.List;
import java.util.Map;

public class AMOSSSolicitors extends ByPage {

    public AMOSSSolicitors() {
        super(
                "AMOSS Solicitors",
                "https://www.amoss.ie/profile/",
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
        List<WebElement> lawyers = MyDriver.wait.findElements(By.className("pk-card-staff"));
        return this.siteUtl.filterLawyersInPage(lawyers, new By[]{By.className("teaser-staff-position")}, true);
    }

    @Override
    protected Object getLawyer(WebElement lawyer) throws Exception {
        String[] socials = super.getSocials(lawyer.findElements(By.cssSelector(".staff-teaser-phone a, .staff-teaser-email a")), false);

        return Map.of(
                "link", extractor.extractLawyerAttribute(lawyer, new By[]{By.cssSelector("a[href*='/profile/']")}, "LINK", "href", LawyerExceptions::linkException),
                "name", extractor.extractLawyerText(lawyer, new By[]{By.className("uk-card-title")}, "NAME", LawyerExceptions::nameException),
                "role", extractor.extractLawyerText(lawyer, new By[]{By.className("teaser-staff-position")}, "ROLE", LawyerExceptions::roleException),
                "firm", this.name,
                "country", "Ireland",
                "practice_area", "",
                "email", socials[0],
                "phone", socials[1].isEmpty() ? "35312120400" : socials[1]
        );
    }
}
