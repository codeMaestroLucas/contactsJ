package org.example.src.sites.to_test.europe;

import org.example.exceptions.LawyerExceptions;
import org.example.src.entities.BaseSites.ByPage;
import org.example.src.entities.MyDriver;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import java.util.List;
import java.util.Map;

public class BedellCristin extends ByPage {

    public BedellCristin() {
        super(
                "Bedell Cristin",
                "https://www.bedellcristin.com/people/?",
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
        List<WebElement> lawyers = MyDriver.wait.findElements(By.className("team-card--component"));
        return siteUtl.filterLawyersInPage(lawyers, new By[]{By.className("roles-and-locations")}, true, validRoles);
    }

    @Override
    public Object getLawyer(WebElement lawyer) throws Exception {
        String[] socials = super.getSocials(lawyer.findElements(By.tagName("a")), false);

        return Map.of(
                "link", extractor.extractLawyerAttribute(lawyer, new By[]{By.tagName("h5"), By.tagName("a")}, "LINK", "href", LawyerExceptions::linkException),
                "name", extractor.extractLawyerText(lawyer, new By[]{By.tagName("h5")}, "NAME", LawyerExceptions::nameException),
                "role", extractor.extractLawyerText(lawyer, new By[]{By.className("roles-and-locations")}, "ROLE", LawyerExceptions::roleException),
                "firm", this.name,
                "country", "Jersey",
                "practice_area", "",
                "email", socials[0],
                "phone", socials[1].isEmpty() ? "441534814700" : socials[1]
        );
    }
}
