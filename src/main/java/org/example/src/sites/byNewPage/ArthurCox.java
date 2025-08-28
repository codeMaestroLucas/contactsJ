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

public class ArthurCox extends ByNewPage {
    public static final Map<String, String> OFFICE_TO_COUNTRY = Map.of(
            "belfast", "England",
            "dublin", "Ireland",
            "london", "England",
            "new york", "USA"
    );

    private final By[] byRoleArray = {
            By.className("search-card-subtitle")
    };


    public ArthurCox() {
        super(
            "Arthur Cox",
            "https://www.arthurcox.com/people/?term=/#search-section",
            20,
            2
        );
    }


    protected void accessPage(int index) throws InterruptedException {
        String otherUrl = "https://www.arthurcox.com/people/?term=&offset=" + index + "/#search-section";
        String url = index == 0 ? this.link : otherUrl;
        this.driver.get(url);
        MyDriver.waitForPageToLoad();
        Thread.sleep(1000L);

        if (index > 0) return;

        // Click on add btn
        MyDriver.clickOnElement(By.id("onetrust-accept-btn-handler"));
    }


    protected List<WebElement> getLawyersInPage() {
        String[] validRoles = new String[]{
                "partner",
                "counsel",
                "director",
                "senior associate"
        };

        try {
            WebDriverWait wait = new WebDriverWait(this.driver, Duration.ofSeconds(10L));

            List<WebElement> lawyers = wait.until(
                    ExpectedConditions.presenceOfAllElementsLocatedBy(
                            By.className("search-result-link")
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
                By.className("h2")
        };
        WebElement element = this.siteUtl.iterateOverBy(byArray, lawyer);
        return element.getText();
    }


    private String getRole(WebElement lawyer) {
        By[] byArray = new By[]{
                By.className("sub-header")
        };
        WebElement element = this.siteUtl.iterateOverBy(byArray, lawyer);
        return element.getText().split("\\|")[0];
    }


    private String getCountry(WebElement lawyer) {
        By[] byArray = new By[]{
                By.className("sub-header")
        };
        WebElement element = this.siteUtl.iterateOverBy(byArray, lawyer);
        String country = element.getText().split("\\|")[1];
        return siteUtl.getCountryBasedInOffice(OFFICE_TO_COUNTRY, country, "");
    }


    private String getPracticeArea() {
        try {
            WebElement div = driver.findElement(By.cssSelector("div.o-container:nth-child(2)"));
            By[] byArray = new By[]{
                    By.className("a[href^='https://www.arthurcox.com/services/']")
            };
            WebElement element = this.siteUtl.iterateOverBy(byArray, div);
            return element.getText();
        } catch (Exception e) {
            return "";
        }
    }


    private String[] getSocials(WebElement lawyer) {
        try {
            List<WebElement> socials = lawyer
                        .findElement(By.cssSelector("div.o-content.background-white"))
                        .findElements(By.cssSelector("a"));
            return super.getSocials(socials, false);

        } catch (Exception e) {
            System.err.println("Error getting socials: " + e.getMessage());
            return new String[]{"", ""};
        }
    }


    public Object getLawyer(WebElement lawyer) throws Exception {
        this.openNewTab(lawyer);

        WebElement div = driver.findElement(By.className("m-header--person__content"));

        String[] socials = this.getSocials(div);

        return Map.of(
            "link", Objects.requireNonNull(driver.getCurrentUrl()),
            "name", this.getName(div),
            "role", this.getRole(div),
            "firm", this.name,
            "country", this.getCountry(div),
            "practice_area", "",
            "email", socials[0],
            "phone", socials[1].isEmpty() ? "" : socials[1]
        );
    }
}
