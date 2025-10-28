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

public class BentsiEnchillLetsaAndAnkomah extends ByPage {
    private final By[] byRoleArray = {
            By.cssSelector("h3 > a:last-of-type")
    };

    public BentsiEnchillLetsaAndAnkomah() {
        super(
                "Bentsi-Enchill Letsa & Ankomah",
                "https://bentsienchill.com/people/",
                1
        );
    }

    protected void accessPage(int index) throws InterruptedException {
        this.driver.get(this.link);
        MyDriver.waitForPageToLoad();
        Thread.sleep(1000L);

        MyDriver.clickOnElementMultipleTimes(By.className("alm-load-more-btn"), 3, 0.5);
    }

    protected List<WebElement> getLawyersInPage() {
        String[] validRoles = {
                "partner",
                "senior associate"
        };

        try {
            WebDriverWait wait = new WebDriverWait(this.driver, Duration.ofSeconds(10L));
            List<WebElement> lawyers = wait.until(
                    ExpectedConditions.presenceOfAllElementsLocatedBy(
                            By.className("alm-item")
                    )
            );
            return this.siteUtl.filterLawyersInPage(lawyers, byRoleArray, true, validRoles);
        } catch (Exception e) {
            throw new RuntimeException("Failed to find lawyer elements", e);
        }
    }

    private String getLink(WebElement lawyer) throws LawyerExceptions {
        By[] byArray = {By.cssSelector("h3 > a:first-of-type")};
        return extractor.extractLawyerAttribute(lawyer, byArray, "LINK", "href", LawyerExceptions::linkException);
    }

    private String getName(WebElement lawyer) throws LawyerExceptions {
        By[] byArray = {By.cssSelector("h3 > a:nth-of-type(2)")};
        return extractor.extractLawyerText(lawyer, byArray, "NAME", LawyerExceptions::nameException);
    }

    private String getRole(WebElement lawyer) throws LawyerExceptions {
        return extractor.extractLawyerText(lawyer, byRoleArray, "ROLE", LawyerExceptions::roleException);
    }

    private String[] getSocials(WebElement lawyer, String name) {
        try {
            name = TreatLawyerParams.treatName(name);
            String[] names = name.split(" ");
            String firstNameLetter = String.valueOf(names[0].charAt(0));
            String lastName = names[names.length - 1];
            String email = (firstNameLetter + lastName).replace("-", "") + "@bentsienchill.com";
            return new String[]{email, ""};
        } catch (Exception e) {
            System.err.println("Error getting socials: " + e.getMessage());
            return new String[]{"", ""};
        }
    }

    public Object getLawyer(WebElement lawyer) throws Exception {
        String name = this.getName(lawyer);
        String[] socials = this.getSocials(lawyer, name);

        return Map.of(
                "link", this.getLink(lawyer),
                "name", name,
                "role", this.getRole(lawyer),
                "firm", this.name,
                "country", "Ghana",
                "practice_area", "",
                "email", socials[0],
                "phone", "233302208888"
        );
    }
}