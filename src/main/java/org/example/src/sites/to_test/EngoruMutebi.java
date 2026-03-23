package org.example.src.sites.to_test;

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

public class EngoruMutebi extends ByPage {

    public EngoruMutebi() {
        super(
                "Engoru Mutebi",
                "https://engorumutebi.co.ug/lawyers/",
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
            List<WebElement> lawyers = wait.until(ExpectedConditions.presenceOfAllElementsLocatedBy(By.className("card-person")));
            return this.siteUtl.filterLawyersInPage(lawyers, new By[]{By.className("what")}, true);
        } catch (Exception e) {
            return List.of();
        }
    }

    @Override
    protected Object getLawyer(WebElement lawyer) throws Exception {
        String name = extractor.extractLawyerText(lawyer, new By[]{By.tagName("h3")}, "NAME", LawyerExceptions::nameException);
        String role = extractor.extractLawyerText(lawyer, new By[]{By.className("what")}, "ROLE", LawyerExceptions::roleException);
        String link = extractor.extractLawyerAttribute(lawyer, new By[]{By.cssSelector("h3 a")}, "LINK", "href", LawyerExceptions::linkException);

        String[] socials = super.getSocials(lawyer.findElements(By.tagName("a")), false);

        return Map.of(
                "link", link,
                "name", name,
                "role", role,
                "firm", this.name,
                "country", "Uganda",
                "practice_area", extractor.extractLawyerText(lawyer, new By[]{By.tagName("ul")}, "PRACTICE AREA", (e) -> ""),
                "email", socials[0],
                "phone", socials[1].isEmpty() ? "256393216520" : socials[1]
        );
    }
}
