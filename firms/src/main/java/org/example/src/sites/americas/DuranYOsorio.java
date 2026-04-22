package org.example.src.sites.americas;

import org.example.exceptions.LawyerExceptions;
import org.example.src.entities.BaseSites.ByNewPage;
import org.example.src.entities.MyDriver;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import java.util.List;
import java.util.Map;

public class DuranYOsorio extends ByNewPage {

    public DuranYOsorio() {
        super(
                "Durán & Osorio",
                "https://www.duranyosorio.com/en/team",
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
        List<WebElement> lawyers = MyDriver.wait.findElements(By.cssSelector("a[data-testid='linkElement']"));
        return this.siteUtl.filterLawyersInPage(lawyers, null, true);
    }

    @Override
    public String openNewTab(WebElement lawyer) throws LawyerExceptions {
        String link = extractor.extractLawyerAttribute(lawyer, new By[]{}, "LINK", "href", LawyerExceptions::linkException);
        MyDriver.openNewTab(link);
        return link;
    }

    @Override
    public Object getLawyer(WebElement lawyer) throws Exception {
        String role = lawyer.getText();

        String link = this.openNewTab(lawyer);
        WebElement container = driver.findElement(By.className("CohWsy"));


        String name = extractor.extractLawyerText(container, new By[]{By.id("comp-k19o7ktm")}, "NAME", LawyerExceptions::nameException);
        String[] socials = super.getSocials(container.findElements(By.tagName("a")), false);

        return Map.of(
                "link", link,
                "name", role,
                "role", role,
                "firm", this.name,
                "country", "Colombia",
                "practice_area", extractor.extractLawyerText(container, new By[]{By.id("comp-k9fmopgv")}, "PRACTICE AREA", LawyerExceptions::practiceAreaException),
                "email", socials[0],
                "phone", "576016183868"
        );
    }
}
