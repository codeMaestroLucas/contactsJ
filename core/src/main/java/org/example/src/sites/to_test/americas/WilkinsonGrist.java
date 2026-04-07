package org.example.src.sites.to_test.americas;

import org.example.exceptions.LawyerExceptions;
import org.example.src.entities.BaseSites.ByPage;
import org.example.src.entities.MyDriver;
import org.example.src.utils.VCard;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.time.Duration;
import java.util.List;
import java.util.Map;

public class WilkinsonGrist extends ByPage {

    public WilkinsonGrist() {
        super(
                "Wilkinson & Grist",
                "http://www.wilgrist.com/wng/index.php/en/our-people",
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
            List<WebElement> lawyers = wait.until(ExpectedConditions.presenceOfAllElementsLocatedBy(By.cssSelector("tr[id^='tr_']")));
            return this.siteUtl.filterLawyersInPage(lawyers, new By[]{By.className("text6")}, true);
        } catch (Exception e) {
            return List.of();
        }
    }

    @Override
    protected Object getLawyer(WebElement lawyer) throws Exception {
        String name = extractor.extractLawyerText(lawyer, new By[]{By.className("text7")}, "NAME", LawyerExceptions::nameException);
        String role = extractor.extractLawyerText(lawyer, new By[]{By.className("text6")}, "ROLE", LawyerExceptions::roleException);
        String link = extractor.extractLawyerAttribute(lawyer, new By[]{By.tagName("a")}, "LINK", "href", LawyerExceptions::linkException);

        String vCardUrl = extractor.extractLawyerAttribute(lawyer, new By[]{By.className("download_btn")}, "VCARD", "href", LawyerExceptions::linkException);
        String[] socials = VCard.withDefaultPatterns().getSocials(vCardUrl);

        return Map.of(
                "link", link,
                "name", name,
                "role", role,
                "firm", this.name,
                "country", "Hong Kong",
                "practice_area", "",
                "email", socials[0],
                "phone", socials[1].isEmpty() ? "85225246011" : socials[1]
        );
    }
}
