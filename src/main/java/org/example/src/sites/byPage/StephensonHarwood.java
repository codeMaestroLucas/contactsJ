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
import java.util.NoSuchElementException;

import static java.util.Map.entry;

public class StephensonHarwood extends ByPage {
    public static final Map<String, String> OFFICE_TO_COUNTRY = Map.ofEntries(
            entry("london", "England"),
            entry("seoul", "Korea (South)"),
            entry("paris", "France"),
            entry("hong kong", "Hong Kong"),
            entry("dubai", "the UAE"),
            entry("beijing", "China"),
            entry("shanghai", "China"),
            entry("guangzhou", "China"),
            entry("athens", "Greece"),
            entry("singapore", "Singapore")
    );


    private final By[] byRoleArray = {
            By.cssSelector("h3")
    };


    public StephensonHarwood() {
        super(
            "Stephenson Harwood",
            "https://www.stephensonharwood.com/people#searchtype=categories;",
            30,
            3
        );
    }


    protected void accessPage(int index) throws InterruptedException {
        if (index == 0) {
            this.driver.get(this.link);
            MyDriver.waitForPageToLoad();
            Thread.sleep(1000L);

            // Click on add btn
            MyDriver.clickOnElement(By.id("ccc-notify-accept"));
            return;
        }

        // Click on pagination
        try {
            WebElement activeLi = driver.findElement(By.cssSelector("ul.pagination > li.active"));
            WebElement nextLi = activeLi.findElement(By.xpath("following-sibling::li[1]/a"));
            MyDriver.clickOnElement(nextLi);
            Thread.sleep(2000L);

        } catch (NoSuchElementException e) {
            System.out.println("No next page available or structure changed.");
        }
    }


    protected List<WebElement> getLawyersInPage() {
        String[] validRoles = new String[]{
                "partner",
                "counsel",
                "managing associate",
                "senior associate",
        };

        try {
            WebDriverWait wait = new WebDriverWait(this.driver, Duration.ofSeconds(10L));

            List<WebElement> lawyers = wait.until(
                    ExpectedConditions.presenceOfAllElementsLocatedBy(
                            By.cssSelector("div.row > div.col-sm-12.col-xs-8")
                    )
            );
            List<WebElement> valid = this.siteUtl.filterLawyersInPage(lawyers, byRoleArray, true, validRoles);

            System.out.println("\ntotal Lawyers:" + valid.size());
            return valid;

        } catch (Exception e) {
            throw new RuntimeException("Failed to find lawyer elements", e);
        }
    }


    private String getLink(WebElement lawyer) {
        By[] byArray = new By[]{
                By.cssSelector("h2 > a")
        };
        WebElement element = this.siteUtl.iterateOverBy(byArray, lawyer);
        return element.getAttribute("href");
    }


    private String getName(WebElement lawyer) {
        By[] byArray = new By[]{
                By.cssSelector("h2 > a")
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
                By.cssSelector("a[href^='/offices/']")
        };
        WebElement element = this.siteUtl.iterateOverBy(byArray, lawyer);
        return siteUtl.getCountryBasedInOffice(OFFICE_TO_COUNTRY, element.getText());
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
