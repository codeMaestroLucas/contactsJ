package org.example.src.sites.byNewPage;

import org.example.src.entities.BaseSites.ByNewPage;
import org.example.src.entities.MyDriver;
import org.jspecify.annotations.Nullable;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.time.Duration;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import static java.util.Map.entry;
import static org.openqa.selenium.By.cssSelector;

public class Oxera extends ByNewPage {
    public static final Map<String, String> OFFICE_TO_COUNTRY = Map.ofEntries(
            entry("31", "the Netherlands"),
            entry("32", "Belgium"),
            entry("33", "Paris"),
            entry("34", "Spain"),
            entry("39", "Italy"),
            entry("44", "England"),
            entry("49", "Germany"),
            entry("353", "Ireland")
    );


    private final By[] byRoleArray = {
            By.className("personTile__content"),
            By.className("personTile__jobTitle")
    };


    public Oxera() {
        super(
            "Oxera",
            "https://www.oxera.com/people/",
            1,
            2
        );
    }


    protected void accessPage(int index) throws InterruptedException {
        this.driver.get(this.link);
        MyDriver.waitForPageToLoad();
        Thread.sleep(1000L);

        // Click on add btn
        MyDriver.clickOnElement(By.id("onetrust-accept-btn-handler"));
    }


    protected List<WebElement> getLawyersInPage() {
        String[] validRoles = new String[]{
                "partner",
                "principal",
                "advisor",
                "chair"
        };

        try {
            WebDriverWait wait = new WebDriverWait(this.driver, Duration.ofSeconds(10L));

            List<WebElement> lawyers = wait.until(
                    ExpectedConditions.presenceOfAllElementsLocatedBy(
                            By.className("personTile")
                    )
            );
            return this.siteUtl.filterLawyersInPage(lawyers, byRoleArray, true, validRoles);

        } catch (Exception e) {
            throw new RuntimeException("Failed to find lawyer elements", e);
        }
    }


    public void openNewTab(WebElement lawyer) {
        MyDriver.openNewTab(lawyer.getAttribute("href"));
    }


    private String getName(WebElement lawyer) {
        By[] byArray = new By[]{
                By.className("header-banner__contentContainer"),
                By.className("title")
        };
        WebElement element = this.siteUtl.iterateOverBy(byArray, lawyer);
        return element.getText();
    }


    private String getRole(WebElement lawyer) {
        By[] byArray = new By[]{
                By.className("header-banner__contentContainer"),
                By.className("header-banner__jobTitle")
        };
        WebElement element = this.siteUtl.iterateOverBy(byArray, lawyer);
        return element.getText();
    }


    //todo: test it latter
    private @Nullable String getCountry(String phone) {
        return this.siteUtl.getCountryBasedInOfficeByPhone(OFFICE_TO_COUNTRY, phone, "");
    }



    private String[] getSocials(WebElement lawyer) {
        String phone = ""; String email = "";
        WebElement div = lawyer.findElement(By.className("wrap-content-center"));

        try {
            phone = div.findElement(cssSelector("p:nth-child(4)")).getText();
            // Remove all non-numeric chars
            phone = phone.replaceAll("\\D", "");
            if (phone.isEmpty()) throw new Exception("Phone number is empty");
        } catch (Exception e) {
            // Some phone isn't in the new page
            phone = "4402077766600";
        }

        email = div
                .findElement(cssSelector("ul > li > a[href^='mailto:']"))
                .getAttribute("href");

        return new String[] { email, phone };
    }


    public Object getLawyer(WebElement lawyer) throws Exception {
        this.openNewTab(lawyer);

        WebElement div = driver.findElement(By.className("wrapper"));

        String[] socials = this.getSocials(div);

        return Map.of(
                "link", Objects.requireNonNull(driver.getCurrentUrl()),
                "name", this.getName(div),
                "role", this.getRole(div),
                "firm", this.name,
                "country", this.getCountry(socials[1]),
                "practice_area", "",
                "email", socials[0],
                "phone", socials[1].isEmpty() ? "" : socials[1]
        );
    }
}
