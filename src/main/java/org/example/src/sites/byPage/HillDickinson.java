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

public class HillDickinson extends ByPage {
    public static final Map<String, String> OFFICE_TO_COUNTRY = Map.ofEntries(
            entry("birmingham", "England"),
            entry("hong kong", "Hong Kong"),
            entry("leeds", "England"),
            entry("limassol", "Cyprus"),
            entry("liverpool", "England"),
            entry("london (city)", "England"),
            entry("london (knightsbridge)", "England"),
            entry("manchester", "England"),
            entry("monaco", "Monaco"),
            entry("newcastle", "England"),
            entry("piraeus", "Greece"),
            entry("singapore", "Singapore")
    );

    private final By[] byRoleArray = {
            By.cssSelector("div > div.u-flex-grow > div:first-child > div")
    };


    public HillDickinson() {
        super(
            "Hill Dickinson",
            "https://www.hilldickinson.com/people?title=&position%5B%5D=31&position%5B%5D=95&position%5B%5D=6492&position%5B%5D=118&position%5B%5D=6364",
            13,
            2
        );
    }


    protected void accessPage(int index) throws InterruptedException {
        String otherUrl = "https://www.hilldickinson.com/people?title=&position%5B31%5D=31&position%5B95%5D=95&position%5B6492%5D=6492&position%5B118%5D=118&position%5B6364%5D=6364&page=" + index;
        String url = index == 0 ? this.link : otherUrl;
        this.driver.get(url);
        MyDriver.waitForPageToLoad();
        Thread.sleep(1000L);

        if (index > 0) return;

        this.siteUtl.clickOnAddBtn(By.id("ccc-recommended-settings"));
    }


    protected List<WebElement> getLawyersInPage() {
        try {
            WebDriverWait wait = new WebDriverWait(this.driver, Duration.ofSeconds(10L));

            return wait.until(
                    ExpectedConditions.presenceOfAllElementsLocatedBy(
                            By.className("card__body")
                    )
            );

        } catch (Exception e) {
            throw new RuntimeException("Failed to find lawyer elements", e);
        }
    }


    private String getLink(WebElement lawyer) {
        By[] byArray = new By[]{
                By.cssSelector("div > h2 > a")
        };
        WebElement element = this.siteUtl.iterateOverBy(byArray, lawyer);
        return element.getAttribute("href");
    }


    private String getName(WebElement lawyer) {
        By[] byArray = new By[]{
                By.cssSelector("div > h2 > a")
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
                By.cssSelector("div > div.u-flex-grow > div.u-mt-1.u-mb-2 > div > div")
        };
        WebElement element = this.siteUtl.iterateOverBy(byArray, lawyer);
        return siteUtl.getCountryBasedInOffice(OFFICE_TO_COUNTRY, element.getText());
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
