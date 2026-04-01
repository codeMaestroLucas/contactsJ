package org.example.src.sites.to_test;

import org.example.exceptions.LawyerExceptions;
import org.example.src.entities.BaseSites.ByPage;
import org.example.src.entities.MyDriver;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;

import java.util.List;
import java.util.Map;

public class NumenLaw extends ByPage {

    public NumenLaw() {
        super(
                "Numen Law",
                "https://numenlaw.com/teams.php",
                1
        );
    }

    @Override
    protected void accessPage(int index) {
        this.driver.get(this.link);
        MyDriver.waitForPageToLoad();
    }

    @Override
    protected List<WebElement> getLawyersInPage() {
        List<WebElement> lawyers = driver.findElements(By.className("team-members"));
        return this.siteUtl.filterLawyersInPage(lawyers, new By[]{By.tagName("h5")}, true);
    }

    @Override
    protected Object getLawyer(WebElement lawyer) throws Exception {
        String roleRaw = extractor.extractLawyerText(lawyer, new By[]{By.tagName("h5")}, "ROLE", LawyerExceptions::roleException);
        String[] socials = super.getSocials(lawyer.findElements(By.tagName("a")), false);

        return Map.of(
                "link", extractor.extractLawyerAttribute(lawyer, new By[]{By.className("read-more")}, "LINK", "href", LawyerExceptions::linkException),
                "name", extractor.extractLawyerText(lawyer, new By[]{By.tagName("h2")}, "NAME", LawyerExceptions::nameException),
                "role", roleRaw.split("-")[0].trim(),
                "firm", this.name,
                "country", "India",
                "practice_area", "",
                "email", socials[0],
                "phone", "911140366657"
        );
    }
}
