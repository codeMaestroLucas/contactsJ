package org.example.src.sites.byNewPage;

import org.example.exceptions.LawyerExceptions;
import org.example.src.entities.BaseSites.ByNewPage;
import org.example.src.entities.MyDriver;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;

import java.util.List;
import java.util.Map;

public class MKK extends ByNewPage {

    public MKK() {
        super(
                "MKK",
                "http://www.mkklaw.net/attorneys_main_eng.asp",
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
        WebElement div = MyDriver.wait.findElement(By.xpath("//*[@id=\"main-menu\"]/div/nav/ul/li[4]/ul/li[1]/ul"));

        List<WebElement> lawyers = div.findElements(By.tagName("li"));

        div = driver.findElement(By.xpath("//*[@id=\"main-menu\"]/div/nav/ul/li[4]/ul/li[3]/ul/li[1]/ul"));
        lawyers.addAll(div.findElements(By.tagName("li")));

        div = driver.findElement(By.xpath("//*[@id=\"main-menu\"]/div/nav/ul/li[4]/ul/li[3]/ul/li[2]/ul"));
        lawyers.addAll(div.findElements(By.tagName("li")));

        return lawyers;
    }

    @Override
    public String openNewTab(WebElement lawyer) throws LawyerExceptions, InterruptedException {
        String link = lawyer.findElement(By.tagName("a")).getAttribute("href");
        MyDriver.openNewTab(link);
        return this.link;
    }

    @Override
    protected Object getLawyer(WebElement lawyer) throws Exception {
        String link = this.openNewTab(lawyer);

        String rolePa = MyDriver.wait.findElement(By.xpath("//*[@id=\"wrap\"]/div/main/div[2]/div/div/div[2]/blockquote/p")).getAttribute("textContent");

        return Map.of(
                "link", link,
                "name", extractor.extractLawyerText(driver.findElement(By.tagName("body")), new By[]{By.xpath("//*[@id=\"wrap\"]/div/main/div[1]/div/div/div/ul/li/div/div/h2")}, "NAME", LawyerExceptions::nameException),
                "role", rolePa,
                "firm", this.name,
                "country", "Indonesia",
                "practice_area", rolePa,
                "email", extractor.extractLawyerText(driver.findElement(By.tagName("body")), new By[]{By.xpath("//*[@id=\"wrap\"]/div/main/div[2]/div/div/div[1]/div[2]/div[2]/p/a")}, "EMAIL", LawyerExceptions::emailException),
                "phone", "62215155555"
        );
    }
}
