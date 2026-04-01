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

public class Kangda extends ByNewPage {

    public Kangda() {
        super(
                "Kangda",
                "http://en.kangdalawyers.com/team",
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
            List<WebElement> lawyers = wait.until(ExpectedConditions.presenceOfAllElementsLocatedBy(By.cssSelector(".team_list li")));
            return this.siteUtl.filterLawyersInPage(lawyers, new By[]{By.cssSelector(".team_c_rt p span:last-child")}, true);
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
        String name = extractor.extractLawyerText(lawyer, new By[]{By.cssSelector(".team_c_rt p span:first-child")}, "NAME", LawyerExceptions::nameException);
        String role = extractor.extractLawyerText(lawyer, new By[]{By.cssSelector(".team_c_rt p span:last-child")}, "ROLE", LawyerExceptions::roleException).replace("|", "").trim();

        String link = this.openNewTab(lawyer);

        WebElement container = driver.findElement(By.className("teamxq_cl_top"));
        String[] socials = super.getSocialsFromText(container.getText());
        String practice = extractor.extractLawyerText(container, new By[]{By.xpath(".//p[contains(text(),'Practices Areas：')]")}, "PRACTICE AREA", LawyerExceptions::practiceAreaException).replace("Practices Areas：", "").trim();

        return Map.of(
                "link", link,
                "name", name,
                "role", role,
                "firm", this.name,
                "country", "China",
                "practice_area", practice,
                "email", socials[0],
                "phone", socials[1].isEmpty() ? "861050867666" : socials[1]
        );
    }
}
