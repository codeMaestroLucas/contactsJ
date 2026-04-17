package org.example.src.sites.asia;

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

public class FGEEbrahimHosain extends ByNewPage {

    public FGEEbrahimHosain() {
        super(
                "FGE Ebrahim Hosain",
                "https://www.fge-eh.com/people/",
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
            return wait.until(ExpectedConditions.presenceOfAllElementsLocatedBy(By.cssSelector("a.team-member-link[href*='https://www.fge-eh.com/people/']")));
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

        String link = this.openNewTab(lawyer);

        String name = extractor.extractLawyerAttribute(driver.findElement(By.tagName("body")), new By[]{By.xpath("//div/div/section[1]/div/div[2]/div/div[1]/div/div/h3")}, "NAME", "textContent", LawyerExceptions::nameException);
        String[] split = extractor.extractLawyerAttribute(driver.findElement(By.className("subtitle")), new By[]{}, "EMAIL", "innerHTML", LawyerExceptions::emailException).split("<br>");
        String email = split[1].trim();
        String role = split[0].trim();

        return Map.of(
                "link", link,
                "name", name,
                "role", role,
                "firm", this.name,
                "country", "Pakistan",
                "practice_area", "",
                "email", email,
                "phone", "92213587583334"
        );
    }
}
