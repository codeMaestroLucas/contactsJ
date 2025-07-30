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

public class ArnoldAndPorter extends ByPage {
    public ArnoldAndPorter() {
        super("Arnold And Porter", "https://www.arnoldporter.com/en/people?offices=6345e0f4-64a2-4698-8117-fb2c8311cfc5,4dfbc043-3bdf-df98-7dbd-9579ed375007,dfcf7436-a067-b337-91ac-e8f2ff7e912a,effc1763-f448-4d06-94f5-9bb5a082a8f8,ef390be5-f9a7-434a-927e-22f81d498b35&titles=c9a37860-0e03-294c-b0ba-bdca9259042b,0def9372-5897-4459-9f95-f67a49d4b484,323a1c1f-00ed-ccd0-7709-668547c15146,570dd7b1-d7f5-4c83-85dd-aba52eb0e6c4,77b92139-93bc-1d04-5d7a-05fccc1eca7a,93ffd6e1-fd46-4514-bf4e-6def93e1df8f,5bad4c39-74aa-4011-ad9f-fb0c31fc44c1,d7bcd7a6-8fa9-45d7-a4f0-75f8e290d723&skip=40&sort=0&reload=false&scroll=8604", 1, 3);
    }

    protected void accessPage(int index) throws InterruptedException {
        this.driver.get(this.link);
        MyDriver.waitForPageToLoad();
        Thread.sleep(1000L);
    }

    protected List<WebElement> getLawyersInPage() {
        By[] webRole = new By[]{By.className("person-item-info"), By.className("person-level")};
        String[] validRoles = new String[]{"partner", "senior associate", "counsel"};

        try {
            WebDriverWait wait = new WebDriverWait(this.driver, Duration.ofSeconds(10L));
            List<WebElement> lawyers = (List)wait.until(ExpectedConditions.presenceOfAllElementsLocatedBy(By.className("person-item")));
            return this.siteUtl.filterLawyersInPage(lawyers, webRole, true, validRoles);
        } catch (Exception e) {
            throw new RuntimeException("Failed to find lawyer elements", e);
        }
    }

    private String getLink(WebElement lawyer) {
        By[] byArray = new By[]{By.className("person-item-info"), By.className("person-item-name")};
        return this.siteUtl.iterateOverBy(byArray, lawyer).getAttribute("href");
    }

    private String getName(WebElement lawyer) {
        By[] byArray = new By[]{By.className("person-item-info"), By.className("person-item-name")};
        return this.siteUtl.iterateOverBy(byArray, lawyer).getText();
    }

    private String getRole(WebElement lawyer) {
        By[] byArray = new By[]{By.className("person-item-info"), By.className("person-level")};
        return this.siteUtl.iterateOverBy(byArray, lawyer).getText();
    }

    private String getCountry(WebElement lawyer) {
        Map<String, String> countries = Map.of("amsterdam", "the Netherlands", "brussels", "Belgium", "london", "England", "seoul", "Korea (South)", "shanghai", "China");
        By[] byArray = new By[]{By.className("person-item-info"), By.className("person-item-office")};
        String country = this.siteUtl.iterateOverBy(byArray, lawyer).getText();
        return (String)countries.getOrDefault(country.toLowerCase().trim(), "");
    }

    private String[] getSocials(WebElement lawyer) {
        try {
            List<WebElement> socials = lawyer.findElement(By.cssSelector("div.person-item-contact-info")).findElements(By.cssSelector("a"));
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
