package org.example.src.sites.to_test;

import org.example.exceptions.LawyerExceptions;
import org.example.src.entities.BaseSites.ByNewPage;
import org.example.src.entities.MyDriver;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;

import java.util.List;
import java.util.Map;

public class LakshmikumaranSridharan extends ByNewPage {

    public LakshmikumaranSridharan() {
        super(
                "Lakshmikumaran & Sridharan",
                "https://www.lakshmisri.com/people?people=a",
                1
        );
    }

    @Override
    protected void accessPage(int index) {
        this.driver.get(this.link);
        MyDriver.waitForPageToLoad();
    }

    @Override
    protected List<WebElement> getLawyersInPage() {
        List<WebElement> lawyers = driver.findElements(By.className("people-card"));
        return this.siteUtl.filterLawyersInPage(lawyers, new By[]{By.cssSelector(".text-bg p")}, true);
    }

    @Override
    public String openNewTab(WebElement lawyer) throws LawyerExceptions {
        MyDriver.clickOnElement(lawyer);
        MyDriver.switchToTab(1);
        return driver.getCurrentUrl();
    }

    @Override
    protected Object getLawyer(WebElement lawyer) throws Exception {
        String name = extractor.extractLawyerText(lawyer, new By[]{By.tagName("h3")}, "NAME", LawyerExceptions::nameException);
        String role = extractor.extractLawyerText(lawyer, new By[]{By.cssSelector(".text-bg p")}, "ROLE", LawyerExceptions::roleException);

        String link = this.openNewTab(lawyer);

        WebElement container = driver.findElement(By.className("email-text"));
        String[] socials = super.getSocialsFromText(container.getText());

        return Map.of(
                "link", link,
                "name", name,
                "role", role,
                "firm", this.name,
                "country", "India",
                "practice_area", "",
                "email", socials[0],
                "phone", socials[1]
        );
    }
}
