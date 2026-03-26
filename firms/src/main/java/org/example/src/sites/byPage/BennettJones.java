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

public class BennettJones extends ByPage {

    private static final Map<String, String> OFFICE_TO_COUNTRY = Map.ofEntries(
            entry("new york", "USA")
    );

    private final By[] byRoleArray = {
            By.cssSelector("div.mb-2")
    };

    public BennettJones() {
        super(
                "Bennet Jones",
                "https://www.bennettjones.com/People/Search/Lawyer-Agents?values=6D1E74C06EE942DFA3B14DD69F7E9BF6%2C33C9C86CCC894A63805ED13E9A754C13%2CEB9EDDD45CFF4E3F93FC710B27A84815",
                32
        );
    }

    @Override
    protected void accessPage(int index) throws InterruptedException {
        String otherUrl = "https://www.bennettjones.com/People/Search/Lawyer-Agents?values=6D1E74C06EE942DFA3B14DD69F7E9BF6%2C33C9C86CCC894A63805ED13E9A754C13%2CEB9EDDD45CFF4E3F93FC710B27A84815&page=" + (index + 1);
        String url = index == 0 ? this.link : otherUrl;
        this.driver.get(url);
        MyDriver.waitForPageToLoad();
        Thread.sleep(1000L);
        MyDriver.rollDown(1, 0.5);
    }

    @Override
    protected List<WebElement> getLawyersInPage() {
        String[] validRoles = new String[]{
                "partner", "counsel", "advisor", "head", "principal"
        };

        try {
            WebDriverWait wait = new WebDriverWait(this.driver, Duration.ofSeconds(10L));
            List<WebElement> lawyers = wait.until(
                    ExpectedConditions.presenceOfAllElementsLocatedBy(
                            By.cssSelector("div.col-lg-4.mb-5")
                    )
            );
            return this.siteUtl.filterLawyersInPage(lawyers, byRoleArray, true, validRoles);
        } catch (Exception e) {
            throw new RuntimeException("Failed to find lawyer elements", e);
        }
    }

    private String getLink(WebElement lawyer) throws LawyerExceptions {
        By[] byArray = new By[]{
                By.cssSelector("h4 > a")
        };
        return extractor.extractLawyerAttribute(lawyer, byArray, "LINK", "href", LawyerExceptions::linkException);
    }

    private String getName(WebElement lawyer) throws LawyerExceptions {
        By[] byArray = new By[]{
                By.cssSelector("h4 > a")
        };
        return extractor.extractLawyerText(lawyer, byArray, "NAME", LawyerExceptions::nameException);
    }

    private String getRole(WebElement lawyer) throws LawyerExceptions {
        return extractor.extractLawyerText(lawyer, byRoleArray, "ROLE", LawyerExceptions::roleException);
    }

    private String getCountry(WebElement lawyer) throws LawyerExceptions {
        By[] byArray = new By[]{
                By.xpath("./div[4]")
        };
        WebElement element = siteUtl.iterateOverBy(byArray, lawyer);
        String office = extractor.extractLawyerText(lawyer, byArray, "COUNTRY", LawyerExceptions::countryException);
        return siteUtl.getCountryBasedInOffice(OFFICE_TO_COUNTRY, office, "Canada");
    }

    private String[] getSocials(WebElement lawyer) {
        try {
            List<WebElement> socials = lawyer.findElements(By.cssSelector("div:last-of-type > a"));
            return super.getSocials(socials, false);
        } catch (Exception e) {
            System.err.println("Error getting socials: " + e.getMessage());
            return new String[]{"", ""};
        }
    }

    @Override
    public Object getLawyer(WebElement lawyer) throws Exception {
        WebElement parentContainer = lawyer.findElement(By.xpath("./.."));
        String[] socials = this.getSocials(parentContainer);

        return Map.of(
                "link", this.getLink(parentContainer),
                "name", this.getName(parentContainer),
                "role", this.getRole(parentContainer),
                "firm", this.name,
                "country", this.getCountry(parentContainer),
                "practice_area", "",
                "email", socials[0],
                "phone", socials[1]
        );
    }
}