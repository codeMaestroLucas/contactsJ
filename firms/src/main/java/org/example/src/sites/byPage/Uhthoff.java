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

public class Uhthoff extends ByPage {

    private final By[] byRoleArray = {
            By.cssSelector(".jet-listing-dynamic-terms__link")
    };

    public Uhthoff() {
        super(
                "Uhthoff",
                "https://uhthoff.com.mx/en/our-team/",
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
        String[] validRoles = {"partner", "senior associate"};
        try {
            WebDriverWait wait = new WebDriverWait(this.driver, Duration.ofSeconds(10));

            WebElement div = wait.until(
                    ExpectedConditions.presenceOfElementLocated(By.xpath("//*[@id=\"hola-filtro\"]/div/div/div"))
            );
            List<WebElement> lawyers = div.findElements(By.cssSelector(".elementor-element-populated"));

            div = driver.findElement(By.xpath("//*[@id=\"hola-filtro-2\"]/div/div/div"));
            lawyers.addAll(div.findElements(By.cssSelector(".elementor-element-populated")));

            return lawyers;
        } catch (Exception e) {
            return List.of();
        }
    }

    private String getLink(WebElement lawyer) throws LawyerExceptions {
        By[] byArray = {By.cssSelector(".elementor-element-5ea9d42 a")};
        return extractor.extractLawyerAttribute(lawyer, byArray, "LINK", "href", LawyerExceptions::linkException);
    }

    private String getName(WebElement lawyer) throws LawyerExceptions {
        By[] byArray = {By.cssSelector(".elementor-element-54fe200 h2")};
        return extractor.extractLawyerText(lawyer, byArray, "NAME", LawyerExceptions::nameException);
    }

    private String getPracticeArea(WebElement lawyer) throws LawyerExceptions {
        return extractor.extractLawyerText(lawyer, byRoleArray, "ROLE", LawyerExceptions::roleException);
    }

    private String getEmail(WebElement lawyer) throws LawyerExceptions {
        By[] byArray = {By.cssSelector(".elementor-element-91465d2 .jet-listing-dynamic-field__content")};
        return extractor.extractLawyerText(lawyer, byArray, "EMAIL", LawyerExceptions::emailException);
    }

    @Override
    public Object getLawyer(WebElement lawyer) throws Exception {
        return Map.of(
                "link", this.getLink(lawyer),
                "name", this.getName(lawyer),
                "role", "",
                "firm", this.name,
                "country", "Mexico",
                "practice_area", this.getPracticeArea(lawyer),
                "email", this.getEmail(lawyer),
                "phone", "5555335060"
        );
    }
}
