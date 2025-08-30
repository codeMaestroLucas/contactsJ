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

public class ThompsonDorfmanSweatman extends ByNewPage {
    String[] validRoles = new String[]{
            "partner",
            "counsel",
            "director",
            "senior associate"
    };

    public ThompsonDorfmanSweatman() {
        super(
                "Thompson Dorfman Sweatman",
                "https://www.tdslaw.com/people/lawyers/",
                1
        );
    }


    protected void accessPage(int index) throws InterruptedException {
        this.driver.get(this.link);
        MyDriver.waitForPageToLoad();
        Thread.sleep(1000L);
        MyDriver.rollDown(1, 5);
    }


    protected List<WebElement> getLawyersInPage() {
        try {
            WebDriverWait wait = new WebDriverWait(this.driver, Duration.ofSeconds(10L));

            return wait.until(
                    ExpectedConditions.presenceOfAllElementsLocatedBy(
                            By.className("_staff_content")
                    )
            );

        } catch (Exception e) {
            throw new RuntimeException("Failed to find lawyer elements", e);
        }
    }


    public void openNewTab(WebElement lawyer) {
        try {
            By[] byArray = {By.cssSelector("a[href^='https://www.tdslaw.com/lawyers/']")};
            String link = extractor.extractLawyerAttribute(lawyer, byArray, "LINK", "href", LawyerExceptions::linkException);
            MyDriver.openNewTab(link);
        } catch (LawyerExceptions e) {
            System.err.println("Failed to open new tab: " + e.getMessage());
        }
    }

    public String getLink() {
        return driver.getCurrentUrl();
    }

    private String getName(WebElement lawyer) throws LawyerExceptions {
        By[] byArray = {By.className("_name-header"), By.cssSelector("h1")};
        return extractor.extractLawyerText(lawyer, byArray, "NAME", LawyerExceptions::nameException);
    }


    private String getRole(WebElement lawyer) throws LawyerExceptions {
        By[] byArray = {By.className("_flex-content"), By.cssSelector("h2 ~ p:nth-of-type(2)")};
        String role = extractor.extractLawyerText(lawyer, byArray, "ROLE", LawyerExceptions::roleException);
        boolean validPosition = siteUtl.isValidPosition(role, validRoles);
        return validPosition ? role : "Invalid Role";
    }


    private String getPracticeArea(WebElement lawyer) {
        try {
            By[] byArray = {
                    By.className("_flex-sidebar"),
                    By.cssSelector("a[href^='https://www.tdslaw.com/services/']")
            };
            return extractor.extractLawyerText(lawyer, byArray, "PRACTICE AREA", LawyerExceptions::practiceAreaException);
        } catch (Exception e) {
            return "";
        }
    }


    private String[] getSocials(WebElement lawyer) {
        try {
            List<WebElement> socials = lawyer
                    .findElement(By.className("_flex-container"))
                    .findElements(By.cssSelector("a"));
            return super.getSocials(socials, false);

        } catch (Exception e) {
            System.err.println("Error getting socials: " + e.getMessage());
            return new String[]{"", ""};
        }
    }


    public Object getLawyer(WebElement lawyer) throws Exception {
        this.openNewTab(lawyer);

        WebElement divOuter = driver.findElement(By.className("_general-content"));
        String role = this.getRole(divOuter);
        if (role.equals("Invalid Role")) return "Invalid Role";

        WebElement divContent = driver.findElement(By.className("_single-people"));

        String[] socials = this.getSocials(divContent);

        return Map.of(
                "link", this.getLink(),
                "name", this.getName(divContent),
                "role", role,
                "firm", this.name,
                "country", "Canada",
                "practice_area", this.getPracticeArea(divOuter),
                "email", socials[0],
                "phone", socials[1].isEmpty() ? "2049571930" : socials[1]
        );
    }
}