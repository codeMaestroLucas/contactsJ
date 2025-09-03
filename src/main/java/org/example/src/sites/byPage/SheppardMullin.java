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

// All the lawyers in one page
public class SheppardMullin extends ByPage {
    public static final Map<String, String> OFFICE_TO_COUNTRY = Map.ofEntries(
            entry("brussels", "Belgium"),
            entry("london", "England"),
            entry("seoul", "Korea (South)"),
            entry("shanghai", "China")
    );


    private final By[] byRoleArray = {
            By.className("attyListName"),
            By.className("title"),
            By.cssSelector("span")
    };


    public SheppardMullin() {
        super(
            "Sheppard Mullin",
            "https://www.sheppardmullin.com/people-directory",
            1,
            2
        );
    }


    protected void accessPage(int index) throws InterruptedException {
        this.driver.get(this.link);
        MyDriver.waitForPageToLoad();
        Thread.sleep(1000L);

        // Click on add btn
        MyDriver.clickOnElement(By.id("cookieClose"));
    }


    protected List<WebElement> getLawyersInPage() {
        String[] validRoles = new String[]{
                "partner",
                "chair",
                "director",
                "advisor",
                "counsel"
        };

        try {
            WebDriverWait wait = new WebDriverWait(this.driver, Duration.ofSeconds(10L));

            List<WebElement> lawyers = wait.until(
                    ExpectedConditions.presenceOfAllElementsLocatedBy(
                            By.className("bioItem")
                    )
            );
            return this.siteUtl.filterLawyersInPage(lawyers, byRoleArray, true, validRoles);

        } catch (Exception e) {
            throw new RuntimeException("Failed to find lawyer elements", e);
        }
    }


    private String getLink(WebElement lawyer) throws LawyerExceptions {
        By[] byArray = new By[]{
                By.className("attyListName"),
                By.className("title"),
                By.cssSelector("a")

        };
        return extractor.extractLawyerAttribute(lawyer, byArray, "LINK", "href", LawyerExceptions::linkException);
    }


    private String getName(WebElement container) throws LawyerExceptions {
        By[] byArray = new By[]{
                By.className("attyListName"),
                By.className("title"),
                By.cssSelector("a")
        };
        return extractor.extractLawyerText(container, byArray, "NAME", LawyerExceptions::nameException);
    }


    private String getRole(WebElement container) throws LawyerExceptions {
        return extractor.extractLawyerText(container, byRoleArray, "ROLE", LawyerExceptions::roleException);
    }


    private String getCountry(WebElement container) throws LawyerExceptions {
        String country = "USA";

        By[] byArray = new By[]{
                By.className("office"),
                By.cssSelector("a")
        };
        // Some lawyers don't have the country opt - retired or invalid
        try {
            country = extractor.extractLawyerText(container, byArray, "COUNTRY", LawyerExceptions::countryException);
        } catch (LawyerExceptions _) {}
        return siteUtl.getCountryBasedInOffice(OFFICE_TO_COUNTRY, country, "USA");
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


    public Object getLawyer(WebElement lawyer) throws Exception {
        String country = this.getCountry(lawyer);
        if (country.equals("USA")) return "Invalid Role";


        String[] socials = this.getSocials(lawyer);

        return Map.of(
                "link", this.getLink(lawyer),
                "name", this.getName(lawyer),
                "role", this.getRole(lawyer),
                "firm", this.name,
                "country", country,
                "practice_area", "",
                "email", socials[0],
                "phone", socials[1].isEmpty() ? "" : socials[1]
        );
    }
}