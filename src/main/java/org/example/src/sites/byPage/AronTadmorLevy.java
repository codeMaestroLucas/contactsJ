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

public class AronTadmorLevy extends ByPage {
    public AronTadmorLevy() {
        super(
            "Aron Tadmor Levy",
            "https://arnontl.com/people/",
            1
        );
    }


    @Override
    protected void accessPage(int index) throws InterruptedException {
        driver.get(this.link);
        MyDriver.waitForPageToLoad();
        Thread.sleep(1000);

        if (index > 0) return;

        siteUtl.clickOnAddBtn(By.cssSelector("button.cmplz-accept"));
    }


    @Override
    protected List<WebElement> getLawyersInPage() {
        By[] webRole = {
                By.className("person-info"),
                By.className("position-title"),
        };

        String[] validRoles = {
                "partner",
                "counsel",
        };

        try {
            // Wait up to 10 seconds for elements to be present
            WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(10));
            List<WebElement> lawyers = wait.until(
                    ExpectedConditions.presenceOfAllElementsLocatedBy(By.cssSelector("div.person.h-100"))
            );

            return siteUtl.filterLawyersInPage(lawyers, webRole, true, validRoles);

        } catch (Exception e) {
            throw new RuntimeException("Failed to find lawyer elements", e);
        }
    }


    private String getLink(WebElement lawyer) {
        By[] byArray = {
            By.cssSelector("a")
        };
        return siteUtl.iterateOverBy(byArray, lawyer).getAttribute("href");
    }


    private String getName(WebElement lawyer) {
        By[] byArray = {
            By.className("person-info"),
            By.className("section-title")
        };
        return siteUtl.iterateOverBy(byArray, lawyer).getText();
    }


    private String getRole(WebElement lawyer) {
        By[] byArray = {
                By.className("person-info"),
                By.className("position-title")
        };
        return siteUtl.iterateOverBy(byArray, lawyer).getText();
    }


    private String[] getSocials(WebElement lawyer) {
        try {
            List<WebElement> socials = lawyer
                    .findElement(By.className("person-contact"))
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
                "country", "Israel",
                "practice_area", "",
                "email", socials[0],
                "phone", socials[1]
        );
    }
}
