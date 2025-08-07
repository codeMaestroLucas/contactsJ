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

public class ByrneWallace extends ByPage {
    public ByrneWallace() {
        super("Byrne Wallace", "https://byrnewallaceshields.com/about-us/our-team/", 1);
    }

    protected void accessPage(int index) throws InterruptedException {
        this.driver.get(this.link);
        MyDriver.waitForPageToLoad();
        Thread.sleep(1000L);
        if (index <= 0) {
            MyDriver.clickOnElement(By.id("bwSubmitButton"));
        }
    }

    protected List<WebElement> getLawyersInPage() {
        By[] webRole = new By[]{By.className("col-md-4"), By.className("sol-category")};
        String[] validRoles = new String[]{"partner", "counsel", "head"};

        try {
            WebDriverWait wait = new WebDriverWait(this.driver, Duration.ofSeconds(10L));
            List<WebElement> lawyers = (List)wait.until(ExpectedConditions.presenceOfAllElementsLocatedBy(By.className("sol-list-item")));
            return this.siteUtl.filterLawyersInPage(lawyers, webRole, true, validRoles);
        } catch (Exception e) {
            throw new RuntimeException("Failed to find lawyer elements", e);
        }
    }

    private String getLink(WebElement lawyer) {
        By[] byArray = new By[]{By.className("col-md-4"), By.className("sollistname"), By.cssSelector("a")};
        WebElement element = this.siteUtl.iterateOverBy(byArray, lawyer);
        return element.getAttribute("href");
    }

    private String getName(WebElement lawyer) {
        By[] byArray = new By[]{By.className("col-md-4"), By.className("sollistname"), By.cssSelector("a")};
        WebElement element = this.siteUtl.iterateOverBy(byArray, lawyer);
        return element.getText();
    }

    private String getRole(WebElement lawyer) {
        By[] byArray = new By[]{By.className("col-md-4"), By.className("sol-category")};
        WebElement element = this.siteUtl.iterateOverBy(byArray, lawyer);
        return element.getText();
    }

    private String getPracticeArea(WebElement lawyer) {
        By[] byArray = new By[]{By.className("col-md-5"), By.className("serv-item"), By.className("serv-content"), By.className("serv-title"), By.cssSelector("a")};
        WebElement element = this.siteUtl.iterateOverBy(byArray, lawyer);
        return element.getText().replace("and", "&");
    }

    private String[] getSocials(WebElement lawyer) {
        try {
            List<WebElement> details = lawyer.findElement(By.className("col-md-4")).findElements(By.className("sol-detail"));
            String email = ((WebElement)details.get(0)).findElement(By.cssSelector("a")).getAttribute("href");
            String phone = this.siteUtl.getContentFromTag((WebElement)details.get(1));
            return new String[]{email, phone};
        } catch (Exception e) {
            System.err.println("Error getting socials: " + e.getMessage());
            return new String[]{"", ""};
        }
    }

    public Object getLawyer(WebElement lawyer) throws Exception {
        String[] socials = this.getSocials(lawyer);
        return Map.of("link", this.getLink(lawyer), "name", this.getName(lawyer), "role", this.getRole(lawyer), "firm", this.name, "country", "Ireland", "practice_area", this.getPracticeArea(lawyer), "email", socials[0], "phone", socials[1]);
    }
}
