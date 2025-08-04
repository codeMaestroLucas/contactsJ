package org.example.src.sites.byPage;

import java.io.PrintStream;
import java.time.Duration;
import java.util.List;
import java.util.Map;
import org.example.src.entities.MyDriver;
import org.example.src.entities.BaseSites.ByPage;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

public class GianniAndOrigoni extends ByPage {
    public GianniAndOrigoni() {
        super("Gianni And Origoni", "https://www.gop.it/people.php?lang=eng", 1, 3);
        OFFICE_TO_COUNTRY = Map.ofEntries(Map.entry("Rome", "Italy"), Map.entry("Milan", "Italy"), Map.entry("Bologna", "Italy"), Map.entry("Padua", "Italy"), Map.entry("Turin", "Italy"), Map.entry("Abu Dhabi", "the UAE"), Map.entry("Brussels", "Belgium"), Map.entry("London", "England"), Map.entry("New York", "USA"), Map.entry("Hong Kong", "Hong Kong"), Map.entry("Shanghai", "China"));
    }

    protected void accessPage(int index) throws InterruptedException {
        this.driver.get(this.link);
        MyDriver.waitForPageToLoad();
        Thread.sleep(1000L);
        WebDriverWait wait = new WebDriverWait(this.driver, Duration.ofSeconds(10L));
        ((WebElement)wait.until(ExpectedConditions.elementToBeClickable(By.className("bottone_people")))).click();
        MyDriver.waitForPageToLoad();
    }

    protected List<WebElement> getLawyersInPage() {
        By[] webRole = new By[]{By.className("campotab2")};
        String[] validRoles = new String[]{"partner", "counsel", "senior associate"};

        try {
            WebDriverWait wait = new WebDriverWait(this.driver, Duration.ofSeconds(10L));
            List<WebElement> lawyers = (List)wait.until(ExpectedConditions.presenceOfAllElementsLocatedBy(By.className("tabella_risu")));
            return this.siteUtl.filterLawyersInPage(lawyers, webRole, true, validRoles);
        } catch (Exception e) {
            throw new RuntimeException("Failed to find lawyer elements", e);
        }
    }

    private String getLink(WebElement lawyer) {
        By[] byArray = new By[]{By.className("campotab6"), By.cssSelector("a")};
        WebElement element = this.siteUtl.iterateOverBy(byArray, lawyer);
        return element.getAttribute("href");
    }

    private String getName(WebElement lawyer) {
        By[] byArray = new By[]{By.className("campotab1")};
        WebElement element = this.siteUtl.iterateOverBy(byArray, lawyer);
        String[] nameParts = element.getText().split(" ");
        StringBuilder name = new StringBuilder();

        for(int i = nameParts.length - 1; i >= 0; --i) {
            name.append(nameParts[i] + " ");
        }

        return name.toString();
    }

    private String getRole(WebElement lawyer) {
        By[] byArray = new By[]{By.className("campotab2")};
        WebElement element = this.siteUtl.iterateOverBy(byArray, lawyer);
        return element.getText();
    }

    private String getCountry(WebElement lawyer) {
        By[] byArray = new By[]{By.className("campotab5")};
        WebElement element = this.siteUtl.iterateOverBy(byArray, lawyer);
        return siteUtl.getCountryBasedInOffice(OFFICE_TO_COUNTRY, element.getText());
    }

    private String getPracticeArea(WebElement lawyer) {
        By[] byArray = new By[]{By.className("campotab3")};
        WebElement element = this.siteUtl.iterateOverBy(byArray, lawyer);
        return element.getText();
    }

    private String[] getSocials(WebElement lawyer) {
        try {
            String phone = lawyer.findElement(By.className("campotab4")).getText();
            String email = lawyer.findElement(By.className("campotab7")).findElement(By.cssSelector("a")).getAttribute("href").replaceAll("\\?.*$", "");
            return new String[]{email, phone};
        } catch (Exception e) {
            PrintStream var10000 = System.err;
            String var10001 = this.getName(lawyer);
            var10000.println("Error getting socials for " + var10001 + ": " + e.getMessage());
            return new String[]{"", ""};
        }
    }

    public Object getLawyer(WebElement lawyer) throws Exception {
        String[] socials = this.getSocials(lawyer);
        return Map.of("link", this.getLink(lawyer), "name", this.getName(lawyer), "role", this.getRole(lawyer), "firm", this.name, "country", this.getCountry(lawyer), "practice_area", this.getPracticeArea(lawyer), "email", socials[0], "phone", socials[1]);
    }
}
