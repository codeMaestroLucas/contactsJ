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

public class GornitzkyAndCo extends ByNewPage {
    public GornitzkyAndCo() {
        super(
            "Gornitzky And Co",
            // Remove this when all the lawyer are registered - few in this page
            "https://www.gornitzky.com/attorneys/?letter=&member=&practice=&role=83#pageContent",
            2
        );
    }


    protected void accessPage(int index) throws InterruptedException {
        String otherUrl = "https://www.gornitzky.com/attorneys/?letter=&member=&practice=&role=8#pageContent";
        String url = index == 0 ? this.link : otherUrl;
        this.driver.get(url);
        MyDriver.waitForPageToLoad();
        Thread.sleep(1000L);
    }


    protected List<WebElement> getLawyersInPage() {
        try {
            WebDriverWait wait = new WebDriverWait(this.driver, Duration.ofSeconds(10L));

            return wait.until(
                    ExpectedConditions.presenceOfAllElementsLocatedBy(
                            By.className("part-member-simple")
                    )
            );

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
                By.className("member-name")
        };
        WebElement element = this.siteUtl.iterateOverBy(byArray, lawyer);
        return element.getText();
    }


    private String getRole(WebElement lawyer) {
        By[] byArray = new By[]{
                By.className("member-role")
        };
        WebElement element = this.siteUtl.iterateOverBy(byArray, lawyer);
        return element.getText();
    }


    private String getPracticeArea() {
        try {
            WebElement lawyer = driver.findElement(By.className("page-sidebar"));
            By[] byArray = new By[]{
                    By.className("sidebar-menu"),
                    By.cssSelector("li > a ")
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
                        .findElement(By.className("member-information-list"))
                        .findElements(By.cssSelector("a"));
            return super.getSocials(socials, false);

        } catch (Exception e) {
            System.err.println("Error getting socials: " + e.getMessage());
            return new String[]{"", ""};
        }
    }


    public Object getLawyer(WebElement lawyer) throws Exception {
        this.openNewTab(lawyer);

        WebElement div = driver.findElement(By.className("member-content-wrap"));

        String[] socials = this.getSocials(div);

        return Map.of(
            "link", Objects.requireNonNull(driver.getCurrentUrl()),
            "name", this.getName(div),
            "role", this.getRole(div),
            "firm", this.name,
            "country", "Israel",
            "practice_area", this.getPracticeArea(),
            "email", socials[0],
            "phone", socials[1].isEmpty() ? "" : socials[1]
        );
    }
}
