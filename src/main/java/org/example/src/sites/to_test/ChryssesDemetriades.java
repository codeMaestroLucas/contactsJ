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

public class ChryssesDemetriades extends ByNewPage {

    public ChryssesDemetriades() {
        super(
                "Chrysses Demetriades & Co",
                "https://www.demetriades.com/lawyers/",
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
            List<WebElement> lawyers = wait.until(ExpectedConditions.presenceOfAllElementsLocatedBy(By.cssSelector(".gdlr-core-personnel-list-title > a")));
            return this.siteUtl.filterLawyersInPage(lawyers, new By[]{By.xpath("./../../div[contains(@class,'gdlr-core-personnel-list-position')]")}, true);
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
        WebElement container = driver.findElement(By.className("gdlr-core-pbf-column-content"));

        String name = extractor.extractLawyerText(container, new By[]{By.tagName("h3")}, "NAME", LawyerExceptions::nameException);
        String role = extractor.extractLawyerText(container, new By[]{By.className("gdlr-core-title-item-caption")}, "ROLE", LawyerExceptions::roleException);

        List<WebElement> links = container.findElements(By.tagName("a"));
        String[] socials = super.getSocials(links, false);

        return Map.of(
                "link", Objects.requireNonNull(driver.getCurrentUrl()),
                "name", name,
                "role", role,
                "firm", this.name,
                "country", "Cyprus",
                "practice_area", "",
                "email", socials[0],
                "phone", socials[1].isEmpty() ? "35725800000" : socials[1]
        );
    }
}
