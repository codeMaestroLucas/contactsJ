package org.example.src.sites.byPage;

import org.example.exceptions.LawyerExceptions;
import org.example.src.entities.BaseSites.ByPage;
import org.example.src.entities.MyDriver;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.time.Duration;
import java.util.List;
import java.util.Map;

public class TripleOKLaw extends ByPage {

    public TripleOKLaw() {
        super(
                "TripleOKLaw",
                "https://www.tripleoklaw.com/people/",
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
        try {
            WebDriverWait wait = new WebDriverWait(this.driver, Duration.ofSeconds(10));
            List<WebElement> lawyers = wait.until(ExpectedConditions.presenceOfAllElementsLocatedBy(By.className("element-item")));
            return this.siteUtl.filterLawyersInPage(lawyers, new By[]{By.className("position")}, true);
        } catch (Exception e) {
            return List.of();
        }
    }

    private String getLink(WebElement lawyer) {
        String link = "";
        try {
            link = lawyer.findElement(By.cssSelector("a[href*='https://www.tripleoklaw.com/people/']")).getAttribute("href");
        } catch (Exception e) {
            link = this.link;
        }

        return link;
    }

    @Override
    protected Object getLawyer(WebElement lawyer) throws Exception {
        String[] socials = super.getSocials(lawyer.findElements(By.tagName("a")), false);

        return Map.of(
                "link", this.getLink(lawyer),
                "name", extractor.extractLawyerAttribute(lawyer, new By[]{By.tagName("h3")}, "NAME", "textContent", LawyerExceptions::nameException),
                "role", extractor.extractLawyerAttribute(lawyer, new By[]{By.className("position")}, "ROLE", "textContent", LawyerExceptions::roleException),
                "firm", this.name,
                "country", "Kenya",
                "practice_area", "",
                "email", socials[0],
                "phone", "254722690796"
        );
    }
}
