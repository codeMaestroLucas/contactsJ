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

public class AddleshawGoddardLLP extends ByPage {
    public static final Map<String, String> OFFICE_TO_COUNTRY = Map.ofEntries(
            Map.entry("33", "France"),
            Map.entry("49", "Germany"),
            Map.entry("353", "Ireland"),
            Map.entry("352", "Luxembourg"),
            Map.entry("48", "Poland"),
            Map.entry("34", "Spain"),
            Map.entry("90", "Turkey"),
            Map.entry("380", "Ukraine"),
            Map.entry("86", "China"),
            Map.entry("91", "India"),
            Map.entry("81", "Japan"),
            Map.entry("82", "Korea (South)"),
            Map.entry("60", "Malaysia"),
            Map.entry("65", "Singapore"),
            Map.entry("971", "the UAE"),
            Map.entry("974", "Qatar"),
            Map.entry("968", "Oman"),
            Map.entry("966", "Saudi Arabia")
    );


    private final By[] byRoleArray = {
            By.className("about")
    };

    public AddleshawGoddardLLP() {
        super(
                "Addleshaw Goddard LLP",
                "https://www.addleshawgoddard.com/en/our-people/?Filter.Query=&Filter.Sector=&Filter.Specialism=&Filter.Role=&Filter.Location=&Filter.ArticleType=&pageNumber=1&size=48",
                35,
                3
        );
    }

    protected void accessPage(int index) throws InterruptedException {
        String otherUrl = "https://www.addleshawgoddard.com/en/our-people/?Filter.Query=&Filter.Sector=&Filter.Specialism=&Filter.Role=Partner&Filter.Location=&Filter.ArticleType=&pageNumber=" + (index + 1) + "&size=48";
        String url = index == 0 ? this.link : otherUrl;
        this.driver.get(url);
        MyDriver.waitForPageToLoad();
        Thread.sleep(1000L);
    }

    protected List<WebElement> getLawyersInPage() {
        String[] validRoles = {"partner", "counsel", "director",  "principal", "senior associate"};

        try {
            WebDriverWait wait = new WebDriverWait(this.driver, Duration.ofSeconds(10L));
            List<WebElement> lawyers = wait.until(
                    ExpectedConditions.presenceOfAllElementsLocatedBy(
                            By.cssSelector("div.item.people")
                    )
            );
            return siteUtl.filterLawyersInPage(lawyers, byRoleArray, false, validRoles);
        } catch (Exception e) {
            throw new RuntimeException("Failed to find lawyer elements", e);
        }
    }

    private String getLink(WebElement lawyer) throws LawyerExceptions {
        By[] byArray = {By.className("title")};
        return extractor.extractLawyerAttribute(lawyer, byArray, "LINK", "href", LawyerExceptions::linkException);
    }

    private String getName(WebElement lawyer) throws LawyerExceptions {
        By[] byArray = {By.className("title")};
        return extractor.extractLawyerAttribute(lawyer, byArray, "NAME", "textContent", LawyerExceptions::nameException);
    }

    private String getRole(WebElement lawyer) throws LawyerExceptions {
        return extractor.extractLawyerAttribute(lawyer, byRoleArray, "ROLE", "textContent", LawyerExceptions::roleException);
    }

    private String getCountry(String phone) throws LawyerExceptions {
        return this.siteUtl.getCountryBasedInOfficeByPhone(OFFICE_TO_COUNTRY, phone, "England");
    }

    private String[] getSocials(WebElement lawyer) {
        try {
            List<WebElement> socials = lawyer.findElements(By.cssSelector(".icon-box > a, .phone-box a"));
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
                "practice_area", "",
                "email", socials[0],
                "phone", socials[1]
        );
    }
}