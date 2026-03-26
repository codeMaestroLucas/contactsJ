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

public class VisionAndAssociates extends ByPage {

    private final By[] byRoleArray = {
            By.cssSelector(".wpbdp-field-title .value")
    };

    public VisionAndAssociates() {
        super(
                "Vision & Associates",
                "https://vision-associates.com/our-people/our_people/partners/",
                2
        );
    }

    private String currentRole = null;

    @Override
    protected void accessPage(int index) throws InterruptedException {
        String otherUrl = "https://vision-associates.com/our-people/our_people/special-counsels/";
        String url = index == 0 ? this.link : otherUrl;
        currentRole = index == 0 ? "Partner" : "Counsel";
        this.driver.get(url);
        MyDriver.waitForPageToLoad();
        Thread.sleep(1000L);
    }

    @Override
    protected List<WebElement> getLawyersInPage() {
        try {
            WebDriverWait wait = new WebDriverWait(this.driver, Duration.ofSeconds(10L));
            return wait.until(
                    ExpectedConditions.presenceOfAllElementsLocatedBy(By.className("wpbdp-listing"))
            );
        } catch (Exception e) {
            throw new RuntimeException("Failed to find lawyer elements", e);
        }
    }

    private String getLink(WebElement lawyer) throws LawyerExceptions {
        By[] byArray = {By.cssSelector(".listing-title a")};
        return extractor.extractLawyerAttribute(lawyer, byArray, "LINK", "href", LawyerExceptions::linkException);
    }

    private String getName(WebElement lawyer) throws LawyerExceptions {
        By[] byArray = {By.cssSelector(".listing-title a")};
        return extractor.extractLawyerText(lawyer, byArray, "NAME", LawyerExceptions::nameException);
    }

    private String getPracticeArea(WebElement lawyer) {
        try {
            By[] byArray = {By.cssSelector(".wpbdp-field-areas_of_expertises .value a")};
            return extractor.extractLawyerText(lawyer, byArray, "PRACTICE AREA", LawyerExceptions::practiceAreaException);
        } catch (Exception e) {
            return "";
        }
    }

    private String[] getSocials(WebElement lawyer) {
        try {
            String email = lawyer.findElement(By.cssSelector(".wpbdp-field-e .value")).getText();
            String phone = lawyer.findElement(By.cssSelector(".wpbdp-field-t .value")).getText();
            return new String[]{email, phone};
        } catch (Exception e) {
            return new String[]{"", ""};
        }
    }

    @Override
    public Object getLawyer(WebElement lawyer) throws Exception {
        String[] socials = this.getSocials(lawyer);

        return Map.of(
                "link", this.getLink(lawyer),
                "name", this.getName(lawyer),
                "role", currentRole,
                "firm", this.name,
                "country", "Vietnam",
                "practice_area", this.getPracticeArea(lawyer),
                "email", socials[0],
                "phone", socials[1]
        );
    }
}
