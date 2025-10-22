package org.example.src.sites._standingBy.otherIssues;

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

public class DWF extends ByPage {
    public static final Map<String, String> OFFICE_TO_COUNTRY = Map.ofEntries(
            Map.entry("australia", "Australia"),
            Map.entry("canada", "Canada"),
            Map.entry("france", "France"),
            Map.entry("germany", "Germany"),
            Map.entry("india", "India"),
            Map.entry("ireland", "Ireland"),
            Map.entry("italy", "Italy"),
            Map.entry("poland", "Poland"),
            Map.entry("qatar", "Qatar"),
            Map.entry("saudi arabia", "Saudi Arabia"),
            Map.entry("spain", "Spain"),
            Map.entry("united arab emirates", "the UAE"),
            Map.entry("united kingdom", "England"),
            Map.entry("united states", "USA")
    );

    private final By[] byRoleArray = {
            By.className("search-item__role")
    };

    public DWF() {
        super(
                "DWF",
                "https://dwfgroup.com/en/people",
                100,
                3
        );
    }

    protected void accessPage(int index) throws InterruptedException {
        this.driver.get(this.link);
        MyDriver.waitForPageToLoad();

        MyDriver.clickOnAddBtn(By.xpath("//*[@id=\"CookieReportsBanner\"]/div[1]/div[2]/a[2]"));

        if (index > 0) return;

        //todo: click in next page correctly
        MyDriver.clickOnElement(By.cssSelector("span.search-pagination__pagenav--next"));
    }

    protected List<WebElement> getLawyersInPage() {
        String[] validRoles = {"partner", "director", "counsel", "principal",  "senior associate"};

        try {
            WebDriverWait wait = new WebDriverWait(this.driver, Duration.ofSeconds(10L));
            List<WebElement> lawyers = wait.until(
                    ExpectedConditions.presenceOfAllElementsLocatedBy(
                            By.className("search-list__item--person")
                    )
            );
            return this.siteUtl.filterLawyersInPage(lawyers, byRoleArray, false, validRoles);
        } catch (Exception e) {
            throw new RuntimeException("Failed to find lawyer elements", e);
        }
    }

    private String getLink(WebElement lawyer) throws LawyerExceptions {
        By[] byArray = {By.cssSelector("div.search-item__title > a")};
        return extractor.extractLawyerAttribute(lawyer, byArray, "LINK", "href", LawyerExceptions::linkException);
    }

    private String getName(WebElement lawyer) throws LawyerExceptions {
        By[] byArray = {By.cssSelector("div.search-item__title > a")};
        return extractor.extractLawyerAttribute(lawyer, byArray, "NAME", "textContent", LawyerExceptions::nameException);
    }

    private String getRole(WebElement lawyer) throws LawyerExceptions {
        return extractor.extractLawyerAttribute(lawyer, byRoleArray, "ROLE", "textContent", LawyerExceptions::roleException);
    }

    private String getCountry(WebElement lawyer) throws LawyerExceptions {
        By[] byArray = {By.className("search-item__country")};
        String country = extractor.extractLawyerAttribute(lawyer, byArray, "COUNTRY", "textContent", LawyerExceptions::countryException);
        return siteUtl.getCountryBasedInOffice(OFFICE_TO_COUNTRY, country, country);
    }

    private String[] getSocials(WebElement lawyer) {
        String email = "";
        String phone = "";
        try {
            email = lawyer.findElement(By.className("search-item__contact-desc-value--email")).getAttribute("href");

            List<WebElement> contacts = lawyer.findElements(By.className("search-item__contact-desc-text"));
            for (WebElement contact : contacts) {
                if (contact.getText().startsWith("T:")) {
                    phone = contact.findElement(By.className("search-item__contact-desc-value")).getText();
                    break;
                }
            }
        } catch (Exception e) {}
        return new String[]{email, phone};
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