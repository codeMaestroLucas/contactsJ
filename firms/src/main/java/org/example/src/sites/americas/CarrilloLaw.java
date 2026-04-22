package org.example.src.sites.americas;

import org.example.exceptions.LawyerExceptions;
import org.example.src.entities.BaseSites.ByNewPage;
import org.example.src.entities.MyDriver;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;

import java.util.List;
import java.util.Map;

public class CarrilloLaw extends ByNewPage {

    public CarrilloLaw() {
        super(
                "Carrillo Law",
                "https://carrillolaw.com/en/equipo/",
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
        WebElement div = MyDriver.wait.findElement(By.xpath("/html/body/div[1]/section[3]/div/div/div"));
        List<WebElement> lawyers = div.findElements(By.cssSelector("a[href*='https://carrillolaw.com/en/miembro/']"));

        div = driver.findElement(By.xpath("/html/body/div[1]/section[2]/div/div/div/div[3]"));
        lawyers.addAll(
                div.findElements(By.cssSelector("a[href*='https://carrillolaw.com/en/miembro/']"))
        );
        return lawyers;
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

        WebElement container = MyDriver.wait.findElement(By.xpath("/html/body/div[1]/section[2]/div/div/div/section/div/div[1]/div/div[5]/div/ul"));

        String name = extractor.extractLawyerText(driver.findElement(By.tagName("body")), new By[]{By.xpath("/html/body/div[1]/section[2]/div/div/div/section/div/div[1]/div/div[3]/div/h1")}, "NAME", LawyerExceptions::nameException);
        String role = extractor.extractLawyerText(driver.findElement(By.tagName("body")), new By[]{By.xpath("/html/body/div[1]/section[2]/div/div/div/section/div/div[1]/div/div[2]/div/h6/a")}, "NAME", LawyerExceptions::nameException);

        String[] socials = super.getSocialsFromText(container.getText());

        return Map.of(
                "link", link,
                "name", name,
                "role", role,
                "firm", this.name,
                "country", "Guatemala",
                "practice_area", "",
                "email", socials[0],
                "phone", socials[1].isEmpty() ? "50224215700" : socials[1]
        );
    }
}
