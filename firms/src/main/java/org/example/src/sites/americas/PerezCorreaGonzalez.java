package org.example.src.sites.americas;

import org.example.exceptions.LawyerExceptions;
import org.example.src.entities.BaseSites.ByNewPage;
import org.example.src.entities.MyDriver;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;

import java.util.List;
import java.util.Map;

public class PerezCorreaGonzalez extends ByNewPage {

    public PerezCorreaGonzalez() {
        super(
                "Perez Correa Gonzalez",
                "https://pcga.mx/en/our-firm/",
                3
        );
    }

    String currentRole = "";

    @Override
    protected void accessPage(int index) throws InterruptedException {
        if (index == 0) {
            this.driver.get(this.link);
            MyDriver.waitForPageToLoad();
            currentRole = "Partner";
        } else {
            String xpath = "//*[@id='ui-id-" + (index + 1) + "']";
            MyDriver.clickOnElement(By.xpath(xpath));
            currentRole = "Counsel";
            Thread.sleep(2000);
        }

    }

    @Override
    protected List<WebElement> getLawyersInPage() {
        try {
            return MyDriver.wait.findElements(By.className("aio-icon-component"));
        } catch (Exception e) {
            return List.of();
        }
    }

    @Override
    public String openNewTab(WebElement lawyer) throws LawyerExceptions {
        String link = extractor.extractLawyerAttribute(lawyer, new By[]{By.tagName("a")}, "LINK", "href", LawyerExceptions::linkException);
        MyDriver.openNewTab(link);
        return link;
    }

    @Override
    protected Object getLawyer(WebElement lawyer) throws Exception {
        String name = extractor.extractLawyerAttribute(lawyer, new By[]{By.className("aio-icon-title")}, "NAME", "textContent", LawyerExceptions::nameException);

        String link = this.openNewTab(lawyer);

        WebElement container = MyDriver.wait.findElement(By.xpath("//*[@id=\"content\"]/div/div[9]/div[2]/div/div/div[1]/div/div/div"));
        String pa = MyDriver.wait.findElement(By.xpath("//*[@id=\"content\"]/div/div[9]/div[2]/div/div/div[2]/div/div/div")).getText();
        String[] socials = super.getSocials(container.findElements(By.tagName("a")), false);

        return Map.of(
                "link", link,
                "name", name,
                "role", currentRole,
                "firm", this.name,
                "country", "Mexico",
                "practice_area", pa,
                "email", socials[0],
                "phone", socials[1].isEmpty() ? "525550520500" : socials[1]
        );
    }
}
