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

public class CliffordChance extends ByPage {
    public static final Map<String, String> OFFICE_TO_COUNTRY = Map.ofEntries(
            entry("abu dhabi", "the UAE"),
            entry("amsterdam", "the Netherlands"),
            entry("barcelona", "Spain"),
            entry("beijing", "China"),
            entry("brussels", "Belgium"),
            entry("bucharest", "Romania"),
            entry("casablanca", "Morocco"),
            entry("cc worldwide ltd", "England"),
            entry("dubai", "the UAE"),
            entry("dusseldorf", "Germany"),
            entry("frankfurt", "Germany"),
            entry("hong kong", "Hong Kong"),
            entry("houston", "USA"),
            entry("istanbul", "Turkey"),
            entry("london", "England"),
            entry("luxembourg", "Luxembourg"),
            entry("madrid", "Spain"),
            entry("milan", "Italy"),
            entry("munich", "Germany"),
            entry("new york", "USA"),
            entry("newcastle", "England"),
            entry("paris", "France"),
            entry("perth", "Australia"),
            entry("prague", "the Czech Republic"),
            entry("riyadh", "Saudi Arabia"),
            entry("rome", "Italy"),
            entry("sao paulo", "Brazil"),
            entry("shanghai", "China"),
            entry("singapore", "Singapore"),
            entry("sydney", "Australia"),
            entry("tokyo", "Japan"),
            entry("warsaw", "Poland"),
            entry("washington", "USA")
    );

    private final By[] byRoleArray = {
            By.className("detail"),
    };


    public CliffordChance() {
        super(
            "Clifford Chance",
            "https://www.cliffordchance.com/people_and_places.html?_charset_=UTF-8&pageitems=50&partnersview=true#partners",
            15,
            3
        );
    }


    protected void accessPage(int index) throws InterruptedException {
        String otherUrl = "https://www.cliffordchance.com/people_and_places.html?_charset_=UTF-8&pageitems=50&partnersview=true&page=" + (index + 1);
        String url = index == 0 ? this.link : otherUrl;
        this.driver.get(url);
        MyDriver.waitForPageToLoad();
        Thread.sleep(1000L);

        if (index > 0) return;

        MyDriver.clickOnAddBtn(By.id("onetrust-accept-btn-handler"));
    }


    protected List<WebElement> getLawyersInPage() {
        String[] validRoles = new String[]{ "partner", "counsel", "senior associate" };

        try {
            WebDriverWait wait = new WebDriverWait(this.driver, Duration.ofSeconds(10L));

            List<WebElement> lawyers = wait.until(
                    ExpectedConditions.presenceOfAllElementsLocatedBy(
                            By.cssSelector("div.tabContent > article.article_result")
                    )
            );
            return this.siteUtl.filterLawyersInPage(lawyers, byRoleArray, false, validRoles);

        } catch (Exception e) {
            throw new RuntimeException("Failed to find lawyer elements", e);
        }
    }


    private String getLink(WebElement lawyer) throws LawyerExceptions {
        By[] byArray = new By[]{
                By.cssSelector("a[href*='https://www.cliffordchance.com/people_and_places/people/']")
        };
        return extractor.extractLawyerAttribute(lawyer, byArray, "LINK", "href", LawyerExceptions::linkException).toLowerCase();
    }


    private String getName(WebElement container) throws LawyerExceptions {
        By[] byArray = new By[]{
                By.cssSelector("a[href*='https://www.cliffordchance.com/people_and_places/people/']")
        };
        return extractor.extractLawyerAttribute(container, byArray, "NAME", "title", LawyerExceptions::nameException);
    }


    private String getRole(WebElement container) throws LawyerExceptions {
        return extractor.extractLawyerAttribute(container, byRoleArray, "ROLE", "innerText", LawyerExceptions::roleException);
    }


    private String getCountry(WebElement container) throws LawyerExceptions {
        By[] byArray = new By[]{
                By.cssSelector("p.p_contact_link + p > a")
        };
        String country = extractor.extractLawyerAttribute(container, byArray, "COUNTRY", "title", LawyerExceptions::countryException)
                .replace("Clifford Chance, ", "").trim();
        return siteUtl.getCountryBasedInOffice(OFFICE_TO_COUNTRY, country, country);
    }


    private String[] getSocials(WebElement lawyer) {
        String email = ""; String phone = "";

        email = lawyer.findElement(By.cssSelector("p > a[href*='mailto:']")).getAttribute("href");
        phone = lawyer.findElement(By.xpath("//*[@id=\"peopledirectory1\"]/article[1]/p[4]")).getAttribute("innerText");

        return new String[] { email, phone };
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