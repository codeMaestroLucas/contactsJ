package org.example.src.sites.europe;

import org.example.exceptions.LawyerExceptions;
import org.example.src.entities.BaseSites.ByNewPage;
import org.example.src.entities.MyDriver;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import java.util.List;
import java.util.Map;

public class CSAssociados extends ByNewPage {

    public CSAssociados() {
        super(
                "CS Associados",
                "https://www.csassociados.pt/en/lawyers/",
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
        List<WebElement> lawyers = MyDriver.wait.findElements(By.cssSelector("a.card-team"));
        return this.siteUtl.filterLawyersInPage(lawyers, new By[]{By.className("card-avatar-subtitle")}, true);
    }

    @Override
    public String openNewTab(WebElement lawyer) throws LawyerExceptions {
        String link = extractor.extractLawyerAttribute(lawyer, new By[]{}, "LINK", "href", LawyerExceptions::linkException);
        MyDriver.openNewTab(link);
        return link;
    }

    @Override
    public Object getLawyer(WebElement lawyer) throws Exception {
        String name = extractor.extractLawyerText(lawyer, new By[]{By.className("card-avatar-title")}, "NAME", LawyerExceptions::nameException);
        String role = extractor.extractLawyerText(lawyer, new By[]{By.className("card-avatar-subtitle")}, "ROLE", LawyerExceptions::roleException);

        String link = this.openNewTab(lawyer);
        WebElement container = driver.findElement(By.className("col-sm-7"));

        String[] socials = super.getSocials(container.findElements(By.tagName("a")), false);

        return Map.of(
                "link", link,
                "name", name,
                "role", role,
                "firm", this.name,
                "country", "Portugal",
                "practice_area", extractor.extractLawyerText(container, new By[]{By.className("list-words-blue")}, "PRACTICE AREA", LawyerExceptions::practiceAreaException),
                "email", socials[0],
                "phone", "351213509200"
        );
    }
}
