package org.example.src.sites.byPage;

import org.example.src.entities.BaseSites.ByPage;
import org.example.src.entities.MyDriver;
import org.example.src.sites.byNewPage.BarneaAndCo;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.time.Duration;
import java.util.List;
import java.util.Map;

import static java.util.Map.copyOf;
import static java.util.Map.entry;

public class WatsonFarleyAndWilliams extends ByPage {
    public static final Map<String, String> OFFICE_TO_COUNTRY = Map.ofEntries(
            entry("athens", "Greece"),
            entry("australia", "Australia"),
            entry("bangkok", "Thailand"),
            entry("china", "China"),
            entry("dubai", "the UAE"),
            entry("dusseldorf", "Germany"),
            entry("d sseldorf", "Germany"),
            entry("france", "France"),
            entry("frankfurt", "Germany"),
            entry("germany", "Germany"),
            entry("greece", "Greece"),
            entry("hamburg", "Germany"),
            entry("hanoi", "Vietnam"),
            entry("hong kong", "Hong Kong"),
            entry("italy", "Italy"),
            entry("japan", "Japan"),
            entry("london", "England"),
            entry("madrid", "Spain"),
            entry("milan", "Italy"),
            entry("munich", "Germany"),
            entry("new york", "USA"),
            entry("paris", "France"),
            entry("republic of korea", "Korea (South)"),
            entry("rome", "Italy"),
            entry("seoul", "Korea (South)"),
            entry("singapore", "Singapore"),
            entry("spain", "Spain"),
            entry("sydney", "Australia"),
            entry("thailand", "Thailand"),
            entry("tokyo", "Japan"),
            entry("uae", "the UAE"),
            entry("united kingdom", "England"),
            entry("usa", "USA"),
            entry("vietnam", "Vietnam")
    );


    private final By[] byRoleArray = {
            By.className("people_left_stats_job")
    };


    public WatsonFarleyAndWilliams() {
        super(
            "Watson Farley And Williams",
            "https://www.wfw.com/people/?reset=yes",
            37,
            3
        );
    }


    protected void accessPage(int index) throws InterruptedException {
        String otherUrl = "https://www.wfw.com/people/?sf_paged=" + (index + 1);
        String url = index == 0 ? this.link : otherUrl;
        this.driver.get(url);
        MyDriver.waitForPageToLoad();
        Thread.sleep(1000L);

        if (index > 0) return;

        MyDriver.clickOnElement(By.id("CybotCookiebotDialogBodyLevelButtonLevelOptinAllowAll"));
    }


    protected List<WebElement> getLawyersInPage() {
        String[] validRoles = new String[]{
                "partner",
                "counsel",
                "senior associate",
                "managing associate",
        };

        try {
            WebDriverWait wait = new WebDriverWait(this.driver, Duration.ofSeconds(10L));

            List<WebElement> lawyers = wait.until(
                    ExpectedConditions.presenceOfAllElementsLocatedBy(
                            By.className("people_list_info")
                    )
            );
            return this.siteUtl.filterLawyersInPage(lawyers, byRoleArray, false, validRoles);

        } catch (Exception e) {
            throw new RuntimeException("Failed to find lawyer elements", e);
        }
    }


    private String getLink(WebElement lawyer) {
        By[] byArray = new By[]{
                By.className("h3_styler"),
                By.cssSelector("a")
        };
        WebElement element = this.siteUtl.iterateOverBy(byArray, lawyer);
        return element.getAttribute("href");
    }


    private String getName(WebElement lawyer) {
        By[] byArray = new By[]{
                By.className("h3_styler"),
                By.cssSelector("a")
        };
        WebElement element = this.siteUtl.iterateOverBy(byArray, lawyer);
        return siteUtl.getContentFromTag(element);
    }


    private String getRole(WebElement lawyer) {
        WebElement element = this.siteUtl.iterateOverBy(byRoleArray, lawyer);
        return siteUtl.getContentFromTag(element.getText());
    }


    private String getCountry(WebElement lawyer) {
        By[] byArray = new By[]{
                By.className("people_left_stats_job"),
                By.cssSelector("a")
        };
        return siteUtl.getCountryBasedInOffice(
            OFFICE_TO_COUNTRY, this.siteUtl.iterateOverBy(byArray, lawyer)
        );
    }


    private String[] getSocials(WebElement lawyer) {
        try {
            List<WebElement> socials = lawyer
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
            "phone", socials[1]
        );
    }
}
