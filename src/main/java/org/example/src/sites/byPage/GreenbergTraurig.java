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

public class GreenbergTraurig extends ByPage {
    public static final Map<String, String> OFFICE_TO_COUNTRY = Map.<String, String>ofEntries(
            entry("amsterdam", "the Netherlands"),
            entry("berlin", "Germany"),
            entry("gt restructuring berlin", "Germany"),
            entry("gt restructuring munich", "Germany"),
            entry("kingdom of saudi arabia", "Saudi Arabia"),
            entry("london", "England"),
            entry("mexico city", "Mexico"),
            entry("milan", "Italy"),
            entry("munich", "Germany"),
            entry("são paulo", "Brazil"),
            entry("seoul", "Korea (South)"),
            entry("shanghai", "China"),
            entry("singapore", "Singapore"),
            entry("singapore*", "Singapore"),
            entry("tel aviv", "Israel"),
            entry("tokyo", "Japan"),
            entry("united arab emirates", "the UAE"),
            entry("warsaw", "Poland")
    );

    private final By[] byRoleArray = {
            By.className("middle-info"),
            By.className("upper-info"),
            By.className("title")
    };

    private final String[] letters = {
            "A", "B", "C", "D", "E", "F", "G", "H", "I", "J",
            "K", "L", "M", "N", "O", "P", "Q", "R", "S", "T",
            "U", "V", "W", "X", "Y", "Z"
    };


    public GreenbergTraurig() {
        super(
                "Greenberg Traurig",
                "https://www.gtlaw.com/en/professionals?letter=A",
                26,
                3
        );
    }


    protected void accessPage(int index) throws InterruptedException {
        String otherUrl = "https://www.gtlaw.com/en/professionals?letter=" + letters[index];
        String url = index == 0 ? this.link : otherUrl;
        this.driver.get(url);
        MyDriver.waitForPageToLoad();
        Thread.sleep(1000L);

        if (index == 0) MyDriver.clickOnAddBtn(By.id("onetrust-accept-btn-handler"));

        MyDriver.clickOnElement(By.cssSelector("button.view-more"));
        Thread.sleep(2000); // Wait for results to load
    }


    protected List<WebElement> getLawyersInPage() {
        String[] validRoles = new String[]{
                "partner",
                "counsel",
                "shareholder",
                "senior associate"
        };

        try {
            WebDriverWait wait = new WebDriverWait(this.driver, Duration.ofSeconds(10L));
            List<WebElement> lawyers = wait.until(ExpectedConditions.presenceOfAllElementsLocatedBy(By.className("info-container")));
            return this.siteUtl.filterLawyersInPage(lawyers, byRoleArray, true, validRoles);
        } catch (Exception e) {
            throw new RuntimeException("Failed to find lawyer elements", e);
        }
    }


    public String getLink(WebElement lawyer) throws LawyerExceptions {
        By[] byArray = new By[]{
                By.className("middle-info"),
                By.className("upper-info"),
                By.className("name"),
        };
        return extractor.extractLawyerAttribute(lawyer, byArray, "LINK", "href", LawyerExceptions::linkException);
    }


    private String getName(WebElement lawyer) throws LawyerExceptions {
        By[] byArray = new By[]{
                By.className("middle-info"),
                By.className("upper-info"),
                By.className("name"),
        };
        return extractor.extractLawyerText(lawyer, byArray, "NAME", LawyerExceptions::nameException);
    }


    private String getRole(WebElement lawyer) throws LawyerExceptions {
        return extractor.extractLawyerText(lawyer, byRoleArray, "ROLE", LawyerExceptions::roleException);
    }


    private String getPracticeArea(WebElement lawyer) {
        try {
            By[] byArray = new By[]{
                    By.className("right-info"),
                    By.className("lower-info"),
                    By.cssSelector("a")
            };
            return extractor.extractLawyerText(lawyer, byArray, "PRACTICE AREA", LawyerExceptions::practiceAreaException);
        } catch (Exception e) {
            return "";
        }
    }

    private String getCountry(WebElement lawyer) throws LawyerExceptions {
        By[] byArray = new By[]{
                By.className("right-info"),
                By.className("upper-info"),
                By.cssSelector("a")
        };
        String office = extractor.extractLawyerText(lawyer, byArray, "COUNTRY", LawyerExceptions::countryException);
        return siteUtl.getCountryBasedInOffice(OFFICE_TO_COUNTRY, office, "USA");
    }


    private String[] getSocials(WebElement lawyer) {
        try {
            String phone = lawyer.findElement((By.className("lower-info")))
                    .findElement(By.className("phone"))
                    .getAttribute("href");

            String email = lawyer.findElement((By.className("lower-info")))
                    .findElement(By.className("email"))
                    .getText();

            return new String[]{email, phone};
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