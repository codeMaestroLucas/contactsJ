package org.example.src.sites.to_test.europe;

import org.example.exceptions.LawyerExceptions;
import org.example.src.entities.BaseSites.ByNewPage;
import org.example.src.entities.MyDriver;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;

import java.util.List;
import java.util.Map;

public class ISOLAS extends ByNewPage {

    public ISOLAS() {
        super(
                "ISOLAS",
                "https://gibraltarlawyers.com/people/",
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
        return MyDriver.wait.findElements(By.tagName("article"));
    }

    @Override
    public String openNewTab(WebElement lawyer) throws Exception {
        String url = extractor.extractLawyerAttribute(lawyer, new By[]{By.tagName("a")}, "LINK", "href", LawyerExceptions::linkException);
        MyDriver.openNewTab(url);
        return url;
    }

    @Override
    protected Object getLawyer(WebElement lawyer) throws Exception {
        String name = extractor.extractLawyerText(lawyer, new By[]{By.tagName("h2")}, "NAME", LawyerExceptions::nameException);
        
        String link = this.openNewTab(lawyer);
        WebElement container = MyDriver.wait.findElement(By.id("people_intro_txt_info"));

        String role = extractor.extractLawyerText(container, new By[]{By.tagName("p")}, "ROLE", LawyerExceptions::roleException);
        String[] socials = this.getSocials(container.findElements(By.tagName("a")), false);

        return Map.of(
                "link", link,
                "name", name,
                "role", role,
                "firm", this.name,
                "country", "Gibraltar",
                "practice_area", "",
                "email", socials[0],
                "phone", socials[1].isEmpty() ? "35020002000" : socials[1]
        );
    }
}
