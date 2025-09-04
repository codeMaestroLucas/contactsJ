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

public class DaviesWardPhillipsAndVineberg extends ByNewPage {
    private final By[] byRoleArray = {
            By.className("link--secondary")
    };

    String[] validRoles = new String[]{
            "partner",
            "counsel"
    };

    public DaviesWardPhillipsAndVineberg() {
        super(
                "Davies Ward Phillips And Vineberg",
                "https://www.dwpv.com/our-people?page=6",
                38
        );
    }


    protected void accessPage(int index) throws InterruptedException {
        // The initial link starts at page 6, so we adjust the index accordingly.
        String url = "https://www.dwpv.com/our-people?page=" + (index + 6);
        this.driver.get(url);
        MyDriver.waitForPageToLoad();
        Thread.sleep(1000L);

        if (index > 0) return;

        // Click on add btn
        MyDriver.clickOnElement(By.id("CybotCookiebotDialogBodyLevelButtonLevelOptinAllowAll"));
    }


    protected List<WebElement> getLawyersInPage() {
        // Filtering by COUNTRY
        String[] validOffices = new String[]{
                "toronto",
                "montréal",
                "montreal"
        };

        try {
            WebDriverWait wait = new WebDriverWait(this.driver, Duration.ofSeconds(10L));

            List<WebElement> lawyers = wait.until(
                    ExpectedConditions.presenceOfAllElementsLocatedBy(
                            By.className("team-landing__results-item")
                    )
            );
            return this.siteUtl.filterLawyersInPage(lawyers, byRoleArray, false, validOffices);

        } catch (Exception e) {
            throw new RuntimeException("Failed to find lawyer elements", e);
        }
    }


    public void openNewTab(WebElement lawyer) {
        try {
            By[] byArray = {By.cssSelector("a")};
            String link = extractor.extractLawyerAttribute(lawyer, byArray, "LINK", "href", LawyerExceptions::linkException);
            MyDriver.openNewTab(link);
        } catch (LawyerExceptions e) {
            System.err.println("Failed to open new tab: " + e.getMessage());
        }
    }

    public String getLink() {
        return driver.getCurrentUrl();
    }

    private String getName(WebElement lawyer) throws LawyerExceptions {
        By[] byArray = {By.className("people-detail-masthead__full-name")};
        return extractor.extractLawyerAttribute(lawyer, byArray, "NAME", "textContent", LawyerExceptions::nameException);
    }


    private String getRole(WebElement lawyer) throws LawyerExceptions {
        By[] byArray = {By.className("people-detail-masthead__location-and-title")};
        String role = extractor.extractLawyerAttribute(lawyer, byArray, "ROLE", "outerHTML", LawyerExceptions::roleException);
        boolean validPosition = siteUtl.isValidPosition(role, validRoles);
        return validPosition ? role : "Invalid Role";

    }


    private String[] getSocials(WebElement lawyer) {
        try {
            List<WebElement> socials = lawyer
                    .findElement(By.className("people-detail-masthead__contact-container"))
                    .findElements(By.cssSelector("a"));
            return super.getSocials(socials, false);

        } catch (Exception e) {
            System.err.println("Error getting socials: " + e.getMessage());
            return new String[]{"", ""};
        }
    }


    public Object getLawyer(WebElement lawyer) throws Exception {

        this.openNewTab(lawyer);

        WebElement div = driver.findElement(By.className("people-detail-masthead__info-container"));

        String role = this.getRole(div);

        if (role.equals("Invalid Role")) return "Invalid Role";

        String[] socials = this.getSocials(div);
        return Map.of(
                "link", this.getLink(),
                "name", this.getName(div),
                "role", role,
                "firm", this.name,
                "country", "Canada",
                "practice_area", "",
                "email", socials[0],
                "phone", socials[1].isEmpty() ? "4168630900" : socials[1]
        );
    }
}