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

public class ConsortiumLegal extends ByPage {
    public ConsortiumLegal() {
        super("Consortium Legal", "https://consortiumlegal.com/en/specialists/", 8, 3);
    }

    protected void accessPage(int index) throws InterruptedException {
        String otherUrl = "https://consortiumlegal.com/en/specialists/?fwp_paged=" + (index + 1);
        String url = index == 0 ? this.link : otherUrl;
        this.driver.get(url);
        MyDriver.waitForPageToLoad();
        Thread.sleep(1000L);
    }

    protected List<WebElement> getLawyersInPage() {
        By[] webRole = new By[]{By.cssSelector("h5")};
        String[] validRoles = new String[]{"partner", "senior associate"};

        try {
            WebDriverWait wait = new WebDriverWait(this.driver, Duration.ofSeconds(10L));
            List<WebElement> lawyers = (List)wait.until(ExpectedConditions.presenceOfAllElementsLocatedBy(By.className("contenido-abogado")));
            return this.siteUtl.filterLawyersInPage(lawyers, webRole, true, validRoles);
        } catch (Exception e) {
            throw new RuntimeException("Failed to find lawyer elements", e);
        }
    }

    private String getLink(WebElement lawyer) {
        By[] byArray = new By[]{By.cssSelector("a")};
        WebElement element = this.siteUtl.iterateOverBy(byArray, lawyer);
        return element.getAttribute("href");
    }

    private String getName(WebElement lawyer) {
        By[] byArray = new By[]{By.cssSelector("a"), By.className("title-abogado")};
        WebElement element = this.siteUtl.iterateOverBy(byArray, lawyer);
        return element.getText();
    }

    private String getRole(WebElement lawyer) {
        By[] byArray = new By[]{By.cssSelector("h5")};
        WebElement element = this.siteUtl.iterateOverBy(byArray, lawyer);
        return element.getText();
    }

    private String getCountry(WebElement lawyer) {
        By[] byArray = new By[]{By.cssSelector("div:last-child")};
        WebElement element = this.siteUtl.iterateOverBy(byArray, lawyer);
        return element.getText().replace("-", "");
    }

    private String[] getSocials(WebElement lawyer) {
        try {
            List<WebElement> socials = lawyer.findElements(By.cssSelector("a"));
            return super.getSocials(socials);
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
