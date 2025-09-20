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

public class ApplebyGlobal extends ByPage {
    public static final Map<String, String> OFFICE_TO_COUNTRY = Map.ofEntries(
            entry("bermuda", "Bermuda"),
            entry("bvi", "the British Virgin Islands"),
            entry("cayman islands", "the Cayman Islands"),
            entry("guernsey", "Guernsey"),
            entry("hong kong", "Hong Kong"),
            entry("isle of man", "Isle of Man"),
            entry("jersey", "Jersey"),
            entry("mauritius", "Mauritius"),
            entry("seychelles", "Seychelles"),
            entry("shanghai", "China")
    );

    public ApplebyGlobal() {
        super(
                "Appleby Global",
                "https://www.applebyglobal.com/people/page/1/",
                9,
                3
        );
    }

    By[] byRoleArray = new By[]{
            By.cssSelector("div")
    };


    protected void accessPage(int index) throws InterruptedException {
        String otherUrl = "https://www.applebyglobal.com/people/page/" + (index + 1) + "/";
        String url = index == 0 ? this.link : otherUrl;
        this.driver.get(url);
        MyDriver.waitForPageToLoad();
        Thread.sleep(1000L);
        if (index == 0) MyDriver.clickOnAddBtn(By.id("onetrust-accept-btn-handler"));
    }

    protected List<WebElement> getLawyersInPage() {
        String[] validRoles = new String[]{"partner", "counsel", "senior associate"};

        try {
            WebDriverWait wait = new WebDriverWait(this.driver, Duration.ofSeconds(10L));
            WebElement div = wait.until(ExpectedConditions.presenceOfElementLocated(By.id("ajax-container")));

            List<WebElement> lawyers =  div.findElements(By.className("grid-item__content"));

            return this.siteUtl.filterLawyersInPage(lawyers, byRoleArray, true, validRoles);
        } catch (Exception e) {
            throw new RuntimeException("Failed to find lawyer elements", e);
        }
    }

    public String getLink(WebElement lawyer) throws LawyerExceptions {
        By[] byArray = new By[]{
                By.cssSelector("a[href*='https://www.applebyglobal.com/people/']")
        };
        return extractor.extractLawyerAttribute(lawyer, byArray, "LINK", "href", LawyerExceptions::linkException);
    }

    private String getName(WebElement lawyer) throws LawyerExceptions {
        By[] byArray = new By[]{
                By.cssSelector("a[href*='https://www.applebyglobal.com/people/']")
        };
        return extractor.extractLawyerAttribute(lawyer, byArray, "NAME", "textContent", LawyerExceptions::nameException);
    }

    private String getRole(WebElement lawyer) throws LawyerExceptions {
        return extractor.extractLawyerAttribute(lawyer, byRoleArray, "ROLE", "textContent", LawyerExceptions::roleException);
    }

    private String getCountry(WebElement lawyer) throws LawyerExceptions {
        By[] byArray = new By[]{
                By.cssSelector("a[href*='https://www.applebyglobal.com/locations/']")
        };
        String country = extractor.extractLawyerText(lawyer, byArray, "COUNTRY", LawyerExceptions::countryException);
        return siteUtl.getCountryBasedInOffice(OFFICE_TO_COUNTRY, country, country);
    }

    private String[] getSocials(WebElement lawyer) {
        try {
            List<WebElement> socials = lawyer.findElements(By.cssSelector("p.u-font-size-14.u-line-height-22 > a"));
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