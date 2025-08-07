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
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static java.util.Map.entry;

public class HFW extends ByPage {
    public static final Map<String, String> OFFICE_TO_COUNTRY = Map.ofEntries(
            entry("abu dhabi", "the UAE"),
            entry("british virgin islands", "the British Virgin Islands"),
            entry("brussels", "Belgium"),
            entry("dubai", "the UAE"),
            entry("geneva", "Switzerland"),
            entry("hong kong", "Hong Kong"),
            entry("houston", "EUA"),
            entry("kuwait", "Kuwait"),
            entry("london", "England"),
            entry("melbourne", "Australia"),
            entry("monaco", "Monaco"),
            entry("paris", "France"),
            entry("perth", "Australia"),
            entry("piraeus", "Greece"),
            entry("rio de janeiro (car)", "Brazil"),
            entry("riyadh", "Saudi Arabia"),
            entry("shanghai", "China"),
            entry("shenzhen", "China"),
            entry("singapore", "Singapore"),
            entry("sydney", "Australia")
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
        String otherUrl = "https://www.hfw.com/people/?_paged=" + (index + 1) ;
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


    private String getLink(WebElement lawyer) {
        By[] byArray = new By[]{
            By.cssSelector("div > a")
        };
        WebElement element = this.siteUtl.iterateOverBy(byArray, lawyer);
        return element.getAttribute("href");
    }


    private String getName(WebElement lawyer) {
        By[] byArray = new By[]{
            By.cssSelector("div > a")
        };
        WebElement element = this.siteUtl.iterateOverBy(byArray, lawyer);
        return element.getText();
    }


    private String getRole(WebElement lawyer) {
        WebElement element = this.siteUtl.iterateOverBy(byRoleArray, lawyer);
        return element.getText();
    }


    private String getCountry(WebElement lawyer) {
        List<WebElement> links = lawyer.findElements(By.cssSelector("a"));
        String country = "";

        for (WebElement link : links) {
            String href = link.getAttribute("href").toLowerCase();
            if (href.contains("our-locations")) {
                Pattern pattern = Pattern.compile(".*/our-locations/([^/]+)/?");
                Matcher matcher = pattern.matcher(href);

                if (matcher.find()) {
                    country = matcher.group(1);
                    break;
                } else {
                    country = href;
                }
            }
        }
        return siteUtl.getCountryBasedInOffice(OFFICE_TO_COUNTRY, country);
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
            "phone", socials[1]);
    }
}
