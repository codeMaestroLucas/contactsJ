package org.example.src.sites._standingBy.otherIsuues;

import org.example.src.entities.BaseSites.ByNewPage;
import org.example.src.entities.MyDriver;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.time.Duration;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import static java.util.Map.entry;

public class Fasken extends ByNewPage {
    public static final Map<String, String> OFFICE_TO_COUNTRY = Map.ofEntries(
            entry("johannesburg", "South Africa"),
            entry("london", "England")
    );

    String[] validRoles = {
            "partner",
            "counsel",
            "senior associate"
    };


    public Fasken() {
        super(
            "Fasken",
            "https://www.fasken.com/en/people",
            5,
            1000
        );
    }


    protected void accessPage(int index) throws InterruptedException {
        String otherUrl = "https://www.fasken.com/en/people#firstResult=" + (15 * index);
        String url = index == 0 ? this.link : otherUrl;
        this.driver.get(url);
        MyDriver.waitForPageToLoad();
        Thread.sleep(1000L);

        if (index > 0) return;

        // Click on add btn
        MyDriver.clickOnElement(By.id("onetrust-accept-btn-handler"));
    }


    protected List<WebElement> getLawyersInPage() {
        try {
            WebDriverWait wait = new WebDriverWait(this.driver, Duration.ofSeconds(10L));

            // Can't locale the elements
            List<WebElement> until = wait.until(
                    ExpectedConditions.presenceOfAllElementsLocatedBy(
                            By.cssSelector("div[part=\"result-list\"] > div")
                    )
            );
            return until;

        } catch (Exception e) {
            throw new RuntimeException("Failed to find lawyer elements", e);
        }
    }


    public void openNewTab(WebElement lawyer) {
        MyDriver.openNewTab(lawyer.getAttribute("href"));
    }


    private String getName(WebElement lawyer) {
        By[] byArray = new By[]{
                By.className("name")
        };
        WebElement element = this.siteUtl.iterateOverBy(byArray, lawyer);
        return element.getText();
    }


    private String getRole(WebElement lawyer) {
        By[] byArray = new By[]{
                By.className("jobtitle")
        };
        WebElement element = this.siteUtl.iterateOverBy(byArray, lawyer);

        String role = element.getText();
        boolean validPosition = siteUtl.isValidPosition(role, validRoles);
        return validPosition ? role : "Invalid Role";
    }


    private String getCountry(WebElement lawyer) {
        By[] byArray = new By[]{
                By.cssSelector("div.category:last-child")
        };
        WebElement element = this.siteUtl.iterateOverBy(byArray, lawyer);
        return siteUtl.getCountryBasedInOffice(OFFICE_TO_COUNTRY, element.getText(), "Canada");
    }


    private String[] getSocials(WebElement lawyer) {
        try {
            List<WebElement> socials = lawyer
                        .findElement(By.className("values"))
                        .findElements(By.cssSelector("a"));
            return super.getSocials(socials, false);

        } catch (Exception e) {
            System.err.println("Error getting socials: " + e.getMessage());
            return new String[]{"", ""};
        }
    }


    public Object getLawyer(WebElement lawyer) throws Exception {
        this.openNewTab(lawyer);

        WebElement div = driver.findElement(By.className("content"));

        String role = this.getRole(div);
        if (role.equals("Invalid Role")) return "Invalid Role";

        String[] socials = this.getSocials(div);

        return Map.of(
            "link", Objects.requireNonNull(driver.getCurrentUrl()),
            "name", this.getName(div),
            "role", role,
            "firm", this.name,
            "country", this.getCountry(div),
            "practice_area", "",
            "email", socials[0],
            "phone", socials[1].isEmpty() ? "15143977400" : socials[1]

        );
    }
}
