package org.example.src.sites.europe;

import org.example.exceptions.LawyerExceptions;
import org.example.src.entities.BaseSites.ByNewPage;
import org.example.src.entities.MyDriver;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;

import java.util.List;
import java.util.Map;

public class PorobijaSpoljaric extends ByNewPage {

    public PorobijaSpoljaric() {
        super(
                "POROBIJA & ŠPOLJARIĆ",
                "https://www.psod.hr/en/team-members",
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
        WebElement div = MyDriver.wait.findElement(By.className("partneri"));
        List<WebElement> lawyers = div.findElements(By.cssSelector("a[href*='/en/attorneys/']"));

        div = driver.findElement(By.className("odvjetnici"));
        lawyers.addAll(div.findElements(By.cssSelector("a[href*='/en/attorneys/']")));

        return lawyers;
    }

    @Override
    public String openNewTab(WebElement lawyer) throws LawyerExceptions {
        String url = lawyer.getAttribute("href");
        MyDriver.openNewTab(url);
        return url;
    }

    @Override
    protected Object getLawyer(WebElement lawyer) throws Exception {
        String name = extractor.extractLawyerAttribute(lawyer, new By[]{By.className("p-o-name")}, "NAME", "textContent", LawyerExceptions::nameException);
        String practice = extractor.extractLawyerAttribute(lawyer, new By[]{By.className("p-o-specialty")}, "PRACTICE AREA", "textContent", LawyerExceptions::practiceAreaException);

        String link = this.openNewTab(lawyer);
        WebElement container = MyDriver.wait.findElement(By.className("single-teammember-facts"));

        String[] socials = this.getSocials(container.findElements(By.cssSelector(".social a")), false);

        return Map.of(
                "link", link,
                "name", name,
                "role", extractor.extractLawyerAttribute(container, new By[]{By.className("teammember-fact-info")}, "ROLE", "textContent", LawyerExceptions::roleException),
                "firm", this.name,
                "country", "Croatia",
                "practice_area", practice,
                "email", socials[0],
                "phone", socials[1].isEmpty() ? "385042373100" : socials[1]
        );
    }
}
