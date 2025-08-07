package org.example.src.sites.byPage;

import org.example.src.entities.BaseSites.ByPage;
import org.example.src.entities.MyDriver;
import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.TimeoutException;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.time.Duration;
import java.util.List;
import java.util.Map;

public class GomezAceboAndPombo extends ByPage {
    public static final Map<String, String> OFFICE_TO_COUNTRY = Map.of(
            "barcelona", "Spain",
            "bilbao", "Spain",
            "brussels", "Belgium",
            "lisbon", "Portugal",
            "london", "England",
            "madrid", "Spain",
            "new york", "USA",
            "valencia", "Spain",
            "vigo", "Spain"
    );


    private final By[] byRoleArray = {
            By.className("cargo")
    };


    public GomezAceboAndPombo() {
        super(
            "Gomez Acebo And Pombo",
            "https://ga-p.com/en/people/",
            4,
            2
        );
    }


    private void goToPage(int pageNumber) throws InterruptedException {
        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(10));

        while (true) {
            try {
                // Try to find the page link
                By pageLocator = By.cssSelector("div.flechas div.numbers a[data-page='" + pageNumber + "']");
                WebElement pageLink = wait.until(ExpectedConditions.presenceOfElementLocated(pageLocator));

                // Wait until it's clickable
                wait.until(ExpectedConditions.elementToBeClickable(pageLocator));

                // Click using JS (more reliable for pagination)
                ((JavascriptExecutor) driver).executeScript("arguments[0].click();", pageLink);

                // Wait for old element to become stale (pagination updates)
                wait.until(ExpectedConditions.stalenessOf(pageLink));

                // Confirm that the new active page matches the page number
                wait.until(ExpectedConditions.presenceOfElementLocated(
                        By.cssSelector("div.flechas div.numbers a.active[data-page='" + pageNumber + "']")
                ));

                break; // Done
            } catch (TimeoutException e) {
                // If not found, click the right arrow to reveal more pages
                By arrowRightLocator = By.cssSelector("div.flechas a.arrow-right");
                WebElement arrowRight = wait.until(ExpectedConditions.elementToBeClickable(arrowRightLocator));

                ((JavascriptExecutor) driver).executeScript("arguments[0].click();", arrowRight);

                Thread.sleep(9000L);
            }
        }
    }


    protected void accessPage(int index) throws InterruptedException {
        this.driver.get(this.link);
        MyDriver.waitForPageToLoad();
        Thread.sleep(1000L);

        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(15));

        // Load more lawyers for page
        By byLoadMore = By.cssSelector("div.numero_por_pagina > div[data-pagina=\"36\"]");
        MyDriver.clickOnElement(byLoadMore);
        Thread.sleep(3000L);

        if (index == 0) return;

        // Click on next Pages
        goToPage(index + 1);
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
                            By.className("personaInfo")
                    )
            );
            return this.siteUtl.filterLawyersInPage(lawyers, byRoleArray, true, validRoles);

        } catch (Exception e) {
            throw new RuntimeException("Failed to find lawyer elements", e);
        }
    }


    private String getLink(WebElement lawyer) {
        By[] byArray = new By[]{
                By.cssSelector("a")
        };
        WebElement element = this.siteUtl.iterateOverBy(byArray, lawyer);
        return element.getAttribute("href");
    }


    private String getName(WebElement lawyer) {
        By[] byArray = new By[]{
                By.className("name")
        };
        WebElement element = this.siteUtl.iterateOverBy(byArray, lawyer);
        return element.getText();
    }


    private String getRole(WebElement lawyer) {
        WebElement element = this.siteUtl.iterateOverBy(byRoleArray, lawyer);
        return element.getText();
    }


    private String getCountry(WebElement lawyer) {
        By[] byArray = new By[]{
                By.cssSelector("a[href^='tel'")
        };
        WebElement element = this.siteUtl.iterateOverBy(byArray, lawyer);
        return siteUtl.getCountryBasedInOffice(OFFICE_TO_COUNTRY, element.getText());
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
        String[] socials = this.getSocials(lawyer);
        return Map.of(
            "link", this.getLink(lawyer),
            "name", this.getName(lawyer),
            "role", this.getRole(lawyer),
            "firm", this.name,
            "country", this.getCountry(lawyer),
            "practice_area", "",
            "email", socials[0],
            "phone", socials[1]
        );
    }
}
