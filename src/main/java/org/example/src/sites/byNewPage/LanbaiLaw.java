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

public class LanbaiLaw extends ByNewPage {

    public LanbaiLaw() {
        super(
                "Lanbai Law",
                "https://www.lanbailawfirm.com/team",
                1
        );
    }

    @Override
    protected void accessPage(int index) throws InterruptedException {
        this.driver.get(this.link);
        Thread.sleep(60000); // Necessary
        MyDriver.waitForPageToLoad();
        MyDriver.clickOnElement(By.id("en"));
        Thread.sleep(4000);
    }

    @Override
    protected List<WebElement> getLawyersInPage() {
        try {
            WebDriverWait wait = new WebDriverWait(this.driver, Duration.ofSeconds(10));
            return wait.until(ExpectedConditions.presenceOfAllElementsLocatedBy(By.cssSelector(".item")));
        } catch (Exception e) {
            throw new RuntimeException("Failed to find lawyer elements", e);
        }
    }

    @Override
    public String openNewTab(WebElement lawyer) throws LawyerExceptions {
        By[] byArray = {By.tagName("a")};
        String link = extractor.extractLawyerAttribute(lawyer, byArray, "LINK", "href", LawyerExceptions::linkException);
        MyDriver.openNewTab(link);
        return link;
    }

    private String getName(WebElement lawyer) throws LawyerExceptions {
        By[] byArray = {By.cssSelector(".name-group h2")};
        return extractor.extractLawyerText(lawyer, byArray, "NAME", LawyerExceptions::nameException);
    }

    private String getRole(WebElement lawyer) throws LawyerExceptions {
        By[] byArray = {By.cssSelector(".name-group h4")};
        return extractor.extractLawyerText(lawyer, byArray, "ROLE", LawyerExceptions::roleException);
    }

    private String getCountry(WebElement lawyer) throws LawyerExceptions {
        By[] byArray = {By.className("info-p")};
        return extractor.extractLawyerText(lawyer, byArray, "COUNTRY", LawyerExceptions::countryException);
    }

    private String[] getSocials() {
        WebElement lawyer = driver.findElement(By.xpath("/html/body/main/section[2]/div/div[1]/div[2]"));
        try {
            String email = lawyer.findElement(By.className("email-text")).getText();
            String phone = lawyer.findElement(By.className("email-phone")).getText();
            return new String[]{email, phone};
        } catch (Exception e) {
            return new String[]{"", ""};
        }
    }

    @Override
    public Object getLawyer(WebElement lawyer) throws Exception {
        this.openNewTab(lawyer);
        WebElement container = driver.findElement(By.xpath("/html/body/main/section[2]/div/div[2]/div[1]"));

        String[] socials = this.getSocials();

        return Map.of(
                "link", Objects.requireNonNull(driver.getCurrentUrl()),
                "name", this.getName(container),
                "role", this.getRole(container),
                "firm", this.name,
                "country", this.getCountry(container),
                "practice_area", "",
                "email", socials[0],
                "phone", socials[1].isEmpty() ? "2132511297130" : socials[1]
        );
    }
}
