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

public class GuttOlkFeldhaus extends ByNewPage {

    public GuttOlkFeldhaus() {
        super(
                "Gütt Olk Feldhaus",
                "https://www.gof-partner.com/en/lawyers",
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
            WebDriverWait wait = new WebDriverWait(this.driver, Duration.ofSeconds(10L));
            List<WebElement> lawyers = wait.until(ExpectedConditions.presenceOfAllElementsLocatedBy(By.className("card")));
            return this.siteUtl.filterLawyersInPage(lawyers, new By[]{By.className("position")}, true);
        } catch (Exception e) {
            return List.of();
        }
    }

    @Override
    public String openNewTab(WebElement lawyer) throws LawyerExceptions {
        String link = lawyer.findElement(By.tagName("a")).getAttribute("href");
        MyDriver.openNewTab(link);
        return link;
    }

    @Override
    protected Object getLawyer(WebElement lawyer) throws Exception {
        this.openNewTab(lawyer);
        WebElement info = driver.findElement(By.className("staff-info"));

        String phone = extractor.extractLawyerText(info, new By[]{By.className("staff-phone")}, "PHONE", LawyerExceptions::phoneException);

        return Map.of(
                "link", Objects.requireNonNull(driver.getCurrentUrl()),
                "name", extractor.extractLawyerText(info, new By[]{By.tagName("h1")}, "NAME", LawyerExceptions::nameException).split(",")[0],
                "role", extractor.extractLawyerText(info, new By[]{By.className("position")}, "ROLE", LawyerExceptions::roleException),
                "firm", this.name,
                "country", "Germany",
                "practice_area", "",
                "email", extractor.extractLawyerText(info, new By[]{By.className("staff-email")}, "EMAIL", LawyerExceptions::emailException),
                "phone", phone.isEmpty() ? "498924224110" : phone
        );
    }
}
