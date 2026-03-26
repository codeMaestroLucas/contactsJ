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

public class JBLaw extends ByNewPage {
    private final String[] validRoles = {"partner", "counsel", "senior associate"};

    public JBLaw() {
        super(
                "JB Law",
                "https://jblaw.nl/en/ons-team/",
                1
        );
    }

    @Override
    protected void accessPage(int index) {
        this.driver.get(this.link);
        MyDriver.waitForPageToLoad();
    }

    @Override
    protected List<WebElement> getLawyersInPage() {
        try {
            WebDriverWait wait = new WebDriverWait(this.driver, Duration.ofSeconds(10L));
            return wait.until(
                    ExpectedConditions.presenceOfAllElementsLocatedBy(
                            By.className("content")
                    )
            );
        } catch (Exception e) {
            throw new RuntimeException("Failed to find lawyer elements", e);
        }
    }

    @Override
    public String openNewTab(WebElement lawyer) throws LawyerExceptions {
        String link = extractor.extractLawyerAttribute(lawyer, new By[]{By.cssSelector("p.moreabout > a")}, "LINK", "href", LawyerExceptions::linkException);
        MyDriver.openNewTab(link);
        return link;
    }

    private String getName(WebElement lawyer) throws LawyerExceptions {
        By[] byArray = {By.tagName("h2")};
        return extractor.extractLawyerText(lawyer, byArray, "NAME", LawyerExceptions::nameException);
    }

    private String getRole(WebElement lawyer) throws LawyerExceptions {
        By[] byArray = {By.className("col-md-5")};
        String text = extractor.extractLawyerText(lawyer, byArray, "ROLE", LawyerExceptions::roleException);
        return siteUtl.isValidPosition(text, validRoles) ? "Partner" : "Invalid Role"; // Assuming from example
    }

    private String[] getSocials(WebElement lawyer) {
        List<WebElement> socials = lawyer.findElements(By.tagName("p"));
        return super.getSocials(socials, true);
    }

    @Override
    public Object getLawyer(WebElement lawyer) throws Exception {
        // Data from list page
        String name = this.getName(lawyer);
        String[] socials = this.getSocials(lawyer);

        // Open new tab to validate role
        this.openNewTab(lawyer);
        String role = this.getRole(driver.findElement(By.tagName("body")));

        if (role.equals("Invalid Role")) {
            return "Invalid Role";
        }

        return Map.of(
                "link", Objects.requireNonNull(driver.getCurrentUrl()),
                "name", name,
                "role", role,
                "firm", this.name,
                "country", "the Netherlands",
                "practice_area", "",
                "email", socials[0].replace("e ", ""),
                "phone", socials[1].isEmpty() ? "310203039400" : socials[1]
        );
    }
}