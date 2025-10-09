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

public class Astrea extends ByNewPage {
    private final String[] validRoles = {"partner", "counsel", "senior associate"};


    private final By[] byRoleArray = {
            By.cssSelector("div[class*='field-position']")
    };

    public Astrea() {
        super(
                "Astrea",
                "https://www.astrealaw.be/en/team",
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
            WebDriverWait wait = new WebDriverWait(this.driver, Duration.ofSeconds(10L));
            return wait.until(
                    ExpectedConditions.presenceOfAllElementsLocatedBy(
                            By.className("node--type-team")
                    )
            );
        } catch (Exception e) {
            throw new RuntimeException("Failed to find lawyer elements", e);
        }
    }

    @Override
    public void openNewTab(WebElement lawyer) throws LawyerExceptions {
        String link = lawyer.getAttribute("href");
        MyDriver.openNewTab(link);
    }

    private String getName(WebElement lawyer) {
        return driver.findElement(By.className("node__header--content")).findElement(By.tagName("h1")).getAttribute("textContent");
    }

    private String getRole(WebElement lawyer) throws LawyerExceptions {
        String role = extractor.extractLawyerAttribute(lawyer, byRoleArray, "ROLE", "textContent", LawyerExceptions::roleException);
        return siteUtl.isValidPosition(role, validRoles) ? role : "Invalid Role";
    }

    private String getPracticeArea(WebElement lawyer) throws LawyerExceptions {
        By[] byArray = new By[]{
                By.cssSelector("ul.field__items > li")
        };
        try {
            WebElement element = siteUtl.iterateOverBy(byArray, lawyer);
            return extractor.extractLawyerAttribute(lawyer, byArray, "PRACTICE AREA", "textContent", LawyerExceptions::practiceAreaException);
        } catch (LawyerExceptions e) {
            return "";
        }
    }

    private String[] getSocials(WebElement lawyer) {
        try {
            List<WebElement> socials = lawyer.findElement(By.className("contact")).findElements(By.tagName("a"));
            return super.getSocials(socials, false);
        } catch (Exception e) {
            System.err.println("Error getting socials: " + e.getMessage());
            return new String[]{"", ""};
        }
    }

    @Override
    public Object getLawyer(WebElement lawyer) throws Exception {
        this.openNewTab(lawyer);
        WebElement div = driver.findElement(By.className("node__header__main"));

        String role = this.getRole(div);
        if (role.equals("Invalid Role")) {
            return "Invalid Role";
        }

        String[] socials = this.getSocials(div);

        return Map.of(
                "link", Objects.requireNonNull(driver.getCurrentUrl()),
                "name", this.getName(div),
                "role", role,
                "firm", this.name,
                "country", "Belgium",
                "practice_area", this.getPracticeArea(driver.findElement(By.id("expertise"))),
                "email", socials[0],
                "phone", socials[1].isEmpty() ? "3232871111" : socials[1]
        );
    }
}