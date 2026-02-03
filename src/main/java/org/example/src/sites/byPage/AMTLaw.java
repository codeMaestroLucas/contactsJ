package org.example.src.sites.byPage;

import org.example.exceptions.LawyerExceptions;
import org.example.src.entities.BaseSites.ByPage;
import org.example.src.entities.MyDriver;
import org.example.src.utils.TreatLawyerParams;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.time.Duration;
import java.util.List;
import java.util.Map;

import static java.util.Map.entry;

public class AMTLaw extends ByPage {
    private final By[] byRoleArray = {
            By.cssSelector("div.info_container > div.texts > p.p2:last-child"),
    };


    public AMTLaw() {
        super(
                "AMT Law",
                "https://www.amt-law.com/en/professionals/",
                1,
                2
        );
    }

    public static final Map<String, String> OFFICE_TO_COUNTRY = Map.ofEntries(
            entry("beijing", "China"),
            entry("shanghai", "China"),
            entry("hong kong", "Hong Kong"),
            entry("singapore", "Singapore"),
            entry("hanoi", "Vietnam"),
            entry("ho chi minh", "Vietnam"),
            entry("ho chi minh city", "Vietnam"),
            entry("bangkok", "Thailand"),
            entry("jakarta", "Indonesia"),
            entry("london", "England"),
            entry("brussels", "Belgium")
    );

    protected void accessPage(int index) throws InterruptedException {
        this.driver.get(this.link);
        MyDriver.waitForPageToLoad();
        Thread.sleep(1000L);
        MyDriver.clickOnAddBtn(By.id("CybotCookiebotDialogBodyLevelButtonLevelOptinAllowAll"));
        // More than 60 rolls
        MyDriver.rollDown(15, 1);
    }


    protected List<WebElement> getLawyersInPage() {
        String[] validRoles = new String[]{
                "partner", "counsel", "advisor", "senior associate"
        };

        try {
            WebDriverWait wait = new WebDriverWait(this.driver, Duration.ofSeconds(10L));
            List<WebElement> lawyers = wait.until(
                    ExpectedConditions.presenceOfAllElementsLocatedBy(
                            By.cssSelector("div.results_inner > div")
                    )
            );
            return this.siteUtl.filterLawyersInPage(lawyers, byRoleArray, false, validRoles);

        } catch (Exception e) {
            throw new RuntimeException("Failed to find lawyer elements", e);
        }
    }


    private String getLink(WebElement lawyer) throws LawyerExceptions {
        By[] byArray = new By[]{
                By.cssSelector("a[href*='/en/professionals/profile/']")
        };
        return extractor.extractLawyerAttribute(lawyer, byArray, "LINK", "href", LawyerExceptions::linkException);
    }


    private String getName(WebElement lawyer) throws LawyerExceptions {
        By[] byArray = new By[]{
                By.cssSelector("p.h4")
        };
        return extractor.extractLawyerAttribute(lawyer, byArray, "NAME", "textContent", LawyerExceptions::nameException);
    }


    private String getRole(WebElement lawyer) throws LawyerExceptions {
        return extractor.extractLawyerText(lawyer, byRoleArray, "ROLE", LawyerExceptions::roleException);
    }


    private String getCountry(WebElement lawyer) throws LawyerExceptions {
        By[] byArray = new By[]{
                By.className("info_container"),
                By.className("location")
        };
        String country = extractor.extractLawyerText(lawyer, byArray, "COUNTRY", LawyerExceptions::countryException);
        return this.siteUtl.getCountryBasedInOffice(OFFICE_TO_COUNTRY, country, "Japan");
    }

    private String constructEmail(String name) {
        String[] nameParts = TreatLawyerParams.treatNameForEmail(name).split("\\s+");
        String firstName = nameParts[0];
        String lastName = nameParts[nameParts.length - 1];
        return firstName + "." + lastName + "@amt-law.com";
    }

    public Object getLawyer(WebElement lawyer) throws Exception {
        String name = this.getName(lawyer);
        String email = this.constructEmail(name);

        return Map.of(
                "link", this.getLink(lawyer),
                "name", name,
                "role", this.getRole(lawyer),
                "firm", this.name,
                "country", this.getCountry(lawyer),
                "practice_area", "",
                "email", email,
                "phone", "81367751000"
        );
    }
}