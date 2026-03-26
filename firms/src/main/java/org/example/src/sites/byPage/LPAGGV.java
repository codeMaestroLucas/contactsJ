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

public class LPAGGV extends ByPage {
    private final By[] byRoleArray = {
            By.className("tm-box-right"),
            By.className("position")
    };

    public LPAGGV() {
        super(
                "LPA-GGV",
                "https://lpa-ggv.de/en/team/",
                1
        );
    }

    protected void accessPage(int index) {
        this.driver.get(this.link);
        MyDriver.waitForPageToLoad();
    }

    protected List<WebElement> getLawyersInPage() {
        String[] validRoles = {"partner", "counsel", "senior associate"};

        try {
            WebDriverWait wait = new WebDriverWait(this.driver, Duration.ofSeconds(10L));
            WebElement lawyersDiv = wait.until(ExpectedConditions.presenceOfElementLocated(By.id("flex-wrap-old")));
            List<WebElement> lawyers = lawyersDiv.findElements(By.cssSelector("a[href*='https://lpalaw.tax/en/team/']"));
            return this.siteUtl.filterLawyersInPage(lawyers, byRoleArray, true, validRoles);
        } catch (Exception e) {
            throw new RuntimeException("Failed to find lawyer elements", e);
        }
    }

    private String getLink(WebElement lawyer)  {
        return lawyer.getAttribute("href");
    }

    private String getName(WebElement lawyer) throws LawyerExceptions {
        By[] byArray = {By.cssSelector("h2")};
        return extractor.extractLawyerAttribute(lawyer, byArray, "NAME", "textContent", LawyerExceptions::nameException);
    }

    private String getRole(WebElement lawyer) throws LawyerExceptions {
        return extractor.extractLawyerAttribute(lawyer, byRoleArray, "ROLE", "textContent", LawyerExceptions::roleException);
    }

    private String[] getSocials(WebElement lawyer) {
        String email = "";
        String phone = "";
        try {
            email = lawyer.findElement(By.className("email")).getAttribute("textContent");
            phone = lawyer.findElement(By.className("phone")).getAttribute("textContent");
        } catch (Exception ignored) {
        }
        return new String[]{email, phone};
    }

    public Object getLawyer(WebElement lawyer) throws Exception {
        WebElement DIV = lawyer.findElement(By.className("tm-box-right"));
        String[] socials = this.getSocials(DIV);

        return Map.of(
                "link", this.getLink(lawyer),
                "name", this.getName(DIV),
                "role", this.getRole(lawyer),
                "firm", this.name,
                "country", "Germany",
                "practice_area", "",
                "email", socials[0],
                "phone", socials[1].isEmpty() ? "4969979610" : socials[1]
        );
    }
}