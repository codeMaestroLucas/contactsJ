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

public class Milbank extends ByPage {

    private static final Map<String, String> OFFICE_TO_COUNTRY = Map.ofEntries(
            entry("beijing", "China"),
            entry("frankfurt", "Germany"),
            entry("hong kong", "Hong Kong"),
            entry("london", "England"),
            entry("los angeles", "USA"),
            entry("munich", "Germany"),
            entry("new york", "USA"),
            entry("são paulo", "Brazil"),
            entry("seoul", "South Korea"),
            entry("singapore", "Singapore"),
            entry("tokyo", "Japan"),
            entry("washington, d.c.", "USA")
    );
    private final By[] byRoleArray = {
            By.className("card__second-line")
    };

    public Milbank() {
        super(
                "Milbank",
                "https://www.milbank.com/en/professionals/index.html?v=attorney&t=20012&20012_name=Partner&t=20014&20014_name=Of%20Counsel&t=20038&20038_name=Partner%2FExecutive%20Director&t=93462&93462_name=General%20Counsel&t=200789&200789_name=Deputy%20General%20Counsel&f=0",
                10
        );
    }

    @Override
    protected void accessPage(int index) throws InterruptedException {
        String otherUrl  = "https://www.milbank.com/en/professionals/index.html?20012_name=Partner&20014_name=Of%20Counsel&20038_name=Partner%2FExecutive%20Director&200789_name=Deputy%20General%20Counsel&93462_name=General%20Counsel&t=20012%2C20014%2C20038%2C93462%2C200789&v=attorney&f="+ (25 * index) + "&s=25";
        String url = index == 0? this.link : otherUrl;
        this.driver.get(url);
        MyDriver.waitForPageToLoad();
        Thread.sleep(1000L);
    }

    @Override
    protected List<WebElement> getLawyersInPage() {
        try {
            WebDriverWait wait = new WebDriverWait(this.driver, Duration.ofSeconds(10L));
            return wait.until(
                    ExpectedConditions.presenceOfAllElementsLocatedBy(
                            By.className("card--attorney")
                    )
            );
        } catch (Exception e) {
            throw new RuntimeException("Failed to find lawyer elements", e);
        }
    }

    private String getLink(WebElement lawyer) throws LawyerExceptions {
        By[] byArray = new By[]{By.cssSelector("a[href*='/professionals/']")};
        return extractor.extractLawyerAttribute(lawyer, byArray, "LINK", "href", LawyerExceptions::linkException);
    }

    private String getName(WebElement lawyer) throws LawyerExceptions {
        By[] byArray = new By[]{
                By.className("card__first-name")
        };
        return extractor.extractLawyerText(lawyer, byArray, "NAME", LawyerExceptions::nameException);
    }

    private String getRole(WebElement lawyer) throws LawyerExceptions {
        return extractor.extractLawyerText(lawyer, byRoleArray, "ROLE", LawyerExceptions::roleException);
    }

    private String getCountry(WebElement lawyer) throws LawyerExceptions {
        By[] byArray = new By[]{
                By.className("card__office-name")
        };
        String office = extractor.extractLawyerText(lawyer, byArray, "COUNTRY", LawyerExceptions::countryException);
        return siteUtl.getCountryBasedInOffice(OFFICE_TO_COUNTRY, office, office);
    }

    private String[] getSocials(WebElement lawyer) {
        try {
            String email = lawyer.findElement(By.className("card__email")).findElement(By.tagName("a")).getAttribute("href");
            String phone = lawyer.findElement(By.className("card__phone")).findElement(By.tagName("a")).getAttribute("href");
            return new String[]{email, phone};
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
                "role", this.getRole(lawyer),
                "firm", this.name,
                "country", this.getCountry(lawyer),
                "practice_area", "",
                "email", socials[0],
                "phone", socials[1]
        );
    }
}