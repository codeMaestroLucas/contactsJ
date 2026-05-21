package org.example.src.sites.to_test.europe;

import org.example.exceptions.LawyerExceptions;
import org.example.src.entities.BaseSites.ByNewPage;
import org.example.src.entities.MyDriver;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;

import java.util.List;
import java.util.Map;

public class LawesomeLegal extends ByNewPage {

    public LawesomeLegal() {
        super(
                "Lawesome Legal",
                "https://www.lawesome.es/en/team",
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
        List<WebElement> lawyers = MyDriver.wait.findElements(By.cssSelector("li.col-12"));
        return this.siteUtl.filterLawyersInPage(lawyers, new By[]{By.tagName("p")}, true);
    }

    @Override
    public String openNewTab(WebElement lawyer) throws LawyerExceptions {
        String url = extractor.extractLawyerAttribute(lawyer, new By[]{By.tagName("a")}, "LINK", "href", LawyerExceptions::linkException);
        MyDriver.openNewTab(url);
        return url;
    }

    @Override
    protected Object getLawyer(WebElement lawyer) throws Exception {
        String name = extractor.extractLawyerText(lawyer, new By[]{By.tagName("h2")}, "NAME", LawyerExceptions::nameException);
        String roleStr = extractor.extractLawyerText(lawyer, new By[]{By.tagName("p")}, "ROLE", LawyerExceptions::roleException);
        
        String role = roleStr.contains("|") ? roleStr.split("\\|")[0].trim() : roleStr;
        String practiceArea = roleStr.contains("|") ? roleStr.split("\\|")[1].trim() : "";

        String link = this.openNewTab(lawyer);
        WebElement container = MyDriver.wait.findElement(By.className("col-lg-6"));

        String[] socials = this.getSocials(container.findElements(By.tagName("a")), false);

        return Map.of(
                "link", link,
                "name", name,
                "role", role,
                "firm", this.name,
                "country", "Spain",
                "practice_area", practiceArea,
                "email", socials[0],
                "phone", socials[1].isEmpty() ? "34944355678" : socials[1]
        );
    }
}
