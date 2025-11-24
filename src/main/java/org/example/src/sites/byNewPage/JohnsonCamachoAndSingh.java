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

public class JohnsonCamachoAndSingh extends ByNewPage {
    public JohnsonCamachoAndSingh() {
        super(
                "Johnson Camacho And Singh",
                "https://www.jcscaribbeanlaw.com/our-team/",
                1
        );
    }

    protected void accessPage(int index) {
        this.driver.get(this.link);
        MyDriver.waitForPageToLoad();
    }

    protected List<WebElement> getLawyersInPage() {
        try {
            WebDriverWait wait = new WebDriverWait(this.driver, Duration.ofSeconds(10L));
            return wait.until(
                    ExpectedConditions.presenceOfAllElementsLocatedBy(
                            By.cssSelector("a[href*='https://www.jcscaribbeanlaw.com/our-team/']")
                    )
            );
        } catch (Exception e) {
            throw new RuntimeException("Failed to find lawyer elements", e);
        }
    }

    public void openNewTab(WebElement lawyer) {
        try {
            MyDriver.openNewTab(lawyer.getAttribute("href"));
        } catch (Exception e) {
            System.err.println("Error opening tab: " + e.getMessage());
        }
    }

    private String getName(WebElement lawyer) throws LawyerExceptions {
        By[] byArray = {By.tagName("h2")};
        return extractor.extractLawyerAttribute(lawyer, byArray, "NAME", "textContent", LawyerExceptions::nameException);
    }

    private String getRole(WebElement lawyer) throws LawyerExceptions {
        String[] validRoles = {"partner", "senior associate"};
        By[] byArray = {By.className("elementor-widget-text-editor")};
        String role = extractor.extractLawyerAttribute(lawyer, byArray, "ROLE", "textContent", LawyerExceptions::roleException);
        boolean validPosition = siteUtl.isValidPosition(role, validRoles);

        return validPosition ? role : "Invalid Role";
    }

    private String[] getSocials(WebElement lawyer) {
        List<WebElement> socials = lawyer.findElements(By.tagName("h3"));
        return super.getSocials(socials, true);
    }

    public Object getLawyer(WebElement lawyer) throws Exception {
        this.openNewTab(lawyer);
        WebElement container = driver.findElement(By.xpath("//*[@id=\"bio-top\"]/div/div[1]/div[2]/div[2]/div"));

        String role = this.getRole(container);

        if (role.equals("Invalid Role")) return "Invalid Role";

        String[] socials = this.getSocials(container);

        return Map.of(
                "link", Objects.requireNonNull(driver.getCurrentUrl()),
                "name", this.getName(container),
                "role", role,
                "firm", this.name,
                "country", "Trinidad & Tobago",
                "practice_area", "",
                "email", socials[0],
                "phone", socials[1].isEmpty() ? "18686228959" : socials[1]
        );
    }
}