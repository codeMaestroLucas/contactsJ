package org.example.src.sites.byPage;

import org.example.src.entities.BaseSites.ByPage;
import org.example.src.entities.MyDriver;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.time.Duration;
import java.util.List;
import java.util.Map;

public class ByrneWallace extends ByPage {
    public ByrneWallace() {
        super(
            "Byrne Wallace",
            "https://byrnewallaceshields.com/about-us/our-team/",
            1,
            1
        );
    }


    @Override
    protected void accessPage(int index) throws InterruptedException {
        String url = (index == 0) ? this.link : "";
        driver.get(url);
        MyDriver.waitForPageToLoad();
        Thread.sleep(1000);

        if (index > 0) return;

        siteUtl.clickOnAddBtn(By.id("bwSubmitButton"));
    }


    @Override
    protected List<WebElement> getLawyersInPage() {
        By[] webRole = {
                By.className("col-md-4"),
                By.className("sol-category"),
        };

        String[] validRoles = {
                "partner",
                "counsel",
                "head"
        };

        try {
            // Wait up to 10 seconds for elements to be present
            WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(10));
            List<WebElement> lawyers = wait.until(
                    ExpectedConditions.presenceOfAllElementsLocatedBy(By.className("sol-list-item"))
            );

            return siteUtl.filterLawyersInPage(lawyers, webRole, true, validRoles);

        } catch (Exception e) {
            throw new RuntimeException("Failed to find lawyer elements", e);
        }
    }


    private String getLink(WebElement lawyer) {
        By[] byArray = {
            By.className("col-md-4"),
            By.className("sollistname"),
            By.cssSelector("a")
        };
        WebElement element = siteUtl.iterateOverBy(byArray, lawyer);
        return element.getAttribute("href");
    }


    private String getName(WebElement lawyer) {
        By[] byArray = {
                By.className("col-md-4"),
                By.className("sollistname"),
                By.cssSelector("a")
        };
        WebElement element = siteUtl.iterateOverBy(byArray, lawyer);
        return element.getText();
    }


    private String getRole(WebElement lawyer) {
        By[] byArray = {
                By.className("col-md-4"),
                By.className("sol-category"),
        };
        WebElement element = siteUtl.iterateOverBy(byArray, lawyer);
        return element.getText();
    }


    private String getPracticeArea(WebElement lawyer) {
        By[] byArray = {
                By.className("col-md-5"),
                By.className("serv-item"),
                By.className("serv-content"),
                By.className("serv-title"),
                By.cssSelector("a")
        };
        WebElement element = siteUtl.iterateOverBy(byArray, lawyer);
        return element.getText().replace("and", "&");
    }


    private String[] getSocials(WebElement lawyer) {
        try {
            List<WebElement> details = lawyer
                    .findElement(By.className("col-md-4"))
                    .findElements(By.className("sol-detail"));

            String email = details.get(0)
                    .findElement(By.cssSelector("a"))
                    .getAttribute("href");

            String phone = siteUtl.getContentFromTag(details.get(1));

        return new String[] {email, phone};

        } catch (Exception e) {
            System.err.println("Error getting socials: " + e.getMessage());
            return new String[]{"", ""};
        }
    }


    @Override
    public Object getLawyer(WebElement lawyer) throws Exception {
        String[] socials = this.getSocials(lawyer);

        return Map.of(
                "link", this.getLink(lawyer),
                "name", this.getName(lawyer),
                "role", this.getRole(lawyer),
                "firm", this.name,
                "country", "Ireland",
                "practice_area", this.getPracticeArea(lawyer),
                "email", socials[0],
                "phone", socials[1]
        );
    }
}
