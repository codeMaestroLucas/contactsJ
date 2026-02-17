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
import java.util.Objects;

public class WongPartnership extends ByPage {

    private final By[] byRoleArray = {
            By.cssSelector(".partner-item span")
    };

    public WongPartnership() {
        super(
                "Wong Partnership",
                "",
                9
        );
    }

    private final String[] otherLinks = {
            "china",
            "india",
            "indonesia",
            "malaysia",
            "middle-east",
            "myanmar",
            "philippines",
            "thailand",
            "vietnam",
    };

    @Override
    protected void accessPage(int index) throws InterruptedException {
        this.driver.get("https://www.wongpartnership.com/wpgnetwork-detail/" + otherLinks[index]);
        MyDriver.waitForPageToLoad();
        Thread.sleep(1000L);
    }

    @Override
    protected List<WebElement> getLawyersInPage() {
        try {
            WebDriverWait wait = new WebDriverWait(this.driver, Duration.ofSeconds(10L));
            return wait.until(
                    ExpectedConditions.presenceOfAllElementsLocatedBy(By.className("partner-item"))
            );
        } catch (Exception e) {
            throw new RuntimeException("Failed to find lawyer elements", e);
        }
    }

    private String getName(WebElement lawyer) throws LawyerExceptions {
        By[] byArray = {By.tagName("h4")};
        return extractor.extractLawyerAttribute(lawyer, byArray, "NAME", "textContent", LawyerExceptions::nameException);
    }

    private String getRole(WebElement lawyer) throws LawyerExceptions {
        return extractor.extractLawyerAttribute(lawyer, byRoleArray, "ROLE", "textContent", LawyerExceptions::roleException);
    }

    private String[] getSocials(WebElement lawyer) {
        try {
            List<WebElement> socials = lawyer.findElements(By.tagName("p"));
            return super.getSocials(socials, true);
        } catch (Exception e) {
            return new String[]{"", ""};
        }
    }

    @Override
    public Object getLawyer(WebElement lawyer) throws Exception {
        String[] socials = this.getSocials(lawyer);

        return Map.of(
                "link", Objects.requireNonNull(driver.getCurrentUrl()),
                "name", this.getName(lawyer),
                "role", this.getRole(lawyer),
                "firm", this.name,
                "country", this.getCountry(),
                "practice_area", "",
                "email", socials[0],
                "phone", socials[1]
        );
    }

    private String getCountry() {
        String sLink = Objects.requireNonNull(driver.getCurrentUrl()).toLowerCase();

        if (sLink.contains("singapore")) return "Singapore";
        if (sLink.contains("china")) return "China";
        if (sLink.contains("india")) return "India";
        if (sLink.contains("indonesia")) return "Indonesia";
        if (sLink.contains("malaysia")) return "Malaysia";
        if (sLink.contains("middle-east")) return "the UAE";
        if (sLink.contains("myanmar")) return "Myanmar";
        if (sLink.contains("philippines")) return "the Philippines";
        if (sLink.contains("thailand")) return "Thailand";
        if (sLink.contains("vietnam")) return "Vietnam";

        return "Unknown";
    }

}
