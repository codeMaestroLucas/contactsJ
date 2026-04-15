package org.example.src.sites.to_test.americas;

import org.example.exceptions.LawyerExceptions;
import org.example.src.entities.BaseSites.ByPage;
import org.example.src.entities.MyDriver;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import java.util.List;
import java.util.Map;
import java.util.Objects;

public class StoccheForbes extends ByPage {

    public StoccheForbes() {
        super(
                "Stocche Forbes",
                "https://www.stoccheforbes.com.br/en/s%C3%B3cio",
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
        return MyDriver.wait.findElements(By.cssSelector("div.item-link-wrapper"));
    }

    @Override
    public Object getLawyer(WebElement lawyer) throws Exception {
        return Map.of(
                "link", Objects.requireNonNull(driver.getCurrentUrl()),
                "name", extractor.extractLawyerAttribute(lawyer, new By[]{By.tagName("img")}, "NAME", "alt", LawyerExceptions::nameException),
                "role", "Partner",
                "firm", this.name,
                "country", "Brazil",
                "practice_area", "",
                "email", "contato@stoccheforbes.com.br",
                "phone", "551133033400"
        );
    }
}
