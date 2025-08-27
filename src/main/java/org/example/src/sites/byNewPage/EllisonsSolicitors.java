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

public class EllisonsSolicitors extends ByNewPage {
    private final By[] byRoleArray = {
            By.cssSelector("p")
    };


    public EllisonsSolicitors() {
        super(
            "Ellisons Solicitors",
            "https://ellisons.com/our-people/",
            5
        );
    }


    protected void accessPage(int index) throws InterruptedException {
        String otherUrl = "https://ellisons.com/our-people/page/" + (index + 1) + "/";
        String url = index == 0 ? this.link : otherUrl;
        this.driver.get(url);
        MyDriver.waitForPageToLoad();
        Thread.sleep(1000L);

        if (index > 0) return;

        // Click on add btn
        MyDriver.clickOnElement(By.id("wt-cli-accept-all-btn"));
    }


    protected List<WebElement> getLawyersInPage() {
        String[] validRoles = new String[]{
                "partner",
                "director",
                "counsel",
                "senior associate"
        };

        try {
            WebDriverWait wait = new WebDriverWait(this.driver, Duration.ofSeconds(10L));

            List<WebElement> lawyers = wait.until(
                    ExpectedConditions.presenceOfAllElementsLocatedBy(
                            By.className("card-footer")
                    )
            );
            return this.siteUtl.filterLawyersInPage(lawyers, byRoleArray, true, validRoles);

        } catch (Exception e) {
            throw new RuntimeException("Failed to find lawyer elements", e);
        }
    }


    public void openNewTab(WebElement lawyer) {
        By[] byArray = new By[]{
                By.cssSelector("a[href^='https://ellisons.com/team/']")
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
                By.className("role")
        };
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


    public Map getLawyer(WebElement lawyer) throws Exception {
        this.openNewTab(lawyer);

        WebElement profile = driver.findElement(By.className("profile-header"));
        WebElement div = driver.findElement(By.className("card-wrapper"));

        String[] socials = this.getSocials(div);

        return Map.of(
                "link", Objects.requireNonNull(driver.getCurrentUrl()),
                "name", this.getName(profile),
                "role", this.getRole(profile),
                "firm", this.name,
                "country", "England",
                "practice_area", "",
                "email", socials[0],
                "phone", socials[1]
        );
    }
}
