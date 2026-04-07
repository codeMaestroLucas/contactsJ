package org.example.src.sites.to_test.americas;

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

public class DSNavarroCastex extends ByNewPage {

    public DSNavarroCastex() {
        super(
                "DS Navarro Castex",
                "https://www.dsavocats.com/en/team/",
                1
        );
    }

    @Override
    protected void accessPage(int index) throws InterruptedException {
        this.driver.get(this.link);
        MyDriver.waitForPageToLoad();
        Thread.sleep(1000L);
    }

    @Override
    protected List<WebElement> getLawyersInPage() {
        try {
            WebDriverWait wait = new WebDriverWait(this.driver, Duration.ofSeconds(10));
            List<WebElement> lawyers = wait.until(ExpectedConditions.presenceOfAllElementsLocatedBy(By.cssSelector("a.filter_item")));
            return this.siteUtl.filterLawyersInPage(lawyers, new By[]{By.className("main_function")}, true);
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
        WebElement container = driver.findElement(By.className("details_profil"));

        String name = extractor.extractLawyerText(null, new By[]{By.tagName("h1")}, "NAME", LawyerExceptions::nameException);
        String role = extractor.extractLawyerText(null, new By[]{By.className("main_function")}, "ROLE", LawyerExceptions::roleException);
        String practice = extractor.extractLawyerText(container, new By[]{By.cssSelector("#collapseOne .card-body li")}, "PRACTICE AREA", LawyerExceptions::practiceAreaException);

        String[] socials = super.getSocials(container.findElements(By.tagName("a")), false);

        return Map.of(
                "link", Objects.requireNonNull(driver.getCurrentUrl()),
                "name", name,
                "role", role,
                "firm", this.name,
                "country", "Argentina",
                "practice_area", practice,
                "email", socials[0],
                "phone", socials[1].isEmpty() ? "541143126500" : socials[1]
        );
    }
}
