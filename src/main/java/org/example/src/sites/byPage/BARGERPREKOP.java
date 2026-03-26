package org.example.src.sites.byPage;

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

public class BARGERPREKOP extends ByNewPage {

    public BARGERPREKOP() {
        super(
                "BARGER PREKOP",
                "https://www.bargerprekop.com/the-team/partners/",
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
            WebElement div = wait.until(ExpectedConditions.presenceOfElementLocated(By.cssSelector("ul.level_1.the_team")));
            return div.findElements(By.cssSelector("a[href*='/the-team/partners/'], a[href*='/the-team/counsel/']"));
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
    public Object getLawyer(WebElement lawyer) throws Exception {
        this.openNewTab(lawyer);
        WebElement div = driver.findElement(By.id("article_wrap"));

        String[] socials = super.getSocials(div.findElements(By.cssSelector("td")), true);

        return Map.of(
                "link", Objects.requireNonNull(driver.getCurrentUrl()),
                "name", extractor.extractLawyerText(div, new By[]{By.cssSelector("h1.sub")}, "NAME", LawyerExceptions::nameException),
                "role", "----",
                "firm", this.name,
                "country", "Slovakia",
                "practice_area", "",
                "email", socials[0],
                "phone", socials[1].isEmpty() ? "+421 2 3211 9890" : socials[1]
        );
    }
}
