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

public class KWKRLaw extends ByNewPage {
    private final By[] byRoleArray = {
            By.className("team-item__position")
    };

    public KWKRLaw() {
        super(
                "KWKR Law",
                "https://kwkr.pl/en/our-team/",
                1
        );
    }

    protected void accessPage(int index) {
        this.driver.get(this.link);
        MyDriver.waitForPageToLoad();
    }

    protected List<WebElement> getLawyersInPage() {
        String[] validRoles = {"partner", "counsel", "advisor", "senior associate", "managing associate"};

        try {
            WebDriverWait wait = new WebDriverWait(this.driver, Duration.ofSeconds(10L));
            List<WebElement> lawyers = wait.until(
                    ExpectedConditions.presenceOfAllElementsLocatedBy(
                            By.className("team-item")
                    )
            );
            return this.siteUtl.filterLawyersInPage(lawyers, byRoleArray, true, validRoles);
        } catch (Exception e) {
            throw new RuntimeException("Failed to find lawyer elements", e);
        }
    }

    public String openNewTab(WebElement lawyer) {
        try {
            String link = lawyer.getAttribute("href");
            MyDriver.openNewTab(link);
        } catch (Exception e) {
            System.err.println("Error opening tab: " + e.getMessage());
        }
        return null;
    }

    private String getName(WebElement lawyer) throws LawyerExceptions {
        By[] byArray = {By.tagName("h1")};
        return extractor.extractLawyerText(lawyer, byArray, "NAME", LawyerExceptions::nameException);
    }

    private String getRole(WebElement lawyer) throws LawyerExceptions {
        try {
            return driver.findElement(By.className("team-detail-position")).getText();
        } catch (Exception e) {
            return "Partner"; // Default or throw
        }
    }

    private String[] getSocials(WebElement lawyer) {
        List<WebElement> socials = lawyer.findElements(By.tagName("a"));
        return super.getSocials(socials, false);
    }

    public Object getLawyer(WebElement lawyer) throws Exception {
        String role = lawyer.findElement(By.className("team-item__position")).getText();

        this.openNewTab(lawyer);
        WebElement aside = driver.findElement(By.tagName("aside")); // Detail contact area

        String[] socials = this.getSocials(aside);

        // Name on detail page usually H1
        String name = driver.findElement(By.tagName("h1")).getText();

        return Map.of(
                "link", Objects.requireNonNull(driver.getCurrentUrl()),
                "name", name,
                "role", role,
                "firm", this.name,
                "country", "Poland",
                "practice_area", "",
                "email", socials[0],
                "phone", socials[1].isEmpty() ? "xxxxxx" : socials[1]
        );
    }
}