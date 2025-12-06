package org.example.src.sites.to_test;

import org.example.exceptions.LawyerExceptions;
import org.example.src.entities.BaseSites.ByPage;
import org.example.src.entities.MyDriver;
import org.example.src.utils.TreatLawyerParams;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.time.Duration;
import java.util.List;
import java.util.Map;

import static java.util.Map.entry;

public class McDermottWillAndEmery extends ByPage {
    public static final Map<String, String> OFFICE_TO_COUNTRY = Map.ofEntries(
            entry("düsseldorf", "Germany"),
            entry("munich", "Germany"),
            entry("frankfurt", "Germany"),
            entry("london", "England"),
            entry("paris", "France"),
            entry("milan", "Italy"),
            entry("brussels", "Belgium")
    );

    private final By[] byRoleArray = {
            By.className("location")
    };

    public McDermottWillAndEmery() {
        super(
                "McDermott Will And Emery",
                "https://www.mwe.com/people/?law=&loc=1153,1156,1157,811,1161,1162,332&tit=140,49762,176,54845,151,174,166,168,57293,15708,57299,51716,51685,57279,57291,49807,55642,163",
                1
        );
    }

    protected void accessPage(int index) {
        this.driver.get(this.link);
        MyDriver.waitForPageToLoad();

        MyDriver.clickOnElementMultipleTimes(
                // more than 10
                By.className("alm-load-more-btn"), 10, 1.5
        );
    }


    protected List<WebElement> getLawyersInPage() {
        String[] validRoles = {"partner", "director", "counsel", "chairman", "advisor"};

        try {
            WebDriverWait wait = new WebDriverWait(this.driver, Duration.ofSeconds(10L));
            List<WebElement> lawyers = wait.until(
                    ExpectedConditions.presenceOfAllElementsLocatedBy(
                            By.className("section-people-search-element")
                    )
            );
            return this.siteUtl.filterLawyersInPage(lawyers, byRoleArray, true, validRoles);
        } catch (Exception e) {
            throw new RuntimeException("Failed to find lawyer elements", e);
        }
    }

    private String getLink(WebElement lawyer) throws LawyerExceptions {
        By[] byArray = {By.cssSelector("a[href*='/people/']")};
        return extractor.extractLawyerAttribute(lawyer, byArray, "LINK", "href", LawyerExceptions::linkException);
    }

    private String getName(WebElement lawyer) throws LawyerExceptions {
        By[] byArray = {By.className("profile-title")};
        String name = extractor.extractLawyerText(lawyer, byArray, "NAME", LawyerExceptions::nameException);
        // Clean prefixes if strictly needed, though Lawyer entity handles standard ones.
        // Just standard extraction here.
        return name;
    }

    private String getRole(WebElement lawyer) throws LawyerExceptions {
        String text = extractor.extractLawyerText(lawyer, byRoleArray, "ROLE", LawyerExceptions::roleException);
        if (text.contains("|")) {
            return text.split("\\|")[0].trim();
        }
        return text;
    }

    private String getCountry(WebElement lawyer) {
        try {
            String location = lawyer.findElement(By.className("location")).getText();
            if (location.contains("|")) {
                location = location.split("\\|")[1].trim();
            }
            return siteUtl.getCountryBasedInOffice(OFFICE_TO_COUNTRY, location, "USA");
        } catch (Exception e) {
            return "USA";
        }
    }

    private String[] getSocials(WebElement lawyer, String name) {
        String email = "";
        String phone = "";
        try {
            phone = lawyer.findElement(By.className("phone-number")).getText();

            // Generate email: (firstNameLetter)(LastName)@mwe.com
            name = TreatLawyerParams.treatNameForEmail(name);
            String[] parts = name.split(" ");
            if (parts.length >= 2) {
                String firstLetter = parts[0].substring(0, 1);
                String lastName = parts[parts.length - 1];
                email = firstLetter + lastName + "@mwe.com";
            }
        } catch (Exception e) {
            // Ignore
        }
        return new String[]{email, phone};
    }

    public Object getLawyer(WebElement lawyer) throws Exception {
        String name = this.getName(lawyer);
        String[] socials = this.getSocials(lawyer, name);

        return Map.of(
                "link", this.getLink(lawyer),
                "name", name,
                "role", this.getRole(lawyer),
                "firm", this.name,
                "country", this.getCountry(lawyer),
                "practice_area", "",
                "email", socials[0],
                "phone", socials[1].isEmpty() ? "" : socials[1]
        );
    }
}
