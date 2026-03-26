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

public class RP extends ByPage {

    public RP() {
        super(
                "R&P",
                "https://www.rplawyers.com/people/",
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
            return wait.until(ExpectedConditions.presenceOfAllElementsLocatedBy(By.className("lawyer-block")));
        } catch (Exception e) {
            return null;
        }
    }

    private String getLink(WebElement lawyer) {
        return lawyer.findElement(By.cssSelector("a[href*='/lawyer/']")).getAttribute("href");
    }

    private String getName(WebElement lawyer) throws LawyerExceptions {
        String href = this.getLink(lawyer);
        String name = href.replace("https://www.rplawyers.com/lawyer/", "").replace("/", "").replace("-", " ");
        return this.siteUtl.titleString(name);
    }

    private String[] getSocials(WebElement lawyer) {
        List<WebElement> socials = lawyer.findElements(By.tagName("a"));
        return super.getSocials(socials, false);
    }

    @Override
    protected Object getLawyer(WebElement lawyer) throws Exception {
        String[] socials = this.getSocials(lawyer);

        return Map.of(
                "link", this.getLink(lawyer),
                "name", this.getName(lawyer),
                "role", "----",
                "firm", this.name,
                "country", "China",
                "practice_area", "",
                "email", socials[0],
                "phone", "862161738270"
        );
    }
}