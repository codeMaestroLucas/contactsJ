package org.example.src.sites.byPage;

import org.example.exceptions.LawyerExceptions;
import org.example.src.entities.BaseSites.ByPage;
import org.example.src.entities.MyDriver;
import org.example.src.utils.TreatLawyerParams;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.time.Duration;
import java.util.List;
import java.util.Map;

public class JeffLeongPoonAndWong extends ByPage {

    private final By[] byRoleArray = {
            By.cssSelector("p.m-b-sm")
    };

    public JeffLeongPoonAndWong() {
        super(
                "Jeff Leong Poon And Wong",
                "https://jlpw.com.my/team/",
                1
        );
    }

    @Override
    protected void accessPage(int index) throws InterruptedException {
        this.driver.get(this.link);
        MyDriver.waitForPageToLoad();
        Thread.sleep(1000L);
    }

    @Override
    protected List<WebElement> getLawyersInPage() {
        try {
            WebDriverWait wait = new WebDriverWait(this.driver, Duration.ofSeconds(10L));
            return wait.until(
                    ExpectedConditions.presenceOfAllElementsLocatedBy(By.tagName("article"))
            );
        } catch (Exception e) {
            throw new RuntimeException("Failed to find lawyer elements", e);
        }
    }

    private String getLink(WebElement lawyer) throws LawyerExceptions {
        By[] byArray = {By.cssSelector("a[href*='https://jlpw.com.my/team/']")};
        return extractor.extractLawyerAttribute(lawyer, byArray, "LINK", "href", LawyerExceptions::linkException);
    }

    private String getName(WebElement lawyer) throws LawyerExceptions {
        By[] byArray = {By.tagName("h4")};
        return extractor.extractLawyerText(lawyer, byArray, "NAME", LawyerExceptions::nameException);
    }

    private String constructEmail(String name) {
        String[] parts = TreatLawyerParams.treatNameForEmail(name).split(" ");
        if (parts.length < 2) return "";
        return (parts[0] + "." + parts[parts.length - 1] + "@jlpw.com.my");
    }

    @Override
    public Object getLawyer(WebElement lawyer) throws Exception {
        String name = this.getName(lawyer);
        return Map.of(
                "link", this.getLink(lawyer),
                "name", name,
                "role", "Partner",
                "firm", this.name,
                "country", "Malaysia",
                "practice_area", "",
                "email", constructEmail(name),
                "phone", "60322763868"
        );
    }
}