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

public class Blakes extends ByPage {
    public Blakes() {
        super("Blakes", "https://www.blakes.com/people/find-a-person/?fc=or%7C%7Cbiopositiongroup%7C%7C9;or%7C%7Cbiopositiongroup%7C%7C8;or%7C%7Cbiocities%7C%7CToronto;or%7C%7Cbiocities%7C%7CCalgary;or%7C%7Cbiocities%7C%7CMontr%C3%A9al;or%7C%7Cbiocities%7C%7CVancouver;or%7C%7Cbiocities%7C%7COttawa;or%7C%7Cbiocities%7C%7CLondon", 1);
    }

    protected void accessPage(int index) throws InterruptedException {
        this.driver.get(this.link);
        MyDriver.waitForPageToLoad();
        Thread.sleep(1000L);
        if (index <= 0) {
            this.siteUtl.clickOnAddBtn(By.id("onetrust-accept-btn-handler"));
        }
    }

    protected List<WebElement> getLawyersInPage() {
        try {
            WebDriverWait wait = new WebDriverWait(this.driver, Duration.ofSeconds(10L));
            return (List)wait.until(ExpectedConditions.presenceOfAllElementsLocatedBy(By.className("blk-card-item-inner")));
        } catch (Exception e) {
            throw new RuntimeException("Failed to find lawyer elements", e);
        }
    }

    private String getLink(WebElement lawyer) {
        By[] byArray = new By[]{By.className("blk-card-item-name")};
        WebElement element = this.siteUtl.iterateOverBy(byArray, lawyer);
        return element.getAttribute("href");
    }

    private String getName(WebElement lawyer) {
        By[] byArray = new By[]{By.className("blk-card-item-name")};
        WebElement element = this.siteUtl.iterateOverBy(byArray, lawyer);
        return element.getText();
    }

    private String getRole(WebElement lawyer) {
        By[] byArray = new By[]{By.className("blk-card-item-description"), By.className("blk-card-item-description-inner")};
        WebElement element = this.siteUtl.iterateOverBy(byArray, lawyer);
        return element.getText();
    }

    private String getCountry(WebElement lawyer) {
        By[] byArray = new By[]{By.className("blk-card-item-description"), By.className("blk-card-item-description-inner")};
        WebElement element = this.siteUtl.iterateOverBy(byArray, lawyer);
        String country = element.getText().split(" \\| ")[1];
        if (country.equalsIgnoreCase("new york")) {
            return "EUA";
        } else {
            return country.equalsIgnoreCase("london") ? "England" : "Canada";
        }
    }

    private String[] getSocials(WebElement lawyer) {
        try {
            List<WebElement> socials = lawyer.findElement(By.className("blk-card-item-description")).findElement(By.className("blk-card-item-description-link-list")).findElements(By.cssSelector("li > a"));
            return super.getSocials(socials);
        } catch (Exception e) {
            System.err.println("Error getting socials: " + e.getMessage());
            return new String[]{"", ""};
        }
    }

    public Object getLawyer(WebElement lawyer) throws Exception {
        String[] socials = this.getSocials(lawyer);
        return Map.of("link", this.getLink(lawyer), "name", this.getName(lawyer), "role", this.getRole(lawyer), "firm", this.name, "country", this.getCountry(lawyer), "practice_area", "", "email", socials[0], "phone", socials[1]);
    }
}
