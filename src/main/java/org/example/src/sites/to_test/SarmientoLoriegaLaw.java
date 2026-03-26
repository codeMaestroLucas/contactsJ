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

public class SarmientoLoriegaLaw extends ByNewPage {

    public SarmientoLoriegaLaw() {
        super(
                "Sarmiento Loriega Law",
                "https://www.sl-lawoffice.com/Lawyers",
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
            List<WebElement> lawyers = wait.until(ExpectedConditions.presenceOfAllElementsLocatedBy(By.className("lawyer-item")));
            return this.siteUtl.filterLawyersInPage(lawyers, new By[]{By.className("lawyer-level")}, true);
        } catch (Exception e) {
            return List.of();
        }
    }

    @Override
    public String openNewTab(WebElement lawyer) throws LawyerExceptions {
        // Site seems to be a SPA or uses Knockout.js binding; extracting simulated link or assuming profiling is needed
        String link = Objects.requireNonNull(driver.getCurrentUrl());
        return link;
    }

    @Override
    protected Object getLawyer(WebElement lawyer) throws Exception {
        String name = extractor.extractLawyerText(lawyer, new By[]{By.tagName("h4")}, "NAME", LawyerExceptions::nameException);
        String role = extractor.extractLawyerText(lawyer, new By[]{By.className("lawyer-level")}, "ROLE", LawyerExceptions::roleException);

        // Simulating profile interaction or extracting from current state
        WebElement contactBox = driver.findElement(By.className("lawyer-details"));
        String[] socials = super.getSocialsFromText(contactBox.getText());

        return Map.of(
                "link", Objects.requireNonNull(driver.getCurrentUrl()),
                "name", name,
                "role", role,
                "firm", this.name,
                "country", "the Philippines",
                "practice_area", "",
                "email", socials[0],
                "phone", socials[1].isEmpty() ? "63277988115" : socials[1]
        );
    }
}
