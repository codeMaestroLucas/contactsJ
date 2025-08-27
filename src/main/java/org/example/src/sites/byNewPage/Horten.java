package org.example.src.sites.byNewPage;

import org.example.src.entities.BaseSites.ByNewPage;
import org.example.src.entities.MyDriver;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.time.Duration;
import java.util.List;
import java.util.Map;
import java.util.Objects;

public class Horten extends ByNewPage {
    private final By[] byRoleArray = {
            By.className("person__title")
    };


    public Horten() {
        super(
            "Horten",
            "https://en.horten.dk/people",
            1
        );
    }


    protected void accessPage(int index) throws InterruptedException {
        this.driver.get(this.link);
        MyDriver.waitForPageToLoad();
        Thread.sleep(1000L);

        // Click on add btn
        MyDriver.clickOnElement(By.className("coi-banner__accept"));
    }


    protected List<WebElement> getLawyersInPage() {
        String[] validRoles = new String[]{
                "partner"
        };

        try {
            WebDriverWait wait = new WebDriverWait(this.driver, Duration.ofSeconds(10L));

            List<WebElement> lawyers = wait.until(
                    ExpectedConditions.presenceOfAllElementsLocatedBy(
                            By.className("person")
                    )
            );
            return this.siteUtl.filterLawyersInPage(lawyers, byRoleArray, false, validRoles);

        } catch (Exception e) {
            throw new RuntimeException("Failed to find lawyer elements", e);
        }
    }


    public void openNewTab(WebElement lawyer) {
        By[] byArray = new By[]{
                By.cssSelector("a")
        };
        WebElement element = this.siteUtl.iterateOverBy(byArray, lawyer);
        MyDriver.openNewTab(element.getAttribute("href"));
    }


    private String getName(WebElement lawyer) {
        By[] byArray = new By[]{
                By.className("name")
        };
        WebElement element = this.siteUtl.iterateOverBy(byArray, lawyer);
        return element.getText();
    }


    private String getRole(WebElement lawyer) {
        By[] byArray = new By[]{
                By.className("title")
        };
        WebElement element = this.siteUtl.iterateOverBy(byArray, lawyer);
        return element.getText();
    }


    private String getPracticeArea() {
        try {
            WebElement lawyer = driver.findElement(By.className("tags-container"));
            By[] byArray = new By[]{
                    By.className("lastTag")
            };
            WebElement element = this.siteUtl.iterateOverBy(byArray, lawyer);
            return element.getText();
        } catch (Exception e) {
            return "";
        }
    }


    private String[] getSocials(WebElement lawyer) {
        try {
            List<WebElement> socials = lawyer
                        .findElements(By.cssSelector("a"));

            // Reverse the strings
            String[] socialsResult = super.getSocials(socials, true);
            for (int i = 0; i < socialsResult.length; i++) {
                StringBuilder sb = new StringBuilder(socialsResult[i]);

                // Assign the reversed string back to its position in the array
                socialsResult[i] = sb.reverse().toString();
            }

            return socialsResult;

        } catch (Exception e) {
            System.err.println("Error getting socials: " + e.getMessage());
            return new String[]{"", ""};
        }
    }


    public Object getLawyer(WebElement lawyer) throws Exception {
        this.openNewTab(lawyer);

        WebElement div = driver.findElement(By.className("hero__content"));

        String[] socials = this.getSocials(div);

        return Map.of(
            "link", Objects.requireNonNull(driver.getCurrentUrl()),
            "name", this.getName(div),
            "role", this.getRole(div),
            "firm", this.name,
            "country", "Denmark",
            "practice_area", this.getPracticeArea(),
            "email", socials[0],
            "phone", socials[1].isEmpty() ? "33344000" : socials[1]
        );
    }
}
