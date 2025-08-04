package org.example.src.sites.byPage;

import org.example.src.entities.BaseSites.ByPage;
import org.example.src.entities.MyDriver;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.sql.SQLOutput;
import java.time.Duration;
import java.util.List;
import java.util.Map;

import static java.util.Map.entry;

public class SpencerWest extends ByPage {
    public static final Map<String, String> OFFICE_TO_COUNTRY = Map.ofEntries(
            entry("1", "USA, Bahamas, British Virgin Islands, Cayman Islands"),
            entry("27", "South Africa"),
            entry("32", "Belgium"),
            entry("33", "France"),
            entry("34", "Spain"),
            entry("41", "Switzerland"),
            entry("44", "England, Scotland, the Channel Islands"),
            entry("48", "Poland"),
            entry("49", "Germany"),
            entry("61", "Australia"),
            entry("65", "Singapore"),
            entry("92", "Pakistan"),
            entry("250", "Rwanda"),
            entry("254", "Kenya"),
            entry("351", "Portugal"),
            entry("357", "Cyprus")
    );


    private final By[] byRoleArray = {
            By.className("person-card__job-description")
    };


    public SpencerWest() {
        super(
            "Spencer West",
            "https://www.spencer-west.com/team/",
            23,
            3
        );
    }


    protected void accessPage(int index) throws InterruptedException {
        String otherUrl = "https://www.spencer-west.com/team/page/" + (index + 1) + "/";
        String url = index == 0 ? this.link : otherUrl;
        this.driver.get(url);
        MyDriver.waitForPageToLoad();
        Thread.sleep(1000L);

        if (index > 0) return;

        this.siteUtl.clickOnAddBtn(By.className("cky-btn-accept"));
    }


    protected List<WebElement> getLawyersInPage() {
        String[] validRoles = new String[]{
                "partner",
                "senior associate"
        };

        try {
            WebDriverWait wait = new WebDriverWait(this.driver, Duration.ofSeconds(10L));

            List<WebElement> lawyers = wait.until(
                    ExpectedConditions.presenceOfAllElementsLocatedBy(
                            By.className("person-card__content")
                    )
            );
            return this.siteUtl.filterLawyersInPage(lawyers, byRoleArray, true, validRoles);

        } catch (Exception e) {
            throw new RuntimeException("Failed to find lawyer elements", e);
        }
    }


    private String getLink(WebElement lawyer) {
        By[] byArray = new By[]{
                By.className("person-card__name")
        };
        WebElement element = this.siteUtl.iterateOverBy(byArray, lawyer);
        return element.getAttribute("href");
    }


    private String getName(WebElement lawyer) {
        By[] byArray = new By[]{
                By.className("person-card__name")
        };
        WebElement element = this.siteUtl.iterateOverBy(byArray, lawyer);
        return element.getText();
    }


    private String getRole(WebElement lawyer) {
        WebElement element = this.siteUtl.iterateOverBy(byRoleArray, lawyer);
        return element.getText();
    }


    private String getCountry(String phone) {
        for (String key : OFFICE_TO_COUNTRY.keySet()) {
            if (phone.startsWith(key)) return OFFICE_TO_COUNTRY.get(key);
        }
        return "------";
    }



    private String getPracticeArea(WebElement lawyer) {
        By[] byArray = new By[]{
                By.className("person-card__job-description")
        };
        WebElement element = this.siteUtl.iterateOverBy(byArray, lawyer);
        String[] elements = element.getText().split("-");
        return elements[elements.length - 1];
    }


    private String[] getSocials(WebElement lawyer) {
        try {
            List<WebElement> socials = lawyer
                        .findElement(By.className("person-card__social-links"))
                        .findElements(By.className("social-link"));
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
            "country", this.getCountry(socials[1]),
            "practice_area", this.getPracticeArea(lawyer),
            "email", socials[0],
            "phone", socials[1]);
    }
}
