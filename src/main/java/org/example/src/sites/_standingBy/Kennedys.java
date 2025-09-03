package org.example.src.sites._standingBy;

import org.example.exceptions.LawyerExceptions;
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

import static java.util.Map.entry;

public class Kennedys extends ByNewPage {
    public static final Map<String, String> OFFICE_TO_COUNTRY = Map.ofEntries(
            entry("auckland", "New Zealand"),
            entry("belfast", "England"),
            entry("bermuda", "Bermuda"),
            entry("birmingham", "England"),
            entry("bogota", "Colombia"),
            entry("brisbane", "Australia"),
            entry("bristol", "England"),
            entry("buenos aires", "Argentina"),
            entry("cambridge", "England"),
            entry("chelmsford", "England"),
            entry("copenhagen", "Denmark"),
            entry("dubai", "the UAE"),
            entry("dublin", "Ireland"),
            entry("edinburgh", "England"),
            entry("glasgow", "England"),
            entry("hong kong", "Hong Kong"),
            entry("leeds", "England"),
            entry("lima", "Peru"),
            entry("london", "England"),
            entry("madrid", "Spain"),
            entry("manchester", "England"),
            entry("melbourne", "Australia"),
            entry("mexico city", "Mexico"),
            entry("muscat", "Oman"),
            entry("newcastle", "England"),
            entry("paris", "France"),
            entry("perth", "Australia"),
            entry("santiago", "Chile"),
            entry("sheffield", "England"),
            entry("singapore", "Singapore"),
            entry("sydney", "Australia"),
            entry("taunton", "England"),
            entry("tel aviv", "Israel"),
            entry("wellington", "New Zealand")
    );


    private final By[] byRoleArray = {
            By.className("search-result-description")
    };

    public Kennedys() {
        super(
            "Kennedys",
            "https://kennedyslaw.com/en/search/?q=",
            125,
            3
        );
    }


    @Override
    protected void accessPage(int index) throws InterruptedException {
        this.driver.get(this.link);
        MyDriver.waitForPageToLoad();
        Thread.sleep(1000L);

        if (index == 0) {
            // Click on add btn
            MyDriver.clickOnElement(By.id("CybotCookiebotDialogBodyLevelButtonLevelOptinAllowAll"));
            MyDriver.clickOnElement(By.xpath("//*[@id=\"__layout\"]/div/main/section/div[1]/div[1]/div[2]/div/div/div/div/div/button[5]"));
        } else {
            //the change of pages doesnt loads more lawyers
            WebElement currentPage = driver.findElement(By.cssSelector("li.kenn-page.active a"));
            int current = Integer.parseInt(currentPage.getText().trim());

            // Find the "next" page by number
            WebElement nextPage = driver.findElement(
                    By.xpath("//li[@class='kenn-page']/a[normalize-space(text())='" + (current + 1) + "']")
            );
            MyDriver.clickOnElement(nextPage);

        }

        MyDriver.waitForPageToLoad();
        Thread.sleep(10000L);
    }


    @Override
    protected List<WebElement> getLawyersInPage() {
        String[] validRoles = new String[]{
                "partner",
                "counsel",
                "director",
                "senior associate"
        };

        try {
            WebDriverWait wait = new WebDriverWait(this.driver, Duration.ofSeconds(10L));

            List<WebElement> lawyers = wait.until(
                    ExpectedConditions.presenceOfAllElementsLocatedBy(
                            By.className("search-result")
                    )
            );
            return this.siteUtl.filterLawyersInPage(lawyers, byRoleArray, true, validRoles);

        } catch (Exception e) {
            throw new RuntimeException("Failed to find lawyer elements", e);
        }
    }


    public void openNewTab(WebElement lawyer) throws LawyerExceptions {
        MyDriver.openNewTab(lawyer.getAttribute("href"));
    }


    private String getName(WebElement lawyer) throws LawyerExceptions {
        By[] byArray = new By[]{
                By.cssSelector("h1")
        };
        return extractor.extractLawyerText(lawyer, byArray, "NAME", LawyerExceptions::nameException);
    }


    private String getRole(WebElement lawyer) throws LawyerExceptions {
        By[] byArray = new By[]{
                By.className("person-bio")
        };
        return extractor.extractLawyerText(lawyer, byArray, "ROLE", LawyerExceptions::roleException);
    }


    private String getCountry(WebElement lawyer) throws LawyerExceptions {
        By[] byArray = new By[]{
                By.className("person-bio"),
                By.className("person-office-country-name")
        };
        String country = extractor.extractLawyerAttribute(lawyer, byArray, "COUNTRY", "innerHTML", LawyerExceptions::countryException);
        return siteUtl.getCountryBasedInOffice(OFFICE_TO_COUNTRY, country, "USA");
    }


    private String getPracticeArea() throws LawyerExceptions {
        WebElement lawyer = driver.findElement(By.className("services-list"));
        By[] byArray = new By[]{
                By.cssSelector("li.service-tile > a")
        };
        return extractor.extractLawyerText(lawyer, byArray, "PRACTICE AREA", LawyerExceptions::practiceAreaException);
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


    @Override
    public Object getLawyer(WebElement lawyer) throws Exception {
        this.openNewTab(lawyer);

        WebElement div = driver.findElement(By.className("person-card"));

        String[] socials = this.getSocials(div);

        return Map.of(
                "link", Objects.requireNonNull(driver.getCurrentUrl()),
                "name", this.getName(div),
                "role", this.getRole(div),
                "firm", this.name,
                "practice_area", this.getPracticeArea(),
                "country", this.getCountry(div),
                "email", socials[0],
                "phone", socials[1].isEmpty() ? "" : socials[1]
        );
    }
}
