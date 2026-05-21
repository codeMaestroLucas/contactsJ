package org.example.src.sites.mundial;

import org.example.exceptions.LawyerExceptions;
import org.example.src.entities.BaseSites.ByPage;
import org.example.src.entities.MyDriver;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import java.util.List;
import java.util.Map;

import static java.util.Map.entry;

public class GibsonDunnCrutcher extends ByPage {
    public static final Map<String, String> OFFICE_TO_COUNTRY = Map.ofEntries(
            entry("brussels", "Belgium"),
            entry("paris", "France"),
            entry("frankfurt", "Germany"),
            entry("munich", "Germany"),
            entry("madrid", "Spain"),
            entry("zurich", "Switzerland"),
            entry("london", "England"),
            entry("riyadh", "Saudi Arabia"),
            entry("abu dhabi", "the UAE"),
            entry("dubai", "the UAE"),
            entry("beijing", "China"),
            entry("hong kong", "Hong Kong"),
            entry("singapore", "Singapore")
    );

    public GibsonDunnCrutcher() {
        super(
                "Gibson Dunn & Crutcher",
                "https://www.gibsondunn.com/people/",
                1,
                3
        );
    }

    @Override
    protected void accessPage(int index) throws InterruptedException {
        this.driver.get(this.link);
        MyDriver.waitForPageToLoad();
        MyDriver.clickOnElementMultipleTimes(
                By.xpath("//*[@id=\"main-content\"]/div/div[6]/button"), 10, 1.4
        );
    }

    @Override
    protected List<WebElement> getLawyersInPage() {
        List<WebElement> lawyers = MyDriver.wait.findElements(By.cssSelector("div.people"));
        return this.siteUtl.filterLawyersInPage(lawyers, new By[]{By.className("title")}, true);
    }

    private String getCountry(WebElement lawyer) throws LawyerExceptions {
        String country = extractor.extractLawyerText(lawyer, new By[] {By.className("contact-details")}, "COUNTRY", LawyerExceptions::countryException);
        return siteUtl.getCountryBasedInOffice(OFFICE_TO_COUNTRY, country, "USA");
    }


    @Override
    protected Object getLawyer(WebElement lawyer) throws Exception {
        String[] socials = this.getSocials(lawyer.findElements(By.tagName("a")), false);

        return Map.of(
                "link", extractor.extractLawyerAttribute(lawyer, new By[]{By.tagName("a")}, "LINK", "href", LawyerExceptions::linkException),
                "name", extractor.extractLawyerText(lawyer, new By[]{By.className("name")}, "NAME", LawyerExceptions::nameException),
                "role", extractor.extractLawyerText(lawyer, new By[]{By.cssSelector("p.title span:first-child")}, "ROLE", LawyerExceptions::roleException),
                "firm", this.name,
                "country", getCountry(lawyer),
                "practice_area", extractor.extractLawyerText(lawyer, new By[]{By.cssSelector("p.title span a")}, "PRACTICE AREA", LawyerExceptions::practiceAreaException),
                "email", socials[0],
                "phone", socials[1].isEmpty() ? "" : socials[1]
        );
    }
}
