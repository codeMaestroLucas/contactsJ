package org.example.src.sites.byNewPage;

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

public class LHAG extends ByNewPage {

    public LHAG() {
        super(
                "LHAG",
                "https://lh-ag.com/people/",
                1
        );
    }

    @Override
    protected void accessPage(int index) throws InterruptedException {
        this.driver.get(this.link);
        MyDriver.waitForPageToLoad();
        MyDriver.rollDown(6, 0.5);
    }

    @Override
    protected List<WebElement> getLawyersInPage() {
        try {

            WebDriverWait wait = new WebDriverWait(this.driver, Duration.ofSeconds(10));
            WebElement div = wait.until(ExpectedConditions.presenceOfElementLocated(
                    By.xpath("//*[@id=\"partner-list\"]/div/div/div[1]")

            ));
            return div.findElements(By.cssSelector("a[href*='https://lh-ag.com/people/']"));
        } catch (Exception e) {
            return List.of();
        }
    }

    @Override
    public String openNewTab(WebElement lawyer) throws LawyerExceptions {
        MyDriver.openNewTab(lawyer.getAttribute("href"));
        return "";
    }

    @Override
    protected Object getLawyer(WebElement lawyer) throws Exception {
        this.openNewTab(lawyer);
        WebElement content = driver.findElement(By.xpath("/html/body/div[5]/section[1]/div/div/div/section[3]/div/div/div"));
        String[] socials = super.getSocials(content.findElements(By.tagName("a")), false);

        return Map.of(
                "link", Objects.requireNonNull(driver.getCurrentUrl()),
                "name", extractor.extractLawyerAttribute(content, new By[]{By.tagName("h1")}, "NAME", "textContent", LawyerExceptions::nameException),
                "role", extractor.extractLawyerAttribute(content, new By[]{By.tagName("h2")}, "ROLE", "textContent", LawyerExceptions::roleException),
                "firm", this.name,
                "country", "Malaysia",
                "practice_area", "",
                "email", socials[0],
                "phone", socials[1].isEmpty() ? "60362085888" : socials[1]
        );
    }
}