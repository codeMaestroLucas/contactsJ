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

public class MMAN extends ByNewPage {

    public MMAN() {
        super(
                "MMAN",
                "https://mman.co.ke/our-people",
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
            List<WebElement> lawyers = wait.until(ExpectedConditions.presenceOfAllElementsLocatedBy(By.className("grids1")));
            return this.siteUtl.filterLawyersInPage(lawyers, new By[]{By.className("people")}, true);
        } catch (Exception e) {
            return List.of();
        }
    }

    @Override
    public String openNewTab(WebElement lawyer) throws LawyerExceptions {
        By[] byArray = {By.cssSelector("h5 a")};
        String link = extractor.extractLawyerAttribute(lawyer, byArray, "LINK", "href", LawyerExceptions::linkException);
        MyDriver.openNewTab(link);
        return link;
    }

    @Override
    protected Object getLawyer(WebElement lawyer) throws Exception {
        String fullText = extractor.extractLawyerText(lawyer, new By[]{By.className("people")}, "NAME", LawyerExceptions::nameException);
        String name = fullText.split("-")[0].trim();
        String role = fullText.contains("-") ? fullText.split("-")[1].trim() : "Partner";

        this.openNewTab(lawyer);
        WebElement container = driver.findElement(By.className("field-items"));

        String email = extractor.extractLawyerText(container, new By[]{By.cssSelector("a[href^='mailto:']")}, "EMAIL", LawyerExceptions::emailException);

        return Map.of(
                "link", Objects.requireNonNull(driver.getCurrentUrl()),
                "name", name,
                "role", role,
                "firm", this.name,
                "country", "Kenya",
                "practice_area", "",
                "email", email,
                "phone", ""
        );
    }
}
