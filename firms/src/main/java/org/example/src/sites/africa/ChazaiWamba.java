package org.example.src.sites.africa;

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

public class ChazaiWamba extends ByNewPage {

    public ChazaiWamba() {
        super(
                "Chazai Wamba",
                "https://www.chazai-wamba.com/en/chazai-wamba-team/",
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
            List<WebElement> lawyers = wait.until(ExpectedConditions.presenceOfAllElementsLocatedBy(By.className("daimg")));
            return this.siteUtl.filterLawyersInPage(lawyers, new By[]{By.tagName("span")}, false);
        } catch (Exception e) {
            return List.of();
        }
    }

    @Override
    public String openNewTab(WebElement lawyer) throws LawyerExceptions {
        String profileLink = extractor.extractLawyerAttribute(lawyer, new By[]{By.tagName("a")}, "LINK", "href", LawyerExceptions::linkException);
        MyDriver.openNewTab(profileLink);
        return profileLink;
    }

    @Override
    protected Object getLawyer(WebElement lawyer) throws Exception {
        this.openNewTab(lawyer);
        WebElement container = driver.findElement(By.className("equicolcontent"));

        String name = driver.findElement(By.tagName("h1")).getText().split(" is ")[0].trim();

        List<WebElement> socialElements = container.findElements(By.cssSelector(".icopar a"));
        String[] socials = super.getSocials(socialElements, false);

        return Map.of(
                "link", Objects.requireNonNull(driver.getCurrentUrl()),
                "name", name,
                "role", extractor.extractLawyerText(container, new By[]{By.tagName("h1")}, "ROLE", LawyerExceptions::roleException),
                "firm", this.name,
                "country", "Cameroon",
                "practice_area", extractor.extractLawyerText(container, new By[]{By.className("puceComp")}, "PRACTICE AREA", LawyerExceptions::practiceAreaException),
                "email", socials[0],
                "phone", socials[1].isEmpty() ? "237233432617" : socials[1]
        );
    }
}
