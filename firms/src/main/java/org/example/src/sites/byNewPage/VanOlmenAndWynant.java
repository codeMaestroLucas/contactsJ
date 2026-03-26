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

public class VanOlmenAndWynant extends ByNewPage {
    private final By[] byRoleArray = {
            By.className("uk-label")
    };

    public VanOlmenAndWynant() {
        super(
                "Van Olmen & Wynant",
                "https://www.vow.be/en/lawyers",
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
                            By.className("uk-inline-clip")
                    )
            );
            return this.siteUtl.filterLawyersInPage(lawyers, byRoleArray, true, validRoles);
        } catch (Exception e) {
            throw new RuntimeException("Failed to find lawyer elements", e);
        }
    }

    public String openNewTab(WebElement lawyer) throws LawyerExceptions {
        try {
            String link = lawyer.findElement(By.tagName("a")).getAttribute("href");
            MyDriver.openNewTab(link);
        } catch (Exception e) {
            throw LawyerExceptions.linkException("Could not find link element");
        }
        return null;
    }

    private String getName(WebElement lawyer) throws LawyerExceptions {
        try {
            String firstName = lawyer.findElement(By.className("field--name-field-first-name")).getText();
            String lastName = lawyer.findElement(By.className("field--name-field-last-name")).getText();
            return firstName + " " + lastName;
        } catch (Exception e) {
            throw LawyerExceptions.nameException("Could not find name elements");
        }
    }

    private String getRole(WebElement lawyer) throws LawyerExceptions {
        By[] byArray = {By.className("field--name-field-function")};
        return extractor.extractLawyerText(lawyer, byArray, "ROLE", LawyerExceptions::roleException);
    }

    private String getPracticeArea(WebElement lawyer) {
        try {
            return lawyer.findElement(By.className("field--name-field-related-practices")).getText();
        } catch (Exception e) {
            return "";
        }
    }

    private String[] getSocials(WebElement lawyer) {
        String email = "";
        String phone = "";
        try {
            email = lawyer.findElement(By.cssSelector(".field--name-field-email a")).getAttribute("href");
            phone = lawyer.findElement(By.cssSelector(".field--name-field-phone .field__item")).getText();
        } catch (Exception e) {
            // Socials not found
        }
        return new String[]{email, phone};
    }

    public Object getLawyer(WebElement lawyer) throws Exception {
        this.openNewTab(lawyer);
        WebElement div = driver.findElement(By.className("group-right"));

        String[] socials = this.getSocials(div);

        return Map.of(
                "link", Objects.requireNonNull(driver.getCurrentUrl()),
                "name", this.getName(div),
                "role", this.getRole(div),
                "firm", this.name,
                "country", "Belgium",
                "practice_area", this.getPracticeArea(div),
                "email", socials[0],
                "phone", socials[1].isEmpty() ? "3226440511" : socials[1]
        );
    }
}