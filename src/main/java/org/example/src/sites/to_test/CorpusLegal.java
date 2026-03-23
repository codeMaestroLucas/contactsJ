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

public class CorpusLegal extends ByNewPage {

    public CorpusLegal() {
        super(
                "Corpus Legal",
                "https://corpus.co.zm/our-team-of-lawyers/",
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
            List<WebElement> lawyers = wait.until(ExpectedConditions.presenceOfAllElementsLocatedBy(By.cssSelector(".vc_column_container.vc_col-sm-1\\/5")));
            return this.siteUtl.filterLawyersInPage(lawyers, new By[]{By.tagName("p")}, true);
        } catch (Exception e) {
            return List.of();
        }
    }

    @Override
    public String openNewTab(WebElement lawyer) throws LawyerExceptions {
        By[] byArray = {By.cssSelector("h4 a")};
        String link = extractor.extractLawyerAttribute(lawyer, byArray, "LINK", "href", LawyerExceptions::linkException);
        MyDriver.openNewTab(link);
        return link;
    }

    @Override
    protected Object getLawyer(WebElement lawyer) throws Exception {
        String name = extractor.extractLawyerText(lawyer, new By[]{By.tagName("h4")}, "NAME", LawyerExceptions::nameException);
        String role = extractor.extractLawyerText(lawyer, new By[]{By.tagName("p")}, "ROLE", LawyerExceptions::roleException);

        this.openNewTab(lawyer);
        WebElement container = driver.findElement(By.xpath("/html/body/div[1]/div/div/div/div[2]/div/div/div[2]/div/div/div"));

        String email = extractor.extractLawyerText(container, new By[]{By.cssSelector("a[href^='mailto:']")}, "EMAIL", LawyerExceptions::emailException);
        String phone = extractor.extractLawyerText(container, new By[]{By.xpath("//p[contains(.,'Phone:')]")}, "PHONE", LawyerExceptions::phoneException);

        return Map.of(
                "link", Objects.requireNonNull(driver.getCurrentUrl()),
                "name", name,
                "role", role,
                "firm", this.name,
                "country", "Zambia",
                "practice_area", extractor.extractLawyerText(container, new By[]{By.xpath("//p[contains(.,'Practice Areas:')]")}, "PRACTICE AREA", LawyerExceptions::practiceAreaException),
                "email", email,
                "phone", phone.isEmpty() ? "260211372300" : phone
        );
    }
}