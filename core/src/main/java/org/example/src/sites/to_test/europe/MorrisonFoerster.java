package org.example.src.sites.to_test.europe;

import org.example.exceptions.LawyerExceptions;
import org.example.src.entities.BaseSites.ByPage;
import org.example.src.entities.MyDriver;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import java.util.List;
import java.util.Map;

public class MorrisonFoerster extends ByPage {

    public MorrisonFoerster() {
        super(
                "Morrison & Foerster",
                "https://www.mofo.com/people?regions=United%20Kingdom",
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
        List<WebElement> lawyers = MyDriver.wait.findElements(By.className("profile-cards__list__item"));
        return siteUtl.filterLawyersInPage(lawyers, new By[]{By.className("profile-card--detailed__text-content__subheading")}, true, validRoles);
    }

    @Override
    public Object getLawyer(WebElement lawyer) throws Exception {
        String[] socials = super.getSocials(lawyer.findElements(By.tagName("a")), false);

        return Map.of(
                "link", extractor.extractLawyerAttribute(lawyer, new By[]{By.className("profile-card--detailed__text-content__profile-link"), By.tagName("a")}, "LINK", "href", LawyerExceptions::linkException),
                "name", extractor.extractLawyerText(lawyer, new By[]{By.className("profile-card--detailed__name")}, "NAME", LawyerExceptions::nameException),
                "role", extractor.extractLawyerText(lawyer, new By[]{By.className("profile-card--detailed__text-content__subheading")}, "ROLE", LawyerExceptions::roleException),
                "firm", this.name,
                "country", "United Kingdom",
                "practice_area", "",
                "email", socials[0],
                "phone", socials[1].isEmpty() ? "442079204000" : socials[1]
        );
    }
}
