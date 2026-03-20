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

public class JLDAndMBLegal extends ByNewPage {

    public JLDAndMBLegal() {
        super(
                "JLDAndMB Legal",
                "http://jldmblaw.net/our-team",
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
            List<WebElement> lawyers = wait.until(ExpectedConditions.presenceOfAllElementsLocatedBy(By.className("grid-item")));
            return this.siteUtl.filterLawyersInPage(lawyers, new By[]{By.className("expertise-sub")}, true);
        } catch (Exception e) {
            return List.of();
        }
    }

    @Override
    public String openNewTab(WebElement lawyer) throws LawyerExceptions {
        By[] byArray = {By.tagName("a")};
        String link = extractor.extractLawyerAttribute(lawyer, byArray, "LINK", "href", LawyerExceptions::linkException);
        MyDriver.openNewTab(link);
        return link;
    }

    private String[] getSocials() {
        try {
            String textContent = driver.findElement(By.xpath("/html/body/div/div/main/div/div/article/div[1]/div[1]/div[2]/div[2]")).getText();
            return super.getSocialsFromText(textContent);

        } catch (Exception e) {
            return new String[]{"", ""};
        }
    }

    @Override
    protected Object getLawyer(WebElement lawyer) throws Exception {
        String name = extractor.extractLawyerText(lawyer, new By[]{By.className("expertise-title")}, "NAME", LawyerExceptions::nameException);
        String role = extractor.extractLawyerText(lawyer, new By[]{By.className("expertise-sub")}, "ROLE", LawyerExceptions::roleException);

        this.openNewTab(lawyer);
        WebElement container = driver.findElement(By.className("right-side"));
        String[] socials = this.getSocials();

        return Map.of(
                "link", Objects.requireNonNull(driver.getCurrentUrl()),
                "name", name,
                "role", role,
                "firm", this.name,
                "country", "Ghana",
                "practice_area", extractor.extractLawyerText(container, new By[]{By.xpath("//div/main/div/div/article/div[1]/div[1]/div[2]/div[3]/div/ul/li[1]")}, "PRACTICE AREA", LawyerExceptions::practiceAreaException),
                "email", socials[0],
                "phone", socials[1].isEmpty() ? "233302784298" : socials[1]
        );
    }
}
