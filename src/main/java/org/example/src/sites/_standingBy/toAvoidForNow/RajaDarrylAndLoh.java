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
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class RajaDarrylAndLoh extends ByPage {
    private final By[] byRoleArray = {
            By.cssSelector(".elementor-element-72efca7 p")
    };

    public RajaDarrylAndLoh() {
        super(
                "Raja Darryl And Loh",
                "https://rajadarrylloh.com/our-people/",
                1,
                3
        );
    }

    @Override
    protected void accessPage(int index) {
        this.driver.get(this.link);
        MyDriver.waitForPageToLoad();
    }

    @Override
    protected List<WebElement> getLawyersInPage() {
        String[] validRoles = {"partner", "head", "senior associate"};
        WebDriverWait wait = new WebDriverWait(this.driver, Duration.ofSeconds(10L));
        return wait.until(
                ExpectedConditions.presenceOfAllElementsLocatedBy(
                        By.cssSelector("div.elementor-widget-wrap.elementor-element-populated")
                )
        );
    }

    private String getLink(WebElement lawyer) throws LawyerExceptions {
        By[] byArray = {By.className("elementor-button-link")};
        return extractor.extractLawyerAttribute(lawyer, byArray, "LINK", "href", LawyerExceptions::linkException);
    }

    private String getName(WebElement lawyer) throws LawyerExceptions {
        By[] byArray = {By.className("elementor-heading-title")};
        return extractor.extractLawyerText(lawyer, byArray, "NAME", LawyerExceptions::nameException);
    }

    private String getRole(WebElement lawyer) throws LawyerExceptions {
        String text = extractor.extractLawyerText(lawyer, byRoleArray, "ROLE", LawyerExceptions::roleException);
        return text.split("<br>")[0].trim();
    }

    private String[] getSocials(WebElement lawyer) {
        String email = "";
        String phone = "";
        try {
            String text = lawyer.findElement(byRoleArray[0]).getText();

            Pattern emailPattern = Pattern.compile("[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,}");
            Matcher emailMatcher = emailPattern.matcher(text);
            if (emailMatcher.find()) {
                email = emailMatcher.group();
            }

            Pattern phonePattern = Pattern.compile("\\+\\d{1,3}[-\\s]?\\d{1,15}");
            Matcher phoneMatcher = phonePattern.matcher(text);
            if (phoneMatcher.find()) {
                phone = phoneMatcher.group();
            }
        } catch (Exception e) {
            System.err.println("Error getting socials: " + e.getMessage());
        }
        return new String[]{email, phone};
    }

    @Override
    public Object getLawyer(WebElement lawyer) throws Exception {
        String[] socials = this.getSocials(lawyer);

        return Map.of(
                "link", this.getLink(lawyer),
                "name", this.getName(lawyer),
                "role", this.getRole(lawyer),
                "firm", this.name,
                "country", "Malaysia",
                "practice_area", "",
                "email", socials[0],
                "phone", socials[1].isEmpty() ? "xxxxxx" : socials[1]
        );
    }
}