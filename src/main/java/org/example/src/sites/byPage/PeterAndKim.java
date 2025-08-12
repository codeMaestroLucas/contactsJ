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

public class PeterAndKim extends ByPage {

    public PeterAndKim() {
        super("Peter And Kim", "https://peterandkim.com/team/", 1, 2);
        OFFICE_TO_COUNTRY = Map.of("geneva", "Switzerland", "perth", "Australia", "seoul", "Korea (South)", "singapore", "Singapore", "sydney", "Australia", "zurich", "Switzerland");
    }

    protected void accessPage(int index) throws InterruptedException {
        this.driver.get(this.link);
        MyDriver.waitForPageToLoad();
        Thread.sleep(1000L);
        if (index <= 0) {
            MyDriver.clickOnElement(By.cssSelector("button.cmplz-btn.cmplz-accept"));
        }
    }

    protected List<WebElement> getLawyersInPage() {
        By[] webRole = new By[]{By.cssSelector("div.flex.gap-2.items-center.self-start.mt-2.text-xs.uppercase.whitespace-nowrap.text-violet-950"), By.cssSelector("span:first-child")};
        String[] validRoles = new String[]{"partner", "counsel", "senior associate"};

        try {
            WebDriverWait wait = new WebDriverWait(this.driver, Duration.ofSeconds(10L));
            List<WebElement> lawyers = (List)wait.until(ExpectedConditions.presenceOfAllElementsLocatedBy(By.cssSelector("div.flex.flex-col.max-w-full > div.flex.flex-col.w-full")));
            return this.siteUtl.filterLawyersInPage(lawyers, webRole, true, validRoles);
        } catch (Exception e) {
            throw new RuntimeException("Failed to find lawyer elements", e);
        }
    }

    private String getLink(WebElement lawyer) {
        WebElement element = driver.findElement(By.cssSelector("a[href^='https']"));
        return element.getAttribute("href");
    }

    private String getName(WebElement lawyer) {
        By[] byArray = new By[]{By.cssSelector("h2")};
        WebElement element = this.siteUtl.iterateOverBy(byArray, lawyer);
        return element.getText();
    }

    private String getRole(WebElement lawyer) {
        By[] byArray = new By[]{By.cssSelector("div.flex.gap-2.items-center.self-start.mt-2.text-xs.uppercase.whitespace-nowrap.text-violet-950"), By.cssSelector("span:first-child")};
        WebElement element = this.siteUtl.iterateOverBy(byArray, lawyer);
        return element.getText();
    }

    private String getCountry(WebElement lawyer) {
        By[] byArray = new By[]{By.cssSelector("div.flex.gap-2.items-center.self-start.mt-2.text-xs.uppercase.whitespace-nowrap.text-violet-950"), By.cssSelector("span:last-child")};
        return siteUtl.getCountryBasedInOffice(
            OFFICE_TO_COUNTRY, this.siteUtl.iterateOverBy(byArray, lawyer)
        );
    }

    private String[] getSocials(WebElement lawyer) {
        try {
            List<WebElement> socials = lawyer.findElement(By.cssSelector("div.flex.gap-4.items-center.self-start.mt-4")).findElements(By.cssSelector("a"));
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
