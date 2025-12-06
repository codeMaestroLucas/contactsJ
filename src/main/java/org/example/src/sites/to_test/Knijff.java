package org.example.src.sites.to_test;

import org.example.exceptions.LawyerExceptions;
import org.example.src.entities.BaseSites.ByPage;
import org.example.src.entities.MyDriver;
import org.example.src.utils.TreatLawyerParams;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.time.Duration;
import java.util.List;
import java.util.Map;

public class Knijff extends ByPage {
    private final By[] byRoleArray = {
            By.className("list-item-content__description")
    };

    public Knijff() {
        super(
                "Knijff",
                "https://www.knijff.com/en/team",
                1
        );
    }

    protected void accessPage(int index) {
        this.driver.get(this.link);
        MyDriver.waitForPageToLoad();
    }

    protected List<WebElement> getLawyersInPage() {
        String[] validRoles = {"partner"};

        try {
            WebDriverWait wait = new WebDriverWait(this.driver, Duration.ofSeconds(10L));
            List<WebElement> lawyers = wait.until(
                    ExpectedConditions.presenceOfAllElementsLocatedBy(
                            By.className("list-item")
                    )
            );
            return this.siteUtl.filterLawyersInPage(lawyers, byRoleArray, true, validRoles);
        } catch (Exception e) {
            throw new RuntimeException("Failed to find lawyer elements", e);
        }
    }

    private String getLink(WebElement lawyer) {
        // Snippet doesn't show a clear profile link on the card container easily accessible for automation in some layouts,
        // but text says "Read Daan's story>".
        try {
            return lawyer.findElement(By.partialLinkText("Read")).getAttribute("href");
        } catch (Exception e) {
            return this.link;
        }
    }

    private String getName(WebElement lawyer) throws LawyerExceptions {
        By[] byArray = {By.className("list-item-content__title")};
        return extractor.extractLawyerText(lawyer, byArray, "NAME", LawyerExceptions::nameException);
    }

    private String getRole(WebElement lawyer) throws LawyerExceptions {
        return extractor.extractLawyerText(lawyer, byRoleArray, "ROLE", LawyerExceptions::roleException);
    }

    private String[] getSocials(String name) {
        String email = "";
        // Generate email: (firstNameLetter).(LastName)@knijff.com
        // Name: Daan Teeuwissen
        name = TreatLawyerParams.treatNameForEmail(name);
        try {
            String[] parts = name.trim().split(" ");
            if (parts.length >= 2) {
                String firstLetter = parts[0].substring(0, 1).toLowerCase();
                String lastName = parts[parts.length - 1].toLowerCase();
                email = firstLetter + "." + lastName + "@knijff.com";
            }
        } catch (Exception e) {
            // Error
        }
        return new String[]{email, ""};
    }

    public Object getLawyer(WebElement lawyer) throws Exception {
        String name = this.getName(lawyer);
        String[] socials = this.getSocials(name);

        return Map.of(
                "link", this.getLink(lawyer),
                "name", name,
                "role", this.getRole(lawyer),
                "firm", this.name,
                "country", "the Netherlands",
                "practice_area", "",
                "email", socials[0],
                "phone", "31294490900"
        );
    }
}
