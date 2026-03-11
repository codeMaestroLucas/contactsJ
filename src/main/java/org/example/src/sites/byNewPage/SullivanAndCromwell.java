package org.example.src.sites.byNewPage;

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

public class SullivanAndCromwell extends ByPage {
    public static final Map<String, String> OFFICE_TO_COUNTRY = Map.ofEntries(
            entry("beijing", "China"),
            entry("brussels", "Belgium"),
            entry("frankfurt", "Germany"),
            entry("hong kong", "China"),
            entry("london", "England"),
            entry("melbourne", "Australia"),
            entry("paris", "France"),
            entry("sydney", "Australia"),
            entry("tokyo", "Japan")
    );

    public SullivanAndCromwell() {
        super(
                "Sullivan & Cromwell",
                "https://www.sullcrom.com/LawyerListing",
                1,
                2
        );
    }

    @Override
    protected void accessPage(int index) throws InterruptedException {
        this.driver.get(this.link);
        MyDriver.waitForPageToLoad();
        Thread.sleep(1000L);
        MyDriver.clickOnElement(By.id("onetrust-accept-btn-handler"));
        MyDriver.clickOnElement(By.xpath("//*[@id=\"filters\"]/div/div[3]/div[2]/button[1]"));
        Thread.sleep(1000L);
        MyDriver.clickOnElement(By.xpath("//*[@id=\"resultsHeading\"]/div/div[2]/button"));
        Thread.sleep(1000L);
    }

    @Override
    protected List<WebElement> getLawyersInPage() {
        String[] validRoles = {"partner", "counsel", "senior associate"};
        By[] roleBy = {By.className("lawyer-name")};
        try {
            WebDriverWait wait = new WebDriverWait(this.driver, Duration.ofSeconds(10L));
            List<WebElement> lawyers = wait.until(ExpectedConditions.presenceOfAllElementsLocatedBy(By.className("BioListingCard_card__Mkk7U")));
            return this.siteUtl.filterLawyersInPage(lawyers, roleBy, true, validRoles);
        } catch (Exception e) {
            throw new RuntimeException("Failed to find lawyer elements", e);
        }
    }

    private String getLink(WebElement lawyer) throws LawyerExceptions {
        return extractor.extractLawyerAttribute(lawyer, new By[]{By.className("headshot")}, "LINK", "href", LawyerExceptions::linkException);
    }

    private String getName(WebElement lawyer) throws LawyerExceptions {
        return extractor.extractLawyerText(lawyer, new By[]{By.className("BioListingCard_heading__LDoOg")}, "NAME", LawyerExceptions::nameException);
    }

    private String getRole(WebElement lawyer) throws LawyerExceptions {
        // Role is usually plain text after the H3 inside the anchor
        return extractor.extractLawyerText(lawyer, new By[]{By.className("lawyer-name")}, "ROLE", LawyerExceptions::roleException).replace(this.getName(lawyer), "").trim();
    }

    private String getCountry(WebElement lawyer) throws LawyerExceptions {
        String country = extractor.extractLawyerText(lawyer, new By[]{By.className("lawyer-location")}, "COUNTRY", LawyerExceptions::countryException).split("\n")[0].trim();
        return siteUtl.getCountryBasedInOffice(OFFICE_TO_COUNTRY, country, "USA");
    }

    private String[] getSocials(WebElement lawyer) {
        try {
            String email = lawyer.findElement(By.cssSelector("a[data-sc-email]")).getAttribute("data-sc-email");
            String phone = lawyer.findElement(By.className("lawyer-location")).getText().split("\n")[1].trim();
            return new String[]{email, phone};
        } catch (Exception e) {
            return new String[]{"", ""};
        }
    }

    @Override
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
