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

public class ACCRALAW extends ByNewPage {

    public ACCRALAW() {
        super(
                "ACCRALAW",
                "https://accralaw.com/our-lawyers/",
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
            return wait.until(ExpectedConditions.presenceOfAllElementsLocatedBy(By.className("td-people-item")));
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
        String name = extractor.extractLawyerText(lawyer, new By[]{By.tagName("h3")}, "NAME", LawyerExceptions::nameException);
        if (name.contains("†")) return "Invalid Role";

        String link = this.openNewTab(lawyer);
        WebElement container = driver.findElement(By.className("elementor-widget-wrap"));

        String email = extractor.extractLawyerText(container, new By[]{By.xpath(".//div[contains(text(), '@')]")}, "EMAIL", LawyerExceptions::emailException);
        String phone = extractor.extractLawyerText(container, new By[]{By.xpath(".//div[contains(text(), '(')]")}, "PHONE", LawyerExceptions::phoneException);
        String practiceArea = extractor.extractLawyerText(container, new By[]{By.className("elementor-shortcode")}, "PRACTICE AREA", LawyerExceptions::practiceAreaException);

        return Map.of(
                "link", link,
                "name", name,
                "role", "Partner",
                "firm", this.name,
                "country", "the Philippines",
                "practice_area", practiceArea,
                "email", email,
                "phone", phone.isEmpty() ? "63288308000" : phone
        );
    }
}
