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

public class LaszczukAndWspolnicy extends ByPage {
    private final By[] byRoleArray = {
            By.className("job-position")
    };


    public LaszczukAndWspolnicy() {
        super(
            "Laszczuk And Wspolnicy",
            "https://laszczuk.pl/en/aktualnosci/zespol/",
            1
        );
    }


    protected void accessPage(int index) throws InterruptedException {
        this.driver.get(this.link);
        MyDriver.waitForPageToLoad();
        Thread.sleep(1000L);

        // Click on add btn
        MyDriver.clickOnElement(By.cssSelector("div#cookies > a.accept"));
    }


    protected List<WebElement> getLawyersInPage() {
        String[] validRoles = new String[]{
                "partner",
                "managing associate",
                "senior associate",
                "counsel"
        };

        try {
            WebDriverWait wait = new WebDriverWait(this.driver, Duration.ofSeconds(10L));

            List<WebElement> lawyers = wait.until(
                    ExpectedConditions.presenceOfAllElementsLocatedBy(
                            By.className("image-caption")
                    )
            );
            return this.siteUtl.filterLawyersInPage(lawyers, byRoleArray, true, validRoles);

        } catch (Exception e) {
            throw new RuntimeException("Failed to find lawyer elements", e);
        }
    }


    private String getLink(WebElement lawyer) {
        By[] byArray = new By[]{
                By.className("medium-header"),
                By.cssSelector("a")
        };
        WebElement element = this.siteUtl.iterateOverBy(byArray, lawyer);
        return element.getAttribute("href");
    }


    private String getName(WebElement lawyer) {
        By[] byArray = new By[]{
                By.className("medium-header"),
                By.cssSelector("a")
        };
        WebElement element = this.siteUtl.iterateOverBy(byArray, lawyer);
        return element.getText();
    }


    private String getRole(WebElement lawyer) {
        WebElement element = this.siteUtl.iterateOverBy(byRoleArray, lawyer);
        return element.getText();
    }


    private String getEmail(WebElement lawyer) {
        By[] byArray = new By[]{
                By.className("mail-address"),
        };
        WebElement element = this.siteUtl.iterateOverBy(byArray, lawyer);
        return siteUtl.getContentFromTag(element.getAttribute("outerHTML")).trim();
    }


    public Object getLawyer(WebElement lawyer) throws Exception {
        return Map.of(
            "link", this.getLink(lawyer),
            "name", this.getName(lawyer),
            "role", this.getRole(lawyer),
            "firm", this.name,
            "country", "Poland",
            "practice_area", "",
            "email", this.getEmail(lawyer),
            "phone", "48223510067"
        );
    }
}
