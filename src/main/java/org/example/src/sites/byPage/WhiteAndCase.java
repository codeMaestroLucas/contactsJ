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

public class WhiteAndCase extends ByPage {
    public static final Map<String, String> OFFICE_TO_COUNTRY = Map.<String, String>ofEntries(
            entry("abu dhabi", "the UAE"),
            entry("astana", "Kazakhstan"),
            entry("australia", "Australia"),
            entry("beijing", "China"),
            entry("berlin", "Germany"),
            entry("boston", "USA"),
            entry("brussels", "Belgium"),
            entry("cairo", "Egypt"),
            entry("canada", "Canada"),
            entry("chicago", "USA"),
            entry("china", "China"),
            entry("doha", "Qatar"),
            entry("dubai", "the UAE"),
            entry("düsseldorf", "Germany"),
            entry("dusseldorf", "Germany"),
            entry("d sseldorf", "Germany"),
            entry("frankfurt", "Germany"),
            entry("geneva", "Switzerland"),
            entry("geneva (white & case s.a.)", "Switzerland"),
            entry("germany", "Germany"),
            entry("hamburg", "Germany"),
            entry("helsinki", "Finland"),
            entry("hong kong sar", "Hong Kong"),
            entry("houston", "USA"),
            entry("india", "India"),
            entry("israel", "Israel"),
            entry("istanbul", "Turkey"),
            entry("johannesburg", "South Africa"),
            entry("london", "England"),
            entry("los angeles", "USA"),
            entry("luxembourg", "Luxembourg"),
            entry("madrid", "Spain"),
            entry("manila global operations center", "the Philippines"),
            entry("melbourne", "Australia"),
            entry("mexico city", "Mexico"),
            entry("miami", "USA"),
            entry("milan", "Italy"),
            entry("muscat", "Oman"),
            entry("new york", "USA"),
            entry("paris", "France"),
            entry("prague", "the Czech Republic"),
            entry("riyadh", "Saudi Arabia"),
            entry("são paulo", "Brazil"),
            entry("seoul", "Korea (South)"),
            entry("shanghai", "China"),
            entry("silicon valley", "USA"),
            entry("singapore", "Singapore"),
            entry("stockholm", "Sweden"),
            entry("sydney", "Australia"),
            entry("switzerland", "Switzerland"),
            entry("taiwan", "Taiwan"),
            entry("tampa global operations center", "USA"),
            entry("tashkent", "Uzbekistan"),
            entry("thailand", "Thailand"),
            entry("tokyo", "Japan"),
            entry("united arab emirates", "the UAE"),
            entry("united states", "USA"),
            entry("warsaw", "Poland"),
            entry("washington dc", "USA")
    );


    private final String[] links = {
            "",
            "https://www.whitecase.com/people/all/association_partner/all/all/all/all/search_api_relevance/DESC",
            "https://www.whitecase.com/people/all/local_partner/all/all/all/all/search_api_relevance/DESC",
            "https://www.whitecase.com/people/all/counsel/all/all/all/all/search_api_relevance/DESC",
            "https://www.whitecase.com/people/all/advisors_analysts/all/all/all/all/search_api_relevance/DESC"
    };

    private final By[] byRoleArray = {
            By.className("lawyer-role-offices")
    };


    public WhiteAndCase() {
        super(
            "White And Case",
            "https://www.whitecase.com/people/all/partner/all/all/all/all/search_api_relevance/DESC",
            5,
            3
        );
    }


    protected void accessPage(int index) throws InterruptedException {
        String url = index == 0 ? this.link : this.links[index];
        this.driver.get(url);
        MyDriver.waitForPageToLoad();
        Thread.sleep(1000L);

        if (index == 0) this.siteUtl.clickOnAddBtn(By.id("onetrust-accept-btn-handler"));

        int resultsValue = Integer.parseInt(
                driver.findElement(By.className("view-header"))
                        .findElement(By.className("results-total"))
                        .getText()
                        .replace("results", "")
                        .trim()
        );

        MyDriver.rollDown((resultsValue / 20) + 1, 4);
    }


    protected List<WebElement> getLawyersInPage() {
        try {
            WebDriverWait wait = new WebDriverWait(this.driver, Duration.ofSeconds(10L));

            return wait.until(
                    ExpectedConditions.presenceOfAllElementsLocatedBy(
                            By.className("bio-body")
                    )
            );

        } catch (Exception e) {
            throw new RuntimeException("Failed to find lawyer elements", e);
        }
    }


    private String getLink(WebElement lawyer) {
        By[] byArray = new By[]{
                By.className("lawyer-name"),
                By.cssSelector("a")
        };
        WebElement element = this.siteUtl.iterateOverBy(byArray, lawyer);
        return element.getAttribute("href");
    }


    private String getName(WebElement lawyer) {
        By[] byArray = new By[]{
                By.className("lawyer-name"),
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
                By.className("lawyer-role-offices")
        };
        WebElement element = this.siteUtl.iterateOverBy(byArray, lawyer);
        String country = element.getText().split(",")[1];
        return siteUtl.getCountryBasedInOffice(OFFICE_TO_COUNTRY, country);
    }


    private String[] getSocials(WebElement lawyer) {
        try {
            List<WebElement> socials = lawyer
                        .findElement(By.className("lawyer-contact-info"))
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
            "phone", socials[1].replaceFirst("2", "").trim()
        );
    }
}
