package org.example.src.sites.byPage;

import java.time.Duration;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Random;
import org.example.src.entities.MyDriver;
import org.example.src.entities.BaseSites.ByPage;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

public class DechertLLP extends ByPage {
    private String currentCountry = "Belgium";
    private static final Map<String, String> LINK_COUNTRY_MAP = Map.of("https://www.dechert.com/people-search.html?cq=262#office=Dubai", "the UAE", "https://www.dechert.com/people-search.html?cq=262#office=Dublin", "Ireland", "https://www.dechert.com/people-search.html?cq=262#office=London", "England", "https://www.dechert.com/people-search.html?cq=262#office=Luxembourg", "Luxembourg", "https://www.dechert.com/people-search.html?cq=262#office=Munich", "Germany", "https://www.dechert.com/people-search.html?cq=262#office=Paris", "France", "https://www.dechert.com/people-search.html?cq=262#office=Singapore", "Singapore");

    public DechertLLP() {
        super("Dechert LLP", "https://www.dechert.com/people-search.html?cq=262#office=Brussels", 8, 3);
    }

    private String getRandomLink() {
        List<String> links = new ArrayList(LINK_COUNTRY_MAP.keySet());
        Random random = new Random();
        int randomIndex = random.nextInt(links.size());
        return (String)links.get(randomIndex);
    }

    protected void accessPage(int index) throws InterruptedException {
        String url = index == 0 ? this.link : this.getRandomLink();
        this.driver.get(url);
        MyDriver.waitForPageToLoad();
        Thread.sleep(1000L);
        if (index > 0) {
            this.currentCountry = (String)LINK_COUNTRY_MAP.getOrDefault(url, "Unknown");
        }

    }

    protected List<WebElement> getLawyersInPage() {
        By[] webRole = new By[]{By.cssSelector("span.uppercase")};
        String[] validRoles = new String[]{"partner", "counsel"};

        try {
            WebDriverWait wait = new WebDriverWait(this.driver, Duration.ofSeconds(10L));
            List<WebElement> lawyers = (List)wait.until(ExpectedConditions.presenceOfAllElementsLocatedBy(By.cssSelector("article.flex-col > div > div.flex-col")));
            return this.siteUtl.filterLawyersInPage(lawyers, webRole, false, validRoles);
        } catch (Exception e) {
            throw new RuntimeException("Failed to find lawyer elements", e);
        }
    }

    private String getLink(WebElement lawyer) {
        WebElement element = this.siteUtl.iterateOverBy(new By[]{By.cssSelector("h3 > a")}, lawyer);
        return element != null ? element.getAttribute("href") : "";
    }

    private String getName(WebElement lawyer) {
        WebElement element = this.siteUtl.iterateOverBy(new By[]{By.cssSelector("h3 > a")}, lawyer);
        return element != null ? element.getText() : "";
    }

    private String getRole(WebElement lawyer) {
        WebElement element = this.siteUtl.iterateOverBy(new By[]{By.className("text-gray-600")}, lawyer);
        return element != null ? element.getText() : "";
    }

    private String getPracticeArea(WebElement lawyer) {
        WebElement element = this.siteUtl.iterateOverBy(new By[]{By.className("mt-1")}, lawyer);
        return element != null ? element.getText() : "";
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

    public Object getLawyer(WebElement lawyer) throws Exception {
        String[] socials = this.getSocials(lawyer);
        return Map.of("link", this.getLink(lawyer), "name", this.getName(lawyer), "role", this.getRole(lawyer), "firm", this.name, "country", this.currentCountry, "practice_area", this.getPracticeArea(lawyer), "email", socials[0], "phone", socials[1]);
    }
}
