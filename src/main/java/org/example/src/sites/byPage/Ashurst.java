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

public class Ashurst extends ByPage {
    public Ashurst() {
        super("Ashurst", "https://www.ashurst.com/en/people/#e=0", 131, 3);
    }

    protected void accessPage(int index) throws InterruptedException {
        String otherUrl = "https://www.ashurst.com/en/people/#e=" + 10 * index;
        String url = index == 0 ? this.link : otherUrl;
        this.driver.get(url);
        MyDriver.waitForPageToLoad();
        Thread.sleep(1000L);
        if (index <= 0) {
            MyDriver.clickOnElement(By.xpath("//*[@id=\"termsfeed-com---nb\"]/div/div[3]/button[1]"));
        }
    }

    protected List<WebElement> getLawyersInPage() {
        By[] webRole = new By[]{By.className("people-info")};
        String[] validRoles = new String[]{"partner", "counsel", "senior associate"};

        try {
            WebDriverWait wait = new WebDriverWait(this.driver, Duration.ofSeconds(10L));
            List<WebElement> lawyers = (List)wait.until(ExpectedConditions.presenceOfAllElementsLocatedBy(By.className("profile-card__info")));
            return this.siteUtl.filterLawyersInPage(lawyers, webRole, true, validRoles);
        } catch (Exception e) {
            throw new RuntimeException("Failed to find lawyer elements", e);
        }
    }

    private String getLink(WebElement lawyer) {
        By[] byArray = new By[]{By.className("people-info"), By.cssSelector("a")};
        WebElement element = this.siteUtl.iterateOverBy(byArray, lawyer);
        return element.getAttribute("href");
    }

    private String getName(WebElement lawyer) {
        By[] byArray = new By[]{By.className("people-info"), By.cssSelector("a")};
        WebElement element = this.siteUtl.iterateOverBy(byArray, lawyer);
        return element.getAttribute("title");
    }

    private String getRole(WebElement lawyer) {
        By[] byArray = new By[]{By.className("people-info")};
        String text = this.siteUtl.iterateOverBy(byArray, lawyer).getText();
        return text.split("\\n")[1];
    }

    private String getCountry(WebElement lawyer) {
        Map<String, String> countries = Map.ofEntries(Map.entry("abu dhabi", "the UAE"), Map.entry("austin", "EUA"), Map.entry("beijing", "China"), Map.entry("brisbane", "Australia"), Map.entry("brisbane - ann st", "Australia"), Map.entry("brussels", "Belgium"), Map.entry("dubai", "the UAE"), Map.entry("dublin", "Ireland"), Map.entry("frankfurt", "Germany"), Map.entry("glasgow", "England"), Map.entry("hong kong", "Hong Kong"), Map.entry("jakarta", "Indonesia"), Map.entry("jeddah", "Saudi Arabia"), Map.entry("london", "England"), Map.entry("los angeles", "EUA"), Map.entry("luxembourg", "Luxembourg"), Map.entry("madrid", "Spain"), Map.entry("melbourne", "Australia"), Map.entry("milan", "Italy"), Map.entry("munich", "Germany"), Map.entry("new york", "EUA"), Map.entry("paris", "France"), Map.entry("perth", "Australia"), Map.entry("port moresby", "Papua New Guinea"), Map.entry("riyadh", "the UAE"), Map.entry("seoul", "Korea (Sout)"), Map.entry("shanghai", "China"), Map.entry("singapore", "Singapore"), Map.entry("sydney", "Australia"), Map.entry("tokyo", "Japan"));
        By[] byArray = new By[]{By.className("people-info"), By.className("profile-location")};
        String country = this.siteUtl.iterateOverBy(byArray, lawyer).getText();
        return (String)countries.getOrDefault(country.toLowerCase(), "");
    }

    private String[] getSocials(WebElement lawyer) {
        try {
            List<WebElement> socials = lawyer.findElement(By.className("profile-contact")).findElements(By.cssSelector("a"));
            return super.getSocials(socials, false);
        } catch (Exception e) {
            System.err.println("Error getting socials: " + e.getMessage());
            return new String[]{"", ""};
        }
    }

    public Object getLawyer(WebElement lawyer) throws Exception {
        String[] socials = this.getSocials(lawyer);
        return Map.of("link", this.getLink(lawyer), "name", this.getName(lawyer), "role", this.getRole(lawyer), "firm", this.name, "country", this.getCountry(lawyer), "practice_area", "", "email", socials[0], "phone", socials[1]);
    }
}
