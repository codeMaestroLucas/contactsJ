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

public class BrighteousLawFirm extends ByNewPage {

    public BrighteousLawFirm() {
        super(
                "Brighteous Law Firm",
                "http://www.zjblf.com/en/category/%E5%9B%A2%E9%98%9F%E4%BB%8B%E7%BB%8D-en/contacts/",
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
            return wait.until(ExpectedConditions.presenceOfAllElementsLocatedBy(By.className("en-team_card_container")));
        } catch (Exception e) {
            return List.of();
        }
    }

    @Override
    public String openNewTab(WebElement lawyer) throws LawyerExceptions {
        By[] byArray = {By.className("card-block-title"), By.tagName("a")};
        String link = extractor.extractLawyerAttribute(lawyer, byArray, "LINK", "href", LawyerExceptions::linkException);
        MyDriver.openNewTab(link);
        return link;
    }

    @Override
    protected Object getLawyer(WebElement lawyer) throws Exception {
        String name = extractor.extractLawyerText(lawyer, new By[]{By.className("card-block-title")}, "NAME", LawyerExceptions::nameException);

        this.openNewTab(lawyer);
        WebElement content = driver.findElement(By.className("col-md-12"));

        String text = content.getText();
        String[] socials = super.getSocialsFromText(text);

        return Map.of(
                "link", MyDriver.getINSTANCE().getCurrentUrl(),
                "name", name,
                "role", "Partner",
                "firm", this.name,
                "country", "China",
                "practice_area", "International Trade",
                "email", socials[0],
                "phone", socials[1].isEmpty() ? "8657187007129" : socials[1]
        );
    }
}
