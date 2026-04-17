package org.example.src.sites.to_test.americas;

import org.example.exceptions.LawyerExceptions;
import org.example.src.entities.BaseSites.ByNewPage;
import org.example.src.entities.MyDriver;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;

import java.util.List;
import java.util.Map;

public class IbarraDelPasoYGallego extends ByNewPage {

    public IbarraDelPasoYGallego() {
        super(
                "Ibarra, del Paso y Gallego",
                "https://www.ibarrapg.com/en/lawyers/",
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
        return MyDriver.wait.findElements(By.className("e-loop-item"));
    }

    @Override
    public String openNewTab(WebElement lawyer) throws LawyerExceptions {
        String link = lawyer.findElement(By.cssSelector("h3 a")).getAttribute("href");
        MyDriver.openNewTab(link);
        return link;
    }

    @Override
    protected Object getLawyer(WebElement lawyer) throws Exception {
        String name = extractor.extractLawyerText(lawyer, new By[]{By.tagName("h3")}, "NAME", LawyerExceptions::nameException);
        String role = extractor.extractLawyerText(lawyer, new By[]{By.tagName("h4")}, "ROLE", LawyerExceptions::roleException);

        String profileLink = this.openNewTab(lawyer);
        WebElement container = MyDriver.wait.findElement(By.xpath("/html/body/div[1]/div[2]/div/div/div[2]/div[1]"));

        return Map.of(
                "link", profileLink,
                "name", name,
                "role", role,
                "firm", this.name,
                "country", "Mexico",
                "practice_area", "",
                "email", extractor.extractLawyerText(container, new By[]{By.className("elementor-icon-box-title")}, "EMAIL", LawyerExceptions::emailException),
                "phone", extractor.extractLawyerText(container, new By[]{By.xpath(".//div[contains(@class, 'icon-phone')]/following-sibling::div")}, "PHONE", LawyerExceptions::phoneException)
        );
    }
}
