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

public class Njord extends ByPage {
    private final By[] byRoleArray = {
            By.className("employee-title-office")
    };


    public Njord() {
        super(
            "Njord",
            "https://www.njordlaw.com/people",
            5,
            2
        );
    }


    protected void accessPage(int index) throws InterruptedException {
        String otherUrl = "https://www.njordlaw.com/people?page=" + index;
        String url = index == 0 ? this.link : otherUrl;
        this.driver.get(url);
        MyDriver.waitForPageToLoad();
        Thread.sleep(1000L);

        if (index > 0) return;

        MyDriver.clickOnElement(By.className("coi-banner__accept"));
    }


    protected List<WebElement> getLawyersInPage() {
        String[] validRoles = new String[]{
                "partner",
                "senior associate"
        };

        try {
            WebDriverWait wait = new WebDriverWait(this.driver, Duration.ofSeconds(10L));

            List<WebElement> lawyers = wait.until(
                    ExpectedConditions.presenceOfAllElementsLocatedBy(
                            By.className("employee-list-item")
                    )
            );
            return this.siteUtl.filterLawyersInPage(lawyers, byRoleArray, false, validRoles);

        } catch (Exception e) {
            throw new RuntimeException("Failed to find lawyer elements", e);
        }
    }


    private String getLink(WebElement lawyer) {
        By[] byArray = new By[]{
                By.className("employee-link")
        };
        WebElement element = this.siteUtl.iterateOverBy(byArray, lawyer);
        return element.getAttribute("href");
    }


    private String getName(WebElement lawyer) {
        By[] byArray = new By[]{
                By.className("employee-title-office"),
                By.className("employee-name")
        };
        WebElement element = this.siteUtl.iterateOverBy(byArray, lawyer);
        return siteUtl.getContentFromTag(element.getAttribute("outerHTML"));
    }


    private String getRole(WebElement lawyer) {
        WebElement element = this.siteUtl.iterateOverBy(byRoleArray, lawyer);
        return element.getAttribute("outerHTML");
    }


    private String getCountry(String phone) {
        String country = phone; // to check if all the phones are registered

        if (phone.startsWith("45")) country = "Denmark";
        else if (phone.startsWith("46")) country = "Sweden";
        else if (phone.startsWith("370")) country = "Lithuania";
        else if (phone.startsWith("371")) country = "Latvia";
        else if (phone.startsWith("372")) country = "Estonia";

        return country;
    }


    private String[] getSocials(WebElement lawyer) {
        try {
            List<WebElement> socials = lawyer.findElements(By.cssSelector("a"));
            String[] socialLinks = super.getSocials(socials, true);

            if (socialLinks[1].isEmpty()) { // Email is empty
                socialLinks = super.getSocials(socials, false);
                // Removing the "tel:%28%2B" from the phone
                socialLinks[1] = socialLinks[1]
                        .replace("282", "")
                        // Just a safeguard that all the invalid number were removed
                        .replaceFirst("2", "")
                        .replaceFirst("8", "")
                        .replaceFirst("2", "");
            }
            return socialLinks;

        } catch (Exception e) {
            System.err.println("Error getting socials: " + e.getMessage());
            return new String[]{"", ""};
        }
    }


    public Object getLawyer(WebElement lawyer) throws Exception {
        String[] socials = this.getSocials(lawyer);
        return Map.of(
                "link", this.getLink(lawyer),
                "name", this.getName(lawyer),
                "role", this.getRole(lawyer),
                "firm", this.name,
                "country", this.getCountry(socials[1]),
                "practice_area", "",
                "email", socials[0],
                "phone", socials[1]);
    }
}
