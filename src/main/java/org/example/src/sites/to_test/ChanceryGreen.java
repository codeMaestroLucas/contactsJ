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

public class ChanceryGreen extends ByNewPage {

    public ChanceryGreen() {
        super(
                "ChanceryGreen",
                "https://www.chancerygreen.com/people",
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
            List<WebElement> lawyers = wait.until(ExpectedConditions.presenceOfAllElementsLocatedBy(By.className("comp-lpgety6f7__item1")));
            return this.siteUtl.filterLawyersInPage(lawyers, new By[]{By.cssSelector("p.font_8")}, true);
        } catch (Exception e) {
            return List.of();
        }
    }

    @Override
    public String openNewTab(WebElement lawyer) throws LawyerExceptions {
        String profileLink = extractor.extractLawyerAttribute(lawyer, new By[]{By.cssSelector("h3 a")}, "LINK", "href", LawyerExceptions::linkException);
        MyDriver.openNewTab(profileLink);
        return profileLink;
    }

    @Override
    protected Object getLawyer(WebElement lawyer) throws Exception {
        this.openNewTab(lawyer);
        WebElement container = driver.findElement(By.className("comp-lzm4c42k-container"));

        String name = extractor.extractLawyerText(container, new By[]{By.tagName("h2")}, "NAME", LawyerExceptions::nameException);
        String role = extractor.extractLawyerText(container, new By[]{By.tagName("h4")}, "ROLE", LawyerExceptions::roleException);

        List<WebElement> socialElements = container.findElements(By.cssSelector("a[href^='mailto:']"));
        String phoneText = container.findElement(By.xpath("//p[contains(.,'m:')]")).getText();
        String[] socials = super.getSocials(socialElements, false);

        return Map.of(
                "link", driver.getCurrentUrl(),
                "name", name,
                "role", role,
                "firm", this.name,
                "country", "New Zealand",
                "practice_area", "",
                "email", socials[0],
                "phone", phoneText.replaceAll("[^0-9]", "")
        );
    }
}
