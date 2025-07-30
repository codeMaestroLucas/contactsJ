package org.example.src.sites.byNewPage;

import org.example.src.entities.BaseSites.ByPage;
import org.example.src.entities.MyDriver;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.time.Duration;
import java.util.List;
import java.util.Map;

public class Sangra extends ByPage {
    public Sangra() {
        super(
            "Sangra",
            "https://www.sangra.com/people/",
            1
        );
    }


    @Override
    protected void accessPage(int index) throws InterruptedException {
        String otherUrl = "";
        String url = (index == 0) ? this.link : otherUrl;
        driver.get(url);
        MyDriver.waitForPageToLoad();
        Thread.sleep(1000);

        if (index > 0) return;

        siteUtl.clickOnAddBtn(By.id(""));
    }


    @Override
    protected List<WebElement> getLawyersInPage() {
        By[] webRole = {
                By.className(""),
        };

        String[] validRoles = {
                "partner",
        };

        try {
            // Wait up to 10 seconds for elements to be present
            WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(10));
            List<WebElement> lawyers = wait.until(
                    ExpectedConditions.presenceOfAllElementsLocatedBy(By.className(""))
            );

            return siteUtl.filterLawyersInPage(lawyers, webRole, true, validRoles);

        } catch (Exception e) {
            throw new RuntimeException("Failed to find lawyer elements", e);
        }
    }


    private String getLink(WebElement lawyer) {
        By[] byArray = {
            By.className(""),
            By.cssSelector("")
        };
        WebElement element = siteUtl.iterateOverBy(byArray, lawyer);
        return element.getAttribute("href");
    }


    private String getName(WebElement lawyer) {
        By[] byArray = {
                By.className(""),
                By.cssSelector("")
        };
        WebElement element = siteUtl.iterateOverBy(byArray, lawyer);
        return element.getText();
    }


    private String getRole(WebElement lawyer) {
        By[] byArray = {
                By.className(""),
                By.cssSelector("")
        };
        WebElement element = siteUtl.iterateOverBy(byArray, lawyer);
        return element.getText();
    }


    private String getCountry(WebElement lawyer) {
        By[] byArray = {
                By.className(""),
                By.cssSelector("")
        };
        WebElement element = siteUtl.iterateOverBy(byArray, lawyer);
        return element.getText();
    }


    private String[] getSocials(WebElement lawyer) {
        try {
            List<WebElement> socials = lawyer
                    .findElement(By.className(""))
                    .findElements(By.cssSelector(""));
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
                "country", "Canada",
                "practice_area", "",
                "email", socials[0],
                "phone", socials[1]
        );
    }


    public static void main(String[] args) {
        Sangra x = new Sangra();
        x.searchForLawyers();
    }
}
