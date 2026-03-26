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

public class BCLPLaw extends ByPage {
    public static final Map<String, String> OFFICE_TO_COUNTRY = Map.ofEntries(
            Map.entry("abu dhabi", "the UAE"),
            Map.entry("al khobar", "Saudi Arabia"),
            Map.entry("berlin", "Germany"),
            Map.entry("brussels", "Belgium"),
            Map.entry("dubai", "the UAE"),
            Map.entry("frankfurt", "Germany"),
            Map.entry("hamburg", "Germany"),
            Map.entry("london", "England"),
            Map.entry("manchester", "England"),
            Map.entry("paris", "France"),
            Map.entry("riyadh", "Saudi Arabia"),
            Map.entry("southampton", "England"),
            Map.entry("sydney", "Australia"),
            Map.entry("tel aviv", "Israel")
    );

    private final By[] byRoleArray = {
            By.className("styles__title--c74ab494")
    };

    public BCLPLaw() {
        super(
                "BCLP Law",
                "https://www.bclplaw.com/en-US/people/index.html",
                66,
                3
        );
    }

    protected void accessPage(int index) {
        String otherUrl = "https://www.bclplaw.com/en-US/people/index.html?f=" + (index * 12);
        String url = index == 0 ? this.link : otherUrl;
        this.driver.get(url);
        MyDriver.waitForPageToLoad();
    }

    protected List<WebElement> getLawyersInPage() {
        String[] validRoles = {"partner", "counsel", "senior associate"};

        try {
            WebDriverWait wait = new WebDriverWait(this.driver, Duration.ofSeconds(10L));
            List<WebElement> lawyers = wait.until(
                    ExpectedConditions.presenceOfAllElementsLocatedBy(
                            By.className("styles__cardContainer--fa480865")
                    )
            );
            return this.siteUtl.filterLawyersInPage(lawyers, byRoleArray, true, validRoles);
        } catch (Exception e) {
            throw new RuntimeException("Failed to find lawyer elements", e);
        }
    }

    private String getLink(WebElement lawyer) throws LawyerExceptions {
        By[] byArray = {By.cssSelector("a")};
        return extractor.extractLawyerAttribute(lawyer, byArray, "LINK", "href", LawyerExceptions::linkException);
    }

    private String getName(WebElement lawyer) throws LawyerExceptions {
        By[] byArray = {By.cssSelector("a")};
        String name1 = extractor.extractLawyerAttribute(lawyer, byArray, "NAME", "aria-label", LawyerExceptions::nameException);
        return name1.split("for")[1];
    }

    private String getRole(WebElement lawyer) throws LawyerExceptions {
        String fullTitle = extractor.extractLawyerText(lawyer, byRoleArray, "ROLE", LawyerExceptions::roleException);
        return fullTitle.split(",")[0].trim();
    }

    private String getCountry(WebElement lawyer) throws LawyerExceptions {
        By[] byArray = {By.className("styles__office--efd126ad")};
        String country = extractor.extractLawyerText(lawyer, byArray, "COUNTRY", LawyerExceptions::countryException);
        return siteUtl.getCountryBasedInOffice(OFFICE_TO_COUNTRY, country, "USA");
    }

    private String[] getSocials(WebElement lawyer) {
        String email = "";
        String phone = "";
        try {
            email = lawyer.findElement(By.className("styles__show--f3b4968c")).getText();
            phone = lawyer.findElement(By.className("gtm-attorney-tile-phone")).getText();
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
                "phone", socials[1].isEmpty() ? "" : socials[1]
        );
    }
}