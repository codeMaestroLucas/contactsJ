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

public class DrewAndNapier extends ByPage {

    public DrewAndNapier() {
        super(
                "DrewAndNapier",
                "https://www.drewnapier.com/Our-Lawyers?name=",
                26,
                2
        );
    }

    @Override
    protected void accessPage(int index) {
        char letter = (char) ('a' + index);
        this.driver.get(this.link + letter);
        MyDriver.waitForPageToLoad();
    }

    @Override
    protected List<WebElement> getLawyersInPage() {
        try {
            WebDriverWait wait = new WebDriverWait(this.driver, Duration.ofSeconds(10L));
            return wait.until(
                    ExpectedConditions.presenceOfAllElementsLocatedBy(
                            By.className("lawyer-item")
                    )
            );
        } catch (Exception e) {
            System.out.println("No lawyers found for the current letter.");
            return List.of();
        }
    }

    private String getLink(WebElement lawyer) throws LawyerExceptions {
        By[] byArray = {By.cssSelector("h3 a")};
        String href = extractor.extractLawyerAttribute(lawyer, byArray, "LINK", "href", LawyerExceptions::linkException);
        return href.startsWith("http") ? href : "https://www.drewnapier.com/" + href;
    }

    private String getName(WebElement lawyer) throws LawyerExceptions {
        By[] byArray = {By.cssSelector("h3 a")};
        return extractor.extractLawyerText(lawyer, byArray, "NAME", LawyerExceptions::nameException);
    }

    private String getRole(WebElement lawyer) throws LawyerExceptions {
        By[] byArray = {By.cssSelector("div.designation p")};
        return extractor.extractLawyerText(lawyer, byArray, "ROLE", LawyerExceptions::roleException);
    }

    private String[] getSocials(WebElement lawyer) {
        try {
            List<WebElement> socials = lawyer.findElements(By.cssSelector("p.contact a"));
            return super.getSocials(socials, false);
        } catch (Exception e) {
            System.err.println("Error getting socials: " + e.getMessage());
            return new String[]{"", ""};
        }
    }

    @Override
    public Object getLawyer(WebElement lawyer) throws Exception {
        String[] socials = this.getSocials(lawyer);
        return Map.of(
                "link", this.getLink(lawyer),
                "name", this.getName(lawyer),
                "role", this.getRole(lawyer),
                "firm", this.name,
                "country", "Singapore",
                "practice_area", "",
                "email", socials[0],
                "phone", socials[1].isEmpty() ? "xxxxxx" : socials[1]
        );
    }
}