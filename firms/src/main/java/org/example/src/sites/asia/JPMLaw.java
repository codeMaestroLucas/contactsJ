package org.example.src.sites.asia;

import org.example.exceptions.LawyerExceptions;
import org.example.src.entities.BaseSites.ByNewPage;
import org.example.src.entities.MyDriver;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;

import java.util.List;
import java.util.Map;

public class JPMLaw extends ByNewPage {

    public JPMLaw() {
        super(
                "JPM Law",
                "https://jmp.law/professionals/",
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
        return MyDriver.wait.findElements(By.cssSelector("h2 > a[href*='https://jmp.law/people/']"));
    }

    @Override
    public String openNewTab(WebElement lawyer) throws LawyerExceptions {
        String link = lawyer.getAttribute("href");
        MyDriver.openNewTab(link);
        return link;
    }

    @Override
    protected Object getLawyer(WebElement lawyer) throws Exception {
        String link = this.openNewTab(lawyer);

        String name = extractor.extractLawyerAttribute(driver.findElement(By.tagName("body")), new By[]{By.xpath("//section/div/div/div/div/div[1]/h2")}, "NAME", "textContent", LawyerExceptions::nameException);
        String role = extractor.extractLawyerAttribute(driver.findElement(By.tagName("body")), new By[]{By.xpath("//section/div/div/div/div/div[2]/h2")}, "ROLE", "textContent", LawyerExceptions::roleException);

        WebElement container = driver.findElement(By.xpath("//div/div/div/div/div/div/div/div/div[1]/div/div[1]"));
        String[] socials = super.getSocials(container.findElements(By.tagName("a")), false);

        return Map.of(
                "link", link,
                "name", name,
                "role", role,
                "firm", this.name,
                "country", "India",
                "practice_area", "",
                "email", socials[0],
                "phone", socials[1].isEmpty() ? "919821032780" : socials[1]
        );
    }
}
