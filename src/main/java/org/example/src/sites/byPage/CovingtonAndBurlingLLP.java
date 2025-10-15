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

public class CovingtonAndBurlingLLP extends ByPage {
    public static final Map<String, String> OFFICE_TO_COUNTRY = Map.ofEntries(
            Map.entry("london", "England"),
            Map.entry("brussels", "Belgium"),
            Map.entry("shanghai", "China"),
            Map.entry("beijing", "China"),
            Map.entry("seoul", "Korea (South)"),
            Map.entry("johannesburg", "South Africa"),
            Map.entry("frankfurt", "Germany"),
            Map.entry("dubai", "the UAE")
    );

    private final By[] byRoleArray = {
            By.className("search-results-card__content__sub-heading")
    };

    public CovingtonAndBurlingLLP() {
        super(
                "Covington And Burling LLP",
                "https://www.cov.com/en/professionals#sort=%40titlesort%20descending&numberOfResults=60&f:Offices=[London,Brussels,Frankfurt,Dubai,Johannesburg,Shanghai,Seoul,Beijing]",
                5,
                2
        );
    }

    protected void accessPage(int index) {
        String otherUrl = "https://www.cov.com/en/professionals#first=" + (index * 60) + "&sort=%40titlesort%20ascending&numberOfResults=60&f:Offices=[London,Brussels,Beijing,Shanghai,Seoul,Johannesburg,Frankfurt,Dubai]";
        String url = index == 0 ? this.link : otherUrl;
        this.driver.get(url);
        MyDriver.waitForPageToLoad();
        MyDriver.clickOnAddBtn(By.id("onetrust-accept-btn-handler"));
    }

    protected List<WebElement> getLawyersInPage() {
        String[] validRoles = {"partner", "counsel", "advisor"};
        try {
            WebDriverWait wait = new WebDriverWait(this.driver, Duration.ofSeconds(10L));
            List<WebElement> lawyers = wait.until(
                    ExpectedConditions.presenceOfAllElementsLocatedBy(
                            By.className("search-results-card__content")
                    )
            );
            return this.siteUtl.filterLawyersInPage(lawyers, byRoleArray, true, validRoles);
        } catch (Exception e) {
            throw new RuntimeException("Failed to find lawyer elements", e);
        }
    }

    public String getLink(WebElement lawyer) throws LawyerExceptions {
        By[] byArray = {By.className("CoveoResultLink")};
        return extractor.extractLawyerAttribute(lawyer, byArray, "LINK", "href", LawyerExceptions::nameException);
    }

    private String getName(WebElement lawyer) throws LawyerExceptions {
        By[] byArray = {By.className("CoveoResultLink")};
        return extractor.extractLawyerText(lawyer, byArray, "NAME", LawyerExceptions::nameException);
    }

    private String getRole(WebElement lawyer) throws LawyerExceptions {
        By[] byArray = {By.className("search-results-card__content__sub-heading")};
        return extractor.extractLawyerText(lawyer, byArray, "ROLE", LawyerExceptions::nameException);
    }


    private String getCountry(WebElement lawyer) throws LawyerExceptions {
        By[] byArray = {
                By.className("unlisted"),
                By.cssSelector("li > span")
        };
        String country = extractor.extractLawyerText(lawyer, byArray, "COUNTRY", LawyerExceptions::countryException);
        return siteUtl.getCountryBasedInOffice(OFFICE_TO_COUNTRY, country, "USA");
    }

    private String[] getSocials(WebElement lawyer) {
        try {
            List<WebElement> socials = lawyer.findElements(By.cssSelector("ul.unlisted a"));
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
                "practice_area", "",
                "email", socials[1],
                "phone", socials[0]
        );
    }
}