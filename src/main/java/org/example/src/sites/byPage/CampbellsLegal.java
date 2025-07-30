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

public class CampbellsLegal extends ByPage {
    public CampbellsLegal() {
        super("Campbells Legal", "https://www.campbellslegal.com/people/", 1, 3);
    }

    protected void accessPage(int index) throws InterruptedException {
        this.driver.get(this.link);
        MyDriver.waitForPageToLoad();
        Thread.sleep(1000L);
        if (index <= 0) {
            this.siteUtl.clickOnAddBtn(By.className("cky-btn-accept"));
            this.siteUtl.clickOnAddBtn(By.className("box--filter-button"));
        }
    }

    protected List<WebElement> getLawyersInPage() {
        By[] webRole = new By[]{By.className("position")};
        String[] validRoles = new String[]{"partner", "counsel", "senior associate"};

        try {
            WebDriverWait wait = new WebDriverWait(this.driver, Duration.ofSeconds(10L));
            List<WebElement> lawyers = (List)wait.until(ExpectedConditions.presenceOfAllElementsLocatedBy(By.cssSelector("div.item.person")));
            return this.siteUtl.filterLawyersInPage(lawyers, webRole, true, validRoles);
        } catch (Exception e) {
            throw new RuntimeException("Failed to find lawyer elements", e);
        }
    }

    private String getLink(WebElement lawyer) {
        By[] byArray = new By[]{By.className("title"), By.className("view_person")};
        WebElement element = this.siteUtl.iterateOverBy(byArray, lawyer);
        return element.getAttribute("href");
    }

    private String getName(WebElement lawyer) {
        By[] byArray = new By[]{By.className("title"), By.className("view_person")};
        WebElement element = this.siteUtl.iterateOverBy(byArray, lawyer);
        return element.getText();
    }

    private String getRole(WebElement lawyer) {
        By[] byArray = new By[]{By.className("position")};
        WebElement element = this.siteUtl.iterateOverBy(byArray, lawyer);
        return element.getText();
    }

    private String getCountry(String phone) {
        String normalized = phone.replace("tel:", "").replace("+", "").trim();
        String country;
        if (normalized.startsWith("1 345")) {
            country = "the Cayman Islands";
        } else if (normalized.startsWith("1 284")) {
            country = "the British Virgin Islands";
        } else if (normalized.startsWith("852")) {
            country = "Hong Kong";
        } else {
            System.out.println("Couldn't find the country with the phone: " + phone);
            country = "------";
        }

        return country;
    }

    private String[] getSocials(WebElement lawyer) {
        try {
            List<WebElement> socials = lawyer.findElement(By.className("meta")).findElements(By.cssSelector("a"));
            return super.getSocials(socials);
        } catch (Exception e) {
            System.err.println("Error getting socials: " + e.getMessage());
            return new String[]{"", ""};
        }
    }

    public Object getLawyer(WebElement lawyer) throws Exception {
        String[] socials = this.getSocials(lawyer);
        return Map.of("link", this.getLink(lawyer), "name", this.getName(lawyer), "role", this.getRole(lawyer), "firm", this.name, "country", this.getCountry(socials[1]), "practice_area", "", "email", socials[0], "phone", socials[1]);
    }
}
