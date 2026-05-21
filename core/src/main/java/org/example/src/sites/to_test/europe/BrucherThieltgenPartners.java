package org.example.src.sites.to_test.europe;

import org.example.exceptions.LawyerExceptions;
import org.example.src.entities.BaseSites.ByPage;
import org.example.src.entities.MyDriver;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;

import java.util.List;
import java.util.Map;

public class BrucherThieltgenPartners extends ByPage {

    public BrucherThieltgenPartners() {
        super(
                "Brucher Thieltgen & Partners",
                "https://brucherlaw.lu/en/our-team/",
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
        List<WebElement> lawyers = MyDriver.wait.findElements(By.className("our_team"));
        return this.siteUtl.filterLawyersInPage(lawyers, new By[]{By.className("job")}, true, validRoles);
    }

    @Override
    protected Object getLawyer(WebElement lawyer) throws Exception {
        String[] socials = super.getSocials(lawyer.findElements(By.tagName("a")), false);

        return Map.of(
                "link", extractor.extractLawyerAttribute(lawyer, new By[]{By.className("name")}, "LINK", "href", LawyerExceptions::linkException),
                "name", extractor.extractLawyerText(lawyer, new By[]{By.className("name")}, "NAME", LawyerExceptions::nameException),
                "role", extractor.extractLawyerText(lawyer, new By[]{By.className("job")}, "ROLE", LawyerExceptions::roleException),
                "firm", this.name,
                "country", "Luxembourg",
                "practice_area", "",
                "email", socials[0],
                "phone", socials[1].isEmpty() ? "3522644441" : socials[1]
        );
    }
}
