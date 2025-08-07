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

public class ApplebyGlobal extends ByPage {
    public ApplebyGlobal() {
        super("Appleby Global", "https://www.applebyglobal.com/people/page/1/", 9, 3);
    }

    protected void accessPage(int index) throws InterruptedException {
        String otherUrl = "https://www.applebyglobal.com/people/page/" + (index + 1) + "/";
        String url = index == 0 ? this.link : otherUrl;
        this.driver.get(url);
        MyDriver.waitForPageToLoad();
        Thread.sleep(1000L);
        if (index <= 0) {
            MyDriver.clickOnElement(By.id("onetrust-accept-btn-handler"));
        }
    }

    protected List<WebElement> getLawyersInPage() {
        By[] webRole = new By[]{By.cssSelector(".u-font-size-12.u-font-weight-normal.u-uppercase.u-letter-spacing-supersmall.u-margin-bottom-10")};
        String[] validRoles = new String[]{"partner", "counsel", "senior associate"};

        try {
            WebDriverWait wait = new WebDriverWait(this.driver, Duration.ofSeconds(10L));
            List<WebElement> lawyers = (List)wait.until(ExpectedConditions.presenceOfAllElementsLocatedBy(By.className("grid-item__content")));
            return this.siteUtl.filterLawyersInPage(lawyers, webRole, true, validRoles);
        } catch (Exception e) {
            throw new RuntimeException("Failed to find lawyer elements", e);
        }
    }

    private String getLink(WebElement lawyer) {
        By[] byArray = new By[]{By.cssSelector("a.u-decoration-none")};
        return this.siteUtl.iterateOverBy(byArray, lawyer).getAttribute("href");
    }

    private String getName(WebElement lawyer) {
        By[] byArray = new By[]{By.cssSelector("a.u-decoration-none")};
        return this.siteUtl.iterateOverBy(byArray, lawyer).getText();
    }

    private String getRole(WebElement lawyer) {
        By[] byArray = new By[]{By.cssSelector(".u-font-size-12.u-font-weight-normal.u-uppercase.u-letter-spacing-supersmall.u-margin-bottom-10")};
        return this.siteUtl.iterateOverBy(byArray, lawyer).getText();
    }

    private String getCountry(WebElement lawyer) {
        By[] byArray = new By[]{By.cssSelector(".u-font-size-12.u-font-weight-normal.u-uppercase.u-letter-spacing-supersmall.u-margin-bottom-10"), By.cssSelector("a.u-decoration-none")};
        String country = this.siteUtl.iterateOverBy(byArray, lawyer).getText();
        if (country.trim().equalsIgnoreCase("bvi")) {
            return "the British Virgin Islands";
        } else {
            StringBuilder countryToReturn = new StringBuilder();

            for(String word : country.split(" ")) {
                countryToReturn.append(word.charAt(0)).append(word.toLowerCase().substring(1)).append(" ");
            }

            return countryToReturn.toString();
        }
    }

    private String[] getSocials(WebElement lawyer) {
        try {
            List<WebElement> socials = lawyer.findElements(By.cssSelector("p.u-font-size-14.u-line-height-22 > a"));
            return super.getSocials(socials, false);
        } catch (Exception e) {
            System.err.println("Error getting socials: " + e.getMessage());
            return new String[]{"", ""};
        }
    }

    public Object getLawyer(WebElement lawyer) {
        String[] socials = this.getSocials(lawyer);
        return Map.of("link", this.getLink(lawyer), "name", this.getName(lawyer), "role", this.getRole(lawyer), "firm", this.name, "country", this.getCountry(lawyer), "practice_area", "", "email", socials[0].replace("?subject=website%20enquiry", ""), "phone", socials[1]);
    }
}
