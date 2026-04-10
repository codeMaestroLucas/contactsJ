package org.example.src.sites.byNewPage;

import org.example.exceptions.LawyerExceptions;
import org.example.src.entities.BaseSites.ByNewPage;
import org.example.src.entities.MyDriver;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;

import java.util.List;
import java.util.Map;

public class FBLAdvogados extends ByNewPage {

    public FBLAdvogados() {
        super(
                "FBL Advogados",
                "https://www.fbladvogados.com/?lang=en",
                1
        );
    }

    @Override
    protected void accessPage(int index) throws InterruptedException {
        this.driver.get(this.link);
        MyDriver.waitForPageToLoad();
        Thread.sleep(1500L);
    }

    @Override
    protected List<WebElement> getLawyersInPage() {
        try {
            List<WebElement> elements = MyDriver.wait.findElements(By.className("gallery-item-container"));
            elements.removeFirst();elements.removeFirst();elements.removeFirst();
            return elements;
        } catch (Exception e) {
            return List.of();
        }
    }

    @Override
    public String openNewTab(WebElement lawyer) throws LawyerExceptions {
        MyDriver.cmdClickOnElement(lawyer);
        return driver.getCurrentUrl();
    }

    @Override
    protected Object getLawyer(WebElement lawyer) throws Exception {

        String link = this.openNewTab(lawyer);

        WebElement container = MyDriver.wait.findElement(By.id("comp-ls1pjqv2"));

        String name = extractor.extractLawyerText(driver.findElement(By.tagName("body")), new By[]{By.tagName("h3")}, "NAME", LawyerExceptions::nameException);
        String role = extractor.extractLawyerText(driver.findElement(By.tagName("body")), new By[]{By.className("wixui-rich-text__text")}, "ROLE", LawyerExceptions::roleException);

        String[] socials = super.getSocials(driver.findElement(By.tagName("body")).findElements(By.cssSelector("p > a")), false);

        return Map.of(
                "link", link,
                "name", name,
                "role", role,
                "firm", this.name,
                "country", "Angola",
                "practice_area", extractor.extractLawyerText(container, new By[]{By.tagName("ul")}, "PRACTICE AREA", LawyerExceptions::practiceAreaException),
                "email", socials[0],
                "phone", socials[1].isEmpty() ? "244222393312" : socials[1]
        );
    }
}
