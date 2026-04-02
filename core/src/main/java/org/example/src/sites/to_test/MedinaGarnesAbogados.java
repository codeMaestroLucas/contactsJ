package org.example.src.sites.to_test;

import org.example.exceptions.LawyerExceptions;
import org.example.src.entities.BaseSites.ByNewPage;
import org.example.src.entities.MyDriver;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;

import java.util.List;
import java.util.Map;

public class MedinaGarnesAbogados extends ByNewPage {

    public MedinaGarnesAbogados() {
        super(
                "Medina Garnes Abogados",
                "https://www.mga.com.do/en/our-lawers/",
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
        try {
            return MyDriver.wait.findElements(By.cssSelector("a[href*='/equipo/']"));
        } catch (Exception e) {
            return List.of();
        }
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

        String email = extractor.extractLawyerText(driver.findElement(By.tagName("body")), new By[]{By.xpath("//a[contains(@href, 'mailto:')]")}, "EMAIL", LawyerExceptions::emailException);

        return Map.of(
                "link", link,
                "name", "",
                "role", "Partner",
                "firm", this.name,
                "country", "Dominican Republic",
                "practice_area", "",
                "email", email,
                "phone", "18095405401"
        );
    }
}
