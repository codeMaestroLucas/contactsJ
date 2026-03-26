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

public class TheartMey extends ByPage {
    private final By[] byRoleArray = {
            By.cssSelector("h4 b")
    };

    public TheartMey() {
        super(
                "Theart Mey",
                "https://www.theartmey.co.za/Our-Team",
                1
        );
    }

    protected void accessPage(int index) {
        this.driver.get(this.link);
        MyDriver.waitForPageToLoad();

        // Load more btn
        MyDriver.clickOnElement(By.id("dnn_ctr161822_2020_PHF_Team_ctl00_Panel1"));
    }

    protected List<WebElement> getLawyersInPage() {
        String[] validRoles = {"director"};

        try {
            WebDriverWait wait = new WebDriverWait(this.driver, Duration.ofSeconds(10L));
            List<WebElement> lawyers = wait.until(
                    ExpectedConditions.presenceOfAllElementsLocatedBy(
                            By.cssSelector("div.ContentPainInnerBorder.TeamLoadImg + .col-md-7")
                    )
            );
            return this.siteUtl.filterLawyersInPage(lawyers, byRoleArray, true, validRoles);
        } catch (Exception e) {
            throw new RuntimeException("Failed to find lawyer elements", e);
        }
    }

    private String getLink(WebElement lawyer) throws LawyerExceptions {
        By[] byArray = {By.className("button-02")};
        return extractor.extractLawyerAttribute(lawyer, byArray, "LINK", "href", LawyerExceptions::linkException);
    }

    private String getName(WebElement lawyer) throws LawyerExceptions {
        By[] byArray = {By.cssSelector("h2 b")};
        return extractor.extractLawyerText(lawyer, byArray, "NAME", LawyerExceptions::nameException);
    }

    private String getRole(WebElement lawyer) throws LawyerExceptions {
        return extractor.extractLawyerText(lawyer, byRoleArray, "ROLE", LawyerExceptions::roleException);
    }

    private String[] getSocials(WebElement lawyer) {
        String email = "";
        String phone = "";
        try {
            // Navigate to the parent, then to the contact info sibling div
            WebElement contactDiv = lawyer.findElement(By.xpath("./following-sibling::div"));
            email = contactDiv.findElement(By.cssSelector("a[href^='mailto:']")).getAttribute("href");

            List<WebElement> contactRows = contactDiv.findElements(By.className("row"));
            for (WebElement row : contactRows) {
                if (row.getText().matches(".*\\d{5,}.*")) { // Find text that looks like a phone number
                    phone = row.getText().trim();
                    break;
                }
            }
        } catch (Exception e) {
            System.err.println("Error getting socials: " + e.getMessage());
        }
        return new String[]{email, phone};
    }

    public Object getLawyer(WebElement lawyer) throws Exception {
        String[] socials = this.getSocials(lawyer);

        return Map.of(
                "link", this.getLink(lawyer),
                "name", this.getName(lawyer),
                "role", this.getRole(lawyer),
                "firm", this.name,
                "country", "South Africa",
                "practice_area", "",
                "email", socials[0],
                "phone", socials[1].isEmpty() ? "0860018155" : socials[1]
        );
    }
}