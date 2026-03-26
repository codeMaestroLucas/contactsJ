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

public class AECO extends ByNewPage {

    public AECO() {
        super(
                "AECO",
                "https://aecolaw.com/team/#partners",
                1
        );
    }

    @Override
    protected void accessPage(int index) throws InterruptedException {
        this.driver.get(this.link);
        MyDriver.waitForPageToLoad();
        Thread.sleep(1000L);
    }

    @Override
    protected List<WebElement> getLawyersInPage() {
        try {
            WebDriverWait wait = new WebDriverWait(this.driver, Duration.ofSeconds(10));
            return wait.until(ExpectedConditions.presenceOfAllElementsLocatedBy(By.cssSelector("h3 a[href*='https://aecolaw.com/']")));
        } catch (Exception e) {
            return List.of();
        }
    }

    @Override
    public String openNewTab(WebElement lawyer) throws LawyerExceptions {
        MyDriver.openNewTab(lawyer.getAttribute("href"));
        return null;
    }

    @Override
    protected Object getLawyer(WebElement lawyer) throws Exception {
        this.openNewTab(lawyer);

        String role = this.getRole();
        if (role.equals("Invalid Role")) return "Invalid Role";

        String name = driver.findElement(By.xpath("/html/body/div/section[2]/div/div[2]/div/div[2]/div/h2")).getAttribute("textContent");
        String container = driver.findElement(By.xpath("/html/body/div/section[2]/div/div[2]/div/div[3]")).getAttribute("textContent");
        String[] socials = super.getSocialsFromText(container);

        return Map.of(
                "link", Objects.requireNonNull(driver.getCurrentUrl()),
                "name", name,
                "role", role,
                "firm", this.name,
                "country", "Turkey",
                "practice_area", container,
                "email", socials[0],
                "phone", socials[1].isEmpty() ? "902123518526" : socials[1]
        );
    }

    private String getRole() {
        String role = driver.findElement(By.xpath("/html/body/div/section[2]/div/div[2]/div/div[1]/div/p")).getAttribute("textContent");
        boolean validPosition = siteUtl.isValidPosition(role, validRoles);
        return validPosition ? role : "Invalid Role";
    }
}
