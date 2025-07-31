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

        if (index > 0) return;
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


    private String getLink(WebElement lawyer) {
        By[] byArray = new By[]{
            By.cssSelector("a")
        };
        WebElement element = this.siteUtl.iterateOverBy(byArray, lawyer);
        return element.getAttribute("href");
    }


    private String getName(WebElement lawyer) {
        By[] byArray = new By[]{
            By.className("info"),
            By.className("t")
        };
        WebElement element = this.siteUtl.iterateOverBy(byArray, lawyer);
        return element.getText();
    }


    private String getCountry(WebElement lawyer) {
        By[] byArray = new By[]{
            By.className("info"),
            By.className("ds"),
            By.cssSelector("dl:last-child > dd")
        };
        WebElement element = this.siteUtl.iterateOverBy(byArray, lawyer);
        return element.getText().toLowerCase().contains("hong kong") ? "Hong Kong" : "China";
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

            return new String[] { email, phone };

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
            "phone", socials[1]);
    }
}
