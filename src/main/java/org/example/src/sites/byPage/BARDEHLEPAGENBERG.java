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

public class BARDEHLEPAGENBERG extends ByPage {
    private final By[] byRoleArray = {
            By.className("overlay-text"),
            By.cssSelector("a > p")
    };


    public BARDEHLEPAGENBERG() {
        super(
            "BARDEHLE PAGENBERG",
            "https://www.bardehle.com/en/team",
            1,
            2
        );
    }


    protected void accessPage(int index) throws InterruptedException {
        this.driver.get(this.link);
        MyDriver.waitForPageToLoad();
        Thread.sleep(1000L);

        // Click on add btn
        MyDriver.clickOnElement(By.className("ccm--save-settings"));
    }



    protected List<WebElement> getLawyersInPage() {
        try {
            WebDriverWait wait = new WebDriverWait(this.driver, Duration.ofSeconds(10L));

            // Position 1 == Partner
            return wait.until(
                    ExpectedConditions.presenceOfAllElementsLocatedBy(
                            By.cssSelector("div.col-xl-4[data-filter-position*='1']")
                    )
            );

        } catch (Exception e) {
            throw new RuntimeException("Failed to find lawyer elements", e);
        }
    }


    private String getLink(WebElement lawyer) {
        By[] byArray = new By[]{
                By.className("overlay-text"),
                By.cssSelector("a")
        };
        WebElement element = this.siteUtl.iterateOverBy(byArray, lawyer);
        return element.getAttribute("href");
    }


    private String getName(WebElement lawyer) {
        By[] byArray = new By[]{
                By.className("overlay-text"),
                By.cssSelector("a > p > b")
        };
        WebElement element = this.siteUtl.iterateOverBy(byArray, lawyer);
        return siteUtl.getContentFromTag(element);
    }


    private String getCountry(String phone) {
        phone = phone.replace("+", "");
        String country = phone;

        if (phone.startsWith("33")) country = "France";
        else if (phone.startsWith("34")) country = "Spain";
        else if (phone.startsWith("49")) country = "Germany";
        else if (phone.startsWith("65")) country = "Singapore";

        return country;
    }


    private String[] getSocials(WebElement lawyer) {
        String email = ""; String phone = "";

        try {
            List<WebElement> socials = lawyer
                        .findElement(By.className("overlay-hover-text"))
                        .findElements(By.cssSelector("a"));

            for (WebElement social : socials) {
                String link = siteUtl.getContentFromTag(social.getAttribute("outerHTML"));

                if (email.isEmpty() && link.contains("(at)")) email = link.replace("(at)", "@");
                else if (phone.isEmpty() && link.contains("+")) phone = link;

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
            "role", "Partner",
            "firm", this.name,
            "country", this.getCountry(socials[1]),
            "practice_area", "",
            "email", socials[0],
            "phone", socials[1]
        );
    }
}
