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

public class Ashurst extends ByPage {
    Map<String, String> OFFICE_TO_COUNTRY = Map.ofEntries(
            Map.entry("abu dhabi", "the UAE"),
            Map.entry("austin", "USA"),
            Map.entry("beijing", "China"),
            Map.entry("brussels", "Belgium"),
            Map.entry("dubai", "the UAE"),
            Map.entry("dublin", "Ireland"),
            Map.entry("frankfurt", "Germany"),
            Map.entry("glasgow", "England"),
            Map.entry("hong kong", "Hong Kong"),
            Map.entry("jakarta", "Indonesia"),
            Map.entry("jeddah", "Saudi Arabia"),
            Map.entry("london", "England"),
            Map.entry("los angeles", "USA"),
            Map.entry("luxembourg", "Luxembourg"),
            Map.entry("madrid", "Spain"),
            Map.entry("milan", "Italy"),
            Map.entry("munich", "Germany"),
            Map.entry("new york", "USA"),
            Map.entry("paris", "France"),
            Map.entry("port moresby", "Papua New Guinea"),
            Map.entry("riyadh", "the UAE"),
            Map.entry("seoul", "Korea (South)"),
            Map.entry("shanghai", "China"),
            Map.entry("singapore", "Singapore"),
            Map.entry("tokyo", "Japan")
    );
    public Ashurst() {
        super(
                "Ashurst",
                "https://www.ashurst.com/en/people/#e=0",
                131,
                3
        );
    }

    protected void accessPage(int index) throws InterruptedException {
        String otherUrl = "https://www.ashurst.com/en/people/#e=" + 10 * index;
        String url = index == 0 ? this.link : otherUrl;
        this.driver.get(url);
        MyDriver.waitForPageToLoad();
        Thread.sleep(1000L);
        if (index <= 0) {
            MyDriver.clickOnElement(By.xpath("//*[@id=\"termsfeed-com---nb\"]/div/div[3]/button[1]"));
        }
    }

    protected List<WebElement> getLawyersInPage() {
        By[] webRole = new By[]{
                By.className("people-info")
        };
        String[] validRoles = new String[]{"partner", "counsel", "senior associate"};

        try {
            WebDriverWait wait = new WebDriverWait(this.driver, Duration.ofSeconds(10L));
            List<WebElement> lawyers = wait.until(ExpectedConditions.presenceOfAllElementsLocatedBy(By.className("profile-card__info")));
            return this.siteUtl.filterLawyersInPage(lawyers, webRole, false, validRoles);
        } catch (Exception e) {
            throw new RuntimeException("Failed to find lawyer elements", e);
        }
    }

    public String getLink(WebElement lawyer) throws LawyerExceptions {
        By[] byArray = new By[]{
                By.className("people-info"),
                By.cssSelector("a")
        };
        return extractor.extractLawyerAttribute(lawyer, byArray, "LINK", "href", LawyerExceptions::linkException);
    }

    private String getName(WebElement lawyer) throws LawyerExceptions {
        By[] byArray = new By[]{
                By.className("people-info"),
                By.cssSelector("a")
        };
        return extractor.extractLawyerAttribute(lawyer, byArray, "NAME", "title", LawyerExceptions::nameException);
    }

    private String getRole(WebElement lawyer) throws LawyerExceptions {
        By[] byArray = { By.className("people-info") };

        String text = extractor.extractLawyerText(lawyer, byArray, "ROLE", LawyerExceptions::roleException);

        String[] split = text.split("\\n");

        if (split.length > 1) {
            return split[1].trim();
        }
        return split[0].trim();
    }

    private String getCountry(WebElement lawyer) throws LawyerExceptions {
        By[] byArray = new By[]{
                By.className("people-info"),
                By.className("profile-location")
        };
        String office = extractor.extractLawyerText(lawyer, byArray, "COUNTRY", LawyerExceptions::countryException);
        return siteUtl.getCountryBasedInOffice(OFFICE_TO_COUNTRY, office, "Australia");
    }

    private String[] getSocials(WebElement lawyer) {
        try {
            List<WebElement> socials = lawyer.findElement(By.className("profile-contact")).findElements(By.cssSelector("a"));
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