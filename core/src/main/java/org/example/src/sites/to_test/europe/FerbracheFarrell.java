package org.example.src.sites.to_test.europe;

import org.example.exceptions.LawyerExceptions;
import org.example.src.entities.BaseSites.ByNewPage;
import org.example.src.entities.MyDriver;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import java.util.List;
import java.util.Map;

public class FerbracheFarrell extends ByNewPage {

    public FerbracheFarrell() {
        super(
                "Ferbrache & Farrell",
                "https://ferbrachefarrell.com/people/",
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
        List<WebElement> lawyers = MyDriver.wait.findElements(By.className("team-grid__item"));
        return siteUtl.filterLawyersInPage(lawyers, new By[]{By.className("team-member__role")}, true, validRoles);
    }

    @Override
    public String openNewTab(WebElement lawyer) throws LawyerExceptions {
        String url = lawyer.getAttribute("href");
        MyDriver.openNewTab(url);
        return url;
    }

    @Override
    protected Object getLawyer(WebElement lawyer) throws Exception {
        String name = extractor.extractLawyerText(lawyer, new By[]{By.className("team-member__name")}, "NAME", LawyerExceptions::nameException);
        
        String link = this.openNewTab(lawyer);
        WebElement container = MyDriver.wait.findElement(By.className("single-team-member__header"));

        String role = extractor.extractLawyerText(container, new By[]{By.className("single-team-member__subheading")}, "ROLE", LawyerExceptions::roleException);
        String[] socials = super.getSocials(container.findElements(By.tagName("a")), false);

        return Map.of(
                "link", link,
                "name", name,
                "role", role,
                "firm", this.name,
                "country", "Guernsey",
                "practice_area", "",
                "email", socials[0],
                "phone", socials[1].isEmpty() ? "441481815000" : socials[1]
        );
    }
}
