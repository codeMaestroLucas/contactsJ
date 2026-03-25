package org.example.src.sites.to_test;

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

public class BossYoung extends ByNewPage {

    public BossYoung() {
        super(
                "Boss & Young",
                "https://www.boss-young.com/teamList",
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
            WebDriverWait wait = new WebDriverWait(this.driver, Duration.ofSeconds(10));
            List<WebElement> lawyers = wait.until(ExpectedConditions.presenceOfAllElementsLocatedBy(By.className("team-box")));
            return this.siteUtl.filterLawyersInPage(lawyers, new By[]{By.className("post")}, true);
        } catch (Exception e) {
            return List.of();
        }
    }

    @Override
    public String openNewTab(WebElement lawyer) throws LawyerExceptions {
        String link = lawyer.getAttribute("href");
        MyDriver.openNewTab(link);
        return link;
    }

    @Override
    protected Object getLawyer(WebElement lawyer) throws Exception {
        this.openNewTab(lawyer);
        WebElement container = driver.findElement(By.className("typeBox"));

        By[] nameBy = {By.xpath("//div[@class='name']")};
        By[] roleBy = {By.xpath("//div[@class='post']")};
        By[] practiceBy = {By.xpath("//span[contains(text(),'业务领域')]/following-sibling::span")};
        By[] phoneBy = {By.className("phoneLine")};
        By[] emailBy = {By.xpath("//span[contains(text(),'邮箱地址')]/following-sibling::span")};

        return Map.of(
                "link", Objects.requireNonNull(driver.getCurrentUrl()),
                "name", extractor.extractLawyerText(driver.findElement(By.tagName("body")), nameBy, "NAME", LawyerExceptions::nameException),
                "role", extractor.extractLawyerText(driver.findElement(By.tagName("body")), roleBy, "ROLE", LawyerExceptions::roleException),
                "firm", this.name,
                "country", "China",
                "practice_area", extractor.extractLawyerText(container, practiceBy, "PRACTICE AREA", LawyerExceptions::practiceAreaException),
                "email", extractor.extractLawyerText(container, emailBy, "EMAIL", LawyerExceptions::emailException),
                "phone", extractor.extractLawyerText(container, phoneBy, "PHONE", LawyerExceptions::phoneException)
        );
    }
}
