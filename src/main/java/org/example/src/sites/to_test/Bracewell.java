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

public class Bracewell extends ByNewPage {

    public Bracewell() {
        super(
                "Bracewell",
                "https://www.bracewell.com/people/?relationships.poa_office=Dubai",
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
            List<WebElement> lawyers = wait.until(ExpectedConditions.presenceOfAllElementsLocatedBy(By.cssSelector("article.card-attorney")));
            return this.siteUtl.filterLawyersInPage(lawyers, new By[]{By.cssSelector("p.fs-16")}, false);
        } catch (Exception e) {
            return List.of();
        }
    }

    @Override
    public String openNewTab(WebElement lawyer) throws LawyerExceptions {
        By[] byArray = {By.cssSelector("h2.entry-title a")};
        String link = extractor.extractLawyerAttribute(lawyer, byArray, "LINK", "href", LawyerExceptions::linkException);
        MyDriver.openNewTab(link);
        return link;
    }

    @Override
    protected Object getLawyer(WebElement lawyer) throws Exception {
        String name = extractor.extractLawyerText(lawyer, new By[]{By.cssSelector("h2.entry-title")}, "NAME", LawyerExceptions::nameException);
        String role = extractor.extractLawyerText(lawyer, new By[]{By.cssSelector("p.fs-16")}, "ROLE", LawyerExceptions::roleException);

        this.openNewTab(lawyer);
        WebElement header = driver.findElement(By.className("page-header-bracewell-row"));

        String email = extractor.extractLawyerAttribute(header, new By[]{By.cssSelector("a[href^='mailto:']")}, "EMAIL", "href", LawyerExceptions::emailException);
        String phone = extractor.extractLawyerText(header, new By[]{By.cssSelector("a[href^='tel:']")}, "PHONE", LawyerExceptions::phoneException);

        return Map.of(
                "link", MyDriver.getINSTANCE().getCurrentUrl(),
                "name", name,
                "role", role,
                "firm", this.name,
                "country", "the UAE",
                "practice_area", "",
                "email", email,
                "phone", phone.isEmpty() ? "97143506817" : phone
        );
    }
}
