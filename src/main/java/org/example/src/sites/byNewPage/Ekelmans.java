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

public class Ekelmans extends ByNewPage {

    public Ekelmans() {
        super(
                "Ekelmans",
                "https://www.ekelmansadvocaten.com/en/our-team/",
                1
        );
    }

    private final String[] validRoles = {"partner", "director", "counsel"};

    @Override
    protected void accessPage(int index) {
        this.driver.get(this.link);
        MyDriver.waitForPageToLoad();
    }

    @Override
    protected List<WebElement> getLawyersInPage() {
        WebDriverWait wait = new WebDriverWait(this.driver, Duration.ofSeconds(10L));
        return wait.until(
                ExpectedConditions.presenceOfAllElementsLocatedBy(
                        By.cssSelector("a[href*='https://www.bld-ekelmans.com/en/team/']")
                )
        );
    }

    @Override
    public void openNewTab(WebElement lawyer) throws LawyerExceptions {
        String link = lawyer.getAttribute("href");
        MyDriver.openNewTab(link);
    }

    private String getName(WebElement lawyer) throws LawyerExceptions {
        By[] byArray = {By.cssSelector("h2.grve-title")};
        return extractor.extractLawyerText(lawyer, byArray, "NAME", LawyerExceptions::nameException);
    }

    private String getRole(WebElement lawyer) throws LawyerExceptions {
        By[] byArray = {By.className("field-functie")};
        String role = extractor.extractLawyerText(lawyer, byArray, "ROLE", LawyerExceptions::roleException);
        boolean validPosition = siteUtl.isValidPosition(role, validRoles);
        return validPosition ? role : "Invalid Role";
    }

    private String[] getSocials(WebElement lawyer) {
        try {
            List<WebElement> socials = lawyer.findElements(By.cssSelector("a[href^='mailto:'], a[href^='tel:']"));
            return super.getSocials(socials, false);
        } catch (Exception e) {
            System.err.println("Error getting socials: " + e.getMessage());
            return new String[]{"", ""};
        }
    }

    @Override
    public Object getLawyer(WebElement lawyer) throws Exception {
        this.openNewTab(lawyer);
        WebElement container = driver.findElement(By.className("grve-column-wrapper-inner"));

        String role = this.getRole(container);
        if (role.equals("Invalid Role")) return "Invalid Role";

        String[] socials = this.getSocials(container);

        return Map.of(
                "link", Objects.requireNonNull(driver.getCurrentUrl()),
                "name", this.getName(container),
                "role", role,
                "firm", this.name,
                "country", "the Netherlands",
                "practice_area", "",
                "email", socials[0],
                "phone", socials[1].isEmpty() ? "31703746300" : socials[1]
        );
    }
}