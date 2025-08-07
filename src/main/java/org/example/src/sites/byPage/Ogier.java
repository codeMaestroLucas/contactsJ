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

public class Ogier extends ByPage {
    public static final Map<String, String> OFFICE_TO_COUNTRY = Map.ofEntries(
            entry("beijing", "China"),
            entry("british virgin islands", "the British Virgin Islands"),
            entry("cayman islands", "the Cayman Islands"),
            entry("dubai", "the UAE"),
            entry("guernsey", "Guernsey"),
            entry("hong kong", "Hong Kong"),
            entry("ireland", "Ireland"),
            entry("jersey", "Jersey"),
            entry("london", "England"),
            entry("luxembourg", "Luxembourg"),
            entry("luxembourg corporate and fund services", "Luxembourg"),
            entry("luxembourg  corporate and fund services", "Luxembourg"),
            entry("luxembourg legal services", "Luxembourg"),
            entry("luxembourg  legal services", "Luxembourg"),
            entry("shanghai", "China"),
            entry("singapore", "Singapore"),
            entry("tokyo", "Japan")
    );


    private final By[] byRoleArray = {
            By.className("body3"),
    };


    public Ogier() {
        super(
            "Ogier",
            "https://www.ogier.com/people/?pageSize=48",
            1,
            3
        );
    }


    protected void accessPage(int index) throws InterruptedException {
        this.driver.get(this.link);
        MyDriver.waitForPageToLoad();
        Thread.sleep(1000L);

        // Click on add btn
        MyDriver.clickOnElement(By.id("checkAll"));

        for (int i = 0; i < 9; i++) {
            MyDriver.clickOnElement(By.cssSelector("div.pagination--component > button.button--component"));
        }
    }


    protected List<WebElement> getLawyersInPage() {
        String[] validRoles = new String[]{
                "partner",
                "director",
                "counsel",
                "managing associate",
                "associate director",
                "senior associate",
        };

        try {
            WebDriverWait wait = new WebDriverWait(this.driver, Duration.ofSeconds(10L));

            List<WebElement> lawyers = wait.until(
                    ExpectedConditions.presenceOfAllElementsLocatedBy(
                            By.className("card-content")
                    )
            );
            return this.siteUtl.filterLawyersInPage(lawyers, byRoleArray, true, validRoles);

        } catch (Exception e) {
            throw new RuntimeException("Failed to find lawyer elements", e);
        }
    }


    private String getLink(WebElement lawyer) {
        By[] byArray = new By[]{
                By.className("card-heading"),
                By.cssSelector("a")
        };
        WebElement element = this.siteUtl.iterateOverBy(byArray, lawyer);
        return element.getAttribute("href");
    }


    private String getName(WebElement lawyer) {
        By[] byArray = new By[]{
                By.className("card-heading"),
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
        List<WebElement> elements = lawyer.findElements(By.cssSelector("p.body3"));
        String country = elements.getLast().getText();
        return siteUtl.getCountryBasedInOffice(OFFICE_TO_COUNTRY, country);
    }

    private String getPracticeArea(WebElement lawyer) {
        List<WebElement> elements = lawyer.findElements(By.cssSelector("p.body3"));
        return elements.get(1).getText();
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
            "practice_area", this.getPracticeArea(lawyer),
            "email", socials[0],
            "phone", socials[1]
        );
    }
}
