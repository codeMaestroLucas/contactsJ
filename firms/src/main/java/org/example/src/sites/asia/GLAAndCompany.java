package org.example.src.sites.asia;

import org.example.exceptions.LawyerExceptions;
import org.example.src.entities.BaseSites.ByPage;
import org.example.src.entities.MyDriver;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.time.Duration;
import java.util.List;
import java.util.Map;

public class GLAAndCompany extends ByPage {

    public GLAAndCompany() {
        super(
                "GLA & Company",
                "https://www.glaco.com/our-team/",
                1
        );
    }

    @Override
    protected void accessPage(int index) throws InterruptedException {
        this.driver.get(this.link);
        MyDriver.waitForPageToLoad();
        MyDriver.clickOnElement(By.xpath("//*[@id=\"container\"]/div[3]/div/div/div/div[2]/form/div[2]/button"));
        Thread.sleep(4000);
    }

    @Override
    protected List<WebElement> getLawyersInPage() {
        try {
            WebDriverWait wait = new WebDriverWait(this.driver, Duration.ofSeconds(10));
            List<WebElement> lawyers = wait.until(ExpectedConditions.presenceOfAllElementsLocatedBy(By.className("attorney-single-row")));
            return this.siteUtl.filterLawyersInPage(lawyers, new By[]{By.className("attorney-row-info-main-position")}, true);
        } catch (Exception e) {
            return List.of();
        }
    }

    @Override
    protected Object getLawyer(WebElement lawyer) throws Exception {
        String[] socials = super.getSocials(lawyer.findElements(By.cssSelector(".attorney-row-info-main-contact a")), false);

        return Map.of(
                "link", extractor.extractLawyerAttribute(lawyer, new By[]{By.cssSelector("h2 a")}, "LINK", "href", LawyerExceptions::linkException),
                "name", extractor.extractLawyerText(lawyer, new By[]{By.tagName("h2")}, "NAME", LawyerExceptions::nameException),
                "role", extractor.extractLawyerText(lawyer, new By[]{By.className("attorney-row-info-main-position")}, "ROLE", LawyerExceptions::roleException),
                "firm", this.name,
                "country", "the UAE",
                "practice_area", "",
                "email", socials[0],
                "phone", socials[1].isEmpty() ? "96555001122" : socials[1]
        );
    }
}
