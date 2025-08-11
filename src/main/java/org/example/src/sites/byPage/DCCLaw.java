package org.example.src.sites.byPage;

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

public class DCCLaw extends ByPage {
    public static final Map<String, String> OFFICE_TO_COUNTRY = Map.ofEntries(
            entry("auckland", "New Zealand"),
            entry("australia", "Australia"),
            entry("brisbane", "Australia"),
            entry("canberra", "Australia"),
            entry("hong kong", "Hong Kong"),
            entry("malaysia", "Malaysia"),
            entry("melbourne", "Australia"),
            entry("new zealand", "New Zealand"),
            entry("newcastle and the hunter region", "Australia"),
            entry("perth", "Australia"),
            entry("singapore", "Singapore"),
            entry("sydney", "Australia")
    );


    private final By[] byRoleArray = {
            By.className("PeopleCard_jobPositions__PRMlJ")
    };


    public DCCLaw() {
        super(
            "DCC Law",
            "https://dcc.com/people/",
            1,
            2
        );
    }


    protected void accessPage(int index) throws InterruptedException {
        this.driver.get(this.link);
        MyDriver.waitForPageToLoad();
        Thread.sleep(1000L);

        MyDriver.rollDown(19, 1.5);
    }


    protected List<WebElement> getLawyersInPage() {
        String[] validRoles = new String[]{
                "principal",
                "senior associate"
        };

        try {
            WebDriverWait wait = new WebDriverWait(this.driver, Duration.ofSeconds(10L));

            List<WebElement> lawyers = wait.until(
                    ExpectedConditions.presenceOfAllElementsLocatedBy(
                            By.className("PeopleCard_large__I5oRR")
                    )
            );
            return this.siteUtl.filterLawyersInPage(lawyers, byRoleArray, true, validRoles);

        } catch (Exception e) {
            throw new RuntimeException("Failed to find lawyer elements", e);
        }
    }


    private String getLink(WebElement lawyer) {
        By[] byArray = new By[]{
                By.className("PeopleCard_fullName__sXUQ5"),
                By.cssSelector("a")
        };
        WebElement element = this.siteUtl.iterateOverBy(byArray, lawyer);
        return element.getAttribute("href");
    }


    private String getName(WebElement lawyer) {
        By[] byArray = new By[]{
                By.className("PeopleCard_fullName__sXUQ5"),
                By.cssSelector("a")
        };
        WebElement element = this.siteUtl.iterateOverBy(byArray, lawyer);
        return element.getText();
    }


    private String getRole(WebElement lawyer) {
        WebElement element = this.siteUtl.iterateOverBy(byRoleArray, lawyer);
        return element.getText();
    }


    private String getCountry(WebElement lawyer) {
        By[] byArray = new By[]{
                By.className("location")
        };
        WebElement element = this.siteUtl.iterateOverBy(byArray, lawyer);
        String country = element.getText();

        if (country.toLowerCase().contains("australia")) {
            return "Australia";
        } else {
            return siteUtl.getCountryBasedInOffice(OFFICE_TO_COUNTRY, country);
        }
    }


    private String[] getSocials(WebElement lawyer) {
        try {
            List<WebElement> socials = lawyer
                        .findElement(By.className("PeopleCard_actions__0szdq"))
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
            "practice_area", "",
            "email", socials[0],
            "phone", socials[1]
        );
    }
}
