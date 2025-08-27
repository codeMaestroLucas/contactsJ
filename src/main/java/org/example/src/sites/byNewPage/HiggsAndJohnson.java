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

public class HiggsAndJohnson extends ByNewPage {
    private final By[] byRoleArray = {
            By.className("title")
    };


    public HiggsAndJohnson() {
        super(
            "Higgs And Johnson",
            "https://higgsjohnson.com/attorneys/",
            1
        );
    }


    protected void accessPage(int index) throws InterruptedException {
        this.driver.get(this.link);
        MyDriver.waitForPageToLoad();
        Thread.sleep(1000L);

        // Click on add btn
        MyDriver.clickOnElement(By.className("cmplz-accept"));
    }


    protected List<WebElement> getLawyersInPage() {
        String[] validRoles = new String[]{
                "partner",
                "counsel",
                "senior associate"
        };

        try {
            WebDriverWait wait = new WebDriverWait(this.driver, Duration.ofSeconds(10L));

            List<WebElement> lawyers = wait.until(
                    ExpectedConditions.presenceOfAllElementsLocatedBy(
                            By.className("prev-content__meta")
                    )
            );
            List<WebElement> webElements = this.siteUtl.filterLawyersInPage(lawyers, byRoleArray, false, validRoles);
            webElements.remove(27); // INVALID - not href
            return webElements;

        } catch (Exception e) {
            throw new RuntimeException("Failed to find lawyer elements", e);
        }
    }


    public void openNewTab(WebElement lawyer) {
        By[] byArray = new By[]{
                By.cssSelector("a[href^='https://higgsjohnson.com/person/']")
        };
        WebElement element = this.siteUtl.iterateOverBy(byArray, lawyer);
        MyDriver.openNewTab(element.getAttribute("href"));
    }


    private String getName(WebElement lawyer) {
        By[] byArray = new By[]{
                By.cssSelector("h1")
        };
        WebElement element = this.siteUtl.iterateOverBy(byArray, lawyer);
        return element.getText().split(",")[0];
    }


    private String getRole(WebElement lawyer) {
        By[] byArray = new By[]{
                By.className("text-center-sm-d")
        };
        WebElement element = this.siteUtl.iterateOverBy(byArray, lawyer);
        return element.getText();
    }


    private String getPracticeArea() {
        WebElement lawyer = driver.findElement(By.cssSelector("ul.plain.related"));
        By[] byArray = new By[]{
                By.cssSelector("a[href^='https://higgsjohnson.com/practice/']")
        };
        WebElement element = this.siteUtl.iterateOverBy(byArray, lawyer);
        return element.getText();
    }


    private String[] getSocials(WebElement lawyer) {
        try {
            List<WebElement> socials = lawyer
                        .findElement(By.className("person-details"))
                        .findElements(By.cssSelector("a"));
            return super.getSocials(socials, false);

        } catch (Exception e) {
            System.err.println("Error getting socials: " + e.getMessage());
            return new String[]{"", ""};
        }
    }


    public Object getLawyer(WebElement lawyer) throws Exception {
        this.openNewTab(lawyer);

        WebElement div = driver.findElement(By.className("banner-inner__left-text"));

        String[] socials = this.getSocials(div);

        return Map.of(
            "link", Objects.requireNonNull(driver.getCurrentUrl()),
            "name", this.getName(div),
            "role", this.getRole(div),
            "firm", this.name,
            "country", "Bahamas",
            "practice_area", this.getPracticeArea(),
            "email", socials[0],
            "phone", socials[1].isEmpty() ? "" : socials[1]
        );
    }
}
