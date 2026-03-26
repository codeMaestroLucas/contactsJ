package org.example.src.sites.byPage;

import org.example.exceptions.LawyerExceptions;
import org.example.src.entities.BaseSites.ByPage;
import org.example.src.entities.MyDriver;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.time.Duration;
import java.util.List;
import java.util.Map;

public class HakunLaw extends ByPage {
    public HakunLaw() {
        super(
                "Hakun Law",
                "https://www.hankunlaw.com/en/portal/list/index/id/2.html?city=&zw=9%2C10%2C11&ly=#detail",
                23
        );
    }


    protected void accessPage(int index) throws InterruptedException {
        String otherUrl = "https://www.hankunlaw.com/en/portal/list/index/id/2.html?city=&zw=9%2C10%2C11&ly=&page=" + (index + 1);
        String url = index == 0 ? this.link : otherUrl;
        this.driver.get(url);
        MyDriver.waitForPageToLoad();
        Thread.sleep(1000L);
    }


    protected List<WebElement> getLawyersInPage() {
        try {
            WebDriverWait wait = new WebDriverWait(this.driver, Duration.ofSeconds(10L));
            return wait.until(
                    ExpectedConditions.presenceOfAllElementsLocatedBy(
                            By.className("law-list")
                    )
            );
        } catch (Exception e) {
            throw new RuntimeException("Failed to find lawyer elements", e);
        }
    }


    public String getLink(WebElement lawyer) throws LawyerExceptions {
        By[] byArray = new By[]{
                By.cssSelector("a")
        };
        return extractor.extractLawyerAttribute(lawyer, byArray, "LINK", "href", LawyerExceptions::linkException);
    }


    private String getName(WebElement lawyer) throws LawyerExceptions {
        By[] byArray = new By[]{
                By.className("info"),
                By.className("t")
        };
        return extractor.extractLawyerText(lawyer, byArray, "NAME", LawyerExceptions::nameException);
    }


    private String getCountry(WebElement lawyer) throws LawyerExceptions {
        By[] byArray = new By[]{
                By.className("info"),
                By.className("ds"),
                By.cssSelector("dl:last-child > dd")
        };
        String office = extractor.extractLawyerText(lawyer, byArray, "COUNTRY", LawyerExceptions::countryException);
        return office.toLowerCase().contains("hong kong") ? "Hong Kong" : "China";
    }


    private String[] getSocials(WebElement lawyer) {
        try {
            String email = lawyer.findElement(By.className("info"))
                    .findElement(By.className("ds"))
                    .findElement(By.cssSelector("dl:first-child > dd"))
                    .getText();

            String phone = lawyer.findElement(By.className("info"))
                    .findElement(By.className("ds"))
                    .findElement(By.cssSelector("dl:nth-child(2) > dd"))
                    .getText();

            return new String[]{email, phone};
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
                "role", "Partner",
                "firm", this.name,
                "country", this.getCountry(lawyer),
                "practice_area", "",
                "email", socials[0],
                "phone", socials[1]
        );
    }
}