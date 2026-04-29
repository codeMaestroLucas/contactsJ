package org.example.src.sites.americas;

import org.example.exceptions.LawyerExceptions;
import org.example.src.entities.BaseSites.ByNewPage;
import org.example.src.entities.MyDriver;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;

import java.util.List;
import java.util.Map;

public class NietoLegal extends ByNewPage {

    public NietoLegal() {
        super(
                "Nieto Legal",
                "https://nietolegal.com/team/",
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
        return MyDriver.wait.findElements(By.cssSelector("h2.elementor-heading-title > a[href*='https://nietolegal.com/']"));
    }

    @Override
    public String openNewTab(WebElement lawyer) throws LawyerExceptions {
        String url = lawyer.getAttribute("href");
        MyDriver.openNewTab(url);
        return url;
    }

    @Override
    protected Object getLawyer(WebElement lawyer) throws Exception {
        String name = lawyer.getText();

        String link = this.openNewTab(lawyer);
        WebElement container = null;
        try {
            container = MyDriver.wait.findElement(By.xpath("//*[@id=\"main\"]/div/section[2]/div/div/div/section[1]/div/div[2]/div"));
        } catch (Exception e) {
            container = driver.findElement(By.tagName("body"));
        }

        String[] socials = this.getSocialsFromText(container.getText());

        return Map.of(
                "link", link,
                "name", name,
                "role", "Check on previous page",
                "firm", this.name,
                "country", "Colombia",
                "practice_area", "",
                "email", socials[0],
                "phone", socials[1].isEmpty() ? "576012104294" : socials[1]
        );
    }
}
