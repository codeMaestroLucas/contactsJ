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

import static java.util.Map.entry;

public class ControlRisks extends ByNewPage {
    public static final Map<String, String> OFFICE_TO_COUNTRY = Map.ofEntries(
            entry("abu dhabi", "the UAE"),
            entry("amsterdam", "the Netherlands"),
            entry("baghdad", "Iraq"),
            entry("basra", "Iraq"),
            entry("berlin", "Germany"),
            entry("bogota", "Colombia"),
            entry("copenhagen", "Denmark"),
            entry("dakar", "Senegal"),
            entry("delhi", "India"),
            entry("dubai", "the UAE"),
            entry("frankfurt", "Germany"),
            entry("hong kong", "Hong Kong"),
            entry("johannesburg", "South Africa"),
            entry("lagos", "Nigeria"),
            entry("london", "England"),
            entry("madrid", "Spain"),
            entry("maputo", "Mozambique"),
            entry("mexico city", "Mexico"),
            entry("mumbai", "India"),
            entry("nairobi", "Kenya"),
            entry("paris", "France"),
            entry("sao paulo", "Brazil"),
            entry("seoul", "Korea (South)"),
            entry("shanghai", "China"),
            entry("saudi arabia", "Saudi Arabia"),
            entry("singapore", "Singapore"),
            entry("sydney", "Australia"),
            entry("tokyo", "Japan"),
            entry("toronto", "Canada")
    );

    String[] validRoles = new String[]{
            "partner",
            "principal",
            "director",
    };


    private final By[] byRoleArray = {
            By.className("bio-card--expert__subtitle")
    };


    public ControlRisks() {
        super(
                "Control Risks",
                "https://www.controlrisks.com/who-we-are/our-experts",
                1,
                3
        );
    }


    protected void accessPage(int index) throws InterruptedException {
        this.driver.get(this.link);
        MyDriver.waitForPageToLoad();
        Thread.sleep(1000L);

        MyDriver.clickOnAddBtn(By.id("onetrust-accept-btn-handler"));

        MyDriver.clickOnElementMultipleTimes(
                By.className("load-more__action"),
                10, // Actually it has 47
                1
        );
    }


    protected List<WebElement> getLawyersInPage() {
        try {
            WebDriverWait wait = new WebDriverWait(this.driver, Duration.ofSeconds(10L));

            List<WebElement> lawyers = wait.until(
                    ExpectedConditions.presenceOfAllElementsLocatedBy(
                            By.className("bio-card--expert__header")
                    )
            );
            return this.siteUtl.filterLawyersInPage(lawyers, byRoleArray, true, validRoles);

        } catch (Exception e) {
            throw new RuntimeException("Failed to find lawyer elements", e);
        }
    }


    public String openNewTab(WebElement lawyer) {
        try {
            By[] byArray = {By.className("bio-card--expert__title"), By.cssSelector("a")};
            String link = extractor.extractLawyerAttribute(lawyer, byArray, "LINK", "href", LawyerExceptions::linkException);
            MyDriver.openNewTab(link);
        } catch (LawyerExceptions e) {
            System.err.println("Failed to open new tab: " + e.getMessage());
        }
        return null;
    }

    public String getLink() {
        return driver.getCurrentUrl();
    }

    private String getName(WebElement lawyer) throws LawyerExceptions {
        By[] byArray = {By.className("bio--banner__container-title"), By.className("bio--banner__title")};
        return extractor.extractLawyerAttribute(lawyer, byArray, "NAME", "outerHTML", LawyerExceptions::nameException);
    }


    private String getRole(WebElement lawyer) throws LawyerExceptions {
        By[] byArray = {By.className("bio--banner__container-title"), By.className("bio--banner__subtitle")};
        String role = extractor.extractLawyerAttribute(lawyer, byArray, "ROLE", "outerHTML", LawyerExceptions::roleException);
        boolean validPosition = siteUtl.isValidPosition(role, validRoles);
        return validPosition ? role : "Invalid Role";
    }


    private String getCountry(WebElement lawyer) throws LawyerExceptions {
        By[] byArray = {By.className("btn__geo")};
        String office = extractor.extractLawyerAttribute(lawyer, byArray, "COUNTRY", "outerHTML", LawyerExceptions::countryException);
        return siteUtl.getCountryBasedInOffice(OFFICE_TO_COUNTRY, office, "USA");
    }


    private String getPracticeArea(WebElement lawyer) {
        try {
            By[] byArray = {By.className("bio__tags"), By.className("bio__list__tag")};
            return extractor.extractLawyerAttribute(lawyer, byArray, "PRACTICE AREA", "textContent ", LawyerExceptions::practiceAreaException);
        } catch (Exception e) {
            return "";
        }
    }


    private String[] getSocials(WebElement lawyer) {
        try {
            List<WebElement> socials = lawyer
                    .findElement(By.className("bio__cta"))
                    .findElements(By.cssSelector("a"));
            return super.getSocials(socials, false);

        } catch (Exception e) {
            System.err.println("Error getting socials: " + e.getMessage());
            return new String[]{"", ""};
        }
    }


    public Object getLawyer(WebElement lawyer) throws Exception {
        this.openNewTab(lawyer);

        WebElement div = driver.findElement(By.className("bio__body"));

        String role = this.getRole(div);
        if (role.equals("Invalid Role")) return "Invalid Role";

        String[] socials = this.getSocials(div);

        return Map.of(
                "link", this.getLink(),
                "name", this.getName(div),
                "role", role,
                "firm", this.name,
                "country", this.getCountry(div),
                "practice_area", this.getPracticeArea(div),
                "email", socials[0],
                "phone", socials[1].isEmpty() ? "" : socials[1]
        );
    }
}