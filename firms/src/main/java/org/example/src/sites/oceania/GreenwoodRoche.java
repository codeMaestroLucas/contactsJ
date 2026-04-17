package org.example.src.sites.oceania;

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

public class GreenwoodRoche extends ByNewPage {

    public GreenwoodRoche() {
        super(
                "Greenwood Roche",
                "https://www.greenwoodroche.com/people",
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
            List<WebElement> lawyers = wait.until(ExpectedConditions.presenceOfAllElementsLocatedBy(By.className("article-listing")));
            return this.siteUtl.filterLawyersInPage(lawyers, new By[]{By.tagName("h6")}, true);
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
        String name = extractor.extractLawyerText(lawyer, new By[]{By.tagName("h4")}, "NAME", LawyerExceptions::nameException);
        String role = extractor.extractLawyerText(lawyer, new By[]{By.tagName("h6")}, "ROLE", LawyerExceptions::roleException);

        this.openNewTab(lawyer);
        WebElement bioInfo = driver.findElement(By.className("bio-info"));

        String[] socials = super.getSocials(bioInfo.findElements(By.tagName("a")), false);
        String phone = extractor.extractLawyerText(bioInfo, new By[]{By.tagName("p")}, "PHONE", LawyerExceptions::phoneException);

        return Map.of(
                "link", Objects.requireNonNull(driver.getCurrentUrl()),
                "name", name,
                "role", role,
                "firm", this.name,
                "country", "New Zealand",
                "practice_area", "",
                "email", socials[0],
                "phone", phone.isEmpty() ? "6493060490" : phone
        );
    }
}
