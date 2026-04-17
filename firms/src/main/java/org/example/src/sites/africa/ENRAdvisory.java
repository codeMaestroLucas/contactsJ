package org.example.src.sites.africa;

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

public class ENRAdvisory extends ByNewPage {

    public ENRAdvisory() {
        super(
                "ENR Advisory",
                "https://enradvisory.com/core-team/",
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
            List<WebElement> lawyers = wait.until(ExpectedConditions.presenceOfAllElementsLocatedBy(By.className("brz-posts__item")));
            return this.siteUtl.filterLawyersInPage(lawyers, new By[]{By.tagName("div")}, false);
        } catch (Exception e) {
            return List.of();
        }
    }

    @Override
    public String openNewTab(WebElement lawyer) throws LawyerExceptions {
        By[] byArray = {By.tagName("a")};
        String link = extractor.extractLawyerAttribute(lawyer, byArray, "LINK", "href", LawyerExceptions::linkException);
        MyDriver.openNewTab(link);
        return link;
    }

    @Override
    protected Object getLawyer(WebElement lawyer) throws Exception {
        String name = extractor.extractLawyerText(lawyer, new By[]{By.className("text-population")}, "NAME", LawyerExceptions::nameException);

        this.openNewTab(lawyer);
        driver.findElement(By.className("brz-column__items"));

        String role = driver.findElement(By.xpath("//div/div[2]/div/div[2]/div[2]/div[2]/div[2]/div/div/p/strong")).getAttribute("textContent");
        String email = driver.findElement(By.xpath("//div/div[2]/div/div[2]/div[2]/div[2]/div[5]/div/div[2]/div/div/p/a/strong")).getAttribute("textContent");

        return Map.of(
                "link", Objects.requireNonNull(driver.getCurrentUrl()),
                "name", name,
                "role", role,
                "firm", this.name,
                "country", "Nigeria",
                "practice_area", "",
                "email", email,
                "phone", "234020170046305"
        );
    }
}
