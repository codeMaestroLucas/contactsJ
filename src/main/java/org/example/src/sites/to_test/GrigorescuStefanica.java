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
import java.util.stream.Collectors;

public class GrigorescuStefanica extends ByNewPage {

    public GrigorescuStefanica() {
        super(
                "Grigorescu Ştefănică",
                "https://www.bpv-grigorescu.com/people/",
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
            List<WebElement> lawyers = wait.until(ExpectedConditions.presenceOfAllElementsLocatedBy(By.tagName("article")));
            return this.siteUtl.filterLawyersInPage(lawyers, new By[]{By.cssSelector("span.U_fz14")}, true);
        } catch (Exception e) {
            return List.of();
        }
    }

    @Override
    public String openNewTab(WebElement lawyer) throws LawyerExceptions {
        String link = lawyer.findElement(By.cssSelector("a.post-thumbnail")).getAttribute("href");
        MyDriver.openNewTab(link);
        return link;
    }

    @Override
    protected Object getLawyer(WebElement lawyer) throws Exception {
        this.openNewTab(lawyer);
        WebElement container = driver.findElement(By.className("C_lawyer__info"));

        By[] nameBy = {By.tagName("h1")};
        By[] roleBy = {By.className("O_article__subtitle")};

        String[] socials = super.getSocials(container.findElements(By.tagName("a")), false);

        List<WebElement> practiceElements = driver.findElements(By.cssSelector(".C_sidebar__box--areas a"));
        String practiceArea = practiceElements.stream().map(WebElement::getText).collect(Collectors.joining(", "));

        return Map.of(
                "link", Objects.requireNonNull(driver.getCurrentUrl()),
                "name", extractor.extractLawyerText(driver.findElement(By.tagName("body")), nameBy, "NAME", LawyerExceptions::nameException),
                "role", extractor.extractLawyerText(driver.findElement(By.tagName("body")), roleBy, "ROLE", LawyerExceptions::roleException),
                "firm", this.name,
                "country", "Romania",
                "practice_area", practiceArea,
                "email", socials[0],
                "phone", socials[1].isEmpty() ? "+40 21 264 16 50" : socials[1]
        );
    }
}
