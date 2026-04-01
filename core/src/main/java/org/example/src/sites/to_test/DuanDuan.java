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

public class DuanDuan extends ByNewPage {

    public DuanDuan() {
        super(
                "Duan & Duan",
                "https://www.duanduan.com/team",
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
            List<WebElement> lawyers = wait.until(ExpectedConditions.presenceOfAllElementsLocatedBy(By.className("lawyerBox")));
            return this.siteUtl.filterLawyersInPage(lawyers, new By[]{By.className("field")}, true);
        } catch (Exception e) {
            return List.of();
        }
    }

    @Override
    public String openNewTab(WebElement lawyer) throws LawyerExceptions {
        String link = extractor.extractLawyerAttribute(lawyer, new By[]{By.xpath(".")}, "LINK", "href", LawyerExceptions::linkException);
        MyDriver.openNewTab(link);
        return link;
    }

    @Override
    protected Object getLawyer(WebElement lawyer) throws Exception {
        String name = extractor.extractLawyerText(lawyer, new By[]{By.className("name")}, "NAME", LawyerExceptions::nameException);
        String role = extractor.extractLawyerText(lawyer, new By[]{By.className("field")}, "ROLE", LawyerExceptions::roleException).replace("职务：", "").trim();

        String link = this.openNewTab(lawyer);

        WebElement container = driver.findElement(By.className("typeBox"));
        String[] socials = super.getSocialsFromText(container.getText());
        String practice = extractor.extractLawyerText(container, new By[]{By.xpath(".//span[contains(text(),'服务领域')]/following-sibling::span")}, "PRACTICE AREA", LawyerExceptions::practiceAreaException);

        return Map.of(
                "link", link,
                "name", name,
                "role", role,
                "firm", this.name,
                "country", "China",
                "practice_area", practice,
                "email", socials[0],
                "phone", socials[1].isEmpty() ? "862162191103" : socials[1]
        );
    }
}
