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

public class RopesAndGray extends ByPage {
    public static final Map<String, String> OFFICE_TO_COUNTRY = Map.ofEntries(
            entry("boston", "USA"),
            entry("chicago", "USA"),
            entry("dublin", "Ireland"),
            entry("hong kong", "Hong Kong"),
            entry("london", "England"),
            entry("los angeles", "USA"),
            entry("new york", "USA"),
            entry("paris", "France"),
            entry("san francisco", "USA"),
            entry("seoul", "Korea (South)"),
            entry("silicon valley", "USA"),
            entry("singapore", "Singapore"),
            entry("tokyo", "Japan"),
            entry("washington dc", "USA")
    );


    private final By[] byRoleArray = {
            By.className("BaseContactCard_contact-card__title__3C_q2")
    };


    public RopesAndGray() {
        super(
                "Ropes And Gray",
                "https://www.ropesgray.com/en/people?offices=46cc9841-9e34-4565-87b2-c4a2042a1721&offices=284963e5-38ec-45fb-87d0-99b21caebd21&offices=f3d1a787-ec2c-4c9b-85ea-0e2a31a295e7&offices=b5a2fb42-35c2-441b-868f-23707ac10631&offices=0e008de0-c344-4e56-9126-8426580d8ab5&offices=e4299641-e33e-4ba2-abb0-702e9ed9aec9&offices=acb21c29-c05e-40e2-b68e-81e3bc3df395&page=16",
                1,
                3
        );
    }


    protected void accessPage(int index) throws InterruptedException {
        this.driver.get(this.link);
        MyDriver.waitForPageToLoad();
        Thread.sleep(1000L);

        MyDriver.clickOnAddBtn(By.id("onetrust-accept-btn-handler"));
    }


    protected List<WebElement> getLawyersInPage() {
        String[] validRoles = new String[]{
                "partner",
                "counsel",
                "senior associate"
        };

        try {
            WebDriverWait wait = new WebDriverWait(this.driver, Duration.ofSeconds(10L));

            List<WebElement> lawyers = wait.until(
                    ExpectedConditions.presenceOfAllElementsLocatedBy(
                            By.className("BaseContactCard_contact-card__content-inner__qiCTT")
                    )
            );
            return this.siteUtl.filterLawyersInPage(lawyers, byRoleArray, true, validRoles);

        } catch (Exception e) {
            throw new RuntimeException("Failed to find lawyer elements", e);
        }
    }


    private String getLink(WebElement lawyer) {
        By[] byArray = new By[]{
                By.className("BaseContactCard_contact-card__name__0tdJy"),
                By.cssSelector("a")
        };
        WebElement element = this.siteUtl.iterateOverBy(byArray, lawyer);
        return element.getAttribute("href");
    }


    private String getName(WebElement lawyer) {
        By[] byArray = new By[]{
                By.className("BaseContactCard_contact-card__name__0tdJy"),
                By.cssSelector("a")
        };
        WebElement element = this.siteUtl.iterateOverBy(byArray, lawyer);
        return element.getText();
    }


    private String getRole(WebElement lawyer) {
        WebElement element = this.siteUtl.iterateOverBy(byRoleArray, lawyer);
        return element.getText();
    }


    private String getCountry(WebElement lawyer) {
        By[] byArray = new By[]{
                By.className("BaseContactCard_contact-card__details__wApVI"),
                By.cssSelector("span")
        };
        return siteUtl.getCountryBasedInOffice(
                OFFICE_TO_COUNTRY, this.siteUtl.iterateOverBy(byArray, lawyer)
        );
    }


    private String[] getSocials(WebElement lawyer) {
        try {
            List<WebElement> socials = lawyer
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
                "phone", socials[1]);
    }
}