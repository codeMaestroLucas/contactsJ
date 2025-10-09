package org.example.src.sites._standingBy.otherIsuues;

import org.example.src.entities.BaseSites.ByNewPage;
import org.example.src.entities.MyDriver;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.interactions.Actions;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.time.Duration;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import static java.util.Map.entry;

/**
 * Transform in NewPage
 */
public class GideLoyretteNouel extends ByNewPage {
    public static final Map<String, String> OFFICE_TO_COUNTRY = Map.ofEntries(
            entry("algiers", "Algeria"),
            entry("brussels", "Belgium"),
            entry("casablanca", "Morocco"),
            entry("istanbul", "Turkey"),
            entry("london", "England"),
            entry("new york city", "USA"),
            entry("paris", "France"),
            entry("shanghai", "China"),
            entry("tunis", "Tunisia"),
            entry("warsaw", "Poland")
    );

    public GideLoyretteNouel() {
        super(
            "Gide Loyrette Nouel",
            "https://www.gide.com/en/lawyers/?",
            38,
            19999
        );
    }


    @Override
    protected void accessPage(int index) throws InterruptedException {
        driver.get(this.link);
        MyDriver.waitForPageToLoad();
        Thread.sleep(1000);

        WebElement elementToClick;


        // Click on filter to load lawyers
        if (index == 0) {
            MyDriver.clickOnElement(By.id("onetrust-accept-btn-handler"));

            elementToClick = driver.findElement(By.className("filters"))
                    .findElement(By.className("list"))
                    .findElement(By.cssSelector("li:last-child"));
            MyDriver.clickOnElement(elementToClick);

            Thread.sleep(2000);

            elementToClick = driver.findElement(By.id("filter-offices"))
                    .findElement(By.className("inner"))
                    .findElement(By.cssSelector("ul > li"));
            MyDriver.clickOnElement(elementToClick);

            Thread.sleep(5000);

        } else { // Click on next page
            //todo: fix this
            WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(10));

            WebElement nextBtn = wait.until(
                    ExpectedConditions.elementToBeClickable(By.cssSelector(".pagination .next"))
            );
            Actions actions = new Actions(driver);
            actions.moveToElement(nextBtn).pause(Duration.ofMillis(200)).click().perform();

        }

    }


    @Override
    protected List<WebElement> getLawyersInPage() {

        By[] webRole = {
                By.className("content"),
                By.className("function"),
        };

        String[] validRoles = new String[]{
                "partner",
                "counsel"
        };

        try {
            // Wait up to 10 seconds for elements to be present
            WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(10));
            List<WebElement> lawyers = wait.until(
                    ExpectedConditions.presenceOfAllElementsLocatedBy(By.className("card-avocat"))
            );

             return null;
//            return siteUtl.filterLawyersInPage(lawyers, webRole, false, validRoles);

        } catch (Exception e) {
            throw new RuntimeException("Failed to find lawyer elements", e);
        }
    }


    @Override
    public void openNewTab(WebElement lawyer) {
        MyDriver.openNewTab(lawyer.findElement(By.cssSelector("a")).getAttribute("href"));
    }


    private String getName(WebElement lawyer) {
        By[] byArray = {
                By.className("infos"),
                By.className("title-1")
        };
        WebElement element = siteUtl.iterateOverBy(byArray, lawyer);
        return element.getText();
    }


    private String getRole(WebElement lawyer) {
        By[] byArray = {
                By.className("infos"),
                By.className("type")
        };
        WebElement element = siteUtl.iterateOverBy(byArray, lawyer);
        return element.getText();
    }


    private String getCountry(WebElement lawyer) {
        By[] byArray = {
                By.cssSelector("div.part > div.rte > p")
        };
        WebElement element = siteUtl.iterateOverBy(byArray, lawyer);
        String country = element.getText().split("\n")[0].trim();
        return siteUtl.getCountryBasedInOffice(OFFICE_TO_COUNTRY, country, "");
    }


    private String getPracticeArea(WebElement lawyer) {
        By[] byArray = {
                By.cssSelector("a[href^='https://www.gide.com/en/practices/']")
        };
        WebElement element = siteUtl.iterateOverBy(byArray, lawyer);
        return element.getText();
    }


    private String[] getSocials(WebElement lawyer) {
        try {
            List<WebElement> socials = lawyer
                    .findElement(By.className("contact"))
                    .findElements(By.cssSelector("a"));
            return super.getSocials(socials, false);

        } catch (Exception e) {
            System.err.println("Error getting socials: " + e.getMessage());
            return new String[]{"", ""};
        }
    }


    @Override
    public Object getLawyer(WebElement lawyer) throws Exception {
        this.openNewTab(lawyer);

        WebElement div = driver.findElement(By.className("text"));

        String[] socials = this.getSocials(div);

        return Map.of(
                "link", Objects.requireNonNull(driver.getCurrentUrl()),
                "name", this.getName(div),
                "role", this.getRole(div),
                "firm", this.name,
                "country", this.getCountry(div),
                "practice_area", this.getPracticeArea(div),
                "email", socials[0],
                "phone", socials[1]
        );
    }
}
