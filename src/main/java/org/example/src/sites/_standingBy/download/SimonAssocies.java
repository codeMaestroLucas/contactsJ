package org.example.src.sites._standingBy.download;

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

public class SimonAssocies extends ByNewPage {

    public SimonAssocies() {
        super(
                "Simon Associés",
                "https://simonassocies.com/en/the-partners/",
                1
        );
    }

    @Override
    protected void accessPage(int index) throws InterruptedException {
        this.driver.get(this.link);
        MyDriver.waitForPageToLoad();
    }

    @Override
    protected List<WebElement> getLawyersInPage() {
        try {
            WebDriverWait wait = new WebDriverWait(this.driver, Duration.ofSeconds(10L));
            return wait.until(
                    ExpectedConditions.presenceOfAllElementsLocatedBy(By.className("team-content"))
            );
        } catch (Exception e) {
            throw new RuntimeException("Failed to find lawyer elements", e);
        }
    }

    @Override
    public String openNewTab(WebElement lawyer) throws LawyerExceptions {
        By[] byArray = {By.cssSelector(".name-team a")};
        String link = extractor.extractLawyerAttribute(lawyer, byArray, "LINK", "href", LawyerExceptions::linkException);
        MyDriver.openNewTab(link);
        return link;
    }

    private String getName(WebElement lawyer) throws LawyerExceptions {
        By[] byArray = {By.tagName("h1")};
        return extractor.extractLawyerText(lawyer, byArray, "NAME", LawyerExceptions::nameException);
    }

    private String getRole(WebElement lawyer) throws LawyerExceptions {
        By[] byArray = {By.className("job-team")};
        String role = extractor.extractLawyerText(lawyer, byArray, "ROLE", LawyerExceptions::roleException);
        return role.split("-")[0].trim();
    }

    private String getPracticeArea(WebElement lawyer) throws LawyerExceptions {
        By[] byArray = {By.className("dep-team")};
        return extractor.extractLawyerText(lawyer, byArray, "PRACTICE", LawyerExceptions::practiceAreaException);
    }

    private String[] getSocials(WebElement lawyer) {
        try {
            String phone = lawyer.findElement(By.className("num-tel")).findElement(By.tagName("a")).getText();
            return new String[]{"", phone};
        } catch (Exception e) {
            return new String[]{"", ""};
        }
    }

    @Override
    public Object getLawyer(WebElement lawyer) throws Exception {
        this.openNewTab(lawyer);
        WebElement div = driver.findElement(By.id("content-single-team"));
        String[] socials = this.getSocials(div);
        String name = this.getName(div);

        return Map.of(
                "link", Objects.requireNonNull(driver.getCurrentUrl()),
                "name", name,
                "role", "Partner",
                "firm", this.name,
                "country", "France",
                "practice_area", this.getPracticeArea(div),
                "email", "",
                "phone", socials[1]
        );
    }
}
