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

public class DillonEustace extends ByPage {
    public DillonEustace() {
        super("Dillon Eustace", "https://www.dilloneustace.com/people/", 1, 2);
    }

    protected void accessPage(int index) throws InterruptedException {
        this.driver.get(this.link);
        MyDriver.waitForPageToLoad();
        Thread.sleep(1000L);
        if (index <= 0) {
            this.siteUtl.clickOnAddBtn(By.id("cookiescript_accept"));
        }
    }

    protected List<WebElement> getLawyersInPage() {
        By[] webRole = new By[]{By.className("styles_detail__Cs__D")};
        String[] validRoles = new String[]{"partner", "counsel", "senior associate"};

        try {
            WebDriverWait wait = new WebDriverWait(this.driver, Duration.ofSeconds(10L));
            List<WebElement> lawyers = (List)wait.until(ExpectedConditions.presenceOfAllElementsLocatedBy(By.className("styles_content___cPpV")));
            return this.siteUtl.filterLawyersInPage(lawyers, webRole, true, validRoles);
        } catch (Exception e) {
            throw new RuntimeException("Failed to find lawyer elements", e);
        }
    }

    private String getLink(WebElement lawyer) {
        By[] byArray = new By[]{By.className("styles_name__m66C_"), By.cssSelector("a")};
        WebElement element = this.siteUtl.iterateOverBy(byArray, lawyer);
        return element.getAttribute("href");
    }

    private String getName(WebElement lawyer) {
        By[] byArray = new By[]{By.className("styles_name__m66C_"), By.cssSelector("a")};
        WebElement element = this.siteUtl.iterateOverBy(byArray, lawyer);
        return element.getText();
    }

    private String getRole(WebElement lawyer) {
        By[] byArray = new By[]{By.className("styles_detail__Cs__D")};
        WebElement element = this.siteUtl.iterateOverBy(byArray, lawyer);
        return element.getText();
    }

    private String getCountry(WebElement lawyer) {
        By[] byArray = new By[]{By.className("styles_detail__Cs__D")};
        WebElement element = this.siteUtl.iterateOverBy(byArray, lawyer);
        String[] options = element.getText().split(",");
        String country = options[options.length - 1].trim();
        if (country.equalsIgnoreCase("dublin")) {
            country = "Ireland";
        } else if (country.equalsIgnoreCase("cayman islands")) {
            country = "the Cayman Islands";
        } else if (country.equalsIgnoreCase("new york")) {
            country = "EUA";
        }

        return country;
    }

    private String getPracticeArea(WebElement lawyer) {
        By[] byArray = new By[]{By.className("styles_expertise__CecK5")};
        WebElement element = this.siteUtl.iterateOverBy(byArray, lawyer);
        return element.getText();
    }

    private String[] getSocials(WebElement lawyer) {
        try {
            List<WebElement> socials = lawyer.findElement(By.className("styles_links__6LPJw")).findElements(By.cssSelector("a"));
            return super.getSocials(socials);
        } catch (Exception e) {
            System.err.println("Error getting socials: " + e.getMessage());
            return new String[]{"", ""};
        }
    }

    public Object getLawyer(WebElement lawyer) throws Exception {
        String[] socials = this.getSocials(lawyer);
        return Map.of("link", this.getLink(lawyer), "name", this.getName(lawyer), "role", this.getRole(lawyer), "firm", this.name, "country", this.getCountry(lawyer), "practice_area", this.getPracticeArea(lawyer), "email", socials[0], "phone", socials[1]);
    }
}
