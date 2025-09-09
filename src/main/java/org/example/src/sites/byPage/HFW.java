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

public class HFW extends ByPage {
    public static final Map<String, String> OFFICE_TO_COUNTRY = Map.ofEntries(
            entry("abu dhabi", "the UAE"),
            entry("british virgin islands", "the British Virgin Islands"),
            entry("brussels", "Belgium"),
            entry("dubai", "the UAE"),
            entry("geneva", "Switzerland"),
            entry("hong kong", "Hong Kong"),
            entry("houston", "USA"),
            entry("kuwait", "Kuwait"),
            entry("london", "England"),
            entry("monaco", "Monaco"),
            entry("paris", "France"),
            entry("piraeus", "Greece"),
            entry("rio de janeiro (car)", "Brazil"),
            entry("riyadh", "Saudi Arabia"),
            entry("shanghai", "China"),
            entry("shenzhen", "China"),
            entry("singapore", "Singapore")
    );


    private final By[] byRoleArray = {
            By.cssSelector("div > div.text-lg.font-medium")
    };


    public HFW() {
        super(
                "HFW",
                "https://www.hfw.com/people/",
                18,
                3
        );
    }


    protected void accessPage(int index) throws InterruptedException {
        String otherUrl = "https://www.hfw.com/people/?_paged=" + (index + 1);
        String url = index == 0 ? this.link : otherUrl;
        this.driver.get(url);
        MyDriver.waitForPageToLoad();
        Thread.sleep(1000L);

        if (index > 0) return;

        MyDriver.clickOnAddBtn(By.id("CybotCookiebotDialogBodyLevelButtonLevelOptinAllowAll"));
    }


    protected List<WebElement> getLawyersInPage() {
        String[] validRoles = new String[]{
                "partner",
                "counsel",
                "senior associate"
        };

        try {
            WebDriverWait wait = new WebDriverWait(this.driver, Duration.ofSeconds(10L));

            WebElement lawyersDiv = wait.until(
                    ExpectedConditions.presenceOfElementLocated(
                            By.xpath("//*[@id=\"block_fe0919fb262bab668f8d9a07e01a6c32\"]/div[3]/div[1]/div/div")
                    )
            );

            List<WebElement> lawyers = lawyersDiv.findElements(By.cssSelector("div.facetwp-template > div > div.parent"));

            return this.siteUtl.filterLawyersInPage(lawyers, byRoleArray, true, validRoles);

        } catch (Exception e) {
            throw new RuntimeException("Failed to find lawyer elements", e);
        }
    }


    public String getLink(WebElement lawyer) throws LawyerExceptions {
        By[] byArray = new By[]{
                By.cssSelector("div > a")
        };
        return extractor.extractLawyerAttribute(lawyer, byArray, "LINK", "href", LawyerExceptions::linkException);
    }


    private String getName(WebElement lawyer) throws LawyerExceptions {
        By[] byArray = new By[]{
                By.cssSelector("div > a")
        };
        return extractor.extractLawyerText(lawyer, byArray, "NAME", LawyerExceptions::nameException);
    }


    private String getRole(WebElement lawyer) throws LawyerExceptions {
        return extractor.extractLawyerText(lawyer, byRoleArray, "ROLE", LawyerExceptions::roleException);
    }


    private String getCountry(WebElement lawyer) throws LawyerExceptions {
        By[] byArray = new By[]{
                By.cssSelector("a[href^='https://www.hfw.com/about-us/our-locations/']")
        };
        String office = extractor.extractLawyerText(lawyer, byArray, "COUNTRY", LawyerExceptions::countryException);
        return siteUtl.getCountryBasedInOffice(OFFICE_TO_COUNTRY, office, "Australia");
    }


    private String[] getSocials(WebElement lawyer) {
        try {
            List<WebElement> socials = lawyer.findElements(By.cssSelector("a"));
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