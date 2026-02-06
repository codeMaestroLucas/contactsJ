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

public class MQMGLD extends ByPage {
    private final By[] byRoleArray = {
            By.className("cargo")
    };

    public MQMGLD() {
        super(
                "MQMGLD",
                "https://www.mqmgld.com/en/team",
                1
        );
    }

    protected void accessPage(int index) {
        this.driver.get(this.link);
        MyDriver.waitForPageToLoad();
    }

    protected List<WebElement> getLawyersInPage() {
        try {
            WebDriverWait wait = new WebDriverWait(this.driver, Duration.ofSeconds(10L));
            WebElement div = wait.until(
                    ExpectedConditions.presenceOfElementLocated(
                            By.xpath("/html/body/section[2]/div[1]/div/div")
                    )
            );
            List<WebElement> lawyers = div.findElements(By.className("item"));
            div = driver.findElement(By.xpath("/html/body/section[2]/div[2]/div/div"));
            lawyers.addAll(div.findElements(By.className("item")));

            div = driver.findElement(By.xpath("/html/body/section[2]/div[3]/div/div"));
            lawyers.addAll(div.findElements(By.className("item")));

            div = driver.findElement(By.xpath("/html/body/section[2]/div[4]/div/div"));
            lawyers.addAll(div.findElements(By.className("item")));

            return lawyers;
        } catch (Exception e) {
            throw new RuntimeException("Failed to find lawyer elements", e);
        }
    }

    private String getLink(WebElement lawyer) throws LawyerExceptions {
        By[] byArray = {By.className("persona_card")};
        return extractor.extractLawyerAttribute(lawyer, byArray, "LINK", "href", LawyerExceptions::linkException);
    }

    private String getName(WebElement lawyer) throws LawyerExceptions {
        By[] byArray = {By.className("h4")};
        return extractor.extractLawyerText(lawyer, byArray, "NAME", LawyerExceptions::nameException);
    }

    private String getRole(WebElement lawyer) throws LawyerExceptions {
        return extractor.extractLawyerText(lawyer, byRoleArray, "ROLE", LawyerExceptions::roleException);
    }

    private String[] getSocials(WebElement lawyer) {
        String email = "";
        try {
            email = lawyer.findElement(By.className("correo")).getText();
        } catch (Exception e) {
            // Email not found
        }
        return new String[]{email, ""};
    }

    public Object getLawyer(WebElement lawyer) throws Exception {
        String[] socials = this.getSocials(lawyer);

        return Map.of(
                "link", this.getLink(lawyer),
                "name", this.getName(lawyer),
                "role", this.getRole(lawyer),
                "firm", this.name,
                "country", "Colombia",
                "practice_area", "",
                "email", socials[0],
                "phone", socials[1].isEmpty() ? "5716013174720" : socials[1]
        );
    }
}