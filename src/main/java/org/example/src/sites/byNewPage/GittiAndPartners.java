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

public class GittiAndPartners extends ByNewPage {
    String[] validRoles = new String[]{
            "partner", "counsel"
    };

    public GittiAndPartners() {
        super(
            "Gitti And Partners",
            "https://www.grplex.com/en/the-lawyers",
            1
        );
    }


    protected void accessPage(int index) throws InterruptedException {
        this.driver.get(this.link);
        MyDriver.waitForPageToLoad();
        Thread.sleep(1000L);

        // Click on add btn
//        MyDriver.clickOnElement(By.id(""));
    }


    protected List<WebElement> getLawyersInPage() {
        List<WebElement> lawyers = new java.util.ArrayList<>(List.of());
        try {
            WebDriverWait wait = new WebDriverWait(this.driver, Duration.ofSeconds(10L));

             lawyers.addAll(wait.until(
                    ExpectedConditions.presenceOfAllElementsLocatedBy(
                            By.cssSelector("div.listingx2 > ul > li > a")
                    )
            ));

             lawyers.addAll(wait.until(
                    ExpectedConditions.presenceOfAllElementsLocatedBy(
                            By.cssSelector("div.listing > ul > li > a")
                    )
            ));

             return lawyers;
        } catch (Exception e) {
            throw new RuntimeException("Failed to find lawyer elements", e);
        }
    }


    public void openNewTab(WebElement lawyer) {
        MyDriver.openNewTab(lawyer.getAttribute("href"));
    }


    private String getName(WebElement lawyer) {
        By[] byArray = new By[]{
                By.cssSelector("h2")
        };
        WebElement element = this.siteUtl.iterateOverBy(byArray, lawyer);
        return element.getText();
    }


    private String getRole(WebElement lawyer) {
        By[] byArray = new By[]{
                By.className("right"),
                By.cssSelector("p")
        };
        WebElement element = this.siteUtl.iterateOverBy(byArray, lawyer);
        String role = element.getText();
        boolean validPosition = siteUtl.isValidPosition(role, validRoles);
        return validPosition ? role : "Invalid Role";
    }


    private String[] getSocials(WebElement lawyer) {
        try {
            List<WebElement> socials = lawyer
                        .findElement(By.className("left"))
                        .findElement(By.className("info-contact"))
                        .findElements(By.cssSelector("a"));
            return super.getSocials(socials, false);

        } catch (Exception e) {
            System.err.println("Error getting socials: " + e.getMessage());
            return new String[]{"", ""};
        }
    }


    public Object getLawyer(WebElement lawyer) throws Exception {
        this.openNewTab(lawyer);

        WebElement div = driver.findElement(By.className("content"));
        String role = this.getRole(div);

        String[] socials = this.getSocials(div);

        return Map.of(
            "link", Objects.requireNonNull(driver.getCurrentUrl()),
            "name", this.getName(div),
            "role", role,
            "firm", this.name,
            "country", "Italy",
            "practice_area", "",
            "email", socials[0],
            "phone", "39027217091"
        );
    }
}
