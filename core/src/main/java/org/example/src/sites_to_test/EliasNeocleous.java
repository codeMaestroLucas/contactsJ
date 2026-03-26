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

public class EliasNeocleous extends ByNewPage {

    public EliasNeocleous() {
        super(
                "Elias Neocleous & Co LLC",
                "https://neo.law/people/",
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
            List<WebElement> lawyers = wait.until(ExpectedConditions.presenceOfAllElementsLocatedBy(By.cssSelector(".elementor-widget-wrap.elementor-element-populated")));
            return this.siteUtl.filterLawyersInPage(lawyers, new By[]{By.className("single-people__position")}, true);
        } catch (Exception e) {
            return List.of();
        }
    }

    @Override
    public String openNewTab(WebElement lawyer) throws LawyerExceptions {
        By[] byArray = {By.cssSelector("h2.elementor-heading-title a")};
        String link = extractor.extractLawyerAttribute(lawyer, byArray, "LINK", "href", LawyerExceptions::linkException);
        MyDriver.openNewTab(link);
        return link;
    }

    @Override
    protected Object getLawyer(WebElement lawyer) throws Exception {
        this.openNewTab(lawyer);
        WebElement container = driver.findElement(By.className("elementor-element-e00687b"));

        String name = extractor.extractLawyerText(null, new By[]{By.className("elementor-element-9c91083"), By.tagName("h2")}, "NAME", LawyerExceptions::nameException);
        String[] socials = super.getSocialsFromText(container.getText());

        String practiceArea = "";
        try {
            practiceArea = driver.findElement(By.className("elementor-element-548de30")).getText().replace("PRACTICE AREA", "").trim();
        } catch (Exception ignored) {}

        return Map.of(
                "link", Objects.requireNonNull(driver.getCurrentUrl()),
                "name", name,
                "role", socials[0].contains("Partner") ? "Partner" : "Lawyer",
                "firm", this.name,
                "country", "Cyprus",
                "practice_area", practiceArea,
                "email", socials[0],
                "phone", socials[1].isEmpty() ? "35725110110" : socials[1]
        );
    }
}
