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

public class ChanceBridge extends ByNewPage {

    public ChanceBridge() {
        super(
                "Chance Bridge",
                "https://chancebridge.com/en/zyry/1/?pid=1",
                5
        );
    }

    @Override
    protected void accessPage(int index) throws InterruptedException {
        String url = index == 0 ? this.link : "https://chancebridge.com/en/zyry/" + (index + 1) + "/?pid=1";
        this.driver.get(url);
        MyDriver.waitForPageToLoad();
    }

    @Override
    protected List<WebElement> getLawyersInPage() {
        try {
            WebDriverWait wait = new WebDriverWait(this.driver, Duration.ofSeconds(10L));
            List<WebElement> lawyers = wait.until(ExpectedConditions.presenceOfAllElementsLocatedBy(By.cssSelector(".wow.fadeInUp")));
            return this.siteUtl.filterLawyersInPage(lawyers, new By[]{By.className("subname")}, true);
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
        WebElement container = driver.findElement(By.className("person-link-type"));

        String name = driver.findElement(By.cssSelector(".person-name h1")).getText().split("\n")[0].trim();
        String role = driver.findElement(By.className("person-title")).getText();

        List<WebElement> socialElements = container.findElements(By.className("person-list-desc"));
        String[] socials = super.getSocials(socialElements, true);

        return Map.of(
                "link", driver.getCurrentUrl(),
                "name", name,
                "role", role,
                "firm", this.name,
                "country", "China",
                "practice_area", "",
                "email", socials[0],
                "phone", socials[1].isEmpty() ? "861085870075" : socials[1]
        );
    }
}
