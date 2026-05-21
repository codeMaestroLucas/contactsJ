package org.example.src.sites.to_test.europe;

import org.example.exceptions.LawyerExceptions;
import org.example.src.entities.BaseSites.ByPage;
import org.example.src.entities.MyDriver;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;

import java.util.List;
import java.util.Map;

public class Waselius extends ByPage {

    public Waselius() {
        super(
                "Waselius",
                "https://www.waselius.fi/people/",
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
        return MyDriver.wait.findElements(By.className("grid-item"));
    }

    @Override
    public Object getLawyer(WebElement lawyer) throws Exception {
        String[] socials = this.getSocials(lawyer.findElements(By.tagName("a")), false);

        return Map.of(
                "link", extractor.extractLawyerAttribute(lawyer, new By[]{By.className("entry-title"), By.tagName("a")}, "LINK", "href", LawyerExceptions::linkException),
                "name", extractor.extractLawyerText(lawyer, new By[]{By.className("entry-title")}, "NAME", LawyerExceptions::nameException),
                "role", extractor.extractLawyerText(lawyer, new By[]{By.xpath(".//p[1]")}, "ROLE", LawyerExceptions::roleException),
                "firm", this.name,
                "country", "Finland",
                "practice_area", "",
                "email", socials[2],
                "phone", socials[1].isEmpty() ? "358966895232" : socials[1]
        );
    }
}
