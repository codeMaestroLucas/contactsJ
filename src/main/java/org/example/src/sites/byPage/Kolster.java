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

public class Kolster extends ByPage {
    private final By[] byRoleArray = {
            By.cssSelector("a > p:last-child")
    };


    public Kolster() {
        super(
            "Kolster",
            "https://www.kolster.fi/en/experts",
            1
        );
    }


    protected void accessPage(int index) throws InterruptedException {
        this.driver.get(this.link);
        MyDriver.waitForPageToLoad();
        Thread.sleep(1000L);

        // Click on add btn
        MyDriver.clickOnElement(By.id("hs-eu-confirmation-button"));
    }


    protected List<WebElement> getLawyersInPage() {
        String[] validRoles = new String[]{
                "partner",
                "director",
                "counsel"
        };

        try {
            WebDriverWait wait = new WebDriverWait(this.driver, Duration.ofSeconds(10L));

            List<WebElement> lawyers = wait.until(
                    ExpectedConditions.presenceOfAllElementsLocatedBy(
                            By.className("experts__item__texts")
                    )
            );
            return this.siteUtl.filterLawyersInPage(lawyers, byRoleArray, true, validRoles);

        } catch (Exception e) {
            throw new RuntimeException("Failed to find lawyer elements", e);
        }
    }


    private String getLink(WebElement lawyer) {
        By[] byArray = new By[]{
                By.cssSelector("a")
        };
        WebElement element = this.siteUtl.iterateOverBy(byArray, lawyer);
        return element.getAttribute("href");
    }


    private String getName(WebElement lawyer) {
        By[] byArray = new By[]{
                By.cssSelector("a > p")
        };
        WebElement element = this.siteUtl.iterateOverBy(byArray, lawyer);
        return element.getText();
    }


    private String getRole(WebElement lawyer) {
        WebElement element = this.siteUtl.iterateOverBy(byRoleArray, lawyer);
        return element.getText();
    }


    private String[] getSocials(WebElement lawyer) {
        String email = ""; String phone = "";

        try {
            List<WebElement> socials = lawyer
                    .findElements(By.cssSelector("a"));

            for (WebElement social : socials) {
                String link = social.getText().trim();

                if (email.isEmpty() && link.contains("@")) email = link;
                else if (phone.isEmpty() && link.startsWith("0")) phone = link;

                if (!email.isEmpty() && !phone.isEmpty()) break;
            }


        } catch (Exception e) {
            System.err.println("Error getting socials: " + e.getMessage());
        }

        return new String[] { email, phone };
    }


    public Object getLawyer(WebElement lawyer) throws Exception {
        String[] socials = this.getSocials(lawyer);
        return Map.of(
            "link", this.getLink(lawyer),
            "name", this.getName(lawyer),
            "role", this.getRole(lawyer),
            "firm", this.name,
            "country", "Finland",
            "practice_area", "",
            "email", socials[0],
            "phone", socials[1]
        );
    }
}
