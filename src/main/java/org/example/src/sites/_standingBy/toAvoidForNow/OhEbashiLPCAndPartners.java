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

public class OhEbashiLPCAndPartners extends ByPage {

    public OhEbashiLPCAndPartners() {
        super(
                "Oh-Ebashi LPC And Partners",
                "https://www.ohebashi.com/en/lawyers/position.php#p01",
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
        try {
            WebDriverWait wait = new WebDriverWait(this.driver, Duration.ofSeconds(10L));
            WebElement partnerSection = wait.until(
                    ExpectedConditions.presenceOfElementLocated(By.id("p01"))
            );
            return partnerSection.findElements(By.cssSelector("li > a"));
        } catch (Exception e) {
            throw new RuntimeException("Failed to find lawyer elements", e);
        }
    }


    private String getLink(WebElement lawyer) throws LawyerExceptions {
        return extractor.extractLawyerAttribute(lawyer, new By[]{}, "LINK", "href", LawyerExceptions::linkException);
    }


    private String getName(WebElement lawyer) throws LawyerExceptions {
        By[] byArray = new By[]{
                By.tagName("span")
        };
        return extractor.extractLawyerText(lawyer, byArray, "NAME", LawyerExceptions::nameException);
    }

    private String constructEmail(String name) {
        if (name == null || name.isEmpty()) {
            return "";
        }
        String cleanedName = name.replaceAll("\\(.*?\\)", "").trim();
        String[] nameParts = cleanedName.toLowerCase().trim().split("\\s+");
        if (nameParts.length < 2) {
            return "";
        }
        String firstName = nameParts[0];
        String lastName = nameParts[nameParts.length - 1];
        return firstName + "." + lastName + "@ohebashi.com";
    }

    public Object getLawyer(WebElement lawyer) throws Exception {
        String name = this.getName(lawyer);
        String email = this.constructEmail(name);

        return Map.of(
                "link", this.getLink(lawyer),
                "name", name,
                "role", "Partner",
                "firm", this.name,
                "country", "Japan",
                "practice_area", "",
                "email", email,
                "phone", "xxxxxx"
        );
    }
}