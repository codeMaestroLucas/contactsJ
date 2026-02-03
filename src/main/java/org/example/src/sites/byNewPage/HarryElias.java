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

public class HarryElias extends ByNewPage {
    private By[] byRole = {
            By.className("profile-title")
    };
    private String[] validRoles = { "partner", "counsel", "senior associate" };


    public HarryElias() {
        super(
                "Harry Elias",
                "https://www.harryelias.com/people/",
                4
        );
    }

    @Override
    protected void accessPage(int index) {
        String otherUrl = "https://www.harryelias.com/people/page/" + (index + 1) + "/";
        String url = index == 0 ? this.link : otherUrl;
        this.driver.get(url);
        MyDriver.waitForPageToLoad();
    }

    @Override
    protected List<WebElement> getLawyersInPage() {

        try {
            WebDriverWait wait = new WebDriverWait(this.driver, Duration.ofSeconds(10L));
            List<WebElement> lawyers = wait.until(
                    ExpectedConditions.presenceOfAllElementsLocatedBy(
                            By.cssSelector("div.profile-grid > a")
                    )
            );
            return siteUtl.filterLawyersInPage(lawyers, byRole, true, validRoles);
        } catch (Exception e) {
            throw new RuntimeException("Failed to find lawyer elements", e);
        }
    }

    @Override
    public String openNewTab(WebElement lawyer) throws LawyerExceptions {
        MyDriver.openNewTab(lawyer.getAttribute("href"));
        return "";
    }

    private String getLink(WebElement lawyer) {
        return this.link;
    }

    private String getName(WebElement lawyer) throws LawyerExceptions {
        By[] byArray = {By.className("profile-name")};
        return extractor.extractLawyerText(lawyer, byArray, "NAME", LawyerExceptions::nameException);
    }

    private String getRole(WebElement lawyer) throws LawyerExceptions {
        By[] byArray = {By.className("profile-title")};
        return extractor.extractLawyerText(lawyer, byArray, "ROLE", LawyerExceptions::roleException);
    }

    private String getPracticeArea(WebElement lawyer) throws LawyerExceptions {
        By[] byArray = {By.className("profile-practice")};
        return extractor.extractLawyerText(lawyer, byArray, "PRACTICE AREA", LawyerExceptions::practiceAreaException);
    }

    private String[] getSocials(WebElement lawyer) {
        try {
            List<WebElement> socials = lawyer.findElements(By.tagName("a"));
            return super.getSocials(socials, true);
        } catch (Exception e) {
            System.err.println("Error getting socials: " + e.getMessage());
            return new String[]{"", ""};
        }
    }

    @Override
    public Object getLawyer(WebElement lawyer) throws Exception {
        this.openNewTab(lawyer);

        WebElement nameDiv = driver.findElement(By.className("profile-banner-section"));
        WebElement socialDiv = driver.findElement(By.className("mobile-contact-social"));

        String[] socials = this.getSocials(socialDiv);

        return Map.of(
                "link", Objects.requireNonNull(driver.getCurrentUrl()),
                "name", this.getName(nameDiv),
                "role", this.getRole(nameDiv),
                "firm", this.name,
                "country", "Singapore",
                "practice_area", this.getPracticeArea(nameDiv),
                "email", socials[0],
                "phone", socials[1].isEmpty() ? "6565350550" : socials[1]
        );
    }
}