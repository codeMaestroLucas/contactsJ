package org.example.src.sites.americas;

import org.example.exceptions.LawyerExceptions;
import org.example.src.entities.BaseSites.ByNewPage;
import org.example.src.entities.MyDriver;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import java.util.List;
import java.util.Map;

public class GuerraGonzalez extends ByNewPage {

    public GuerraGonzalez() {
        super(
                "Guerra González y Asociados",
                "https://en.guerragonzalez-abogados.com/#firma",
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
        return MyDriver.wait.findElements(By.cssSelector("a[href*='https://en.guerragonzalez-abogados.com/lawyers/']"));
    }

    @Override
    public String openNewTab(WebElement lawyer) throws LawyerExceptions {
        String link = lawyer.getAttribute("href");
        MyDriver.openNewTab(link);
        return link;
    }

    @Override
    public Object getLawyer(WebElement lawyer) throws Exception {
        String name = lawyer.getAttribute("title");

        String link = this.openNewTab(lawyer);
        WebElement container = driver.findElement(By.xpath("//div/div[1]/div/div[2]/div/div/div/div"));

        String[] socials = super.getSocialsFromText(container.getText());

        return Map.of(
                "link", link,
                "name", name,
                "role", "Partner",
                "firm", this.name,
                "country", "Mexico",
                "practice_area", container.getText(),
                "email", socials[0],
                "phone", socials[1].isEmpty() ? "525554886100" : socials[1]
        );
    }
}
