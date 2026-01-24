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

public class MUC extends ByPage {
    private final By[] byRoleArray = {
            By.className("t-entry-title")
    };

    public MUC() {
        super(
            "MUC",
            "https://muclaw.mx/en/team/",
            1
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
            WebElement partnersDiv = wait.until(
                    ExpectedConditions.presenceOfElementLocated(By.xpath("//*[@id=\"row-unique-2\"]/div"))
            );
            List<WebElement> lawyers = partnersDiv.findElements(By.className("tmb-light"));
            WebElement counselDiv = driver.findElement(By.xpath("//*[@id=\"row-unique-10\"]/div"));
            lawyers.addAll(counselDiv.findElements(By.className("tmb-light")));

            return lawyers;
        } catch (Exception e) {
            throw new RuntimeException("Failed to find lawyer elements", e);
        }
    }

    private String getLink(WebElement lawyer) throws LawyerExceptions {
        By[] byArray = {By.cssSelector("h3.t-entry-title a")};
        return extractor.extractLawyerAttribute(lawyer, byArray, "LINK", "href", LawyerExceptions::linkException);
    }

    private String getName(WebElement lawyer) throws LawyerExceptions {
        By[] byArray = {By.className("t-entry-title")};
        return extractor.extractLawyerText(lawyer, byArray, "NAME", LawyerExceptions::nameException);
    }

    private String[] getSocials(WebElement lawyer) {
        try {
            By[] byEmail = {
                    By.className("t-entry-excerpt"),
                    By.tagName("p")
            };
            String email = extractor.extractLawyerAttribute(lawyer, byEmail, "EMAIL", "innerHTML", LawyerExceptions::emailException);
            return new String[]{email, ""};
        } catch (Exception e) {
            return new String[]{"", ""};
        }
    }

    public Object getLawyer(WebElement lawyer) throws Exception {
        String[] socials = this.getSocials(lawyer);

        return Map.of(
                "link", this.getLink(lawyer),
                "name", this.getName(lawyer),
                "role", "",
                "firm", this.name,
                "country", "Mexico",
                "practice_area", "",
                "email", socials[0],
                "phone", "525552643568"
        );
    }
}
