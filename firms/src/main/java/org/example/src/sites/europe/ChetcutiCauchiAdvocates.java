package org.example.src.sites.europe;

import org.example.exceptions.LawyerExceptions;
import org.example.src.entities.BaseSites.ByPage;
import org.example.src.entities.MyDriver;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;

import java.util.List;
import java.util.Map;

public class ChetcutiCauchiAdvocates extends ByPage {

    public ChetcutiCauchiAdvocates() {
        super(
                "Chetcuti Cauchi Advocates",
                "https://www.ccmalta.com/our-people",
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
        List<WebElement> lawyers = MyDriver.wait.findElements(By.className("people-item"));
        return this.siteUtl.filterLawyersInPage(lawyers, new By[]{By.className("text-color-rosewood")}, true, validRoles);
    }

    @Override
    protected Object getLawyer(WebElement lawyer) throws Exception {
        String[] socials = this.getSocials(lawyer.findElements(By.className("profile-contact-details")), false);

        return Map.of(
                "link", extractor.extractLawyerAttribute(lawyer, new By[]{By.className("profile-img-overlay")}, "LINK", "href", LawyerExceptions::linkException),
                "name", extractor.extractLawyerText(lawyer, new By[]{By.tagName("h4")}, "NAME", LawyerExceptions::nameException),
                "role", extractor.extractLawyerText(lawyer, new By[]{By.className("text-base")}, "ROLE", LawyerExceptions::roleException),
                "firm", this.name,
                "country", "Malta",
                "practice_area", "",
                "email", socials[0].replace("?subject=ccmalta%20query%20subject:%20our%20people", ""),
                "phone", socials[1].isEmpty() ? "35622056111" : socials[1]
        );
    }
}
