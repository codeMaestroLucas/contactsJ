package org.example.src.sites.europe;

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

public class BechBruun extends ByNewPage {

    public BechBruun() {
        super(
                "Bech-Bruun",
                "https://www.bechbruun.com/en/employees",
                1
        );
    }

    @Override
    protected void accessPage(int index) throws InterruptedException {
        this.driver.get(this.link);
        MyDriver.waitForPageToLoad();

        // More than 40 rolls
        MyDriver.clickOnElementMultipleTimes(By.xpath("//*[@id=\"__nuxt\"]/div/main/div/div/div/div/div/div[3]/button"), 5, 0.7);
    }

    @Override
    protected List<WebElement> getLawyersInPage() {
        try {
            WebDriverWait wait = new WebDriverWait(this.driver, Duration.ofSeconds(10));
            List<WebElement> lawyers = wait.until(ExpectedConditions.presenceOfAllElementsLocatedBy(By.cssSelector("a.employee")));
            return this.siteUtl.filterLawyersInPage(lawyers, new By[]{By.className("employee__value")}, true);
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
        WebElement container = driver.findElement(By.cssSelector("div.info__contact"));

        By[] nameBy = {By.className("info__title")};
        By[] roleBy = {By.cssSelector(".info__position span:first-child")};

        String[] socials = super.getSocials(container.findElements(By.tagName("a")), false);

        return Map.of(
                "link", Objects.requireNonNull(driver.getCurrentUrl()),
                "name", extractor.extractLawyerAttribute(driver.findElement(By.className("info__header")), nameBy, "NAME", "textContent", LawyerExceptions::nameException),
                "role", extractor.extractLawyerAttribute(driver.findElement(By.className("info__position")), roleBy, "ROLE", "textContent", LawyerExceptions::roleException),
                "firm", this.name,
                "country", "Denmark",
                "practice_area", "",
                "email", socials[0],
                "phone", socials[1].isEmpty() ? "72 27 00 00" : socials[1]
        );
    }
}
