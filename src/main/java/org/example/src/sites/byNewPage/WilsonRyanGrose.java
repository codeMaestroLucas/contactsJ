package org.example.src.sites.byNewPage;

import org.example.exceptions.LawyerExceptions;
import org.example.src.entities.BaseSites.ByNewPage;
import org.example.src.entities.MyDriver;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.time.Duration;
import java.util.List;
import java.util.Map;
import java.util.Objects;

public class WilsonRyanGrose extends ByNewPage {

    public WilsonRyanGrose() {
        super(
                "wilson/ryan/grose",
                "https://www.wrg.com.au/our-knowledge/business/?gad_source=1&gclid=CjwKCAiA74G9BhAEEiwA8kNfpTab-OOGsnN8Up1C0A23jtqE4Jckg5AuA1rc_MB_IDBSsAGE_a3ppBoCHMAQAvD_BwE",
                1
        );
    }

    private final String[] validRoles = {"partner", "counsel", "senior associate"};

    @Override
    protected void accessPage(int index) throws InterruptedException {
        this.driver.get(this.link);
        MyDriver.waitForPageToLoad();
    }

    @Override
    protected List<WebElement> getLawyersInPage() {
        try {
            WebDriverWait wait = new WebDriverWait(this.driver, Duration.ofSeconds(10));
            return wait.until(ExpectedConditions.presenceOfAllElementsLocatedBy(By.className("item")));
        } catch (Exception e) {
            throw new RuntimeException("Failed to find lawyers", e);
        }
    }

    @Override
    public String openNewTab(WebElement lawyer) throws LawyerExceptions {
        MyDriver.openNewTab(lawyer.getAttribute("href"));
        return link;
    }

    private String getName(WebElement lawyer) throws LawyerExceptions {
        By[] byArray = {By.tagName("h5")};
        return extractor.extractLawyerText(lawyer, byArray, "NAME", LawyerExceptions::nameException);
    }

    private String getRole() {
        String role = driver.findElement(By.cssSelector("div.team-detail-top div.title > h5")).getAttribute("textContent");
        boolean validPosition = siteUtl.isValidPosition(role, validRoles);
        return validPosition ? role : "Invalid Role";
    }

    private String getPracticeArea() {
        return driver.findElement(By.xpath("//*[@id=\"page-content\"]/div[2]/div[3]/div/div/div[1]/p[1]")).getAttribute("textContent");
    }

    private String[] getSocials(WebElement lawyer) {
        try {
            List<WebElement> socials = lawyer.findElements(By.className("contact-link"));
            return super.getSocials(socials, false);
        } catch (Exception e) {
            return new String[]{"", ""};
        }
    }

    @Override
    public Object getLawyer(WebElement lawyer) throws Exception {
        String name = this.getName(lawyer);
        this.openNewTab(lawyer);

        WebElement itemsDiv = driver.findElement(By.className("items"));
        String role = this.getRole();
        if (role.equals("Invalid Role")) return "Invalid Role";

        String[] socials = this.getSocials(itemsDiv);

        return Map.of(
                "link", Objects.requireNonNull(driver.getCurrentUrl()),
                "name", name,
                "role", role,
                "firm", this.name,
                "country", "Australia",
                "practice_area", this.getPracticeArea(),
                "email", socials[0],
                "phone", socials[1].isEmpty() ? "753535421" : socials[1]
        );
    }
}
