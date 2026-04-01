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

public class DunnCox extends ByNewPage {

    public DunnCox() {
        super(
                "DunnCox",
                "https://dunncox.com/our-attorneys/",
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
            List<WebElement> lawyers = wait.until(ExpectedConditions.presenceOfAllElementsLocatedBy(By.className("attorney")));
            return this.siteUtl.filterLawyersInPage(lawyers, new By[]{By.className("role")}, true);
        } catch (Exception e) {
            return List.of();
        }
    }

    @Override
    public String openNewTab(WebElement lawyer) throws LawyerExceptions {
        By[] byArray = {By.className("attorney-cell")};
        String link = extractor.extractLawyerAttribute(lawyer, byArray, "LINK", "href", LawyerExceptions::linkException);
        MyDriver.openNewTab(link);
        return link;
    }

    @Override
    protected Object getLawyer(WebElement lawyer) throws Exception {
        String roleFromList = extractor.extractLawyerText(lawyer, new By[]{By.className("role")}, "ROLE", LawyerExceptions::roleException);

        this.openNewTab(lawyer);
        WebElement container = driver.findElement(By.className("accordion-inner"));

        String name = driver.findElement(By.tagName("h1")).getText();
        String[] socials = super.getSocialsFromText(container.getText());

        return Map.of(
                "link", Objects.requireNonNull(driver.getCurrentUrl()),
                "name", name,
                "role", roleFromList,
                "firm", this.name,
                "country", "Jamaica",
                "practice_area", "",
                "email", socials[0],
                "phone", socials[1].isEmpty() ? "8769221500" : socials[1]
        );
    }
}
