package org.example.src.sites._standingBy;

import org.example.src.entities.BaseSites.ByNewPage;
import org.example.src.entities.MyDriver;
import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.interactions.Actions;

import java.util.List;
import java.util.Map;
import java.util.Objects;

public class CMS extends ByNewPage {
    private final By[] byRoleArray = {
            By.className(""),
            By.cssSelector("")
    };


    public CMS() {
        super(
            "CMS",
            "https://cms.law/en/int/people",
            100,
            1000
        );
    }


    protected void accessPage(int index) throws InterruptedException {
        if (index == 0) {
            this.driver.get(this.link);
            MyDriver.waitForPageToLoad();
            Thread.sleep(1000L);

            // Click on add btn
            MyDriver.clickOnElement(By.id("cookie-apply-all"));

        } else {
            WebElement nextButton = driver.findElement(
                    By.cssSelector("li.page-item.active ~ li > button.page-link--next")
            );

            //todo: fix the click
            new Actions(driver)
                    .moveToElement(nextButton)
                    .perform();
            ;
            Thread.sleep(1200L);
            ((JavascriptExecutor) driver).executeScript("arguments[0].click();", nextButton);

            Thread.sleep(15000L);
        }

    }


    protected List<WebElement> getLawyersInPage() {
        String[] validRoles = new String[]{
                "partner"
        };

        try {
            return null;
//            WebDriverWait wait = new WebDriverWait(this.driver, Duration.ofSeconds(10L));
//
//            List<WebElement> lawyers = wait.until(
//                    ExpectedConditions.presenceOfAllElementsLocatedBy(
//                            By.className("")
//                    )
//            );
//            return this.siteUtl.filterLawyersInPage(lawyers, byRoleArray, true, validRoles);

        } catch (Exception e) {
            throw new RuntimeException("Failed to find lawyer elements", e);
        }
    }


    public void openNewTab(WebElement lawyer) {
        By[] byArray = new By[]{
                By.className(""),
                By.cssSelector("")
        };
        WebElement element = this.siteUtl.iterateOverBy(byArray, lawyer);
        MyDriver.openNewTab(element.getAttribute("href"));
    }


    private String getName(WebElement lawyer) {
        By[] byArray = new By[]{
                By.className(""),
                By.cssSelector("")
        };
        WebElement element = this.siteUtl.iterateOverBy(byArray, lawyer);
        return element.getText();
    }


    private String getRole(WebElement lawyer) {
        By[] byArray = new By[]{
                By.className(""),
                By.cssSelector("")
        };
        WebElement element = this.siteUtl.iterateOverBy(byArray, lawyer);
        return element.getText();
    }


    private String getCountry(WebElement lawyer) {
        By[] byArray = new By[]{
                By.className(""),
                By.cssSelector("")
        };
        WebElement element = this.siteUtl.iterateOverBy(byArray, lawyer);
        return element.getText();
    }


    private String[] getSocials(WebElement lawyer) {
        try {
            List<WebElement> socials = lawyer
                        .findElement(By.className(""))
                        .findElements(By.cssSelector("a"));
            return super.getSocials(socials, false);

        } catch (Exception e) {
            System.err.println("Error getting socials: " + e.getMessage());
            return new String[]{"", ""};
        }
    }


    public Object getLawyer(WebElement lawyer) throws Exception {
        this.openNewTab(lawyer);

        WebElement div = driver.findElement(By.className(""));

        String[] socials = this.getSocials(div);
        return Map.of(
            "link", Objects.requireNonNull(driver.getCurrentUrl()),
            "name", this.getName(div),
            "role", this.getRole(div),
            "firm", this.name,
            "country", this.getCountry(div),
            "practice_area", "",
            "email", socials[0],
            "phone", socials[1].isEmpty() ? "" : socials[1]
        );
    }

    public static void main(String[] args) {
        CMS x = new CMS();
        x.searchForLawyers();
    }
}
