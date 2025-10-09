package org.example.src.sites._standingBy.toAvoidForNow;

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

public class CityYuwaPartners extends ByPage {
    private final By[] byRoleArray = {
            By.className("position")
    };


    public CityYuwaPartners() {
        super(
                "City-Yuwa Partners",
                "https://www.city-yuwa.com/global/en/attorneys/",
                1,
                15
        );
    }


    protected void accessPage(int index) throws InterruptedException {
        this.driver.get(this.link);
        MyDriver.waitForPageToLoad();
        Thread.sleep(1000L);
    }


    protected List<WebElement> getLawyersInPage() {
        String[] validRoles = new String[]{
                "partner"
        };

        try {
            WebDriverWait wait = new WebDriverWait(this.driver, Duration.ofSeconds(10L));
            List<WebElement> lawyers = wait.until(
                    ExpectedConditions.presenceOfAllElementsLocatedBy(
                            By.cssSelector("div.attorneys-list-box > a")
                    )
            );
            return this.siteUtl.filterLawyersInPage(lawyers, byRoleArray, true, validRoles);

        } catch (Exception e) {
            throw new RuntimeException("Failed to find lawyer elements", e);
        }
    }


    private String getLink(WebElement lawyer) throws LawyerExceptions {
        return extractor.extractLawyerAttribute(lawyer, new By[]{}, "LINK", "href", LawyerExceptions::linkException);
    }


    private String getName(WebElement lawyer) throws LawyerExceptions {
        By[] byArray = new By[]{
                By.className("name-jp")
        };
        // Name format is "LastName, FirstName", so we reorder it.
        String name = extractor.extractLawyerText(lawyer, byArray, "NAME", LawyerExceptions::nameException);
        String[] parts = name.split(",\\s*");
        return (parts.length > 1) ? parts[1] + " " + parts[0] : name;
    }


    private String getRole(WebElement lawyer) throws LawyerExceptions {
        return extractor.extractLawyerText(lawyer, byRoleArray, "ROLE", LawyerExceptions::roleException);
    }

    private String constructEmail(String fullName) {
        if (fullName == null || fullName.isEmpty()) {
            return "";
        }
        // Name should be "FirstName LastName" at this point
        String[] nameParts = fullName.toLowerCase().trim().split("\\s+");
        if (nameParts.length < 2) {
            return "";
        }
        String firstName = nameParts[0];
        String lastName = nameParts[nameParts.length - 1];
        return firstName + "." + lastName + "@city-yuwa.com";
    }

    public Object getLawyer(WebElement lawyer) throws Exception {
        String name = this.getName(lawyer);
        String email = this.constructEmail(name);

        return Map.of(
                "link", this.getLink(lawyer),
                "name", name,
                "role", this.getRole(lawyer),
                "firm", this.name,
                "country", "Japan",
                "practice_area", "",
                "email", email,
                "phone", "xxxxxx"
        );
    }
}