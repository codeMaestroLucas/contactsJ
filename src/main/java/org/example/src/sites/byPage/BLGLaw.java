package org.example.src.sites.byPage;

import java.time.Duration;
import java.util.List;
import java.util.Map;
import org.example.src.entities.MyDriver;
import org.example.src.entities.BaseSites.ByPage;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

public class BLGLaw extends ByPage {
    public BLGLaw() {
        super("BLG Law", "https://www.blg.com/en/people#q=%20&sort=%40biolastname%20ascending&numberOfResults=100", 5);
    }

    protected void accessPage(int index) throws InterruptedException {
        String otherUrl = String.format("https://www.blg.com/en/people#q=%%20&first=%d&sort=%%40biolastname%%20ascending&numberOfResults=100", 100 * index);
        String url = index == 0 ? this.link : otherUrl;
        this.driver.get(url);
        MyDriver.waitForPageToLoad();
        Thread.sleep(1000L);
        if (index == 0) {
            this.siteUtl.clickOnAddBtn(By.id("onetrust-accept-btn-handler"));
        }

        Thread.sleep(5000L);
    }

    protected List<WebElement> getLawyersInPage() {
        By[] webRole = new By[]{By.className("key-contacts-title")};
        String[] validRoles = new String[]{"partner", "counsel", "senior associate"};

        try {
            WebDriverWait wait = new WebDriverWait(this.driver, Duration.ofSeconds(10L));
            List<WebElement> lawyers = (List)wait.until(ExpectedConditions.presenceOfAllElementsLocatedBy(By.className("bio-card-content")));
            return this.siteUtl.filterLawyersInPage(lawyers, webRole, false, validRoles);
        } catch (Exception e) {
            throw new RuntimeException("Failed to find lawyer elements", e);
        }
    }

    private String getLink(WebElement lawyer) {
        By[] byArray = new By[]{By.className("key-contacts-name"), By.className("coveo-card-title")};
        WebElement element = this.siteUtl.iterateOverBy(byArray, lawyer);
        return element.getAttribute("href");
    }

    private String getName(WebElement lawyer) {
        By[] byArray = new By[]{By.className("key-contacts-name"), By.className("coveo-card-title")};
        WebElement element = this.siteUtl.iterateOverBy(byArray, lawyer);
        return this.siteUtl.getContentFromTag(element.getAttribute("outerHTML"));
    }

    private String getRole(WebElement lawyer) {
        By[] byArray = new By[]{By.className("key-contacts-title")};
        WebElement element = this.siteUtl.iterateOverBy(byArray, lawyer);
        return this.siteUtl.getContentFromTag(element);
    }

    private String[] getSocials(WebElement lawyer) {
        try {
            List<WebElement> socials = lawyer.findElement(By.className("contact-list")).findElements(By.cssSelector("a"));
            return super.getSocials(socials, false);
        } catch (Exception e) {
            System.err.println("Error getting socials: " + e.getMessage());
            return new String[]{"", ""};
        }
    }

    public Object getLawyer(WebElement lawyer) throws Exception {
        String[] socials = this.getSocials(lawyer);
        return Map.of("link", this.getLink(lawyer), "name", this.getName(lawyer), "role", this.getRole(lawyer), "firm", this.name, "country", "Canada", "practice_area", "", "email", socials[0], "phone", socials[1]);
    }
}
