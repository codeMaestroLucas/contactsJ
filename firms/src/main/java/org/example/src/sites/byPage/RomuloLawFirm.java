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

public class RomuloLawFirm extends ByPage {
    private final By[] byRole = {
        By.cssSelector("div.wpb_text_column.wpb_content_element")
    };
    private final String[] validRoles = {"partner", "counsel", "senior associate"};


    public RomuloLawFirm() {
        super(
                "Romulo Law Firm",
                "https://www.romulo.com/all/",
                1
        );
    }

    @Override
    protected void accessPage(int index) throws InterruptedException {
        this.driver.get(this.link);
        MyDriver.waitForPageToLoad();
        // More 50 rolls
        MyDriver.rollDown(2, 0.5);
    }

    @Override
    protected List<WebElement> getLawyersInPage() {
        try {
            WebDriverWait wait = new WebDriverWait(this.driver, Duration.ofSeconds(10L));
            List<WebElement> lawyers = wait.until(
                    ExpectedConditions.presenceOfAllElementsLocatedBy(
                            By.cssSelector("div.col.span_12.dark.left")
                    )
            );
            return siteUtl.filterLawyersInPage(lawyers, byRole, true, validRoles);
        } catch (Exception e) {
            throw new RuntimeException("Failed to find lawyer elements", e);
        }
    }

    private String getLink(WebElement lawyer) throws LawyerExceptions {
        By[] byArray = {By.xpath(".//a[contains(text(), 'View Full Profile')]")};
        return extractor.extractLawyerAttribute(lawyer, byArray, "LINK", "href", LawyerExceptions::linkException);
    }

    private String getName(WebElement lawyer) throws LawyerExceptions {
        By[] byArray = {By.cssSelector("p > span[style*='font-weight: bold']")};
        return extractor.extractLawyerText(lawyer, byArray, "NAME", LawyerExceptions::nameException);
    }

    private String getRole(WebElement lawyer) throws LawyerExceptions {
        String role = extractor.extractLawyerText(lawyer, byRole, "ROLE", LawyerExceptions::roleException);

        for (String validRole : validRoles) {
            if (role.toLowerCase().contains(validRole.toLowerCase())) {
                return validRole; // return normalized role
            }
        }
        return role;
    }

    private String[] getSocials(WebElement lawyer) {
        List<WebElement> socials = lawyer.findElements(By.cssSelector("div.wpb_wrapper"));
        return super.getSocials(socials, true);
    }

    @Override
    public Object getLawyer(WebElement lawyer) throws Exception {
        String[] socials = this.getSocials(lawyer);
        String email = "";
        try {
            email = socials[0].split(": ")[1];
        } catch (Exception e) {
            email =  socials[0];
        }

        return Map.of(
                "link", this.getLink(lawyer),
                "name", this.getName(lawyer),
                "role", this.getRole(lawyer),
                "firm", this.name,
                "country", "the Philippines",
                "practice_area", "",
                "email", email,
                "phone", socials[1].isEmpty() ? "63285559555" : socials[1]
        );
    }
}