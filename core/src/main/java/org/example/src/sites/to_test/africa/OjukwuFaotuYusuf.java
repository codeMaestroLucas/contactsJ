package org.example.src.sites.to_test.africa;

import org.example.exceptions.LawyerExceptions;
import org.example.src.entities.BaseSites.ByPage;
import org.example.src.entities.MyDriver;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import java.util.List;
import java.util.Map;

public class OjukwuFaotuYusuf extends ByPage {

    public OjukwuFaotuYusuf() {
        super(
                "Ojukwu Faotu & Yusuf",
                "https://ofy-lawyers.com/lawyers/",
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
        List<WebElement> lawyers = MyDriver.wait.findElements(By.cssSelector("div.team-member"));
        return this.siteUtl.filterLawyersInPage(lawyers, new By[]{By.className("team-member-position")}, true);
    }

    @Override
    public Object getLawyer(WebElement lawyer) throws Exception {
        String[] socials = super.getSocials(lawyer.findElements(By.tagName("a")), false);

        return Map.of(
                "link", extractor.extractLawyerAttribute(lawyer, new By[]{By.tagName("h3"), By.tagName("a")}, "LINK", "href", LawyerExceptions::linkException),
                "name", extractor.extractLawyerText(lawyer, new By[]{By.tagName("h3")}, "NAME", LawyerExceptions::nameException),
                "role", extractor.extractLawyerText(lawyer, new By[]{By.className("team-member-position")}, "ROLE", LawyerExceptions::roleException),
                "firm", this.name,
                "country", "Nigeria",
                "practice_area", "",
                "email", socials[0],
                "phone", "2348033246872"
        );
    }
}
