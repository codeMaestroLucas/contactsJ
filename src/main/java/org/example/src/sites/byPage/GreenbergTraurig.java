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

public class GreenbergTraurig extends ByPage {
    public static final Map<String, String> OFFICE_TO_COUNTRY = Map.<String, String>ofEntries(
            entry("albany", "EUA"),
            entry("amsterdam", "the Netherlands"),
            entry("atlanta", "EUA"),
            entry("austin", "EUA"),
            entry("berlin", "Germany"),
            entry("boston", "EUA"),
            entry("charlotte", "EUA"),
            entry("chicago", "EUA"),
            entry("dallas", "EUA"),
            entry("delaware", "EUA"),
            entry("denver", "EUA"),
            entry("fort lauderdale", "EUA"),
            entry("gt restructuring berlin", "Germany"),
            entry("gt restructuring munich", "Germany"),
            entry("houston", "EUA"),
            entry("kingdom of saudi arabia", "Saudi Arabia"),
            entry("las vegas", "EUA"),
            entry("london", "England"),
            entry("long island", "EUA"),
            entry("los angeles", "EUA"),
            entry("mexico city", "Mexico"),
            entry("miami", "EUA"),
            entry("milan", "Italy"),
            entry("minneapolis", "EUA"),
            entry("munich", "Germany"),
            entry("new jersey", "EUA"),
            entry("new york", "EUA"),
            entry("northern virginia", "EUA"),
            entry("orange county", "EUA"),
            entry("orlando", "EUA"),
            entry("philadelphia", "EUA"),
            entry("phoenix", "EUA"),
            entry("portland", "EUA"),
            entry("sacramento", "EUA"),
            entry("salt lake city", "EUA"),
            entry("san diego", "EUA"),
            entry("san francisco", "EUA"),
            entry("são paulo", "Brazil"),
            entry("seoul", "Korea (South)"),
            entry("shanghai", "China"),
            entry("silicon valley", "EUA"),
            entry("singapore", "Singapore"),
            entry("singapore*", "Singapore"),
            entry("tallahassee", "EUA"),
            entry("tampa", "EUA"),
            entry("tel aviv", "Israel"),
            entry("tokyo", "Japan"),
            entry("united arab emirates", "the UAE"),
            entry("warsaw", "Poland"),
            entry("washington, d.c.", "EUA"),
            entry("west palm beach", "EUA"),
            entry("westchester county", "EUA")
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

        if (index == 0) this.siteUtl.clickOnAddBtn(By.id("onetrust-accept-btn-handler"));

        // Click on load more btn
        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(10));

        WebElement totalLawyers = wait.until(ExpectedConditions.elementToBeClickable(By.className("result-count-section")));
        int numberOfLawyers = Integer.parseInt(
                totalLawyers.findElement(By.className("results-count")).getText().trim()
        );

        WebElement viewMoreButton = wait.until(ExpectedConditions.elementToBeClickable(By.cssSelector("button.view-more")));
        viewMoreButton.click();

        MyDriver.rollDown(numberOfLawyers / 10, 1);
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

            List<WebElement> lawyers = wait.until(
                    ExpectedConditions.presenceOfAllElementsLocatedBy(
                            By.className("info-container")
                    )
            );
            return this.siteUtl.filterLawyersInPage(lawyers, byRoleArray, true, validRoles);

        } catch (Exception e) {
            throw new RuntimeException("Failed to find lawyer elements", e);
        }
    }


    private String getLink(WebElement lawyer) {
        By[] byArray = new By[]{
            By.className("middle-info"),
            By.className("upper-info"),
            By.className("name"),
        };
        WebElement element = this.siteUtl.iterateOverBy(byArray, lawyer);
        return element.getAttribute("href");
    }


    private String getName(WebElement lawyer) {
        By[] byArray = new By[]{
            By.className("middle-info"),
            By.className("upper-info"),
            By.className("name"),
        };
        WebElement element = this.siteUtl.iterateOverBy(byArray, lawyer);
        return element.getText();
    }


    private String getRole(WebElement lawyer) {
        WebElement element = this.siteUtl.iterateOverBy(byRoleArray, lawyer);
        return element.getText();
    }


    private String getPracticeArea(WebElement lawyer) {
        By[] byArray = new By[]{
            By.className("right-info"),
            By.className("lower-info"),
            By.cssSelector("a")
        };
        WebElement element = this.siteUtl.iterateOverBy(byArray, lawyer);
        return element.getText();
    }

    private String getCountry(WebElement lawyer) {
        By[] byArray = new By[]{
            By.className("right-info"),
            By.className("upper-info"),
            By.cssSelector("a")
        };
        WebElement element = this.siteUtl.iterateOverBy(byArray, lawyer);
        return siteUtl.getCountryBasedInOffice(OFFICE_TO_COUNTRY, element.getText());
    }


    private String[] getSocials(WebElement lawyer) {
        try {
            String phone = lawyer.findElement((By.className("lower-info")))
                    .findElement(By.className("phone"))
                    .getAttribute("href");

            String email = lawyer.findElement((By.className("lower-info")))
                    .findElement(By.className("email"))
                    .getText();

            return new String[] { email, phone };

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
            "phone", socials[1]);
    }
}
