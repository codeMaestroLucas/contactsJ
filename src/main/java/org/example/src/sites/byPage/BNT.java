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

public class BNT extends ByPage {
    public BNT() {
        super(
            "BNT",
            "https://bnt.eu/attorneys/?field_1_contains=&bps_form=33837&bps_form_page=%2Fattorneys%2F",
            1,
            3
        );
    }


    @Override
    protected void accessPage(int index) throws InterruptedException {
        String url = (index == 0) ? this.link : "";
        driver.get(url);
        MyDriver.waitForPageToLoad();
        Thread.sleep(1000);

        siteUtl.clickOnAddBtn(By.className("cc_bottom_info"));

        MyDriver.rollDown(1, 0.5);
    }


    @Override
    protected List<WebElement> getLawyersInPage() {
        By[] webRole = {
                By.id("profile-group-fields"),
                By.cssSelector("[data-field-id='23']"),
                By.cssSelector("span")
        };

        String[] validRoles = {
                "partner",
                "counsel",
                "senior associate"
        };

        try {
            // Wait up to 10 seconds for elements to be present
            WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(10));
            List<WebElement> lawyers = wait.until(
                    ExpectedConditions.presenceOfAllElementsLocatedBy(By.className("item"))
            );

            return siteUtl.filterLawyersInPage(lawyers, webRole, true, validRoles);

        } catch (Exception e) {
            throw new RuntimeException("Failed to find lawyer elements", e);
        }
    }


    private String getLink(WebElement lawyer) {
        By[] byArray = {
            By.className("item-block"),
            By.className("member-name"),
            By.cssSelector("a")
        };
        WebElement element = siteUtl.iterateOverBy(byArray, lawyer);
        return element.getAttribute("href");
    }


    private String getName(WebElement lawyer) {
        By[] byArray = {
                By.className("item-block"),
                By.className("member-name"),
        };
        WebElement element = siteUtl.iterateOverBy(byArray, lawyer);
        return element.getText();
    }


    private String getRole(WebElement lawyer) {
        By[] byArray = {
                By.id("profile-group-fields"),
                By.cssSelector("[data-field-id='23']"),
                By.cssSelector("span")
        };
        WebElement element = siteUtl.iterateOverBy(byArray, lawyer);
        return siteUtl.getContentFromTag(element);
    }


    private String getCountry(WebElement lawyer) {
        By[] byArray = {
                By.id("profile-group-fields"),
                By.cssSelector("[data-field-id='152']"),
                By.cssSelector("span")
        };
        WebElement element = siteUtl.iterateOverBy(byArray, lawyer);
        String country = siteUtl.getContentFromTag(element);

        return country.equalsIgnoreCase("czech republic") ? "the Czech Republic" : country;
    }


    private String[] getSocials(WebElement lawyer) {
        try {
            List<WebElement> socials = lawyer
                    .findElement(By.id("profile-group-fields"))
                    .findElements(By.cssSelector("a"));
            return super.getSocials(socials);

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
                "country", this.getCountry(lawyer),
                "practice_area", "",
                "email", socials[0],
                "phone", socials[1]
        );
    }
}
