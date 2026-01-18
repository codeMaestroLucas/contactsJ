package org.example.src.sites.byPage;

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

public class StoneKing extends ByPage {
    private final By[] byRoleArray = {
            By.xpath("//article//div[contains(@class,'field--name-field-job-title')]")
    };

    public StoneKing() {
        super(
                "Stone King",
                "https://www.stoneking.co.uk/our-people",
                1
        );
    }

    @Override
    protected void accessPage(int index) throws InterruptedException {
        this.driver.get(this.link);
        MyDriver.waitForPageToLoad();
        MyDriver.clickOnAddBtn(By.id("ccc-recommended-settings"));
    }

    @Override
    protected List<WebElement> getLawyersInPage() {
        String[] validRoles = {"partner", "chair", "ceo", "head", "senior associate"};
        try {
            WebDriverWait wait = new WebDriverWait(this.driver, Duration.ofSeconds(10L));
            List<WebElement> lawyers = wait.until(
                    ExpectedConditions.presenceOfAllElementsLocatedBy(
                            By.tagName("article")
                    )
            );
            return this.siteUtl.filterLawyersInPage(lawyers, byRoleArray, true, validRoles);
        } catch (Exception e) {
            throw new RuntimeException("Failed to find lawyer elements", e);
        }
    }

    private String getLink(WebElement lawyer) throws LawyerExceptions {
        By[] byArray = {By.cssSelector("a[href*='/our-people/']")};
        return extractor.extractLawyerAttribute(lawyer, byArray, "LINK", "href", LawyerExceptions::linkException);
    }

    private String getName(WebElement lawyer) throws LawyerExceptions {
        By[] byArray = {By.xpath("//article//div[contains(@class,'text-lg')]")};
        return extractor.extractLawyerText(lawyer, byArray, "NAME", LawyerExceptions::nameException);
    }

    private String getRole(WebElement lawyer) throws LawyerExceptions {
        return extractor.extractLawyerText(lawyer, byRoleArray, "ROLE", LawyerExceptions::roleException);
    }

    private String[] getSocials(WebElement lawyer, String name) {
        try {
            List<WebElement> phones = lawyer.findElements(By.cssSelector(".field--name-field-telephone a"));
            String phone = "";
            if (!phones.isEmpty()) {
                phone = phones.get(0).getText();
            }

            // Email format (firstName)(LastName)@stoneking.co.uk
            String cleanName = TreatLawyerParams.treatName(name);
            String[] parts = cleanName.split(" ");
            String email = "";

            if (parts.length >= 1) {
                String firstName = parts[0].toLowerCase();
                String lastName = parts[parts.length - 1].toLowerCase();
                email = firstName + lastName + "@stoneking.co.uk";
            }

            return new String[]{email, phone};
        } catch (Exception e) {
            System.err.println("Error getting socials: " + e.getMessage());
            return new String[]{"", ""};
        }
    }

    @Override
    public Object getLawyer(WebElement lawyer) throws Exception {
        String name = this.getName(lawyer);
        String[] socials = this.getSocials(lawyer, name);

        return Map.of(
                "link", this.getLink(lawyer),
                "name", name,
                "role", this.getRole(lawyer),
                "firm", this.name,
                "country", "England",
                "practice_area", "",
                "email", socials[0],
                "phone", socials[1].isEmpty() ? "xxxxxx" : socials[1]
        );
    }
}
