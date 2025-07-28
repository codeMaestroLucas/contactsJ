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

public class AlTamimi extends ByPage {
    public AlTamimi() {
        super(
            "Al Tamimi",
            "https://www.tamimi.com/about-us/partners/",
            12
        );
    }

    @Override
    protected void accessPage(int index) throws InterruptedException {
        String otherUrl = "https://www.tamimi.com/about-us/partners/?paged=" + (index + 1);
        String url = (index == 0) ? this.link : otherUrl;
        driver.get(url);
        MyDriver.waitForPageToLoad();
        Thread.sleep(1000);

        if (index > 0) return;

        siteUtl.clickOnAddBtn(By.id("CybotCookiebotDialogBodyLevelButtonLevelOptinAllowAll"));
    }


    @Override
    protected List<WebElement> getLawyersInPage() {
        By[] webRole = {
                By.className("key-designation")
        };

        String[] validRoles = {
                "chairman",
                "partner",
        };

        try {
            // Wait up to 10 seconds for elements to be present
            WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(10));
            List<WebElement> lawyers = wait.until(
                    ExpectedConditions.presenceOfAllElementsLocatedBy(By.className("key-contact-detail"))
            );

            return siteUtl.filterLawyersInPage(lawyers, webRole, true, validRoles);

        } catch (Exception e) {
            throw new RuntimeException("Failed to find lawyer elements", e);
        }
    }

    private String getLink(WebElement lawyer) {
        By[] byArray = {
            By.cssSelector("a:first-child")
        };
        return siteUtl.iterateOverBy(byArray, lawyer).getAttribute("href");
    }


    private String getName(WebElement lawyer) {
        By[] byArray = {
            By.cssSelector("a:first-child")
        };
        return siteUtl.iterateOverBy(byArray, lawyer).getText();
    }

    private String getRole(WebElement lawyer) {
        By[] byArray = {
            By.className("key-designation")
        };
        return siteUtl.iterateOverBy(byArray, lawyer).getText();
    }


    private String[] getSocials(WebElement lawyer) {
        List<WebElement> socials = lawyer
                .findElements(By.cssSelector(".key-contact-info > a"));

        return super.getSocials(socials);
    }


    @Override
    public Object getLawyer(WebElement lawyer) throws Exception {
        String[] socials = this.getSocials(lawyer);

        return Map.of(
                "link", this.getLink(lawyer),
                "name", this.getName(lawyer),
                "role", this.getRole(lawyer),
                "firm", this.name,
                "country", "",
                "practice_area", "",
                "email", socials[0],
                "phone", this.link.contains("mumbai") ? "+91 22 400 10000" : "+91 80 4016 0000"
        );
    }
}
