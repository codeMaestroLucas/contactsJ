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

public class MitelAndAsociatii extends ByPage {
    private final By[] byRoleArray = {
            By.tagName("h6")
    };

    public MitelAndAsociatii() {
        super(
                "Mitel And Asociaţii",
                "https://mitelpartners.ro/our-team/",
                1
        );
    }

    protected void accessPage(int index) {
        this.driver.get(this.link);
        MyDriver.waitForPageToLoad();
    }

    protected List<WebElement> getLawyersInPage() {
        String[] validRoles = {
                "partner",
                "counsel",
                "managing associate",
                "senior associate"
        };

        try {
            WebDriverWait wait = new WebDriverWait(this.driver, Duration.ofSeconds(10L));
            WebElement partnersDiv = wait.until(ExpectedConditions.presenceOfElementLocated(By.xpath("//*[@id=\"post-1287\"]/div/div/section[2]/div/div/div")));
            List<WebElement> lawyers = partnersDiv.findElements(By.cssSelector("div.elementor-inner-column[data-id]"));

            WebElement otherDiv = wait.until(ExpectedConditions.presenceOfElementLocated(By.xpath("//*[@id=\"post-1287\"]/div/div/section[3]/div/div")));
            lawyers.addAll(otherDiv.findElements(By.cssSelector("div.elementor-inner-column[data-id]")));


            return this.siteUtl.filterLawyersInPage(lawyers, byRoleArray, false, validRoles);
        } catch (Exception e) {
            throw new RuntimeException("Failed to find lawyer elements", e);
        }
    }

    private String getLink(WebElement lawyer) {
        return this.link;
    }

    private String getName(WebElement lawyer) throws LawyerExceptions {
        By[] byArray = {By.cssSelector("h5.elementor-heading-title")};
        return extractor.extractLawyerText(lawyer, byArray, "NAME", LawyerExceptions::nameException);
    }

    private String getRole(WebElement lawyer) throws LawyerExceptions {
        return extractor.extractLawyerText(lawyer, byRoleArray, "ROLE", LawyerExceptions::roleException);
    }

    private String[] getSocials(WebElement lawyer) {
        String email = "";
        String phone = "";
        try {
            email = lawyer.findElement(By.cssSelector(".elementor-widget-text-editor a[href^='mailto:']")).getAttribute("href");
            String text = lawyer.findElement(By.cssSelector(".elementor-widget-text-editor p")).getText();
            String[] lines = text.split("\n");
            if (lines.length > 0 && lines[0].startsWith("Phone:")) {
                phone = lines[0].replace("Phone:", "").trim();
            }
        } catch (Exception e) {
            // Socials not found
        }
        return new String[]{email, phone};
    }

    public Object getLawyer(WebElement lawyer) throws Exception {
        String[] socials = this.getSocials(lawyer);

        return Map.of(
                "link", this.getLink(lawyer),
                "name", this.getName(lawyer),
                "role", this.getRole(lawyer),
                "firm", this.name,
                "country", "Romania",
                "practice_area", "",
                "email", socials[0],
                "phone", socials[1].isEmpty() ? "40213143155" : socials[1]
        );
    }
}