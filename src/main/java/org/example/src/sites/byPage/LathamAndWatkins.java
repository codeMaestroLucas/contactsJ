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

public class LathamAndWatkins extends ByPage {
    public static final Map<String, String> OFFICE_TO_COUNTRY = Map.ofEntries(
            entry("austin", "EUA"),
            entry("beijing", "China"),
            entry("boston", "EUA"),
            entry("brussels", "Belgium"),
            entry("century city", "EUA"),
            entry("chicago", "EUA"),
            entry("dubai", "the UAE"),
            entry("düsseldorf", "Germany"),
            entry("frankfurt", "Germany"),
            entry("hamburg", "Germany"),
            entry("hong kong", "Hong Kong"),
            entry("houston", "EUA"),
            entry("london", "England"),
            entry("los angeles", "EUA"),
            entry("los angeles gso", "EUA"),
            entry("madrid", "Spain"),
            entry("milan", "Italy"),
            entry("munich", "Germany"),
            entry("new york", "EUA"),
            entry("orange county", "EUA"),
            entry("paris", "France"),
            entry("riyadh", "Saudi Arabia"),
            entry("san diego", "EUA"),
            entry("san francisco", "EUA"),
            entry("seoul", "Korea (South)"),
            entry("silicon valley", "EUA"),
            entry("singapore", "Singapore"),
            entry("tel aviv", "Israel"),
            entry("tokyo", "Japan"),
            entry("washington, d.c.", "EUA")
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

        this.siteUtl.clickOnAddBtn(By.id("onetrust-accept-btn-handler"));
    }


    protected List<WebElement> getLawyersInPage() {
        String[] validRoles = new String[]{
                "partner",
                "chair",
                "counsel"
        };

        try {
            WebDriverWait wait = new WebDriverWait(this.driver, Duration.ofSeconds(10L));

            List<WebElement> lawyers = wait.until(
                    ExpectedConditions.presenceOfAllElementsLocatedBy(
                            By.className("contacts__card-content")
                    )
            );
            return this.siteUtl.filterLawyersInPage(lawyers, byRoleArray, true, validRoles);

        } catch (Exception e) {
            throw new RuntimeException("Failed to find lawyer elements", e);
        }
    }


    private String getLink(WebElement lawyer) {
        By[] byArray = new By[]{
                By.cssSelector("h3 > a")
        };
        WebElement element = this.siteUtl.iterateOverBy(byArray, lawyer);
        return element.getAttribute("href");
    }


    private String getName(WebElement lawyer) {
        By[] byArray = new By[]{
                By.cssSelector("h3  > a > span")
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
                By.className("contacts__card-detail"),
                By.cssSelector("span")
        };
        WebElement element = this.siteUtl.iterateOverBy(byArray, lawyer);
        String country = element.getText().trim().toLowerCase();
        return OFFICE_TO_COUNTRY.getOrDefault(country, country);
    }


    private String[] getSocials(WebElement lawyer) {
        try {
            String email = driver.findElement(By.cssSelector("p[data-field='@peopleemailaddress'] > span")).getText();
            String phone = driver.findElement(By.cssSelector("p[data-field='@peopledirectdialnumber'] > span")).getText();


            return new String[]{ email, phone };
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
            "phone", socials[1]);
    }
}
