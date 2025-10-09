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

public class HiswaraBunjaminAndTandjung extends ByPage {

    public HiswaraBunjaminAndTandjung() {
        super(
                "Hiswara BunjaminAndTandjung",
                "https://www.hbtlaw.com/our-people",
                1
        );
    }

    @Override
    protected void accessPage(int index) {
        this.driver.get(this.link);
        MyDriver.waitForPageToLoad();
    }

    @Override
    protected List<WebElement> getLawyersInPage() {
        try {
            WebDriverWait wait = new WebDriverWait(this.driver, Duration.ofSeconds(10L));
            return wait.until(
                    ExpectedConditions.presenceOfAllElementsLocatedBy(
                            By.className("profile-info")
                    )
            );
        } catch (Exception e) {
            throw new RuntimeException("Failed to find lawyer elements", e);
        }
    }

    private String getLink(WebElement lawyer) throws LawyerExceptions {
        By[] byArray = {
                By.className("profile-link")
        };
        return extractor.extractLawyerAttribute(lawyer, byArray, "LINK", "href", LawyerExceptions::linkException);
    }

    private String getName(WebElement lawyer) throws LawyerExceptions {
        By[] byArray = {
                By.className("profile-link")
        };
        return extractor.extractLawyerText(lawyer, byArray, "NAME", LawyerExceptions::nameException);
    }

    private String getRole(WebElement lawyer) throws LawyerExceptions {
        By[] byArray = {
                By.className("desc")
        };
        String fullDesc = extractor.extractLawyerText(lawyer, byArray, "ROLE", LawyerExceptions::roleException);
        return fullDesc.split(",")[0].trim();
    }

    private String[] getSocials(WebElement lawyer) {
        String email = "";
        String phone = "";
        try {
            email = lawyer.findElement(By.className("email")).getAttribute("href");
            phone = lawyer.findElement(By.cssSelector("ul li a")).getAttribute("href");
        } catch (Exception e) {
            System.err.println("Error getting socials: " + e.getMessage());
        }
        return new String[]{email, phone};
    }

    @Override
    public Object getLawyer(WebElement lawyer) throws Exception {
        String[] socials = this.getSocials(lawyer);
        return Map.of(
                "link", this.getLink(lawyer),
                "name", this.getName(lawyer),
                "role", this.getRole(lawyer),
                "firm", this.name,
                "country", "Indonesia",
                "practice_area", "",
                "email", socials[0],
                "phone", socials[1].isEmpty() ? "xxxxxx" : socials[1]
        );
    }
}