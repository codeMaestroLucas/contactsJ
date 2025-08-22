package org.example.src.sites.byPage;

import org.example.src.entities.BaseSites.ByPage;
import org.example.src.entities.MyDriver;
import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.interactions.Actions;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.time.Duration;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import static java.util.Map.entry;

public class HerbertSmithFreehillsKramer extends ByPage {
    public static final Map<String, String> OFFICE_TO_COUNTRY = Map.ofEntries(
            entry("africa", "Africa"),
            entry("africa group", "Africa Group"),
            entry("americas", "Americas"),
            entry("asia", "Asia"),
            entry("australia", "Australia"),
            entry("bangkok", "Thailand"),
            entry("belfast", "England"),
            entry("brisbane", "Australia"),
            entry("brussels", "Belgium"),
            entry("central asia group", "Central Asia Group"),
            entry("dubai", "the UAE"),
            entry("europe", "Europe"),
            entry("germany", "Germany"),
            entry("hong kong", "Hong Kong"),
            entry("india group", "India Group"),
            entry("israel group", "Israel Group"),
            entry("jakarta", "Indonesia"),
            entry("johannesburg", "South Africa"),
            entry("kazakhstan group", "Kazakhstan Group"),
            entry("korea group", "Korea Group"),
            entry("latin america group", "Latin America Group"),
            entry("london", "England"),
            entry("london canary wharf", "England"),
            entry("luxembourg", "Luxembourg"),
            entry("madrid", "Spain"),
            entry("mainland china", "China"),
            entry("malaysia group", "Malaysia Group"),
            entry("melbourne", "Australia"),
            entry("middle east", "Middle East"),
            entry("milan", "Italy"),
            entry("new york", "USA"),
            entry("nordic group", "Nordic Group"),
            entry("paris", "France"),
            entry("perth", "Australia"),
            entry("riyadh", "Saudi Arabia"),
            entry("silicon valley", "USA"),
            entry("singapore", "Singapore"),
            entry("switzerland group", "Switzerland Group"),
            entry("sydney", "Australia"),
            entry("tokyo", "Japan"),
            entry("ukraine group", "Ukraine Group"),
            entry("vietnam group", "Vietnam Group"),
            entry("washington dc", "USA"),
            entry("us", "USA")
    );


    private final By[] byRoleArray = {
            By.className("desc")
    };


    public HerbertSmithFreehillsKramer() {
        super(
            "Herbert Smith Freehills Kramer",
            "https://www.hsfkramer.com/en_US/our-people",
            1, // Couldfare protection (26pages)
            1
        );
    }


    protected void accessPage(int index) throws InterruptedException {
        String otherUrl = "https://www.hsfkramer.com/en_US/our-people?page=" + (index + 1);
        String url = index == 0 ? this.link : otherUrl;
        this.driver.get(url);
        MyDriver.waitForPageToLoad();
        Thread.sleep(1000L);

        if (index > 0) return;

        // Click on add btn
        MyDriver.clickOnElement(By.id("ccc-recommended-settings"));
    }


    protected List<WebElement> getLawyersInPage() {
        String[] validRoles = new String[]{
                "partner",
                "counsel",
                "advisor",
                "director",
                "senior associate"
        };

        try {
            WebDriverWait wait = new WebDriverWait(this.driver, Duration.ofSeconds(10L));

            List<WebElement> lawyers = wait.until(
                    ExpectedConditions.presenceOfAllElementsLocatedBy(
                            By.className("profile-info")
                    )
            );
            return this.siteUtl.filterLawyersInPage(lawyers, byRoleArray, true, validRoles);

        } catch (Exception e) {
            throw new RuntimeException("Failed to find lawyer elements", e);
        }
    }


    private String getLink(WebElement lawyer) {
        By[] byArray = new By[]{
                By.className("profile-link")
        };
        WebElement element = this.siteUtl.iterateOverBy(byArray, lawyer);
        return element.getAttribute("href");
    }


    private String getName(WebElement lawyer) {
        By[] byArray = new By[]{
                By.className("profile-link")
        };
        WebElement element = this.siteUtl.iterateOverBy(byArray, lawyer);
        return element.getText();
    }


    private String getRole(WebElement lawyer) {
        WebElement element = this.siteUtl.iterateOverBy(byRoleArray, lawyer);
        return element.getText();
    }


    private String getCountry(WebElement lawyer) {
        String text = lawyer.findElement(By.className("desc")).getText();

        String[] parts = text.split(",");
        if (parts.length < 2) {
            return ""; // no country info found
        }

        String[] split = parts[1]
                .replace("and", " ")
                .replaceAll("[,./]", "") // remove punctuation
                .trim()
                .split("\\s+"); // split by spaces

        for (String sp : split) {
            String country = OFFICE_TO_COUNTRY.get(sp.toLowerCase());
            if (country != null && !country.isEmpty()) {
                return country;
            }
        }

        return "";
    }


    private String[] getSocials(WebElement lawyer) {
        try {
            List<WebElement> socials = lawyer
                        .findElement(By.className("footer"))
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
            "phone", socials[1].isEmpty() ? "" : socials[1]
        );
    }
}
