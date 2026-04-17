package org.example.src.sites.to_test.americas;

import org.example.exceptions.LawyerExceptions;
import org.example.src.entities.BaseSites.ByNewPage;
import org.example.src.entities.MyDriver;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;

import java.util.List;
import java.util.Map;

public class RebazaAlcazarDeLasCasas extends ByNewPage {

    public RebazaAlcazarDeLasCasas() {
        super(
                "Rebaza, Alcázar & De Las Casas",
                "https://rebaza-alcazar.com/en/the-team/partners/",
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
        return MyDriver.wait.findElements(By.cssSelector(".et_pb_button"));
    }

    @Override
    public String openNewTab(WebElement lawyer) throws LawyerExceptions {
        String link = lawyer.getAttribute("href");
        MyDriver.openNewTab(link);
        return link;
    }

    @Override
    protected Object getLawyer(WebElement lawyer) throws Exception {
        String name = lawyer.getText();
        String profileLink = this.openNewTab(lawyer);
        WebElement container = MyDriver.wait.findElement(By.className("et_pb_row_1"));

        return Map.of(
                "link", profileLink,
                "name", name,
                "role", "Partner",
                "firm", this.name,
                "country", "Peru",
                "practice_area", "",
                "email", extractor.extractLawyerText(container, new By[]{By.xpath(".//a[contains(@href, 'mailto')]")}, "EMAIL", LawyerExceptions::emailException),
                "phone", extractor.extractLawyerText(container, new By[]{By.xpath(".//p[contains(text(), 'T:')]")}, "PHONE", LawyerExceptions::phoneException)
        );
    }
}
