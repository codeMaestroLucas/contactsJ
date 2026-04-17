package org.example.src.sites.europe;

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

public class BDGSAssociates extends ByNewPage {

    public BDGSAssociates() {
        super(
                "BDGS Associates",
                "https://www.bdgs-associes.com/en/our-lawyers/",
                1
        );
    }

    @Override
    protected void accessPage(int index) throws InterruptedException {
        this.driver.get(this.link);
        MyDriver.waitForPageToLoad();
    }

    @Override
    protected List<WebElement> getLawyersInPage() {
        try {
            WebDriverWait wait = new WebDriverWait(this.driver, Duration.ofSeconds(10));
            WebElement until = wait.until(ExpectedConditions.presenceOfElementLocated(By.xpath("//*[@id=\"advanced-tabs-desc-wrap-7944\"]/div/div/div[1]/div[2]/div[2]")));
            return until.findElements(By.cssSelector("div.team-author-name a[href*='https://www.bdgs-associes.com/en/dt_team/']"));
        } catch (Exception e) {
            return List.of();
        }
    }

    @Override
    public String openNewTab(WebElement lawyer) throws LawyerExceptions {
        MyDriver.openNewTab(lawyer.getAttribute("href"));
        return link;
    }

    @Override
    protected Object getLawyer(WebElement lawyer) throws Exception {
        this.openNewTab(lawyer);

        String role = this.getRole();
        if (role.equals("Invalid Role")) return "Invalid Role";

        String textContent = driver.findElement(By.xpath("//div/div[3]/div/div/div/div[3]")).getAttribute("textContent");
        String[] socials = super.getSocialsFromText(textContent);

        return Map.of(
                "link", Objects.requireNonNull(driver.getCurrentUrl()),
                "name", extractor.extractLawyerAttribute(driver.findElement(By.tagName("body")), new By[] {By.tagName("h1")}, "NAME", "textContent", LawyerExceptions::nameException),
                "role", role,
                "firm", this.name,
                "country", "France",
                "practice_area", "",
                "email", socials[0].replaceFirst("_", ""),
                "phone", socials[1].isEmpty() ? "33142992222" : socials[1]
        );
    }

    private String getRole() {
        try {
            String role = driver.findElement(By.xpath("//div/div[3]/div/div/div/div[2]/div/h6/span/strong")).getAttribute("textContent");
            boolean validPosition = siteUtl.isValidPosition(role, validRoles);
            return validPosition ? role : "Invalid Role";
        } catch (Exception e) {
            return "Invalid Role";
        }
    }
}
