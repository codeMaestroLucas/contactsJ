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

public class LathamAndWatkins extends ByPage {
    public static final Map<String, String> OFFICE_TO_COUNTRY = Map.ofEntries(
            entry("beijing", "China"),
            entry("brussels", "Belgium"),
            entry("dubai", "the UAE"),
            entry("düsseldorf", "Germany"),
            entry("frankfurt", "Germany"),
            entry("hamburg", "Germany"),
            entry("hong kong", "Hong Kong"),
            entry("london", "England"),
            entry("madrid", "Spain"),
            entry("milan", "Italy"),
            entry("munich", "Germany"),
            entry("paris", "France"),
            entry("riyadh", "Saudi Arabia"),
            entry("seoul", "Korea (South)"),
            entry("singapore", "Singapore"),
            entry("tel aviv", "Israel"),
            entry("tokyo", "Japan")
    );


    private final By[] byRoleArray = {
            By.className("contacts__card-title"),
            By.cssSelector("span")
    };


    public LathamAndWatkins() {
        super(
                "Latham And Watkins",
                "https://www.lw.com/en/people#sort=%40peoplerankbytitle%20ascending%3B%40peoplelastname%20ascending&f:@peopleoffices=[Beijing,Brussels,Dubai,D%C3%BCsseldorf,Frankfurt,Hamburg,Hong%20Kong,London,Madrid,Milan,Munich,Orange%20County,Paris,Riyadh,Seoul,Singapore,Tel%20Aviv,Tokyo]",
                62,
                3
        );
    }


    protected void accessPage(int index) throws InterruptedException {
        String otherUrl = "https://www.lw.com/en/people#first=" + index * 20 + "&sort=%40peoplerankbytitle%20ascending%3B%40peoplelastname%20ascending&f:@peopleoffices=[Beijing,Brussels,Dubai,D%C3%BCsseldorf,Frankfurt,Hamburg,Hong%20Kong,London,Madrid,Milan,Munich,Orange%20County,Paris,Riyadh,Seoul,Singapore,Tel%20Aviv,Tokyo]";
        String url = index == 0 ? this.link : otherUrl;
        this.driver.get(url);
        MyDriver.waitForPageToLoad();
        Thread.sleep(1000L);

        if (index > 0) return;
        MyDriver.clickOnElement(By.id("onetrust-accept-btn-handler"));
    }


    protected List<WebElement> getLawyersInPage() {
        String[] validRoles = new String[]{
                "partner",
                "chair",
                "counsel"
        };

        try {
            WebDriverWait wait = new WebDriverWait(this.driver, Duration.ofSeconds(10L));
            return wait.until(
                    ExpectedConditions.presenceOfAllElementsLocatedBy(
                            By.className("contacts__card-content")
                    )
            );
        } catch (Exception e) {
            throw new RuntimeException("Failed to find lawyer elements", e);
        }
    }


    public String getLink(WebElement lawyer) throws LawyerExceptions {
        By[] byArray = new By[]{
                By.cssSelector("h3 > a")
        };
        return extractor.extractLawyerAttribute(lawyer, byArray, "LINK", "href", LawyerExceptions::linkException);
    }


    private String getName(WebElement lawyer) throws LawyerExceptions {
        By[] byArray = new By[]{
                By.cssSelector("h3  > a > span")
        };
        return extractor.extractLawyerAttribute(lawyer, byArray, "NAME", "textContent", LawyerExceptions::nameException);
    }


    private String getRole(WebElement lawyer) throws LawyerExceptions {
        return extractor.extractLawyerAttribute(lawyer, byRoleArray, "ROLE", "textContent", LawyerExceptions::roleException);
    }


    private String getCountry(WebElement lawyer) throws LawyerExceptions {
        By[] byArray = new By[]{
                By.className("contacts__card-detail"),
                By.cssSelector("span")
        };
        String office = extractor.extractLawyerAttribute(lawyer, byArray, "COUNTRY", "textContent", LawyerExceptions::countryException);
        return siteUtl.getCountryBasedInOffice(OFFICE_TO_COUNTRY, office, "USA");
    }


    private String[] getSocials(WebElement lawyer) {
        String email = "";
        String phone = "";
        try {
            email = lawyer.findElement(By.cssSelector("p[data-field='@peopleemailaddress'] > span")).getText();
            phone = lawyer.findElement(By.cssSelector("p[data-field='@peopledirectdialnumber'] > span")).getText();
        } catch (Exception ignored) {
        }
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