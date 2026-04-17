package org.example.src.sites.mundial;

import org.example.exceptions.LawyerExceptions;
import org.example.src.entities.BaseSites.ByNewPage;
import org.example.src.entities.MyDriver;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.time.Duration;
import java.util.List;
import java.util.Map;

public class Bracewell extends ByNewPage {

    public Bracewell() {
        super(
                "Bracewell",
                "https://www.bracewell.com/people/?relationships.poa_office=Dubai",
                3,
                2
        );
    }
    String currentLink = "";
    String currentCountry = "";

    @Override
    protected void accessPage(int index) throws InterruptedException {
        switch (index) {
            case 0:
                currentLink = this.link;
                currentCountry = "the UAE";
                break;
            case 1:
                currentLink = "https://www.bracewell.com/people/?relationships.poa_office=London";
                currentCountry = "England";
                break;
            case 2:
                currentLink = "https://www.bracewell.com/people/?relationships.poa_office=Paris";
                currentCountry = "France";
                break;
        }

        this.driver.get(currentLink);
        MyDriver.waitForPageToLoad();
    }

    @Override
    protected List<WebElement> getLawyersInPage() {
        try {
            WebDriverWait wait = new WebDriverWait(this.driver, Duration.ofSeconds(10));
            List<WebElement> lawyers = wait.until(ExpectedConditions.presenceOfAllElementsLocatedBy(By.cssSelector("article.card-attorney")));
            return this.siteUtl.filterLawyersInPage(lawyers, new By[]{By.cssSelector("p.fs-16")}, false);
        } catch (Exception e) {
            return List.of();
        }
    }

    @Override
    public String openNewTab(WebElement lawyer) throws LawyerExceptions {
        By[] byArray = {By.cssSelector("h2.entry-title a")};
        String link = extractor.extractLawyerAttribute(lawyer, byArray, "LINK", "href", LawyerExceptions::linkException);
        MyDriver.openNewTab(link);
        return link;
    }

    @Override
    protected Object getLawyer(WebElement lawyer) throws Exception {
        String name = extractor.extractLawyerText(lawyer, new By[]{By.cssSelector("h2.entry-title")}, "NAME", LawyerExceptions::nameException);
        String role = extractor.extractLawyerText(lawyer, new By[]{By.cssSelector("p.fs-16")}, "ROLE", LawyerExceptions::roleException);

        String link = this.openNewTab(lawyer);
        WebElement header = driver.findElement(By.className("page-header-bracewell-row"));

        String[] socials = super.getSocials(header.findElements(By.cssSelector("a")), false);

        return Map.of(
                "link", link,
                "name", name,
                "role", role,
                "firm", this.name,
                "country", currentCountry,
                "practice_area", "",
                "email", socials[0],
                "phone", socials[1].isEmpty() ? "97143506817" : socials[1]
        );
    }
}
