package org.example.src.sites.americas;

import org.example.exceptions.LawyerExceptions;
import org.example.src.entities.BaseSites.ByNewPage;
import org.example.src.entities.MyDriver;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;

import java.util.List;
import java.util.Map;

public class JustenPereira extends ByNewPage {

    public JustenPereira() {
        super(
                "Justen Pereira",
                "https://justen.com.br/#advogados",
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
        try {
            WebElement div = MyDriver.wait.findElement(By.id("gallery-1"));
            return div.findElements(By.tagName("figure"));
        } catch (Exception e) {
            return List.of();
        }
    }

    @Override
    public String openNewTab(WebElement lawyer) throws LawyerExceptions {
        MyDriver.cmdClickOnElement(lawyer);
        MyDriver.waitForPageToLoad();
        return driver.getCurrentUrl();
    }

    @Override
    protected Object getLawyer(WebElement lawyer) throws Exception {

        String link = this.openNewTab(lawyer);

        WebElement container = MyDriver.wait.findElement(By.xpath("/html/body/div/div[1]/div/div[1]/div/div[2]"));
        String name = extractor.extractLawyerText(driver.findElement(By.tagName("body")), new By[]{By.xpath("/html/body/div/div[1]/div/div[1]/div/div[2]/div[1]/div/h2")}, "NAME", LawyerExceptions::nameException);
        String email = extractor.extractLawyerText(container, new By[]{By.xpath(".//h2[contains(text(), '@')]")}, "EMAIL", LawyerExceptions::emailException);

        return Map.of(
                "link", link,
                "name", name,
                "role", "Partner",
                "firm", this.name,
                "country", "Brazil",
                "practice_area", "",
                "email", email,
                "phone", "551137061500"
        );
    }
}
