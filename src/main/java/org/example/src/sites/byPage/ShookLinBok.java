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

public class ShookLinBok extends ByPage {

    public ShookLinBok() {
        super(
                "Shook Lin & Bok",
                "https://www.shooklin.com/en/our-partners?start=0",
                7
        );
    }

    @Override
    protected void accessPage(int index) throws InterruptedException {
        String otherUrl = "https://www.shooklin.com/en/our-partners?start=" + (index * 9);
        String url = index == 0 ? this.link : otherUrl;
        this.driver.get(url);
        MyDriver.waitForPageToLoad();
    }

    @Override
    protected List<WebElement> getLawyersInPage() {
        try {
            WebDriverWait wait = new WebDriverWait(this.driver, Duration.ofSeconds(10));
            return wait.until(ExpectedConditions.presenceOfAllElementsLocatedBy(By.className("itemContainer")));
        } catch (Exception e) {
            return List.of();
        }
    }

    @Override
    protected Object getLawyer(WebElement lawyer) throws Exception {
        String name = extractor.extractLawyerText(lawyer, new By[]{By.className("catItemTitle")}, "NAME", LawyerExceptions::nameException);
        String link = extractor.extractLawyerAttribute(lawyer, new By[]{By.cssSelector(".catItemTitle a")}, "LINK", "href", LawyerExceptions::linkException);

        WebElement contactInfo = lawyer.findElement(By.className("catItemExtraFields"));
        String[] socials = super.getSocialsFromText(contactInfo.getAttribute("textContent"));

        return Map.of(
                "link", link,
                "name", name,
                "role", "Partner",
                "firm", this.name,
                "country", "Singapore",
                "practice_area", "",
                "email", socials[0],
                "phone", socials[1].isEmpty() ? "6565351944" : socials[1]
        );
    }
}
