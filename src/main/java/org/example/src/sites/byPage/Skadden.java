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

public class Skadden extends ByPage {
    public static final Map<String, String> OFFICE_TO_COUNTRY = Map.ofEntries(
            entry("abu dhabi", "the UAE"),
            entry("beijing", "China"),
            entry("brussels", "Belgium"),
            entry("frankfurt", "Germany"),
            entry("hong kong", "Hong Kong"),
            entry("london", "England"),
            entry("munich", "Germany"),
            entry("paris", "France"),
            entry("sao paulo", "Brazil"),
            entry("seoul", "Korea (South)"),
            entry("singapore", "Singapore"),
            entry("tokyo", "Japan"),
            entry("toronto", "Canada")
    );

    private final String[] letters = {
            "A","B","C","D","E","F","G","H","I","J","K","L","M",
            "N","O","P","Q","R","S","T","U","V","W","X","Y","Z"
    };

    private final By[] byRoleArray = {
            By.className("professional-result-card-position")
    };


    public Skadden() {
        super(
            "Skadden",
            "https://www.skadden.com/professionals?skip=0&letter=a&hassearched=true",
            26,
            3
        );
    }


    protected void accessPage(int index) throws InterruptedException {
        String currentVowel = letters[index];
        String otherUrl = "https://www.skadden.com/professionals?skip=0&letter=" + currentVowel.toLowerCase() + "&hassearched=true";
        String url = index == 0 ? this.link : otherUrl;
        this.driver.get(url);
        MyDriver.waitForPageToLoad();
        Thread.sleep(1000L);

        if (index == 0) MyDriver.clickOnElement(By.id("onetrust-accept-btn-handler"));

        MyDriver.clickOnElementMultipleTimes(
                By.className("professionals-landing-results-load-more"),
                10, 0.5
        );
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
                            By.className("professional-result-card")
                    )
            );
            return this.siteUtl.filterLawyersInPage(lawyers, byRoleArray, true, validRoles);

        } catch (Exception e) {
            throw new RuntimeException("Failed to find lawyer elements", e);
        }
    }


    private String getLink(WebElement lawyer) throws LawyerExceptions {
        By[] byArray = new By[]{
                By.className("professional-result-card-link")

        };
        return extractor.extractLawyerAttribute(lawyer, byArray, "LINK", "href", LawyerExceptions::linkException);
    }


    private String getName(WebElement container) throws LawyerExceptions {
        By[] byArray = new By[]{
                By.className("professional-result-card-link")
        };
        return extractor.extractLawyerAttribute(container, byArray, "NAME", "title", LawyerExceptions::nameException);
    }


    private String getRole(WebElement container) throws LawyerExceptions {
        return extractor.extractLawyerText(container, byRoleArray, "ROLE", LawyerExceptions::roleException);
    }


    private String getPracticeArea(WebElement container) throws LawyerExceptions {
        String PA = extractor.extractLawyerText(container, byRoleArray, "PRACTICE AREA", LawyerExceptions::practiceAreaException);
        String[] split = PA.split("-");
        return split[split.length - 1].trim();
    }


    private String getCountry(WebElement container) throws LawyerExceptions {
        By[] byArray = new By[]{
                By.className("professional-result-card-contact"),
                By.cssSelector("a[href^='/locations/']")
        };
        String country = extractor.extractLawyerText(container, byArray, "COUNTRY", LawyerExceptions::countryException);
        return siteUtl.getCountryBasedInOffice(OFFICE_TO_COUNTRY, country, "USA");
    }


    private String[] getSocials(WebElement lawyer) {
        try {
            List<WebElement> socials = lawyer
                    .findElement(By.className("professional-result-card-contact"))
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
                "practice_area", this.getPracticeArea(lawyer),
                "email", socials[0],
                "phone", socials[1].isEmpty() ? "" : socials[1]
        );
    }
}