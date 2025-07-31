package org.example.src.sites._standingBy;

import org.example.src.entities.BaseSites.ByPage;
import org.example.src.entities.MyDriver;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.time.Duration;
import java.util.List;
import java.util.Map;

public class Roschier extends ByPage {
    public Roschier() {
        super(
            "Roschier",
            "https://www.roschier.com/people",
            1,
            1
        );
    }


    @Override
    protected void accessPage(int index) throws InterruptedException {
        driver.get(this.link);
        MyDriver.waitForPageToLoad();

        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(10));

        // Accept cookies if present
        try {
            WebElement cookieBtn = wait.until(ExpectedConditions.elementToBeClickable(
                    By.id("CybotCookiebotDialogBodyLevelButtonLevelOptinAllowAll")));
            cookieBtn.click();
        } catch (Exception e) {
            System.out.println("No cookie banner found.");
        }

        // Selecting Partner filter
        WebElement filterPosition = wait.until(ExpectedConditions.elementToBeClickable(By.id("select-position")));
        filterPosition.click();

        WebElement option = wait.until(ExpectedConditions.elementToBeClickable(
                By.cssSelector("option[value='990']")));
        option.click();

        WebElement searchButton = wait.until(ExpectedConditions.elementToBeClickable(
                By.className("search-form__submit")));
        searchButton.click();

        MyDriver.rollDown(1, 0.5);

        // Click on Load More option (up to 5 times)
        for (int i = 0; i < 5; i++) {
            try {
                WebElement button = wait.until(ExpectedConditions.elementToBeClickable(
                        By.cssSelector(".text-center .wp-block-button .wp-block-button__link")));
                button.click();

                MyDriver.rollDown(1, 5000);
            } catch (Exception e) {
                System.out.println("No more 'Load More' button found or click failed: " + e.getMessage());
                break;
            }
        }
    }


    @Override
    protected List<WebElement> getLawyersInPage() {
        try {
            // Wait up to 10 seconds for elements to be present
            WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(10));
            return wait.until(
                    ExpectedConditions.presenceOfAllElementsLocatedBy(By.className("card--person"))
            );

        } catch (Exception e) {
            throw new RuntimeException("Failed to find lawyer elements", e);
        }
    }


    private String getLink(WebElement lawyer) {
        By[] byArray = {
            By.className("card__link")
        };
        WebElement element = siteUtl.iterateOverBy(byArray, lawyer);
        return element.getAttribute("href");
    }


    private String getName(WebElement lawyer) {
        By[] byArray = {
                By.className("card__body"),
                By.className("card__title"),
                By.className("wp-block-button__link")
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
                By.className("card__body"),
                By.className("card__text")
        };
        WebElement element = siteUtl.iterateOverBy(byArray, lawyer);
        String country = element.getText();
        return country;
    }


    private String[] getSocials(WebElement lawyer) {
        try {
            List<WebElement> socials = lawyer
                    .findElement(By.className("card__footer"))
                    .findElement(By.className("card__contact"))
                    .findElement(By.className("card__contact__item"))
                    .findElements(By.cssSelector("a"));
            return super.getSocials(socials, false);

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
                "role", "Partner",
                "firm", this.name,
                "country", this.getCountry(lawyer),
                "practice_area", "",
                "email", socials[0],
                "phone", socials[1]
        );
    }


    public static void main(String[] args) {
        Roschier x = new Roschier();
        x.searchForLawyers();
    }
}
