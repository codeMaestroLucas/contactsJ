package org.example.src.sites.byPage;

import java.time.Duration;
import java.util.List;
import java.util.Map;
import org.example.src.entities.MyDriver;
import org.example.src.entities.BaseSites.ByPage;
import org.openqa.selenium.By;
import org.openqa.selenium.Dimension;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

public class DittmarAndIndrenius extends ByPage {
    public DittmarAndIndrenius() {
        super("Dittmar And Indrenius", "https://www.dittmar.fi/people/?", 1);
    }

    protected void accessPage(int index) throws InterruptedException {
        this.driver.get(this.link);
        MyDriver.waitForPageToLoad();
        Thread.sleep(1000L);
        if (index <= 0) {
            MyDriver.clickOnElement(By.id("ccc-recommended-settings"));
        }
    }

    protected List<WebElement> getLawyersInPage() {
        By[] webRole = new By[]{By.className("person--main"), By.className("person-title"), By.cssSelector("div")};
        String[] validRoles = new String[]{"partner", "counsel", "senior associate"};

        try {
            WebDriverWait wait = new WebDriverWait(this.driver, Duration.ofSeconds(10L));
            List<WebElement> lawyers = (List)wait.until(ExpectedConditions.presenceOfAllElementsLocatedBy(By.className("person--data")));
            return this.siteUtl.filterLawyersInPage(lawyers, webRole, true, validRoles);
        } catch (Exception e) {
            throw new RuntimeException("Failed to find lawyer elements", e);
        }
    }

    private String getLink(WebElement lawyer) {
        By[] byArray = new By[]{
                By.className("contact-row"),
                By.cssSelector("a[href^='https://www.dittmar.fi/people/']")
        };
        WebElement element = this.siteUtl.iterateOverBy(byArray, lawyer);
        return element.getAttribute("href");
    }

    private String getName(WebElement lawyer) {
        By[] byArray = new By[]{By.className("person--main"), By.className("entry-title")};
        WebElement element = this.siteUtl.iterateOverBy(byArray, lawyer);
        return element.getText();
    }

    private String getRole(WebElement lawyer) {
        By[] byArray = new By[]{By.className("person--main"), By.className("person-title"), By.cssSelector("div")};
        WebElement element = this.siteUtl.iterateOverBy(byArray, lawyer);
        return element.getText();
    }

    private String[] getSocials(WebElement lawyer) {
        try {
            List<WebElement> socials = lawyer.findElement(By.className("contact-row")).findElements(By.cssSelector("ul > li > a"));
            return super.getSocials(socials, false);
        } catch (Exception e) {
            System.err.println("Error getting socials: " + e.getMessage());
            return new String[]{"", ""};
        }
    }

    public Object getLawyer(WebElement lawyer) throws Exception {
        String[] socials = this.getSocials(lawyer);
        return Map.of("link", this.getLink(lawyer), "name", this.getName(lawyer), "role", this.getRole(lawyer), "firm", this.name, "country", "Finland", "practice_area", "", "email", socials[0], "phone", socials[1]);
    }
}
