package org.example.src.sites.to_test.europe;

import org.example.exceptions.LawyerExceptions;
import org.example.src.entities.BaseSites.ByNewPage;
import org.example.src.entities.MyDriver;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;

import java.util.List;
import java.util.Map;

public class GovernanceIreland extends ByNewPage {

    public GovernanceIreland() {
        super(
                "Governance Ireland",
                "https://governanceireland.ie/meet-our-team/",
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
        List<WebElement> lawyers = MyDriver.wait.findElements(By.className("mega_team_case"));
        return this.siteUtl.filterLawyersInPage(lawyers, new By[]{By.cssSelector(".member-name span")}, true);
    }

    @Override
    protected Object getLawyer(WebElement lawyer) throws Exception {
        String name = extractor.extractLawyerText(lawyer, new By[]{By.className("member-name")}, "NAME", LawyerExceptions::nameException).split("\n")[0].trim();
        String role = extractor.extractLawyerText(lawyer, new By[]{By.cssSelector(".member-name span")}, "ROLE", LawyerExceptions::roleException);

        String link = this.openNewTab(lawyer.findElement(By.tagName("a")));
        
        String[] socials = super.getSocials(lawyer.findElements(By.cssSelector(".member-info p a, .member-social a")), false);

        return Map.of(
                "link", link,
                "name", name,
                "role", role,
                "firm", this.name,
                "country", "Ireland",
                "practice_area", "",
                "email", socials[0],
                "phone", socials[1].isEmpty() ? "353877628215" : socials[1]
        );
    }

    @Override
    public String openNewTab(WebElement lawyer) throws LawyerExceptions {
        String url = driver.getCurrentUrl(); // Site uses same page details but strategy requested ByNewPage
        return url;
    }
}
