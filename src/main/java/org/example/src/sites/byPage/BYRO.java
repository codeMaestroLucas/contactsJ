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

public class BYRO extends ByPage {
    public BYRO() {
        super(
                "BYRO",
                "https://www.byro.legal/team",
                1
        );
    }

    protected void accessPage(int index) {
        this.driver.get(this.link);
        MyDriver.waitForPageToLoad();
        MyDriver.clickOnAddBtn(By.cssSelector("button.accept.sqs-button-element--primary.sqs-cookie-banner-v2-accept.sqs-cookie-banner-v2-cta"));
    }

    protected List<WebElement> getLawyersInPage() {
        try {
            WebDriverWait wait = new WebDriverWait(this.driver, Duration.ofSeconds(10L));
            return wait.until(
                    ExpectedConditions.presenceOfAllElementsLocatedBy(
                            By.cssSelector("div.image-card.sqs-dynamic-text-container")
                    )
            );
        } catch (Exception e) {
            throw new RuntimeException("Failed to find lawyer elements", e);
        }
    }

    private String getLink(WebElement lawyer) throws LawyerExceptions {
        By[] byArray = {By.tagName("a")};
        return extractor.extractLawyerAttribute(lawyer, byArray, "LINK", "href", LawyerExceptions::linkException);
    }

    private String getName(WebElement lawyer) throws LawyerExceptions {
        By[] byArray = {By.tagName("h3")};
        return extractor.extractLawyerText(lawyer, byArray, "NAME", LawyerExceptions::nameException);
    }

    private String[] getSocials(WebElement lawyer) {
        String email = "";
        String phone = "";
        try {
            String captionText = lawyer
                    .findElement(By.className("image-subtitle"))
                    .findElement(By.cssSelector("p:nth-of-type(2)"))
                    .getText();
            String[] lines = captionText.split("\n");
            for (String line : lines) {
                if (line.contains("@")) {
                    email = line.trim();
                } else if (line.startsWith("+")) {
                    phone = line.trim();
                }
            }
        } catch (Exception e) {}
        return new String[]{email, phone};
    }

    public Object getLawyer(WebElement lawyer) throws Exception {
        String[] socials = this.getSocials(lawyer);

        return Map.of(
                "link", this.getLink(lawyer),
                "name", this.getName(lawyer),
                "role", "Partner",
                "firm", this.name,
                "country", "Finland",
                "practice_area", "",
                "email", socials[0],
                "phone", socials[1].isEmpty() ? "3580201551313" : socials[1]
        );
    }
}