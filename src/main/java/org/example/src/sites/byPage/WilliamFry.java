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

public class WilliamFry extends ByPage {
    public static final Map<String, String> OFFICE_TO_COUNTRY = Map.of(
            "cork", "Ireland",
            "dublin head office", "Ireland",
            "dublin", "Ireland",
            "london", "England",
            "new york", "USA",
            "san francisco", "USA"
    );


    private final By[] byRoleArray = {
            By.className("role")
    };


    public WilliamFry() {
        super(
            "William Fry",
            "https://www.williamfry.com/search-people/",
            1,
            2
        );
    }


    protected void accessPage(int index) throws InterruptedException {
        this.driver.get(this.link);
        MyDriver.waitForPageToLoad();
        Thread.sleep(1000L);

        MyDriver.clickOnElement(By.id("onetrust-accept-btn-handler"));

        String loadMoreButtonTxt = driver.findElement(By.className("load_more_button")).getText();
        int timesToRollDown = Integer.parseInt(loadMoreButtonTxt.replaceAll("[^0-9]", "")) / 10;

        for (int i = 0; i < timesToRollDown; i++) {
            try {
                MyDriver.clickOnElement(By.className("load_more_button"));
                Thread.sleep(1000L);

            } catch (Exception e) {
                break;
            }
        }
    }


    protected List<WebElement> getLawyersInPage() {
        String[] validRoles = new String[]{
                "partner",
                "chairperson",
                "managing associate",
                "senior associate",
        };

        try {
            WebDriverWait wait = new WebDriverWait(this.driver, Duration.ofSeconds(10L));

            List<WebElement> lawyers = wait.until(
                    ExpectedConditions.presenceOfAllElementsLocatedBy(
                            By.className("content")
                    )
            );
            return this.siteUtl.filterLawyersInPage(lawyers, byRoleArray, true, validRoles);

        } catch (Exception e) {
            throw new RuntimeException("Failed to find lawyer elements", e);
        }
    }


    private String getLink(WebElement lawyer) {
        By[] byArray = new By[]{
                By.cssSelector("a")
        };
        WebElement element = this.siteUtl.iterateOverBy(byArray, lawyer);
        return element.getAttribute("href");
    }


    private String getName(WebElement lawyer) {
        By[] byArray = new By[]{
                By.cssSelector("a"),
                By.className("title"),
        };
        WebElement element = this.siteUtl.iterateOverBy(byArray, lawyer);
        return element.getText();
    }


    private String getRole(WebElement lawyer) {
        WebElement element = this.siteUtl.iterateOverBy(byRoleArray, lawyer);
        return element.getText();
    }


    private String getCountry(WebElement lawyer) {
        try {
            By[] byArray = new By[]{
                    By.className("contact_details"),
                    By.className("location"),
                    By.className("list__common__link")
            };
            WebElement element = this.siteUtl.iterateOverBy(byArray, lawyer);
            String country = this.siteUtl.getContentFromTag(element).split("-")[0].trim();
            return siteUtl.getCountryBasedInOffice(OFFICE_TO_COUNTRY, country, "");

        } catch (Exception e) {
            System.err.println(e.getMessage());
            return "";
        }
    }


    private String[] getSocials(WebElement lawyer) {
        try {
            List<WebElement> socials = lawyer
                        .findElement(By.className("contact_details"))
                        .findElements(By.cssSelector("a"));
            return super.getSocials(socials, false);

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
            "practice_area", "",
            "email", socials[0],
            "phone", socials[1]
        );
    }
}
