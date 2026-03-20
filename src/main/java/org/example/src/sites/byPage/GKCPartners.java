package org.example.src.sites.byPage;

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

public class GKCPartners extends ByPage {

    public GKCPartners() {
        super(
                "GKC Partners",
                "https://www.gkcpartners.com/people.html",
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
            List<WebElement> lawyers = wait.until(ExpectedConditions.presenceOfAllElementsLocatedBy(By.className("bio-item")));
            return this.siteUtl.filterLawyersInPage(lawyers, new By[]{By.className("field--name-field-job-title")}, true);
        } catch (Exception e) {
            return List.of();
        }
    }

    @Override
    protected Object getLawyer(WebElement lawyer) throws Exception {
        String[] socials = super.getSocials(lawyer.findElements(By.cssSelector(".lawyer-contact-info a")), false);
        String role = extractor.extractLawyerText(lawyer, new By[]{By.className("lawyer-role-offices")}, "ROLE", LawyerExceptions::roleException);

        return Map.of(
                "link", extractor.extractLawyerAttribute(lawyer, new By[]{By.cssSelector(".lawyer-name a")}, "LINK", "href", LawyerExceptions::linkException),
                "name", extractor.extractLawyerText(lawyer, new By[]{By.className("lawyer-name")}, "NAME", LawyerExceptions::nameException),
                "role", role,
                "firm", this.name,
                "country", "Turkey",
                "practice_area", role,
                "email", socials[0],
                "phone", socials[1].isEmpty() ? "902123551300" : socials[1]
        );
    }
}
