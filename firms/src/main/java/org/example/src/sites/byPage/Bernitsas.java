package org.example.src.sites.byPage;

import org.example.exceptions.LawyerExceptions;
import org.example.src.entities.BaseSites.ByPage;
import org.example.src.entities.MyDriver;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.time.Duration;
import java.util.List;
import java.util.Map;

public class Bernitsas extends ByPage {

    public Bernitsas() {
        super(
                "Bernitsas",
                "https://bernitsaslaw.com/lawyers",
                4
        );
    }

    @Override
    protected void accessPage(int index) throws InterruptedException {
        String otherUrl = "https://bernitsaslaw.com/lawyers?page=" + index;
        String url = index == 0 ? this.link : otherUrl;
        this.driver.get(url);
        MyDriver.waitForPageToLoad();
    }

    @Override
    protected List<WebElement> getLawyersInPage() {
        try {
            WebDriverWait wait = new WebDriverWait(this.driver, Duration.ofSeconds(10));
            WebElement div = wait.until(ExpectedConditions.presenceOfElementLocated(By.xpath("//*[@id=\"block-rocket-path-theme-content\"]/div/div/div/div[2]")));
            List<WebElement> lawyers = div.findElements(By.cssSelector(".views-row"));
            return this.siteUtl.filterLawyersInPage(lawyers, new By[]{By.className("field--field-seniority")}, true);
        } catch (Exception e) {
            return List.of();
        }
    }

    @Override
    public Object getLawyer(WebElement lawyer) throws Exception {
        By[] roleBy = {By.className("field--field-seniority")};
        By[] linkBy = {By.cssSelector("a")};

        String name = extractor.extractLawyerText(lawyer, new By[]{By.className("field--field-first-name")}, "FIRST NAME", LawyerExceptions::nameException) + " " +
                extractor.extractLawyerText(lawyer, new By[]{By.className("field--field-last-name")}, "LAST NAME", LawyerExceptions::nameException);

        String[] socials = super.getSocials(lawyer.findElements(By.cssSelector("a")), false);

        return Map.of(
                "link", extractor.extractLawyerAttribute(lawyer, linkBy, "LINK", "href", LawyerExceptions::linkException),
                "name", name.trim(),
                "role", extractor.extractLawyerText(lawyer, roleBy, "ROLE", LawyerExceptions::roleException),
                "firm", this.name,
                "country", "Greece",
                "practice_area", "",
                "email", socials[0],
                "phone", socials[1].isEmpty() ? "302103392950" : socials[1]
        );
    }
}
