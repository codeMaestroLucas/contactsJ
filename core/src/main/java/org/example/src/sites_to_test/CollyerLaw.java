package org.example.src.sites_to_test;

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

public class CollyerLaw extends ByNewPage {

    public CollyerLaw() {
        super(
                "Collyer Law",
                "https://www.collyerlaw.com/team",
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
            List<WebElement> lawyers = wait.until(ExpectedConditions.presenceOfAllElementsLocatedBy(By.className("_FiCX")));
            return this.siteUtl.filterLawyersInPage(lawyers, new By[]{By.xpath(".//div[contains(@id,'comp-limyrgzv2')]")}, true);
        } catch (Exception e) {
            return List.of();
        }
    }

    @Override
    public String openNewTab(WebElement lawyer) throws LawyerExceptions {
        By[] byArray = {By.xpath(".//a[contains(.,'View Profile')]")};
        String link = extractor.extractLawyerAttribute(lawyer, byArray, "LINK", "href", LawyerExceptions::linkException);
        MyDriver.openNewTab(link);
        return link;
    }

    @Override
    protected Object getLawyer(WebElement lawyer) throws Exception {
        // Socials are on the main team page according to prompt
        String[] socials = super.getSocials(List.of(lawyer), true);

        String name = extractor.extractLawyerText(lawyer, new By[]{By.xpath(".//div[contains(@id,'comp-limyrgzt3')]")}, "NAME", LawyerExceptions::nameException);
        String role = extractor.extractLawyerText(lawyer, new By[]{By.xpath(".//div[contains(@id,'comp-limyrgzv2')]")}, "ROLE", LawyerExceptions::roleException);

        return Map.of(
                "link", this.openNewTab(lawyer),
                "name", name,
                "role", role,
                "firm", this.name,
                "country", "Singapore",
                "practice_area", "",
                "email", socials[0],
                "phone", socials[1].isEmpty() ? "6569502875" : socials[1]
        );
    }
}
