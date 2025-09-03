package org.example.src.sites.byPage;

import org.example.exceptions.LawyerExceptions;
import org.example.src.entities.BaseSites.ByPage;
import org.example.src.entities.MyDriver;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.time.Duration;
import java.util.List;
import java.util.Map;

import static java.util.Map.entry;

// A lot of USA Lawyers
public class MorganLewis extends ByPage {
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
            By.className("c-content-team__title")
    };


    public MorganLewis() {
        super(
            "Morgan Lewis",
            "https://www.morganlewis.com/our-people",
            41,
            2
        );
    }


    protected void accessPage(int index) throws InterruptedException {
        this.driver.get(this.link);
        MyDriver.waitForPageToLoad();
        Thread.sleep(1000L);

        if (index == 0) {
            // Click on add btn
            MyDriver.clickOnElement(By.id("onetrust-accept-btn-handler"));

//            this.tryToFilter();

        } else {
            WebElement nextBtn = driver.findElement(By.className("c-pagination__list"))
                    .findElement(By.cssSelector("a.c-pagination__link.js-pagination-link.next"));
            MyDriver.clickOnElement(nextBtn);
            MyDriver.waitForPageToLoad();
        }

    }

    private void tryToFilter() {
        MyDriver.clickOnElement(
                By.xpath("//*[@id=\"contentWrapper\"]/div/div[2]/div/div[2]/div[4]/div[3]/div/div/ul/li[2]")
        );

        MyDriver.clickOnElement(
                By.xpath("//*[@id=\"contentWrapper\"]/div/div[2]/div/div[2]/div[4]/div[3]/div/div/ul/li[2]/div/a")
        );

        List<WebElement> filterOpts = driver
                .findElement(By.xpath("//*[@id=\"contentWrapper\"]/div/div[2]/div/div[2]/div[4]/div[3]/div/div/ul/li[2]"))
                .findElements(By.cssSelector("div.c-filter__subcategory.from-grouped"));

        for (WebElement opt : filterOpts) {
            MyDriver.clickOnElement(opt);
        }
    }


    protected List<WebElement> getLawyersInPage() {
        String[] validRoles = new String[]{
                "partner",
                "counsel"
        };

        try {
            WebDriverWait wait = new WebDriverWait(this.driver, Duration.ofSeconds(10L));

            List<WebElement> lawyers = wait.until(
                    ExpectedConditions.presenceOfAllElementsLocatedBy(
                            By.className("c-content_team__card-details")
                    )
            );
            return this.siteUtl.filterLawyersInPage(lawyers, byRoleArray, true, validRoles);

        } catch (Exception e) {
            throw new RuntimeException("Failed to find lawyer elements", e);
        }
    }


    private String getLink(WebElement lawyer) throws LawyerExceptions {
        By[] byArray = new By[]{
                By.className("c-content_team__link")

        };
        return extractor.extractLawyerAttribute(lawyer, byArray, "LINK", "href", LawyerExceptions::linkException);
    }


    private String getName(WebElement container) throws LawyerExceptions {
        By[] byArray = new By[]{
                By.className("c-content_team__link"),
                By.className("c-content-team__name")
        };
        return extractor.extractLawyerText(container, byArray, "NAME", LawyerExceptions::nameException)
                .split(",")[0];
    }


    private String getRole(WebElement container) throws LawyerExceptions {
        return extractor.extractLawyerText(container, byRoleArray, "ROLE", LawyerExceptions::roleException);
    }


    private String getCountry(WebElement container) throws LawyerExceptions {
        By[] byArray = new By[]{
                By.className("c-content_team__card-info"),
                By.cssSelector("a[href^='/locations/']")
        };
        String country = extractor.extractLawyerText(container, byArray, "COUNTRY", LawyerExceptions::countryException);
        return siteUtl.getCountryBasedInOffice(OFFICE_TO_COUNTRY, country, "USA");
    }


    private String[] getSocials(WebElement lawyer) {
        String email = ""; String phone = "";

        email = lawyer
                .findElement(By.className("c-content_team__card-contact"))
                .findElement(By.cssSelector("a[href*='mailto:']"))
                .getAttribute("href");
        phone = lawyer
                .findElement(By.className("c-content_team__card-info"))
                .findElement(By.className("c-content-team__number"))
                .getText();

        return new String[] { email, phone };
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
                "phone", socials[1].isEmpty() ? "" : socials[1]
        );
    }
}