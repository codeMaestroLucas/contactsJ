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

public class GORG extends ByNewPage {
    private final By[] byRoleArray = {
            By.className("position")
    };


    public GORG() {
        super(
            "GÖRG",
            "https://www.goerg.de/en/our-people",
            31
        );
    }


    protected void accessPage(int index) throws InterruptedException {
        String otherUrl = "https://www.goerg.de/en/our-people?page=" + (index + 1);
        String url = index == 0 ? this.link : otherUrl;
        this.driver.get(url);
        MyDriver.waitForPageToLoad();
        Thread.sleep(1000L);


        // Click on add btn
        MyDriver.clickOnElement(By.cssSelector("button.button.accept"));
    }


    protected List<WebElement> getLawyersInPage() {
        String[] validRoles = new String[]{
                "partner"
        };

        try {
            WebDriverWait wait = new WebDriverWait(this.driver, Duration.ofSeconds(10L));

            List<WebElement> lawyers = wait.until(
                    ExpectedConditions.presenceOfAllElementsLocatedBy(
                            By.className("views-row")
                    )
            );
            return this.siteUtl.filterLawyersInPage(lawyers, byRoleArray, true, validRoles);

        } catch (Exception e) {
            throw new RuntimeException("Failed to find lawyer elements", e);
        }
    }


    public void openNewTab(WebElement lawyer) {
        By[] byArray = new By[]{
                By.className("text-container")
        };
        WebElement element = this.siteUtl.iterateOverBy(byArray, lawyer);
        MyDriver.openNewTab(element.getAttribute("href"));
    }


    private String getName(WebElement lawyer) {
        By[] byArray = new By[]{
                By.className("headline"),
                By.className("person-title"),
        };
        WebElement element = this.siteUtl.iterateOverBy(byArray, lawyer);
        return element.getText();
    }


    private String getRole(WebElement lawyer) {
        By[] byArray = new By[]{
                By.className("headline"),
                By.className("person-subline")
        };
        WebElement element = this.siteUtl.iterateOverBy(byArray, lawyer);
        return element.getText();
    }


    private String getPracticeArea() {
        By[] byArray = new By[]{
                By.className("field-fields-of-law"),
                By.cssSelector("div > a")
        };
        WebElement lawyer = driver.findElement(By.className("fields-of-law-wrapper"));

        WebElement element = this.siteUtl.iterateOverBy(byArray, lawyer);
        return element.getText();
    }


    private String[] getSocials(WebElement lawyer) {
        try {
            List<WebElement> socials = lawyer
                        .findElements(By.cssSelector("a"));
            return super.getSocials(socials, false);

        } catch (Exception e) {
            System.err.println("Error getting socials: " + e.getMessage());
            return new String[]{"", ""};
        }
    }


    public Object getLawyer(WebElement lawyer) throws Exception {
        this.openNewTab(lawyer);

        WebElement div = driver.findElement(By.className("contact-information"));

        String[] socials = this.getSocials(div);

        return Map.of(
            "link", Objects.requireNonNull(driver.getCurrentUrl()),
            "name", this.getName(div),
            "role", this.getRole(div),
            "firm", this.name,
            "country", "Germany",
            "practice_area", this.getPracticeArea(),
            "email", socials[0],
            "phone", socials[1].isEmpty() ? "" : socials[1]
        );
    }
}
