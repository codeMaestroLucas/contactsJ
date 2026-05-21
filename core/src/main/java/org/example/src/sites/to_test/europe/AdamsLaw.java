package org.example.src.sites.to_test.europe;

import org.example.exceptions.LawyerExceptions;
import org.example.src.entities.BaseSites.ByPage;
import org.example.src.entities.MyDriver;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;

import java.util.List;
import java.util.Map;

public class AdamsLaw extends ByPage {

    public AdamsLaw() {
        super(
                "Adams Law",
                "https://adamslaw.ie/about-adams-law-solicitors",
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
        List<WebElement> lawyers = MyDriver.wait.findElements(By.className("team-member-info"));
        return this.siteUtl.filterLawyersInPage(lawyers, new By[]{By.className("team-member-profession")}, true);
    }

    @Override
    protected Object getLawyer(WebElement lawyer) throws Exception {
        String[] socials = super.getSocials(lawyer.findElements(By.tagName("a")), false);

        return Map.of(
                "link", extractor.extractLawyerAttribute(lawyer, new By[]{By.className("team-member-link")}, "LINK", "href", LawyerExceptions::linkException),
                "name", extractor.extractLawyerText(lawyer, new By[]{By.className("team-member-name")}, "NAME", LawyerExceptions::nameException),
                "role", extractor.extractLawyerText(lawyer, new By[]{By.className("team-member-profession")}, "ROLE", LawyerExceptions::roleException),
                "firm", this.name,
                "country", "Ireland",
                "practice_area", "",
                "email", socials[0],
                "phone", socials[1].isEmpty() ? "35316789090" : socials[1]
        );
    }
}
