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

public class DGKV extends ByNewPage {
    String[] validRoles = new String[]{
            "partner",
            "counsel",
            "senior associate"
    };

    String[] links = {
            "", // Start from the counsels, to register then at once and then remove this link
            "https://dgkv.com/lawyers?first_name=&last_name=&position=1&practice_area=&industry=&language=#results",
            "https://dgkv.com/lawyers?first_name=&last_name=&position=3&practice_area=&industry=&language=#results"
    };


    public DGKV() {
        super(
            "DGKV",
            "https://dgkv.com/lawyers?first_name=&last_name=&position=2&practice_area=&industry=&language=#results",
            3,
            1000
        );
    }


    protected void accessPage(int index) throws InterruptedException {
        String otherUrl = this.links[index];
        String url = index == 0 ? this.link : otherUrl;
        this.driver.get(url);
        MyDriver.waitForPageToLoad();
        Thread.sleep(1000L);

        if (index > 0) return;

        // Click on add btn
        MyDriver.clickOnElement(By.id("c-p-bn"));
    }


    protected List<WebElement> getLawyersInPage() {
        try {
            WebDriverWait wait = new WebDriverWait(this.driver, Duration.ofSeconds(10L));

            return wait.until(
                    ExpectedConditions.presenceOfAllElementsLocatedBy(
                            By.className("team-member--alt")
                    )
            );

        } catch (Exception e) {
            throw new RuntimeException("Failed to find lawyer elements", e);
        }
    }


    public void openNewTab(WebElement lawyer) {
        MyDriver.openNewTab(lawyer.getAttribute("href"));
    }


    private String getName(WebElement lawyer) {
        By[] byArray = new By[]{
                By.className("page-header__title")
        };
        WebElement element = this.siteUtl.iterateOverBy(byArray, lawyer);
        return element.getText();
    }


    private String getRole(WebElement lawyer) {
        By[] byArray = new By[]{
                By.className("page-header__subtitle")
        };
        WebElement element = this.siteUtl.iterateOverBy(byArray, lawyer);
        String role = element.getText();
        boolean validPosition = siteUtl.isValidPosition(role, validRoles);
        return validPosition ? role : "Invalid Role";
    }


    private String[] getSocials(WebElement lawyer) {
        try {
            List<WebElement> socials = lawyer
                        .findElement(By.className("contact-info"))
                        .findElements(By.cssSelector("a"));
            return super.getSocials(socials, false);

        } catch (Exception e) {
            System.err.println("Error getting socials: " + e.getMessage());
            return new String[]{"", ""};
        }
    }


    public Object getLawyer(WebElement lawyer) throws Exception {
        this.openNewTab(lawyer);

        WebElement div = driver.findElement(By.className("page-header"));

        String[] socials = this.getSocials(div);

        return Map.of(
            "link", Objects.requireNonNull(driver.getCurrentUrl()),
            "name", this.getName(div),
            "role", this.getRole(div),
            "firm", this.name,
            "country", "Bulgaria",
            "practice_area", "",
            "email", socials[0],
            "phone", socials[1].isEmpty() ? "3592932161" : socials[1]
        );
    }

    public static void main(String[] args) {
        DGKV x = new DGKV();
        x.searchForLawyers();
    }
}
