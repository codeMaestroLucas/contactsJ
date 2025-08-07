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
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static java.util.Map.entry;

public class WinstonAndStrawn extends ByPage {
    public static final Map<String, String> OFFICE_TO_COUNTRY = Map.ofEntries(
            entry("brussels", "Belgium"),
            entry("charlotte", "USA"),
            entry("chicago", "USA"),
            entry("dallas", "USA"),
            entry("houston", "USA"),
            entry("london", "England"),
            entry("los angeles", "USA"),
            entry("miami", "USA"),
            entry("new york", "USA"),
            entry("paris", "France"),
            entry("san francisco", "USA"),
            entry("sao paulo", "Brazil"),
            entry("silicon valley", "USA"),
            entry("washington dc", "USA")
    );


    private final By[] byRoleArray = {
            By.cssSelector("div.col-sm-2.align-self-baseline.d-none.d-sm-block"),
            By.cssSelector("p")
    };


    public WinstonAndStrawn() {
        super(
            "Winston And Strawn",
            "https://www.winston.com/en/professionals?of=5103%2C278%2C1039905%2C286%2C282&po=1000002%2C1000001",
            3,
            2
        );
    }


    protected void accessPage(int index) throws InterruptedException {
        String otherUrl = "https://www.winston.com/en/professionals?f=" + (index * 20) + "&of=5103%2C278%2C1039905%2C286%2C282&po=1000002%2C1000001";
        String url = index == 0 ? this.link : otherUrl;
        this.driver.get(url);
        MyDriver.waitForPageToLoad();
        Thread.sleep(1000L);

        if (index > 0) return;

        MyDriver.clickOnElement(By.xpath("//*[@id=\"app\"]/section/div/div/div/div/div/div[2]/button"));
    }


    @Override
    protected List<WebElement> getLawyersInPage() {
        try {
            WebDriverWait wait = new WebDriverWait(this.driver, Duration.ofSeconds(10L));
            WebElement lawyersDiv = wait.until(ExpectedConditions.presenceOfElementLocated(By.id("0")));
            return lawyersDiv.findElements(By.className("row"));
        } catch (Exception e) {
            throw new RuntimeException("Failed to find lawyer elements", e);
        }
    }

    private String getLink(WebElement lawyer) {
        By[] byArray = new By[]{
            By.cssSelector("a[href^='/en/professionals/']")
        };
        WebElement element = this.siteUtl.iterateOverBy(byArray, lawyer);
        return element.getAttribute("href");
    }

    private String getName(WebElement lawyer) {
        By[] byArray = new By[]{
            By.cssSelector("a[href^='/en/professionals/']"),
            By.cssSelector("img")
        };
        WebElement element = this.siteUtl.iterateOverBy(byArray, lawyer);
        return element.getAttribute("alt");
    }

    private String getRole(WebElement lawyer) {
        WebElement element = this.siteUtl.iterateOverBy(byRoleArray, lawyer);
        return element.getText();
    }

    private String getCountry(WebElement lawyer) {
        String country = "";

        By[] byArray = new By[]{
                By.cssSelector("a[href^='/en/locations/']")
        };
        WebElement element = this.siteUtl.iterateOverBy(byArray, lawyer);
        String href = element.getAttribute("href");

        assert href != null;

        Matcher matcher = Pattern.compile("/en/locations/([a-z\\-]+)").matcher(href);
        if (matcher.find()) {
            country = matcher.group(1);
        }

        return siteUtl.getCountryBasedInOffice(OFFICE_TO_COUNTRY, country);
    }

    private String[] getSocials(WebElement lawyer) {
        try {
            List<WebElement> socials = lawyer.findElements(By.cssSelector("a"));
            return super.getSocials(socials, false);
        } catch (Exception e) {
            System.err.println("Error getting socials: " + e.getMessage());
            return new String[]{"", ""};
        }
    }

    @Override
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
                "phone", socials[1].replaceFirst("2", "")
        );
    }
}
