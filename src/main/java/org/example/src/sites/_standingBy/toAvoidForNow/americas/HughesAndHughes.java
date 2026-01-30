package org.example.src.sites._standingBy.toAvoidForNow.americas;

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

public class HughesAndHughes extends ByPage {
    private final By[] byRoleArray = {
            By.className("peopleNameLink")
    };

    public HughesAndHughes() {
        super(
                "Hughes And Hughes",
                "https://www.hughes.com.uy/our_people",
                1,
                1
        );
    }

    protected void accessPage(int index) throws InterruptedException {
        this.driver.get(this.link);
        MyDriver.waitForPageToLoad();
        Thread.sleep(1000L);
    }

    protected List<WebElement> getLawyersInPage() {
        String[] validRoles = {"partner", "associate"};

        try {
            WebDriverWait wait = new WebDriverWait(this.driver, Duration.ofSeconds(10L));
            List<WebElement> lawyers = wait.until(
                    ExpectedConditions.presenceOfAllElementsLocatedBy(
                            By.className("area_contac")
                    )
            );

            // Filter using class name logic for role as the text isn't directly in a span
            return lawyers.stream()
                    .filter(l -> l.getAttribute("class").contains("type-partner") || l.getAttribute("class").contains("type-associate"))
                    .toList();
        } catch (Exception e) {
            throw new RuntimeException("Failed to find lawyer elements", e);
        }
    }

    private String getLink(WebElement lawyer) throws LawyerExceptions {
        By[] byArray = {By.className("peopleNameLink")};
        return extractor.extractLawyerAttribute(lawyer, byArray, "LINK", "href", LawyerExceptions::linkException);
    }

    private String getName(WebElement lawyer) throws LawyerExceptions {
        By[] byArray = {By.className("peopleNameLink")};
        return extractor.extractLawyerText(lawyer, byArray, "NAME", LawyerExceptions::nameException);
    }

    private String getRole(WebElement lawyer) {
        String classes = lawyer.getAttribute("class");
        if (classes.contains("partner")) return "Partner";
        return "Associate";
    }

    private String[] getSocials(WebElement lawyer) {
        try {
            List<WebElement> socials = lawyer.findElements(By.tagName("a"));
            return super.getSocials(socials, false);
        } catch (Exception e) {
            return new String[]{"", ""};
        }
    }

    public Object getLawyer(WebElement lawyer) throws Exception {
        String[] socials = this.getSocials(lawyer);

        return Map.of(
                "link", this.getLink(lawyer),
                "name", this.getName(lawyer),
                "role", this.getRole(lawyer),
                "firm", this.name,
                "country", "Uruguay",
                "practice_area", "",
                "email", socials[0],
                "phone", socials[1].isEmpty() ? extractor.extractLawyerText(lawyer, new By[]{By.className("contactTelefono")}, "PHONE", LawyerExceptions::phoneException) : socials[1]
        );
    }
}
