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

public class ONVLaw extends ByNewPage {
    private final By[] byRoleArray = {
            By.cssSelector("div.elementor-widget-text-editor p")
    };

    public ONVLaw() {
        super(
                "ONV Law",
                "https://onvlaw.ro/team/",
                1
        );
    }

    private final String[] validRoles = {"partner", "counsel", "senior associate"};

    protected void accessPage(int index) {
        this.driver.get(this.link);
        MyDriver.waitForPageToLoad();
    }

    protected List<WebElement> getLawyersInPage() {
        try {
            WebDriverWait wait = new WebDriverWait(this.driver, Duration.ofSeconds(10L));
            List<WebElement> lawyers = wait.until(ExpectedConditions.presenceOfAllElementsLocatedBy(
                    By.cssSelector("a[href*='/team/']")
            ));
            return lawyers.subList(6, lawyers.size());
        } catch (Exception e) {
            throw new RuntimeException("Failed to find lawyer elements", e);
        }
    }

    public String openNewTab(WebElement lawyer) {
        try {
            MyDriver.openNewTab(lawyer.getAttribute("href"));
        } catch (Exception e) {
            System.err.println("Could not find link for lawyer: " + e.getMessage());
        }
        return null;
    }

    private String getName(WebElement div) throws LawyerExceptions {
        By[] byArray = {By.tagName("h1")};
        return extractor.extractLawyerText(div, byArray, "NAME", LawyerExceptions::nameException);
    }

    private String getRole(WebElement div) throws LawyerExceptions {
        By[] byArray = {By.className("elementor-widget-theme-post-excerpt")};
        String role = extractor.extractLawyerText(div, byArray, "ROLE", LawyerExceptions::roleException);
        boolean validPosition = siteUtl.isValidPosition(role, validRoles);
        return validPosition ? role : "Invalid Role";
    }

    private String getPracticeArea(WebElement div) {
        try {
            // Taking the first item from the expertise list
            WebElement item = div.findElement(By.cssSelector("ul.elementor-icon-list-items .elementor-icon-list-text"));
            return item.getText();
        } catch (Exception e) {
            return "";
        }
    }

    private String[] getSocials(WebElement div) {
        String email = "";
        try {
            email = div.findElement(By.cssSelector("a[href^='mailto:']")).getAttribute("href").replace("mailto:", "");
        } catch (Exception e) {
            // Social not found
        }
        return new String[]{email, ""};
    }

    public Object getLawyer(WebElement lawyer) throws Exception {
        this.openNewTab(lawyer);
        WebElement mainContainer = driver.findElement(By.tagName("body"));

        String role = this.getRole(mainContainer);
        if (role.equals("Invalid Role")) return "Invalid Role";

        String[] socials = this.getSocials(mainContainer);

        return Map.of(
                "link", Objects.requireNonNull(driver.getCurrentUrl()),
                "name", this.getName(mainContainer),
                "role", role,
                "firm", this.name,
                "country", "Romania",
                "practice_area", this.getPracticeArea(mainContainer),
                "email", socials[0],
                "phone", "0040213152147"
        );
    }
}