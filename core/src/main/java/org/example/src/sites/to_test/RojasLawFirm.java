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
import java.util.Objects;

public class RojasLawFirm extends ByNewPage {

    public RojasLawFirm() {
        super(
                "Rojas Law Firm",
                "https://rojas-lawfirm.com/en/lawyers/",
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
            return wait.until(ExpectedConditions.presenceOfAllElementsLocatedBy(By.cssSelector(".elementor-widget-container > p > a")));
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
        this.openNewTab(lawyer);
        WebElement container = driver.findElement(By.xpath("//*[@id=\"main\"]/div/section[1]/div[2]/div[1]/div"));

        String name = extractor.extractLawyerText(container, new By[]{By.tagName("h1")}, "NAME", LawyerExceptions::nameException);
        String role = extractor.extractLawyerText(container, new By[]{By.className("elementor-post-info__terms-list-item")}, "ROLE", LawyerExceptions::roleException);
        String practice = extractor.extractLawyerText(container, new By[]{By.xpath("//h4[contains(.,'Practice Areas')]/following-sibling::div")}, "PRACTICE AREA", LawyerExceptions::practiceAreaException);

        List<WebElement> links = container.findElements(By.tagName("a"));
        String[] socials = super.getSocials(links, false);

        return Map.of(
                "link", Objects.requireNonNull(driver.getCurrentUrl()),
                "name", name,
                "role", role,
                "firm", this.name,
                "country", "Bolivia",
                "practice_area", practice,
                "email", socials[0],
                "phone", socials[1].isEmpty() ? "59122113165" : socials[1]
        );
    }
}
