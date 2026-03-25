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

public class BrandlTalos extends ByNewPage {

    public BrandlTalos() {
        super(
                "Brandl Talos",
                "https://brandltalos.com/en/team/",
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
            List<WebElement> lawyers = wait.until(ExpectedConditions.presenceOfAllElementsLocatedBy(By.cssSelector("a.team-item")));
            // Role info is encoded in class but the SiteUtils filter checks text/textContent
            return this.siteUtl.filterLawyersInPage(lawyers, new By[]{By.className("title")}, false);
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
        String name = extractor.extractLawyerText(lawyer, new By[]{By.className("title")}, "NAME", LawyerExceptions::nameException);

        this.openNewTab(lawyer);
        WebElement container = driver.findElement(By.className("team-detail-infos"));

        String email = extractor.extractLawyerAttribute(container, new By[]{By.className("email")}, "EMAIL", "href", LawyerExceptions::emailException);
        String practice = extractor.extractLawyerText(container, new By[]{By.className("info-item-fachgebiete")}, "PRACTICE AREA", LawyerExceptions::practiceAreaException);

        return Map.of(
                "link", MyDriver.getINSTANCE().getCurrentUrl(),
                "name", name,
                "role", "Partner",
                "firm", this.name,
                "country", "Austria",
                "practice_area", practice,
                "email", email,
                "phone", "4315225700"
        );
    }
}
