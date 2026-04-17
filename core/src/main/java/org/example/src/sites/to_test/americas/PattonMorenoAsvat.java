package org.example.src.sites.to_test.americas;

import org.example.exceptions.LawyerExceptions;
import org.example.src.entities.BaseSites.ByPage;
import org.example.src.entities.MyDriver;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;

import java.util.List;
import java.util.Map;

public class PattonMorenoAsvat extends ByPage {

    public PattonMorenoAsvat() {
        super(
                "Patton, Moreno & Asvat",
                "https://www.pmalawyers.com/attorneys",
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
        return MyDriver.wait.findElements(By.cssSelector("[id^='comp-k9cgxefb']"));
    }

    @Override
    protected Object getLawyer(WebElement lawyer) throws Exception {
        String name = extractor.extractLawyerText(lawyer, new By[]{By.cssSelector("p:nth-child(1)")}, "NAME", LawyerExceptions::nameException);
        String role = extractor.extractLawyerText(lawyer, new By[]{By.cssSelector("p:nth-child(2)")}, "ROLE", LawyerExceptions::roleException);
        String email = extractor.extractLawyerText(lawyer, new By[]{By.tagName("a")}, "EMAIL", LawyerExceptions::emailException);

        return Map.of(
                "link", lawyer.findElement(By.tagName("a")).getAttribute("href"),
                "name", name,
                "role", role,
                "firm", this.name,
                "country", "Panama",
                "practice_area", "",
                "email", email,
                "phone", "xxxxxx"
        );
    }
}
