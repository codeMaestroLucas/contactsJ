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

public class RoedlAndPartner extends ByPage {
    private final By[] byRoleArray = {
            By.cssSelector("div.ms-rtestate-field")
    };

    public RoedlAndPartner() {
        super(
                "Rödl And Partner",
                "https://www.roedl.lt/en/team/",
                1
        );
    }

    protected void accessPage(int index) {
        this.driver.get(this.link);
        MyDriver.waitForPageToLoad();
    }

    protected List<WebElement> getLawyersInPage() {
        String[] validRoles = {"partner", "head of"};

        try {
            WebDriverWait wait = new WebDriverWait(this.driver, Duration.ofSeconds(10L));
            List<WebElement> lawyers = wait.until(
                    ExpectedConditions.presenceOfAllElementsLocatedBy(
                            By.className("ms-webpart-chrome")
                    )
            );
            return this.siteUtl.filterLawyersInPage(lawyers, byRoleArray, true, validRoles);
        } catch (Exception e) {
            throw new RuntimeException("Failed to find lawyer elements", e);
        }
    }

    private String getLink(WebElement lawyer) throws LawyerExceptions {
        By[] byArray = {By.cssSelector("a[href*='/Pages/']")};
        return extractor.extractLawyerAttribute(lawyer, byArray, "LINK", "href", LawyerExceptions::linkException);
    }

    private String getName(WebElement lawyer) throws LawyerExceptions {
        By[] byArray = {By.className("ms-webpart-titleText")};
        return extractor.extractLawyerText(lawyer, byArray, "NAME", LawyerExceptions::nameException);
    }

    private String getRole(WebElement lawyer) throws LawyerExceptions {
        // The role is usually in the first or second paragraph of the text field
        String fullText = extractor.extractLawyerText(lawyer, byRoleArray, "ROLE", LawyerExceptions::roleException);
        String[] lines = fullText.split("\n");
        for (String line : lines) {
            if (siteUtl.isValidPosition(line, new String[]{"partner", "head", "audit", "senior associate"})) {
                return line.trim();
            }
        }
        return "Invalid Role";
    }

    private String[] getSocials(WebElement lawyer) {
        String email = "";
        String phone = "";
        try {
            email = lawyer.findElement(By.cssSelector("a[href^='mailto:']")).getAttribute("href").replace("mailto:", "");
            String fullText = lawyer.findElement(By.className("ms-rtestate-field")).getText();
            if (fullText.contains("Phone:")) {
                phone = fullText.split("Phone:")[1].split("\n")[0].trim();
            }
        } catch (Exception e) {
            // Social not found, ignore
        }
        return new String[]{email, phone};
    }

    public Object getLawyer(WebElement lawyer) throws Exception {
        String[] socials = this.getSocials(lawyer);
        String role = this.getRole(lawyer);
        if (role.equals("Invalid Role")) return "Invalid Role";

        return Map.of(
                "link", this.getLink(lawyer),
                "name", this.getName(lawyer),
                "role", role,
                "firm", this.name,
                "country", "Lithuania",
                "practice_area", "",
                "email", socials[0],
                "phone", socials[1].isEmpty() ? "37052123590" : socials[1]
        );
    }
}