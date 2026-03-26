package org.example.src.sites.byPage;

import org.example.exceptions.LawyerExceptions;
import org.example.src.entities.BaseSites.ByPage;
import org.example.src.entities.MyDriver;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.time.Duration;
import java.util.List;
import java.util.Map;

public class BureauPlattner extends ByPage {

    private final By[] byRoleArray = {
            By.className("inner-title"),
            By.tagName("h5")
    };

    public BureauPlattner() {
        super(
                "Bureau Plattner",
                "https://www.bureauplattner.com/it/team",
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
        String[] validRoles = new String[]{
                "partner", "consultant", "counsel"
        };

        try {
            WebDriverWait wait = new WebDriverWait(this.driver, Duration.ofSeconds(10L));
            List<WebElement> lawyers = wait.until(
                    ExpectedConditions.presenceOfAllElementsLocatedBy(
                            By.className("consultantEl")
                    )
            );
            return this.siteUtl.filterLawyersInPage(lawyers, byRoleArray, true, validRoles);
        } catch (Exception e) {
            throw new RuntimeException("Failed to find lawyer elements", e);
        }
    }

    private String getLink(WebElement lawyer) throws LawyerExceptions {
        // The link is not explicitly needed for ByPage if we extract everything here,
        // but typically we point to the current page or a profile link if available.
        return this.link;
    }

    private String getName(WebElement lawyer) throws LawyerExceptions {
        By[] byArray = new By[]{
                By.className("inner-title"),
                By.tagName("h3")
        };
        return extractor.extractLawyerText(lawyer, byArray, "NAME", LawyerExceptions::nameException);
    }

    private String getRole(WebElement lawyer) throws LawyerExceptions {
        return extractor.extractLawyerText(lawyer, byRoleArray, "ROLE", LawyerExceptions::roleException);
    }

    private String[] getSocials(WebElement lawyer) {
        try {
            // Logic to find the corresponding details container based on data-ref
            String ref = lawyer.getAttribute("data-ref");
            if (ref == null) return new String[]{"", ""};

            WebElement detailsContainer = driver.findElement(
                    By.cssSelector(".moreInfoContainer div[data-ref='" + ref + "']")
            );

            List<WebElement> socialLinks = detailsContainer.findElements(By.cssSelector("a[href^='mailto'], a[href^='tel']"));
            return super.getSocials(socialLinks, false);
        } catch (Exception e) {
            System.err.println("Error getting socials: " + e.getMessage());
            return new String[]{"", ""};
        }
    }

    @Override
    public Object getLawyer(WebElement lawyer) throws Exception {
        String[] socials = this.getSocials(lawyer);

        return Map.of(
                "link", this.getLink(lawyer),
                "name", this.getName(lawyer),
                "role", this.getRole(lawyer),
                "firm", this.name,
                "country", "Italy",
                "practice_area", "",
                "email", socials[0],
                "phone", "390225060760"
        );
    }
}
