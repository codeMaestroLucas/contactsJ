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

public class BDKAdvokati extends ByNewPage {

    public BDKAdvokati() {
        super(
                "BDK Advokati",
                "https://bdkadvokati.com/people",
                1
        );
    }

    public static final Map<String, String> OFFICE_TO_COUNTRY = Map.of(
            "387", "Bosnia and Herzegovina",
            "382", "Montenegro",
            "389", "North Macedonia",
            "381", "Serbia"
    );

    @Override
    protected void accessPage(int index) throws InterruptedException {
        this.driver.get(this.link);
        MyDriver.waitForPageToLoad();
    }

    @Override
    protected List<WebElement> getLawyersInPage() {
        try {
            WebDriverWait wait = new WebDriverWait(this.driver, Duration.ofSeconds(10));
            WebElement until = wait.until(ExpectedConditions.presenceOfElementLocated(By.xpath("//*[@id=\"root\"]/div[4]/main[1]/div/section[2]/ul")));
            List<WebElement> lawyers = until.findElements(By.cssSelector("li.col-span-1"));
            return this.siteUtl.filterLawyersInPage(lawyers, new By[]{By.tagName("article")}, false);
        } catch (Exception e) {
            return List.of();
        }
    }

    @Override
    public String openNewTab(WebElement lawyer) throws LawyerExceptions {
        String link = lawyer.findElement(By.cssSelector("a.block")).getAttribute("href");
        MyDriver.openNewTab(link);
        return link;
    }

    @Override
    protected Object getLawyer(WebElement lawyer) throws Exception {
        this.openNewTab(lawyer);
        WebElement container = driver.findElement(By.cssSelector("div.sm\\:pb-12"));

        By[] nameBy = {By.tagName("h1")};
        By[] roleBy = {By.cssSelector("p.text-dark-blue")};

        String[] socials = super.getSocials(container.findElements(By.tagName("a")), false);
        String country = this.getCountry(socials[1]);

        return Map.of(
                "link", Objects.requireNonNull(driver.getCurrentUrl()),
                "name", extractor.extractLawyerAttribute(container, nameBy, "NAME", "textContent", LawyerExceptions::nameException),
                "role", extractor.extractLawyerAttribute(container, roleBy, "ROLE", "textContent", LawyerExceptions::roleException),
                "firm", this.name,
                "country", country,
                "practice_area", "",
                "email", socials[0],
                "phone", socials[1].isEmpty() ? "+381 11 3284 212" : socials[1]
        );
    }

    private String getCountry(String social) {
        return siteUtl.getCountryBasedInOffice(OFFICE_TO_COUNTRY, social, "Serbia");
    }
}
