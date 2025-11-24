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

public class Kallan extends ByPage {
    private final By[] byRoleArray = {
            By.className("name")
    };

    public Kallan() {
        super(
                "Kallan",
                "https://www.kallan-legal.de/en/ansprechpartner/",
                1
        );
    }

    protected void accessPage(int index) {
        this.driver.get(this.link);
        MyDriver.waitForPageToLoad();
    }

    protected List<WebElement> getLawyersInPage() {
        String[] validRoles = {"partner", "counsel"};

        try {
            WebDriverWait wait = new WebDriverWait(this.driver, Duration.ofSeconds(10L));
            List<WebElement> lawyers = wait.until(
                    ExpectedConditions.presenceOfAllElementsLocatedBy(
                            By.className("ansprechpartner")
                    )
            );
            return this.siteUtl.filterLawyersInPage(lawyers, byRoleArray, true, validRoles);
        } catch (Exception e) {
            throw new RuntimeException("Failed to find lawyer elements", e);
        }
    }

    private String getLink(WebElement lawyer) throws LawyerExceptions {
        By[] byArray = {By.cssSelector("a.image")};
        return extractor.extractLawyerAttribute(lawyer, byArray, "LINK", "href", LawyerExceptions::linkException);
    }

    private String getName(WebElement lawyer) throws LawyerExceptions {
        By[] byArray = {By.cssSelector("div.name strong")};
        return extractor.extractLawyerText(lawyer, byArray, "NAME", LawyerExceptions::nameException);
    }

    private String getRole(WebElement lawyer) throws LawyerExceptions {
        try {
            // Role is in div.name text, usually after <br>
            String text = lawyer.findElement(By.className("name")).getText();
            // Split by newline and take 2nd line or check for role keywords
            String[] lines = text.split("\n");
            for (String line : lines) {
                if (siteUtl.isValidPosition(line, new String[]{"Partner", "Counsel", "Associate"})) {
                    return line;
                }
            }
            return text; // Fallback
        } catch (Exception e) {
            throw LawyerExceptions.roleException("Could not extract role");
        }
    }

    private String[] getSocials(WebElement lawyer) {
        String email = "";
        String phone = "";
        try {
            WebElement contactDiv = lawyer.findElement(By.className("contact"));
            phone = contactDiv.getText().split("\n")[0].replace("T", "").trim();
            email = contactDiv.findElement(By.tagName("a")).getText();
        } catch (Exception e) {
            // Socials not found
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
                "country", "Germany",
                "practice_area", "",
                "email", socials[0],
                "phone", socials[1].isEmpty() ? "49302266990" : socials[1]
        );
    }
}