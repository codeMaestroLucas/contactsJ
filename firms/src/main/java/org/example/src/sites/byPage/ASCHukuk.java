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

public class ASCHukuk extends ByPage {
    private final By[] byRoleArray = {
            By.cssSelector(".lawyers-box-header span")
    };

    public ASCHukuk() {
        super(
                "ASC hukuk",
                "https://www.aschukuk.com/en/team",
                1
        );
    }

    @Override
    protected void accessPage(int index) throws InterruptedException {
        this.driver.get(this.link);
        MyDriver.waitForPageToLoad();
    }

    @Override
    protected List<WebElement> getLawyersInPage() {
        String[] validRoles = {"partner", "ceo", "senior associate"};
        try {
            WebDriverWait wait = new WebDriverWait(this.driver, Duration.ofSeconds(10L));
            List<WebElement> lawyers = wait.until(
                    ExpectedConditions.presenceOfAllElementsLocatedBy(
                            By.className("lawyers-box")
                    )
            );
            return this.siteUtl.filterLawyersInPage(lawyers, byRoleArray, true, validRoles);
        } catch (Exception e) {
            throw new RuntimeException("Failed to find lawyer elements", e);
        }
    }

    private String getLink(WebElement lawyer) throws LawyerExceptions {
        By[] byArray = {By.cssSelector("a[href*='https://www.aschukuk.com/en/team/https://www.aschukuk.com/en/team/']")};
        String lawyerLink = extractor.extractLawyerAttribute(lawyer, byArray, "LINK", "href", LawyerExceptions::linkException);
        return lawyerLink.isEmpty() ? this.link : lawyerLink;
    }

    private String getName(WebElement lawyer) throws LawyerExceptions {
        By[] byArray = {By.tagName("h3")};
        return extractor.extractLawyerText(lawyer, byArray, "NAME", LawyerExceptions::nameException);
    }

    private String getRole(WebElement lawyer) throws LawyerExceptions {
        return extractor.extractLawyerText(lawyer, byRoleArray, "ROLE", LawyerExceptions::roleException);
    }

    private String[] getSocials(WebElement lawyer, String name) {
        try {
            List<WebElement> socials = lawyer.findElements(By.cssSelector(".lawyers-box-social a"));
            String[] extracted = super.getSocials(socials, false);

            if (extracted[0].isEmpty() && name != null) {
                String cleanName = TreatLawyerParams.treatName(name);
                String[] parts = cleanName.split(" ");
                if (parts.length >= 1) {
                    String firstName = parts[0].toLowerCase();
                    String lastName = parts[parts.length - 1].toLowerCase();
                    extracted[0] = firstName + "." + lastName + "@aschukuk.com";
                }
            }
            return extracted;
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
                "country", "Turkey",
                "practice_area", "",
                "email", socials[0],
                "phone", "902122849882"
        );
    }
}
