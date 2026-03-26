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

public class AllianceLaw extends ByNewPage {

    public AllianceLaw() {
        super(
                "Alliance Law",
                "https://alliancelaw.com.eg/people/",
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
            WebElement div = wait.until(
                    ExpectedConditions.presenceOfElementLocated(By.xpath("//*[@id=\"content\"]/div/div[3]"))
            );

            List<WebElement> lawyers = div.findElements(By.cssSelector("div.jet-listing-grid__item"));

            div = driver.findElement(By.xpath("//*[@id=\"content\"]/div/div[4]"));
            lawyers.addAll(div.findElements(By.cssSelector("div.jet-listing-grid__item")));

            div = driver.findElement(By.xpath("//*[@id=\"content\"]/div/div[5]"));
            lawyers.addAll(div.findElements(By.cssSelector("div.jet-listing-grid__item")));

            return lawyers;
        } catch (Exception e) {
            return List.of();
        }
    }

    @Override
    public String openNewTab(WebElement lawyer) throws LawyerExceptions {
        By[] byArray = {By.cssSelector("a.elementor-element[href*='https://alliancelaw.com.eg/profile/']")};
        String link = extractor.extractLawyerAttribute(lawyer, byArray, "LINK", "href", LawyerExceptions::linkException);
        MyDriver.openNewTab(link);
        return link;
    }

    @Override
    protected Object getLawyer(WebElement lawyer) throws Exception {
        String name = extractor.extractLawyerAttribute(lawyer, new By[]{By.cssSelector("h2.elementor-heading-title")}, "NAME", "textContent", LawyerExceptions::nameException);
        String role = extractor.extractLawyerAttribute(lawyer, new By[]{By.cssSelector("p.elementor-heading-title")}, "ROLE", "textContent", LawyerExceptions::roleException);

        this.openNewTab(lawyer);


        WebElement container = driver.findElement(By.tagName("body"));
        String[] socials = super.getSocials(container.findElements(By.tagName("a")), false);


        return Map.of(
                "link", Objects.requireNonNull(driver.getCurrentUrl()),
                "name", name,
                "role", role,
                "firm", this.name,
                "country", "Egypt",
                "practice_area", "",
                "email", socials[0],
                "phone", "20227365001"
        );
    }
}
