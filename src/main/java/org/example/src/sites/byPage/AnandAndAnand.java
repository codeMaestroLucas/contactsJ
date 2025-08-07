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

public class AnandAndAnand extends ByPage {
    public AnandAndAnand() {
        super("Anand And Anand", "https://www.anandandanand.com/our-team/page/1/", 2);
    }

    protected void accessPage(int index) throws InterruptedException {
        String otherUrl = "https://www.anandandanand.com/our-team/page/2/";
        String url = index == 0 ? this.link : otherUrl;
        this.driver.get(url);
        MyDriver.waitForPageToLoad();
        Thread.sleep(1000L);
        if (index <= 0) {
            MyDriver.clickOnElement(By.cssSelector("button.ds-popup-close"));
        }
    }

    protected List<WebElement> getLawyersInPage() {
        By[] webRole = new By[]{By.className("position")};
        String[] validRoles = new String[]{"partner"};

        try {
            WebDriverWait wait = new WebDriverWait(this.driver, Duration.ofSeconds(10L));
            List<WebElement> lawyers = (List)wait.until(ExpectedConditions.presenceOfAllElementsLocatedBy(By.cssSelector("div.attorney-info.card-body.card__background.col-12")));
            return this.siteUtl.filterLawyersInPage(lawyers, webRole, true, validRoles);
        } catch (Exception e) {
            throw new RuntimeException("Failed to find lawyer elements", e);
        }
    }

    private String getLink(WebElement lawyer) {
        By[] byArray = new By[]{By.className("stretched-link")};
        return this.siteUtl.iterateOverBy(byArray, lawyer).getAttribute("href");
    }

    private String getName(WebElement lawyer) {
        By[] byArray = new By[]{By.className("title"), By.className("h5")};
        return this.siteUtl.iterateOverBy(byArray, lawyer).getText();
    }

    private String getRole(WebElement lawyer) {
        By[] byArray = new By[]{By.className("position")};
        return this.siteUtl.iterateOverBy(byArray, lawyer).getText();
    }

    private String[] getSocials(WebElement lawyer) {
        try {
            List<WebElement> socials = lawyer.findElements(By.className("stretched-link"));
            return super.getSocials(socials, false);
        } catch (Exception e) {
            System.err.println("Error getting socials: " + e.getMessage());
            return new String[]{"", ""};
        }
    }

    public Object getLawyer(WebElement lawyer) throws Exception {
        String[] socials = this.getSocials(lawyer);
        return Map.of("link", this.getLink(lawyer), "name", this.getName(lawyer), "role", this.getRole(lawyer), "firm", this.name, "country", "India", "practice_area", "", "email", socials[0], "phone", socials[1]);
    }
}
