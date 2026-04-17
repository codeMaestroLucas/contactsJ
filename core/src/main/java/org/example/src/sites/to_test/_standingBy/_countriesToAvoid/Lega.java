package org.example.src.sites.to_test._standingBy._countriesToAvoid;

import org.example.src.entities.BaseSites.ByPage;
import org.example.src.entities.MyDriver;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;

import java.util.List;
import java.util.Map;

public class Lega extends ByPage {

    public Lega() {
        super(
                "Leĝa",
                "https://lega.law/en/nuestro-equipo/",
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
        return MyDriver.wait.findElements(By.xpath("//div[contains(@class, 'column')]//strong"));
    }

    @Override
    protected Object getLawyer(WebElement lawyer) throws Exception {
        WebElement container = lawyer.findElement(By.xpath("./parent::div"));

        String name = lawyer.getText();
        String role = container.getText().split("\n")[1];
        String[] socials = this.getSocials(container.findElements(By.tagName("a")), false);

        return Map.of(
                "link", container.findElement(By.className("overlay-link")).getAttribute("href"),
                "name", name,
                "role", role,
                "firm", this.name,
                "country", "Venezuela",
                "practice_area", "",
                "email", socials[0],
                "phone", "xxxxxx"
        );
    }
}