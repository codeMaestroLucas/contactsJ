package org.example.src.sites.americas;

import org.example.exceptions.LawyerExceptions;
import org.example.src.entities.BaseSites.ByPage;
import org.example.src.entities.MyDriver;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;

import java.util.List;
import java.util.Map;

public class RomeroPineda extends ByPage {

    public RomeroPineda() {
        super(
                "Romero Pineda",
                "https://www.romeropineda.com/en/nuestra-firma/#nuestro-equipo",
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
        WebElement div = MyDriver.wait.findElement(By.xpath("//*[@id=\"nuestro-equipo\"]/div/div/div/div[2]/div/div/div"));
        List<WebElement> lawyers = div.findElements(By.cssSelector(".qodef-grid-item.team"));

        div = driver.findElement(By.xpath("//*[@id=\"qodef-page-content\"]/div/div/div/section[6]/div/div/div/div[2]/div/div"));
        lawyers.addAll(div.findElements(By.cssSelector(".qodef-grid-item.team")));

        return lawyers;
    }

    @Override
    protected Object getLawyer(WebElement lawyer) throws Exception {
        String email = extractor.extractLawyerText(lawyer, new By[]{By.className("qodef-e-degree")}, "EMAIL", LawyerExceptions::emailException);

        return Map.of(
                "link", extractor.extractLawyerAttribute(lawyer, new By[]{By.className("qodef-e-title"), By.tagName("a")}, "LINK", "href", LawyerExceptions::linkException),
                "name", extractor.extractLawyerText(lawyer, new By[]{By.className("qodef-e-title")}, "NAME", LawyerExceptions::nameException),
                "role", "-----",
                "firm", this.name,
                "country", "El Salvador",
                "practice_area", "",
                "email", email,
                "phone", "50325055555"
        );
    }
}
