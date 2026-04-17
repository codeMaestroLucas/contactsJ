package org.example.src.sites.mundial;

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

public class SquirePattonBoggs extends ByPage {
    private final By[] byRoleArray = {
            By.className("person-card__position")
    };

    public static final Map<String, String> OFFICE_TO_COUNTRY = Map.ofEntries(
            entry("abu dhabi", "the UAE"),
            entry("amsterdam", "the Netherlands"),
            entry("astana", "Kazakhstan"),
            entry("baku", "Azerbaijan"),
            entry("beijing", "China"),
            entry("beirut", "Lebanon"),
            entry("berlin", "Germany"),
            entry("birmingham", "England"),
            entry("boeblingen", "Germany"),
            entry("bratislava", "Slovakia"),
            entry("brussels", "Belgium"),
            entry("dubai", "the UAE"),
            entry("dublin", "Ireland"),
            entry("frankfurt", "Germany"),
            entry("geneva", "Switzerland"),
            entry("hong kong", "China"),
            entry("leeds", "England"),
            entry("london", "England"),
            entry("madrid", "Spain"),
            entry("manchester", "England"),
            entry("milan", "Italy"),
            entry("paris", "France"),
            entry("perth", "Australia"),
            entry("prague", "the Czech Republic"),
            entry("riyadh", "Saudi Arabia"),
            entry("santo domingo", "the Dominican Republic"),
            entry("shanghai", "China"),
            entry("singapore", "Singapore"),
            entry("sydney", "Australia"),
            entry("tokyo", "Japan"),
            entry("warsaw", "Poland")
    );

    public SquirePattonBoggs() {
        super(
                "Squire Patton Boggs",
                "https://www.squirepattonboggs.com/our-people/?PageNumber=1",
                1,
                3
        );
    }

    protected void accessPage(int index) {
        this.driver.get(this.link);
        MyDriver.waitForPageToLoad();
        // More than 40
        MyDriver.clickOnElementMultipleTimes(By.xpath("//*[@id=\"content-list\"]/div/div[2]/button"), 5, 1);
    }

    protected List<WebElement> getLawyersInPage() {
        try {
            WebDriverWait wait = new WebDriverWait(this.driver, Duration.ofSeconds(10L));
            List<WebElement> lawyers = wait.until(
                    ExpectedConditions.presenceOfAllElementsLocatedBy(
                            By.className("c-person-card")
                    )
            );
            return this.siteUtl.filterLawyersInPage(lawyers, byRoleArray, true);
        } catch (Exception e) {
            throw new RuntimeException("Failed to find lawyer elements", e);
        }
    }

    private String getLink(WebElement lawyer) throws LawyerExceptions {
        By[] byArray = {By.cssSelector("a[href*='/our-people/']")};
        return extractor.extractLawyerAttribute(lawyer, byArray, "LINK", "href", LawyerExceptions::linkException);
    }

    private String getName(WebElement lawyer) throws LawyerExceptions {
        By[] byArray = {By.className("h4")};
        return extractor.extractLawyerText(lawyer, byArray, "NAME", LawyerExceptions::nameException);
    }

    private String getRole(WebElement lawyer) throws LawyerExceptions {
        return extractor.extractLawyerText(lawyer, byRoleArray, "ROLE", LawyerExceptions::roleException);
    }

    private String getCountry(WebElement lawyer) {
        String country = lawyer.findElement(By.className("person-card__location")).getText().split(" \\| ")[0];
        return this.siteUtl.getCountryBasedInOffice(OFFICE_TO_COUNTRY, country, "USA");
    }

    private String[] getSocials(WebElement lawyer) {
        String[] ps = super.getSocials(lawyer.findElements(By.tagName("p")), true);
        String[] socials = super.getSocialsFromText(lawyer.getText());
        return new String[]{socials[0], ps[1]};
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