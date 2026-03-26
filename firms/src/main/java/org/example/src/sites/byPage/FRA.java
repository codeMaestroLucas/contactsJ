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

public class FRA extends ByPage {
    private final By[] byRoleArray = {
            By.cssSelector("div[fs-cmsfilter-field='position'].experts-listing-page")
    };

    public static final Map<String, String> OFFICE_TO_COUNTRY = Map.ofEntries(
            Map.entry("dubai", "the UAE"),
            Map.entry("london", "England"),
            Map.entry("paris", "France"),
            Map.entry("seoul", "Korea (South)"),
            Map.entry("zurich", "Switzerland")
    );

    public FRA() {
        super(
                "FRA",
                "https://www.forensicrisk.com/experts",
                4,
                2
        );
    }

    protected void accessPage(int index) {
        String otherUrl = this.link + "?db551e79_page=" + (index + 1);
        String url = index == 0 ? this.link : otherUrl;
        this.driver.get(url);
        MyDriver.waitForPageToLoad();
    }

    protected List<WebElement> getLawyersInPage() {
        String[] validRoles = {"partner", "director", "manager", "advisor", "senior associate"};

        try {
            WebDriverWait wait = new WebDriverWait(this.driver, Duration.ofSeconds(10L));
            List<WebElement> lawyers = wait.until(
                    ExpectedConditions.presenceOfAllElementsLocatedBy(
                            By.className("profile-card-alt")
                    )
            );
            return this.siteUtl.filterLawyersInPage(lawyers, byRoleArray, false, validRoles);
        } catch (Exception e) {
            throw new RuntimeException("Failed to find lawyer elements", e);
        }
    }

    private String getLink(WebElement lawyer) throws LawyerExceptions {
        By[] byArray = {By.className("profile-card-alt__image")};
        return extractor.extractLawyerAttribute(lawyer, byArray, "LINK", "href", LawyerExceptions::linkException);
    }

    private String getName(WebElement lawyer) throws LawyerExceptions {
        WebElement nameDiv = lawyer.findElement(By.cssSelector("a.profile-card-alt__content[href*='/experts/'] > div.profile-card__name-wrap"));
        try {
            String fName = nameDiv.findElement(By.cssSelector("h3[fs-cmssort-field='name']")).getAttribute("textContent");
            String lName = nameDiv.findElement(By.cssSelector("h3[fs-cmssort-field='lastname']")).getAttribute("textContent");
            return fName + " " + lName;
        } catch (Exception e) {
            throw LawyerExceptions.nameException("Could not extract name parts");
        }
    }

    private String getRole(WebElement lawyer) throws LawyerExceptions {
        String role = extractor.extractLawyerAttribute(lawyer, byRoleArray, "ROLE", "textArea", LawyerExceptions::roleException);
        if (role.toLowerCase().contains("manager")) role = "Managing Associate";
        return role;
    }

    private String getCountry(WebElement lawyer) throws LawyerExceptions {
        By[] byArray = {By.cssSelector("div[fs-cmsfilter-field='location']")};
        String country = extractor.extractLawyerAttribute(lawyer, byArray, "COUNTRY", "textArea", LawyerExceptions::countryException);
        return siteUtl.getCountryBasedInOffice(OFFICE_TO_COUNTRY, country, "USA");
    }

    private String getPracticeArea(WebElement lawyer) {
        try {
            By[] byArray = {By.cssSelector("div[fs-cmsfilter-field='expertise']")};
            return extractor.extractLawyerAttribute(lawyer, byArray, "PRACTICE AREA", "textArea", LawyerExceptions::practiceAreaException);
        } catch (Exception e) {
            return "";
        }
    }

    private String[] getSocials(WebElement lawyer) {
        try {
            List<WebElement> socials = lawyer.findElements(By.cssSelector("div.profile-card__social a"));
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
                "phone", socials[1]
        );
    }
}