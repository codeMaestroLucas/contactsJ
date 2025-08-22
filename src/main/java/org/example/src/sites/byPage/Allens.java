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

public class Allens extends ByPage {
    public static final Map<String, String> OFFICE_TO_COUNTRY = Map.ofEntries(
            entry("auckland", "New Zealand"),
            entry("australia", "Australia"),
            entry("brisbane", "Australia"),
            entry("canberra", "Australia"),
            entry("hanoi", "Vietnam"),
            entry("ho chi minh city", "Vietnam"),
            entry("hong kong", "Hong Kong"),
            entry("malaysia", "Malaysia"),
            entry("melbourne", "Australia"),
            entry("new zealand", "New Zealand"),
            entry("newcastle and the hunter region", "Australia"),
            entry("perth", "Australia"),
            entry("papua new guinea", "Australia"),
            entry("port moresby", "Papua New Guinea"),
            entry("singapore", "Singapore"),
            entry("sydney", "Australia")
    );


    private final By[] byRoleArray = {
            By.className("block-search-result__description"),
            By.cssSelector("p")
    };


    public Allens() {
        super(
            "Allens",
            "https://www.allens.com.au/search/people/?q=",
            25,
            2
        );
    }


    protected void accessPage(int index) throws InterruptedException {
        String otherUrl = "https://www.allens.com.au/search/people/?p=" + (index + 1);
        String url = index == 0 ? this.link : otherUrl;
        this.driver.get(url);
        MyDriver.waitForPageToLoad();
        Thread.sleep(1000L);
    }


    protected List<WebElement> getLawyersInPage() {
        String[] validRoles = new String[]{
                "partner",
                "chair",
                "counsel",
                "managing associate",
                "senior associate",
        };

        try {
            WebDriverWait wait = new WebDriverWait(this.driver, Duration.ofSeconds(10L));

            List<WebElement> lawyers = wait.until(
                    ExpectedConditions.presenceOfAllElementsLocatedBy(
                            By.cssSelector("div.search-results-page__content > div.block-search-result")
                    )
            );
            return this.siteUtl.filterLawyersInPage(lawyers, byRoleArray, true, validRoles);

        } catch (Exception e) {
            throw new RuntimeException("Failed to find lawyer elements", e);
        }
    }


    private String getLink(WebElement lawyer) {
        By[] byArray = new By[]{
                By.className("block-search-result__link")
        };
        WebElement element = this.siteUtl.iterateOverBy(byArray, lawyer);
        return element.getAttribute("href");
    }


    private String getName(WebElement lawyer) {
        By[] byArray = new By[]{
                By.className("block-search-result__link")
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
                By.className("location-contact__city")
        };
        return siteUtl.getCountryBasedInOffice(
            OFFICE_TO_COUNTRY, this.siteUtl.iterateOverBy(byArray, lawyer)
        );
    }


    private String getPracticeArea(WebElement lawyer) {
        try {
            By[] byArray = new By[]{
                    By.className("compact-link-list__item")
            };
            WebElement element = this.siteUtl.iterateOverBy(byArray, lawyer);
            return siteUtl.getContentFromTag(element.getAttribute("outerHTML"));
        } catch (Exception e) {
            return "";
        }
    }



    private String[] getSocials(WebElement lawyer) {
        try {
            List<WebElement> socials = lawyer
                        .findElement(By.className("location-contact__list"))
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
            "practice_area", this.getPracticeArea(lawyer),
            "email", socials[0],
            "phone", socials[1].isEmpty() ? "" : socials[1]
        );
    }
}
