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

public class IkeyiShittuAndCo extends ByNewPage {

    public IkeyiShittuAndCo() {
        super(
                "Ikeyi ShittuAndCo",
                "https://isc.ng/team-category/partner/",
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
            List<WebElement> lawyers = wait.until(ExpectedConditions.presenceOfAllElementsLocatedBy(By.className("cp-attorneys-style-3")));
            return this.siteUtl.filterLawyersInPage(lawyers, new By[]{By.className("destination")}, true);
        } catch (Exception e) {
            return List.of();
        }
    }

    @Override
    public String openNewTab(WebElement lawyer) throws LawyerExceptions {
        By[] byArray = {By.cssSelector("h3 a")};
        String link = extractor.extractLawyerAttribute(lawyer, byArray, "LINK", "href", LawyerExceptions::linkException);
        MyDriver.openNewTab(link);
        return link;
    }

    @Override
    protected Object getLawyer(WebElement lawyer) throws Exception {
        String name = extractor.extractLawyerText(lawyer, new By[]{By.tagName("h3")}, "NAME", LawyerExceptions::nameException);
        String role = extractor.extractLawyerText(lawyer, new By[]{By.className("destination")}, "ROLE", LawyerExceptions::roleException);

        this.openNewTab(lawyer);
        WebElement container = driver.findElement(By.className("wpb_wrapper"));

        String email = extractor.extractLawyerText(container, new By[]{By.cssSelector("a[href^='mailto:']")}, "EMAIL", LawyerExceptions::emailException);
        String contactText = container.getText();
        String[] socials = super.getSocialsFromText(contactText);

        return Map.of(
                "link", Objects.requireNonNull(driver.getCurrentUrl()),
                "name", name,
                "role", role,
                "firm", this.name,
                "country", "Nigeria",
                "practice_area", extractor.extractLawyerText(container, new By[]{By.xpath("//h3[contains(.,'Practice Areas')]/following-sibling::ul[1]")}, "PRACTICE AREA", LawyerExceptions::practiceAreaException),
                "email", email,
                "phone", socials[1].isEmpty() ? "02016324728" : socials[1]
        );
    }
}
