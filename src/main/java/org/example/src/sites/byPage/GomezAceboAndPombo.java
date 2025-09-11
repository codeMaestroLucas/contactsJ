package org.example.src.sites.byPage;

import org.example.exceptions.LawyerExceptions;
import org.example.src.entities.BaseSites.ByPage;
import org.example.src.entities.MyDriver;
import org.openqa.selenium.*;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.time.Duration;
import java.util.List;
import java.util.Map;

import static java.util.Map.entry;

public class GomezAceboAndPombo extends ByPage {
    public static final Map<String, String> OFFICE_TO_COUNTRY = Map.ofEntries(
            entry("brussels", "Belgium"),
            entry("lisbon", "Portugal"),
            entry("london", "England"),
            entry("new york", "USA")
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
                By pageLocator = By.cssSelector("div.flechas div.numbers a[data-page='" + pageNumber + "']");
                WebElement pageLink = wait.until(ExpectedConditions.presenceOfElementLocated(pageLocator));
                wait.until(ExpectedConditions.elementToBeClickable(pageLocator));
                ((JavascriptExecutor) driver).executeScript("arguments[0].click();", pageLink);
                wait.until(ExpectedConditions.stalenessOf(pageLink));
                wait.until(ExpectedConditions.presenceOfElementLocated(By.cssSelector("div.flechas div.numbers a.active[data-page='" + pageNumber + "']")));
                break;
            } catch (TimeoutException e) {
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

        By byLoadMore = By.cssSelector("div.numero_por_pagina > div[data-pagina=\"36\"]");
        MyDriver.clickOnElement(byLoadMore);
        Thread.sleep(3000L);

        if (index == 0) return;
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
            List<WebElement> lawyers = wait.until(ExpectedConditions.presenceOfAllElementsLocatedBy(By.className("personaInfo")));
            return this.siteUtl.filterLawyersInPage(lawyers, byRoleArray, true, validRoles);
        } catch (Exception e) {
            throw new RuntimeException("Failed to find lawyer elements", e);
        }
    }


    public String getLink(WebElement lawyer) throws LawyerExceptions {
        By[] byArray = new By[]{
                By.cssSelector("a")
        };
        return extractor.extractLawyerAttribute(lawyer, byArray, "LINK", "href", LawyerExceptions::linkException);
    }


    private String getName(WebElement lawyer) throws LawyerExceptions {
        By[] byArray = new By[]{
                By.className("name")
        };
        return extractor.extractLawyerText(lawyer, byArray, "NAME", LawyerExceptions::nameException);
    }


    private String getRole(WebElement lawyer) throws LawyerExceptions {
        return extractor.extractLawyerText(lawyer, byRoleArray, "ROLE", LawyerExceptions::roleException);
    }


    private String getCountry(WebElement lawyer) throws LawyerExceptions {
        By[] byArray = new By[]{
                By.cssSelector("a[href^='tel'")
        };
        String phoneLink = extractor.extractLawyerAttribute(lawyer, byArray, "COUNTRY", "href", LawyerExceptions::countryException);
        return siteUtl.getCountryBasedInOffice(OFFICE_TO_COUNTRY, phoneLink, "Spain");
    }


    private String[] getSocials(WebElement lawyer) {
        try {
            List<WebElement> socials = lawyer.findElements(By.cssSelector("a"));
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