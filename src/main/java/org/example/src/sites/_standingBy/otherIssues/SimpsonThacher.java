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

public class SimpsonThacher extends ByPage {
    public static final Map<String, String> OFFICE_TO_COUNTRY = Map.ofEntries(
            Map.entry("beijing", "China"),
            Map.entry("brussels", "Belgium"),
            Map.entry("hong kong", "Hong Kong"),
            Map.entry("london", "England"),
            Map.entry("luxembourg", "Luxembourg"),
            Map.entry("são paulo", "Brazil"),
            Map.entry("tokyo", "Japan")
    );

    private final By[] byRoleArray = {
            By.className("contact-title")
    };

    public SimpsonThacher() {
        super(
                "Simpson Thacher",
                "https://www.stblaw.com/our-team?searchId=896284e9-2181-4b38-bc0b-6d68bcf96cbf&directory=lawyers",
                1
        );
    }

    protected void accessPage(int index) {
        this.driver.get(this.link);
        MyDriver.waitForPageToLoad();

        MyDriver.clickOnAddBtn(By.className("cm-btn-success"));

        WebElement filter = driver.findElement(By.id("filter_offices"));
        filter
                .findElement(By.cssSelector("option[value='fc35cc0e-743d-6a02-aaf8-ff0000765f2c']"))
                .click();

        driver.findElement(By.xpath("//*[@id=\"pager2\"]/span[1]/span/select/option[5]")).click();

    }

    protected List<WebElement> getLawyersInPage() {
        String[] validRoles = {"partner", "counsel"};

        try {
            WebDriverWait wait = new WebDriverWait(this.driver, Duration.ofSeconds(10L));
            List<WebElement> lawyers = wait.until(
                    ExpectedConditions.presenceOfAllElementsLocatedBy(
                            By.className("contact-item-container")
                    )
            );
            return this.siteUtl.filterLawyersInPage(lawyers, byRoleArray, true, validRoles);
        } catch (Exception e) {
            throw new RuntimeException("Failed to find lawyer elements", e);
        }
    }

    private String getLink(WebElement lawyer) throws LawyerExceptions {
        By[] byArray = {By.cssSelector("div.contact-name > a")};
        return extractor.extractLawyerAttribute(lawyer, byArray, "LINK", "href", LawyerExceptions::linkException);
    }

    private String getName(WebElement lawyer) throws LawyerExceptions {
        By[] byArray = {By.cssSelector("div.contact-name > a")};
        return extractor.extractLawyerText(lawyer, byArray, "NAME", LawyerExceptions::nameException);
    }

    private String getRole(WebElement lawyer) throws LawyerExceptions {
        return extractor.extractLawyerText(lawyer, byRoleArray, "ROLE", LawyerExceptions::roleException);
    }

    private String getCountry(WebElement lawyer) throws LawyerExceptions {
        By[] byArray = {By.className("contact-office")};
        String country = extractor.extractLawyerText(lawyer, byArray, "COUNTRY", LawyerExceptions::countryException);
        return siteUtl.getCountryBasedInOffice(OFFICE_TO_COUNTRY, country, "USA");
    }

    private String[] getSocials(WebElement lawyer) {
        String email = "";
        String phone = "";
        try {
            email = lawyer.findElement(By.cssSelector("div.contact-email > a")).getAttribute("href");
            phone = lawyer.findElement(By.className("contact-phone")).getText();
        } catch (Exception e) {
            // Social not found, ignore
        }
        return new String[]{email, phone};
    }

    public Object getLawyer(WebElement lawyer) throws Exception {
        String[] socials = this.getSocials(lawyer);
        return Map.of(
                "link", this.getLink(lawyer),
                "name", this.getName(lawyer),
                "role", this.getRole(lawyer),
                "firm", this.name,
//                "country", this.getCountry(lawyer),
                "country", "England",
                "practice_area", "",
                "email", socials[0],
                "phone", socials[1]
        );
    }
}