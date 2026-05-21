package org.example.src.sites.to_test.europe;

import org.example.exceptions.LawyerExceptions;
import org.example.src.entities.BaseSites.ByPage;
import org.example.src.entities.MyDriver;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import java.util.List;
import java.util.Map;

public class TrinityInternational extends ByPage {

    public TrinityInternational() {
        super(
                "Trinity International",
                "https://www.trinityllp.com/meettheteam/",
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
        return MyDriver.wait.findElements(By.cssSelector("li[data-name]"));
    }

    @Override
    public Object getLawyer(WebElement lawyer) throws Exception {
        String rawText = extractor.extractLawyerText(lawyer, new By[]{By.tagName("p")}, "SOCIALS", LawyerExceptions::socialsException);
        String[] socials = this.getSocialsFromText(rawText);

        return Map.of(
                "link", extractor.extractLawyerAttribute(lawyer, new By[]{By.tagName("a")}, "LINK", "href", LawyerExceptions::linkException),
                "name", lawyer.getAttribute("data-name"),
                "role", "Lawyer",
                "firm", this.name,
                "country", "United Kingdom",
                "practice_area", lawyer.getAttribute("data-services"),
                "email", socials[0],
                "phone", socials[1].isEmpty() ? "442079977040" : socials[1]
        );
    }
}
