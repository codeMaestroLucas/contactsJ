package org.example.src.sites.byNewPage;

import org.example.exceptions.LawyerExceptions;
import org.example.src.entities.BaseSites.ByNewPage;
import org.example.src.entities.MyDriver;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.time.Duration;
import java.util.List;
import java.util.Map;
import java.util.Objects;

public class TALaw extends ByNewPage {

    public TALaw() {
        super(
                "TA Law",
                "https://www.taoanlaw.com/professionals/en",
                1
        );
    }

    @Override
    protected void accessPage(int index) {
        this.driver.get(this.link);
        MyDriver.waitForPageToLoad();
    }

    private final By[] byRoleArray = {By.tagName("b")};


    @Override
    protected List<WebElement> getLawyersInPage() {
        String[] validRoles = {"partner", "counsel", "senior associate"};

        try {
            WebDriverWait wait = new WebDriverWait(this.driver, Duration.ofSeconds(10));
            List<WebElement> lawyers = wait.until(ExpectedConditions.presenceOfAllElementsLocatedBy(By.cssSelector("li[name='tl_Nm']")));

            return this.siteUtl.filterLawyersInPage(lawyers, byRoleArray, false, validRoles);
        } catch (Exception e) {
            throw new RuntimeException("Failed to find lawyer elements", e);
        }
    }

    @Override
    public String openNewTab(WebElement lawyer) throws LawyerExceptions {
        try {
            WebElement pic = lawyer.findElement(By.className("pic"));
            String onclick = pic.getAttribute("onclick");
            String link = "https://www.taoanlaw.com" + onclick.substring(onclick.indexOf("'") + 1, onclick.lastIndexOf("'"));
            MyDriver.openNewTab(link);
            return link;
        } catch (Exception e) {
            throw LawyerExceptions.linkException("Onclick link not found");
        }
    }

    private String getName(WebElement lawyer) throws LawyerExceptions {
        By[] byArray = {By.className("name")};
        return extractor.extractLawyerText(lawyer, byArray, "NAME", LawyerExceptions::nameException);
    }

    private String getRole(WebElement lawyer) throws LawyerExceptions {
        By[] byArray = {By.className("zhiwei")};
        return extractor.extractLawyerText(lawyer, byArray, "ROLE", LawyerExceptions::roleException);
    }

    private String getPracticeArea(WebElement lawyer) {
        try {
            String text = lawyer.getText();
            if (text.contains("Practice Areas：")) {
                return text.split("Practice Areas：")[1].trim();
            }
            return "";
        } catch (Exception e) {
            return "";
        }
    }

    private String getEmail(WebElement lawyer) throws LawyerExceptions {
        String text = lawyer.getText();
        if (text.contains("@")) {
            String[] parts = text.split("\n");
            for (String part : parts) {
                if (part.contains("@")) return part.trim();
            }
        }
        throw LawyerExceptions.emailException("Email not found in text");
    }

    @Override
    public Object getLawyer(WebElement lawyer) throws Exception {
        this.openNewTab(lawyer);
        WebElement container = driver.findElement(By.className("info"));

        return Map.of(
                "link", Objects.requireNonNull(driver.getCurrentUrl()),
                "name", this.getName(container),
                "role", this.getRole(container),
                "firm", this.name,
                "country", "China",
                "practice_area", this.getPracticeArea(container),
                "email", this.getEmail(container),
                "phone", "861065403885"
        );
    }
}
