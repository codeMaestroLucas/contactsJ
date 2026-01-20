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

public class AlstonAndBirdLLP extends ByPage {
    private final By[] byRoleArray = {
            By.className("contact-level")
    };

    private String currentCountry = "Brussels";

    public AlstonAndBirdLLP() {
        super(
                "Alston and Bird LLP",
                "https://www.alston.com/en/professionals?office=499cdc01-9d0b-4414-a6c1-b3f8a8bae680&reload=false&scroll=462",
                2
        );
    }

    @Override
    protected void accessPage(int index) throws InterruptedException {
        String otherUrl = index == 0 ? this.link : "https://www.alston.com/en/professionals?office=c8a737cf-f50c-403f-8afe-12ae745f9b9a&skip=30&reload=false&scroll=4408";
        currentCountry = index == 0 ? currentCountry : "England";

        this.driver.get(otherUrl);
        MyDriver.waitForPageToLoad();
        Thread.sleep(1000L);
    }
    @Override
    protected List<WebElement> getLawyersInPage() {
        String[] validRoles = {"partner", "counsel", "senior associate"};
        try {
            WebDriverWait wait = new WebDriverWait(this.driver, Duration.ofSeconds(10L));
            List<WebElement> lawyers = wait.until(
                    ExpectedConditions.presenceOfAllElementsLocatedBy(
                            By.className("contact-card")
                    )
            );
            return this.siteUtl.filterLawyersInPage(lawyers, byRoleArray, true, validRoles);
        } catch (Exception e) {
            throw new RuntimeException("Failed to find lawyer elements", e);
        }
    }

    private String getLink(WebElement lawyer) throws LawyerExceptions {
        By[] byArray = {By.className("contact-name"), By.tagName("a")};
        return extractor.extractLawyerAttribute(lawyer, byArray, "LINK", "href", LawyerExceptions::linkException);
    }

    private String getName(WebElement lawyer) throws LawyerExceptions {
        By[] byArray = {By.className("contact-name")};
        return extractor.extractLawyerText(lawyer, byArray, "NAME", LawyerExceptions::nameException);
    }

    private String getRole(WebElement lawyer) throws LawyerExceptions {
        return extractor.extractLawyerText(lawyer, byRoleArray, "ROLE", LawyerExceptions::roleException);
    }

    private String[] getSocials(WebElement lawyer) {
        try {
            List<WebElement> socials = lawyer.findElements(By.cssSelector(".contact-details a"));
            return super.getSocials(socials, false);
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
                "country", this.currentCountry,
                "practice_area", "",
                "email", socials[0],
                "phone", socials[1].isEmpty() ? "" : socials[1]
        );
    }
}
