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

public class ClearyGottlieb extends ByPage {
    public static final Map<String, String> OFFICE_TO_COUNTRY = Map.ofEntries(
            Map.entry("abu dhabi", "the UAE"),
            Map.entry("brussels", "Belgium"),
            Map.entry("cologne", "Germany"),
            Map.entry("frankfurt", "Germany"),
            Map.entry("hong kong", "Hong Kong"),
            Map.entry("london", "England"),
            Map.entry("milan", "Italy"),
            Map.entry("paris", "France"),
            Map.entry("rome", "Italy"),
            Map.entry("são paulo", "Brazil"),
            Map.entry("seoul", "Korea (South)")
    );

    private final By[] byRoleArray = {
            By.className("search-results__title")
    };

    public ClearyGottlieb() {
        super(
                "Cleary Gottlieb",
                "https://www.clearygottlieb.com/professionals?position=1",
                1
        );
    }

    protected void accessPage(int index) throws InterruptedException {
        this.driver.get(this.link);
        MyDriver.waitForPageToLoad();
        Thread.sleep(1000L);

        MyDriver.clickOnAddBtn(By.className("trustarc-reject-btn"));

        MyDriver.clickOnElementMultipleTimes(
                By.xpath("//*[@id=\"maincontent\"]/main/div[2]/section[4]/section/button"), 20, 0.3
        );
    }

    protected List<WebElement> getLawyersInPage() {
        String[] validRoles = {"partner", "counsel"};
        try {
            WebDriverWait wait = new WebDriverWait(this.driver, Duration.ofSeconds(10L));
            List<WebElement> lawyers = wait.until(
                    ExpectedConditions.presenceOfAllElementsLocatedBy(
                            By.className("search-results__content")
                    )
            );
            return this.siteUtl.filterLawyersInPage(lawyers, byRoleArray, true, validRoles);
        } catch (Exception e) {
            throw new RuntimeException("Failed to find lawyer elements", e);
        }
    }

    private String getLink(WebElement lawyer) throws LawyerExceptions {
        By[] byArray = {By.cssSelector("h3.search-results__name > a")};
        return extractor.extractLawyerAttribute(lawyer, byArray, "LINK", "href", LawyerExceptions::linkException);
    }

    private String getName(WebElement lawyer) throws LawyerExceptions {
        By[] byArray = {By.cssSelector("h3.search-results__name > a")};
        return extractor.extractLawyerText(lawyer, byArray, "NAME", LawyerExceptions::nameException);
    }

    private String getRole(WebElement lawyer) throws LawyerExceptions {
        return extractor.extractLawyerText(lawyer, byRoleArray, "ROLE", LawyerExceptions::roleException);
    }

    private String getCountry(WebElement lawyer) throws LawyerExceptions {
        By[] byArray = {By.className("search-results__location")};
        String office = extractor.extractLawyerText(lawyer, byArray, "COUNTRY", LawyerExceptions::countryException);
        return siteUtl.getCountryBasedInOffice(OFFICE_TO_COUNTRY, office, "USA");
    }

    private String[] getSocials(WebElement lawyer) {
        String email = "";
        String phone = "";
        try {
            email = lawyer.findElement(By.className("search-results__email")).getAttribute("href");
            phone = lawyer.findElement(By.className("search-results__phone")).getAttribute("href");
        } catch (Exception e) {}
        return new String[]{email, phone};
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