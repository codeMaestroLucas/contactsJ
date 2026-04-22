package org.example.src.sites.europe;

import org.example.exceptions.LawyerExceptions;
import org.example.src.entities.BaseSites.ByNewPage;
import org.example.src.entities.MyDriver;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import java.util.List;
import java.util.Map;

public class CasePro extends ByNewPage {

    public CasePro() {
        super(
                "CasePro",
                "https://www.casepro.bg/en",
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
        List<WebElement> lawyers = MyDriver.wait.findElements(By.cssSelector("div.team-card"));
        return this.siteUtl.filterLawyersInPage(lawyers, new By[]{By.className("job-title")}, true);
    }

    @Override
    public String openNewTab(WebElement lawyer) throws LawyerExceptions {
        String link = extractor.extractLawyerAttribute(lawyer, new By[]{By.tagName("a")}, "LINK", "href", LawyerExceptions::linkException);
        MyDriver.openNewTab(link);
        return link;
    }

    @Override
    public Object getLawyer(WebElement lawyer) throws Exception {
        String name = extractor.extractLawyerAttribute(lawyer, new By[]{By.className("team-member-name")}, "NAME", "textContent", LawyerExceptions::nameException);
        String role = extractor.extractLawyerAttribute(lawyer, new By[]{By.className("job-title")}, "ROLE", "textContent", LawyerExceptions::roleException);

        String link = this.openNewTab(lawyer);
        WebElement container = MyDriver.wait.findElement(By.className("description"));

        String[] socials = super.getSocialsFromText(container.findElement(By.className("social-links")).getText());

        return Map.of(
                "link", link,
                "name", name,
                "role", role,
                "firm", this.name,
                "country", "Bulgaria",
                "practice_area", extractor.extractLawyerAttribute(container, new By[]{By.className("areas-of-expertise")}, "PRACTICE AREA", "textContent", LawyerExceptions::practiceAreaException),
                "email", socials[0],
                "phone", socials[1].isEmpty() ? "35924260383" : socials[1]
        );
    }
}
