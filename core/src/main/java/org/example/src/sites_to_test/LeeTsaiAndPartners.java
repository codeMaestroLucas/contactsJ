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

public class LeeTsaiAndPartners extends ByNewPage {

    public LeeTsaiAndPartners() {
        super(
                "Lee Tsai & Partners",
                "https://www.leetsai.com/people.php",
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
            return wait.until(ExpectedConditions.presenceOfAllElementsLocatedBy(By.className("lawyer-sin")));
        } catch (Exception e) {
            return List.of();
        }
    }

    @Override
    public String openNewTab(WebElement lawyer) throws LawyerExceptions {
        String link = extractor.extractLawyerAttribute(lawyer, new By[]{By.tagName("a")}, "LINK", "href", LawyerExceptions::linkException);
        MyDriver.openNewTab(link);
        return link;
    }

    @Override
    protected Object getLawyer(WebElement lawyer) throws Exception {
        this.openNewTab(lawyer);
        WebElement bioInfo = driver.findElement(By.className("webuse"));
        WebElement content = driver.findElement(By.className("blog_details_inner"));

        String name = extractor.extractLawyerText(driver.findElement(By.tagName("body")), new By[]{By.className("lawyer-info"), By.tagName("h4")}, "NAME", LawyerExceptions::nameException);
        String role = extractor.extractLawyerText(driver.findElement(By.tagName("body")), new By[]{By.className("lawyer-info"), By.tagName("h6")}, "ROLE", LawyerExceptions::roleException);

        String[] socials = super.getSocials(bioInfo.findElements(By.className("info-contact")), true);
        String practice = extractor.extractLawyerText(content, new By[]{By.id("practices")}, "PRACTICE AREA", LawyerExceptions::practiceAreaException);

        return Map.of(
                "link", Objects.requireNonNull(driver.getCurrentUrl()),
                "name", name,
                "role", role,
                "firm", this.name,
                "country", "Taiwan",
                "practice_area", practice,
                "email", socials[0],
                "phone", socials[1].isEmpty() ? "886223785780" : socials[1]
        );
    }
}
