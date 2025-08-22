package org.example.src.sites.byNewPage;

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

public class Frontier extends ByNewPage {
    public static final Map<String, String> OFFICE_TO_COUNTRY = Map.ofEntries(
            entry("32", "Belgium"),
            entry("33", "Paris"),
            entry("34", "Spain"),
            entry("44", "England"),
            entry("49", "Germany"),
            entry("353", "Ireland")
    );


    public Frontier() {
        super(
            "Frontier",
            "https://www.frontier-economics.com/uk/en/about/people/",
            34,
            2
        );
    }


    @Override
    protected void accessPage(int index) throws InterruptedException {
        String otherUrl = "https://www.frontier-economics.com/uk/en/about/people/?page=" + (index + 1);
        String url = (index == 0) ? this.link : otherUrl;
        driver.get(url);
        MyDriver.waitForPageToLoad();
        Thread.sleep(1000);

        if (index > 0) return;

        // Click on add btn
        MyDriver.clickOnElement(By.id("onetrust-accept-btn-handler"));
    }


    @Override
    protected List<WebElement> getLawyersInPage() {
        By[] webRole = {
                By.className("person-item-role"),
                By.cssSelector("span")
        };

        String[] validRoles = {
                "director",
                "manager",
                "senior associate"
        };

        try {
            // Wait up to 10 seconds for elements to be present
            WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(10));
            List<WebElement> lawyers = wait.until(
                    ExpectedConditions.presenceOfAllElementsLocatedBy(By.className("person-item"))
            );

            return siteUtl.filterLawyersInPage(lawyers, webRole, false, validRoles);

        } catch (Exception e) {
            throw new RuntimeException("Failed to find lawyer elements", e);
        }
    }

    @Override
    public void openNewTab(WebElement lawyer) {
        MyDriver.openNewTab(lawyer.getAttribute("href"));
    }


    private String getName(WebElement lawyer) {
        By[] byArray = {
                By.className("person-profile-banner-name")
        };
        WebElement element = siteUtl.iterateOverBy(byArray, lawyer);
        return element.getText();
    }


    private String getRole(WebElement lawyer) {
        By[] byArray = {
                By.className("person-profile-banner-title")
        };
        WebElement element = siteUtl.iterateOverBy(byArray, lawyer);
        return element.getText();
    }


    private Object getCountry(String phone) {
        // Remove all non-numeric chars
        phone = phone.replaceAll("\\D", "");

        for (String key : OFFICE_TO_COUNTRY.keySet()) {
            if (phone.startsWith(key)) {
                return OFFICE_TO_COUNTRY.get(key);
            }
        }

        return phone;
    }


    private String[] getSocials(WebElement lawyer) {
        String email = ""; String phone = "";
        WebElement socialsDiv = lawyer.findElement(By.className("banner-contact-details"));

        phone = socialsDiv.findElement(By.cssSelector("li")).getText();
        email = socialsDiv.findElement(By.cssSelector("a[href^='mailto:']")).getAttribute("href");

        return new String[] { email, phone };
    }


    @Override
    public Object getLawyer(WebElement lawyer) throws Exception {
        this.openNewTab(lawyer);

        WebElement div = driver.findElement(By.className("person-profile-banner-contact-details"));
        String[] socials = this.getSocials(div);

        return Map.of(
                "link", Objects.requireNonNull(driver.getCurrentUrl()),
                "name", this.getName(div),
                "role", this.getRole(div),
                "firm", this.name,
                "country", this.getCountry(socials[1]),
                "practice_area", "",
                "email", socials[0],
                "phone", socials[1]
        );
    }
}
