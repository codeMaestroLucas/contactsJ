package org.example.src.sites.americas;

import org.example.exceptions.LawyerExceptions;
import org.example.src.entities.BaseSites.ByPage;
import org.example.src.entities.MyDriver;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import java.util.List;
import java.util.Map;

public class HulbertVolio extends ByPage {

    public HulbertVolio() {
        super(
                "Hulbert Volio",
                "https://hulbertvolio.com/en/#socios",
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
        List<WebElement> lawyers = MyDriver.wait.findElements(By.cssSelector("li.item-team-member"));
        return this.siteUtl.filterLawyersInPage(lawyers, new By[]{By.className("team-positions")}, true);
    }

    @Override
    public Object getLawyer(WebElement lawyer) throws Exception {
        String[] socials = super.getSocials(lawyer.findElements(By.tagName("a")), false);

        return Map.of(
                "link", extractor.extractLawyerAttribute(lawyer, new By[]{By.cssSelector(".team_title a")}, "LINK", "href", LawyerExceptions::linkException),
                "name", extractor.extractLawyerText(lawyer, new By[]{By.cssSelector(".team_title a")}, "NAME", LawyerExceptions::nameException),
                "role", extractor.extractLawyerText(lawyer, new By[]{By.className("team-positions")}, "ROLE", LawyerExceptions::roleException),
                "firm", this.name,
                "country", "Costa Rica",
                "practice_area", "",
                "email", socials[0],
                "phone", "50622016640"
        );
    }
}
