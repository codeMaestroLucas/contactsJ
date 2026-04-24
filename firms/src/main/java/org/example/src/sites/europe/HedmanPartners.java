package org.example.src.sites.europe;

import org.example.exceptions.LawyerExceptions;
import org.example.src.entities.BaseSites.ByPage;
import org.example.src.entities.MyDriver;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;

import java.util.List;
import java.util.Map;

public class HedmanPartners extends ByPage {

    public HedmanPartners() {
        super(
                "HEDMAN PARTNERS",
                "https://hedmanpartners.fi/tiimi/",
                1
        );
    }

    @Override
    protected void accessPage(int index) throws InterruptedException {
        this.driver.get(this.link);
        MyDriver.waitForPageToLoad();
        MyDriver.rollDownToBottom(0.2);
    }

    @Override
    protected List<WebElement> getLawyersInPage() {
        return MyDriver.wait.findElements(By.cssSelector("a.profile-glass-box"));
    }

    @Override
    protected Object getLawyer(WebElement lawyer) throws Exception {
        String[] socials = this.getSocialsFromText(lawyer.getText());

        return Map.of(
                "link", extractor.extractLawyerAttribute(lawyer, new By[]{}, "LINK", "href", LawyerExceptions::linkException),
                "name", extractor.extractLawyerText(lawyer, new By[]{By.className("profile-name")}, "NAME", LawyerExceptions::nameException),
                "role", "",
                "firm", this.name,
                "country", "Finland",
                "practice_area", "",
                "email", socials[0],
                "phone", socials[1]
        );
    }
}
