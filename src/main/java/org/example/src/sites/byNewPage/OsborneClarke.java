package org.example.src.sites.byNewPage;

import org.example.exceptions.LawyerExceptions;
import org.example.src.entities.BaseSites.ByNewPage;
import org.example.src.entities.MyDriver;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.time.Duration;
import java.util.List;
import java.util.Map;

public class OsborneClarke extends ByNewPage {
    private final By[] byArray = { By.className("MO11__role") };

    public static final Map<String, String> OFFICE_TO_COUNTRY = Map.ofEntries(
            Map.entry("32", "Belgium"),
            Map.entry("86", "China"),
            Map.entry("33", "France"),
            Map.entry("49", "Germany"),
            Map.entry("91", "India"),
            Map.entry("39", "Italy"),
            Map.entry("48", "Poland"),
            Map.entry("65", "Singapore"),
            Map.entry("34", "Spain"),
            Map.entry("46", "Sweden"),
            Map.entry("31", "the Netherlands"),
            Map.entry("44", "England"),
            Map.entry("1", "USA")
    );

    public OsborneClarke() {
        super(
                "Osborne Clarke",
//                "https://www.osborneclarke.com/lawyers",
               "https://www.osborneclarke.com/lawyers?page=2",
                72,
                3
        );
    }

    @Override
    protected void accessPage(int index) throws InterruptedException {
        String otherUrl = "https://www.osborneclarke.com/lawyers?page=" + index;
        String url = index == 0 ? this.link : otherUrl;
        this.driver.get(url);
        MyDriver.waitForPageToLoad();
    }

    @Override
    protected List<WebElement> getLawyersInPage() {
        String[] validRoles = {"partner", "counsel", "senior associate"};
        try {
            WebDriverWait wait = new WebDriverWait(this.driver, Duration.ofSeconds(10L));
            List<WebElement> lawyers = wait.until(
                    ExpectedConditions.presenceOfAllElementsLocatedBy(
                            By.className("MO11")
                    )
            );
            return siteUtl.filterLawyersInPage(lawyers, byArray, true, validRoles);
        } catch (Exception e) {
            throw new RuntimeException("Failed to find lawyer elements", e);
        }
    }

    @Override
    public void openNewTab(WebElement lawyer) throws LawyerExceptions {
        By[] byArray = { By.className("MO11__title") };
        String link = extractor.extractLawyerAttribute(lawyer, byArray, "LINK", "href", LawyerExceptions::linkException);
        MyDriver.openNewTab(link);
    }

    private String getName(WebElement lawyer) throws LawyerExceptions {
        By[] byArray = { By.className("MO11__title") };
        return extractor.extractLawyerText(lawyer, byArray, "NAME", LawyerExceptions::nameException);
    }

    private String getRole(WebElement lawyer) throws LawyerExceptions {
        return extractor.extractLawyerText(lawyer, byArray, "ROLE", LawyerExceptions::roleException);
    }

    private String getCountry(String phone) throws LawyerExceptions {
        return this.siteUtl.getCountryBasedInOfficeByPhone(OFFICE_TO_COUNTRY, phone, "Not Found");
    }

    private String[] getSocials() {
        try {
            List<WebElement> socials = driver.findElements(By.cssSelector("a[href^='mailto:'], a[href^='tel:']"));
            return super.getSocials(socials, false);

        } catch (Exception e) {
            return new String[] { "", "" };
        }
    }

    @Override
    public Object getLawyer(WebElement lawyer) throws Exception {
        openNewTab(lawyer);

        String[] socials = this.getSocials();
        String LINK = this.driver.getCurrentUrl();
        assert LINK != null;

        MyDriver.closeCurrentTab();
        return Map.of(
                "link", LINK,
                "name", this.getName(lawyer),
                "role", this.getRole(lawyer),
                "firm", this.name,
                "country", this.getCountry(socials[1]),
                "practice_area", "",
                "email", socials[0],
                "phone", socials[1]
        );
    }
}