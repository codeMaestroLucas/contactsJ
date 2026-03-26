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

public class SquirePattonBoggs extends ByPage {
    private final By[] byRoleArray = {
            By.className("professional-title")
    };

    public static final Map<String, String> OFFICE_TO_COUNTRY = Map.ofEntries(
            Map.entry("1", "USA"),
            Map.entry("7", "Kazakhstan"),
            Map.entry("31", "the Netherlands"),
            Map.entry("32", "Belgium"),
            Map.entry("33", "France"),
            Map.entry("34", "Spain"),
            Map.entry("39", "Italy"),
            Map.entry("41", "Switzerland"),
            Map.entry("44", "England"),
            Map.entry("48", "Poland"),
            Map.entry("49", "Germany"),
            Map.entry("61", "Australia"),
            Map.entry("65", "Singapore"),
            Map.entry("81", "Japan"),
            Map.entry("86", "China"),
            Map.entry("353", "Ireland"),
            Map.entry("420", "the Czech Republic"),
            Map.entry("421", "Slovakia"),
            Map.entry("852", "Hong Kong"),
            Map.entry("961", "Lebanon"),
            Map.entry("966", "Saudi Arabia"),
            Map.entry("971", "the UAE")
    );

    public SquirePattonBoggs() {
        super(
                "Squire Patton Boggs",
                "https://www.squirepattonboggs.com/en/professionals/?PageNumber=1",
                1,
                3
        );
    }

    protected void accessPage(int index) {
        this.driver.get(this.link);
        MyDriver.waitForPageToLoad();
        // More than 40
        MyDriver.clickOnElementMultipleTimes(By.xpath("/html/body/div[1]/div/section[2]/button"), 10, 1);
    }

    protected List<WebElement> getLawyersInPage() {
        String[] validRoles = {"director", "partner", "counsel", "principal", "advisor", "senior associate"};

        try {
            WebDriverWait wait = new WebDriverWait(this.driver, Duration.ofSeconds(10L));
            List<WebElement> lawyers = wait.until(
                    ExpectedConditions.presenceOfAllElementsLocatedBy(
                            By.className("person")
                    )
            );
            return this.siteUtl.filterLawyersInPage(lawyers, byRoleArray, true, validRoles);
        } catch (Exception e) {
            throw new RuntimeException("Failed to find lawyer elements", e);
        }
    }

    private String getLink(WebElement lawyer) throws LawyerExceptions {
        By[] byArray = {By.cssSelector("h2.name > a")};
        return extractor.extractLawyerAttribute(lawyer, byArray, "LINK", "href", LawyerExceptions::linkException);
    }

    private String getName(WebElement lawyer) throws LawyerExceptions {
        By[] byArray = {By.className("name")};
        return extractor.extractLawyerText(lawyer, byArray, "NAME", LawyerExceptions::nameException);
    }

    private String getRole(WebElement lawyer) throws LawyerExceptions {
        return extractor.extractLawyerText(lawyer, byRoleArray, "ROLE", LawyerExceptions::roleException);
    }

    private String getCountry(String phone) throws LawyerExceptions {
        return this.siteUtl.getCountryBasedInOfficeByPhone(OFFICE_TO_COUNTRY, phone, "Not Found");
    }

    private String[] getSocials(WebElement lawyer) {
        String email = "";
        String phone = "";
        try {
            email = lawyer.findElement(By.cssSelector("span.email > a")).getAttribute("href");
            phone = lawyer.findElement(By.className("office-phone")).getAttribute("href");
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
                "country", this.getCountry(socials[1]),
                "practice_area", "",
                "email", socials[0],
                "phone", socials[1]
        );
    }
}