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

public class FoxAndMandal extends ByPage {
    public FoxAndMandal() {
        super("Fox And Mandal", "https://foxandmandal.co.in/our-team/?title=&designation=partner", 1);
    }

    protected void accessPage(int index) throws InterruptedException {
        this.driver.get(this.link);
        MyDriver.waitForPageToLoad();
        Thread.sleep(1000L);

        if (index == 0) MyDriver.clickOnElement(By.id("custom-accept"));
    }

    protected List<WebElement> getLawyersInPage() {
        try {
            WebDriverWait wait = new WebDriverWait(this.driver, Duration.ofSeconds(10L));
            return wait.until(ExpectedConditions.presenceOfAllElementsLocatedBy(By.className("qDetails")));
        } catch (Exception e) {
            throw new RuntimeException("Failed to find lawyer elements", e);
        }
    }

    private String getName(WebElement lawyer) {
        By[] byArray = new By[]{By.cssSelector("h5")};
        WebElement element = this.siteUtl.iterateOverBy(byArray, lawyer);
        return this.siteUtl.getContentFromTag(element);
    }

    private String[] getSocials(WebElement lawyer) {
        try {
            List<WebElement> socials = lawyer.findElements(By.cssSelector("a"));
            return super.getSocials(socials, false);
        } catch (Exception e) {
            System.err.println("Error getting socials: " + e.getMessage());
            return new String[]{"", ""};
        }
    }

    public Object getLawyer(WebElement lawyer) throws Exception {
        String[] socials = this.getSocials(lawyer);
        return Map.of("link", this.link, "name", this.getName(lawyer), "role", "Partner", "firm", this.name, "country", "India", "practice_area", "", "email", socials[0], "phone", "+91 99588 84550");
    }
}
