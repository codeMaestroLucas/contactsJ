package org.example.src.sites.byPage;

import java.time.Duration;
import java.util.List;
import java.util.Map;
import org.example.src.entities.MyDriver;
import org.example.src.entities.BaseSites.ByPage;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

public class CollasCrill extends ByPage {

    public CollasCrill() {
        super("Collas Crill", "https://www.collascrill.com/people/all-locations/partners/all-practices/", 1, 3);
        OFFICE_TO_COUNTRY = Map.of(
                "bvi", "the British Virgin Islands",
                "cayman", "the Cayman Islands",
                "london", "England",
                "guernsey","Guernsey",
                "jersey", "Jersey"
        );
    }

    protected void accessPage(int index) throws InterruptedException {
        this.driver.get(this.link);
        MyDriver.waitForPageToLoad();
        Thread.sleep(1000L);
        if (index <= 0) {
            MyDriver.clickOnElement(By.className("cky-btn-accept"));
        }
    }

    protected List<WebElement> getLawyersInPage() {
        By[] webRole = new By[]{By.className("title")};
        String[] validRoles = new String[]{"partner", "counsel", "senior associate", "chairman"};

        try {
            WebDriverWait wait = new WebDriverWait(this.driver, Duration.ofSeconds(10L));
            List<WebElement> lawyers = (List)wait.until(ExpectedConditions.presenceOfAllElementsLocatedBy(By.className("overlay-contents")));
            return this.siteUtl.filterLawyersInPage(lawyers, webRole, false, validRoles);
        } catch (Exception e) {
            throw new RuntimeException("Failed to find lawyer elements", e);
        }
    }

    private String getLink(WebElement lawyer) {
        By[] byArray = new By[]{By.cssSelector("a")};
        WebElement element = this.siteUtl.iterateOverBy(byArray, lawyer);
        return element.getAttribute("href");
    }

    private String getName(WebElement lawyer) {
        By[] byArray = new By[]{By.className("name-desktop")};
        WebElement element = this.siteUtl.iterateOverBy(byArray, lawyer);
        String textToReturn = element.getText();
        return textToReturn.isEmpty() ? this.siteUtl.getContentFromTag(element) : textToReturn;
    }

    private String getRole(WebElement lawyer) {
        By[] byArray = new By[]{By.className("title")};
        WebElement element = this.siteUtl.iterateOverBy(byArray, lawyer);
        String textToReturn = element.getText();
        return textToReturn.isEmpty() ? this.siteUtl.getContentFromTag(element) : textToReturn;
    }

    private String getCountry(WebElement lawyer) {
        By[] byArray = new By[]{By.className("location")};
        return siteUtl.getCountryBasedInOffice(
            OFFICE_TO_COUNTRY, this.siteUtl.iterateOverBy(byArray, lawyer)
        );
}

    private String[] getSocials(WebElement lawyer) {
        try {
            List<WebElement> socials = lawyer.findElement(By.className("icons")).findElements(By.cssSelector("a"));
            return super.getSocials(socials, false);
        } catch (Exception e) {
            System.err.println("Error getting socials: " + e.getMessage());
            return new String[]{"", ""};
        }
    }

    public Object getLawyer(WebElement lawyer) throws Exception {
        String[] socials = this.getSocials(lawyer);
        return Map.of("link", this.getLink(lawyer), "name", this.getName(lawyer), "role", this.getRole(lawyer), "firm", this.name, "country", this.getCountry(lawyer), "practice_area", "", "email", socials[0], "phone", socials[1]);
    }
}
