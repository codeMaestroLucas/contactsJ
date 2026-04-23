package org.example.src.sites.americas;

import org.example.exceptions.LawyerExceptions;
import org.example.src.entities.BaseSites.ByNewPage;
import org.example.src.entities.MyDriver;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import java.util.List;
import java.util.Map;

public class MontesOlivos extends ByNewPage {

    public MontesOlivos() {
        super(
                "Montes, Olivos, Eyzaguirre & Arostegui",
                "https://gmoe.cl/equipo/",
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
        return MyDriver.wait.findElements(By.cssSelector("div.elementor-button-wrapper > a[href*='https://gmoe.cl//']"));
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

        WebElement container = MyDriver.wait.findElement(By.xpath("//div/div/section[2]/div/div[2]/div"));
        MyDriver.scrollToBottom(0.6);
        String name = extractor.extractLawyerText(container, new By[]{By.className("elementor-heading-title")}, "NAME", LawyerExceptions::nameException);
        String email = null;
        try {
            email = driver.findElement(By.xpath("//div/div/section[2]/div/div[1]/div/div[2]/div/ul/li/span[2]")).getAttribute("textContent");
        } catch (Exception e) {
            email = super.getSocialsFromText(driver.findElement(By.tagName("body")).getText())[0];
        }

        return Map.of(
                "link", link,
                "name", name,
                "role", "",
                "firm", this.name,
                "country", "Chile",
                "practice_area", "",
                "email", email,
                "phone", "56223644200"
        );
    }
}
