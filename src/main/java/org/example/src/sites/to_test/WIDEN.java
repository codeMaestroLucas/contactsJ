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

public class WIDEN extends ByPage {

    public WIDEN() {
        super(
                "WIDEN",
                "https://widen.legal/people/",
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
            List<WebElement> lawyers = wait.until(ExpectedConditions.presenceOfAllElementsLocatedBy(By.className("group/card")));
            return this.siteUtl.filterLawyersInPage(lawyers, new By[]{By.className("translation-block")}, true);
        } catch (Exception e) {
            return List.of();
        }
    }

    @Override
    protected Object getLawyer(WebElement lawyer) throws Exception {
        String name = extractor.extractLawyerText(lawyer, new By[]{By.className("text-2xl")}, "NAME", LawyerExceptions::nameException);
        String role = extractor.extractLawyerText(lawyer, new By[]{By.className("translation-block")}, "ROLE", LawyerExceptions::roleException);
        String link = extractor.extractLawyerAttribute(lawyer, new By[]{By.cssSelector("a[aria-label]")}, "LINK", "href", LawyerExceptions::linkException);

        String[] socials = super.getSocials(lawyer.findElements(By.tagName("a")), false);

        return Map.of(
                "link", link,
                "name", name,
                "role", role,
                "firm", this.name,
                "country", "Lithuania",
                "practice_area", "",
                "email", socials[0],
                "phone", socials[1].isEmpty() ? "37052107430" : socials[1]
        );
    }
}
