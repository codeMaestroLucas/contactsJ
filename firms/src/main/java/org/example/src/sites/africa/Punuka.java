package org.example.src.sites.africa;

import org.example.exceptions.LawyerExceptions;
import org.example.src.entities.BaseSites.ByNewPage;
import org.example.src.entities.MyDriver;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import java.util.List;
import java.util.Map;

public class Punuka extends ByNewPage {

    public Punuka() {
        super(
                "Punuka",
                "https://punuka.com/about/people/partners/",
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
        return MyDriver.wait.findElements(By.cssSelector("a[href*='https://punuka.com/about/people/'][class*='eael-wrapper-link']"));
    }

    @Override
    public String openNewTab(WebElement lawyer) throws LawyerExceptions {
        String link = lawyer.getAttribute("href");
        MyDriver.openNewTab(link);
        return link;
    }

    @Override
    public Object getLawyer(WebElement lawyer) throws Exception {
        String link = this.openNewTab(lawyer);
        WebElement container = driver.findElement(By.xpath("//div/section[3]/div/div[2]/div"));

        String name = extractor.extractLawyerText(driver.findElement(By.tagName("body")), new By[]{By.xpath("//div/section[2]/div[2]/div[1]/div/div[1]/div/h1")}, "NAME", LawyerExceptions::nameException);

        String[] socials = super.getSocials(container.findElements(By.tagName("a")), false);

        return Map.of(
                "link", link,
                "name", name,
                "role", "Partner",
                "firm", this.name,
                "country", "Nigeria",
                "practice_area", container.getText(),
                "email", socials[0],
                "phone", "234 1 270 4789"
        );
    }
}
