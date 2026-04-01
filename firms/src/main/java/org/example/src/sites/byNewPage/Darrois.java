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

public class Darrois extends ByNewPage {

    public Darrois() {
        super(
                "Darrois",
                "https://www.darrois.com/en/avocat/",
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
        By[] byRoleArray = {By.cssSelector("p.uppercase")};
        try {
            WebDriverWait wait = new WebDriverWait(this.driver, Duration.ofSeconds(10));
            List<WebElement> lawyers = wait.until(ExpectedConditions.presenceOfAllElementsLocatedBy(By.cssSelector("div.row > div.domaine")));
            return this.siteUtl.filterLawyersInPage(lawyers, byRoleArray, true);
        } catch (Exception e) {
            return List.of();
        }
    }

    @Override
    public String openNewTab(WebElement lawyer) throws LawyerExceptions {
        By[] byArray = {By.cssSelector("a[href*='https://www.darrois.com/en/']")};
        String link = extractor.extractLawyerAttribute(lawyer, byArray, "LINK", "href", LawyerExceptions::linkException);
        MyDriver.openNewTab(link);
        return link;
    }

    private String getName(WebElement lawyer) throws LawyerExceptions {
        By[] byArray = {By.className("title-lawyer")};
        return extractor.extractLawyerText(lawyer, byArray, "NAME", LawyerExceptions::nameException);
    }

    private String getRole(WebElement lawyer) throws LawyerExceptions {
        By[] byArray = {By.cssSelector("p.uppercase")};
        return extractor.extractLawyerText(lawyer, byArray, "ROLE", LawyerExceptions::roleException);
    }

    private String getPracticeArea(WebElement lawyer) {
        try {
            By[] byArray = {By.xpath("//div[@data-content='domains']//li")};
            return extractor.extractLawyerText(lawyer, byArray, "PRACTICE AREA", LawyerExceptions::practiceAreaException);
        } catch (Exception e) {
            return "";
        }
    }

    @Override
    public Object getLawyer(WebElement lawyer) throws Exception {
        String name = this.getName(lawyer);
        String role = this.getRole(lawyer);

        String link = this.openNewTab(lawyer);

        WebDriverWait wait = new WebDriverWait(this.driver, Duration.ofSeconds(10));
        WebElement container = wait.until(ExpectedConditions.presenceOfElementLocated(By.xpath("//*[@id=\"js-body\"]/main/div/div[2]/div/div[1]")));

        String[] socials = super.getSocialsFromText(container.getAttribute("textContent"));

        return Map.of(
                "link", link,
                "name", name,
                "role", role,
                "firm", this.name,
                "country", "France",
                "practice_area", this.getPracticeArea(driver.findElement(By.xpath("//*[@id=\"js-body\"]/main/div/div[2]/div/div[1]/p[2]"))),
                "email", socials[0],
                "phone", socials[1].isEmpty() ? "33145021919" : socials[1]
        );
    }
}
