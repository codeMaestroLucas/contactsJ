package org.example.src.sites.byNewPage;

import org.example.exceptions.LawyerExceptions;
import org.example.src.entities.BaseSites.ByNewPage;
import org.example.src.entities.MyDriver;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;

import java.util.List;
import java.util.Map;

public class DeLaVegaMartinezRojas extends ByNewPage {

    public DeLaVegaMartinezRojas() {
        super(
                "De La Vega & Martínez Rojas",
                "https://www.dlvmr.com.mx/equipo",
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
            return MyDriver.wait.findElements(By.className("equipo-item"));
        } catch (Exception e) {
            return List.of();
        }
    }

    @Override
    public String openNewTab(WebElement lawyer) throws LawyerExceptions {
        String link = extractor.extractLawyerAttribute(lawyer, new By[]{By.className("equipo-link")}, "LINK", "href", LawyerExceptions::linkException);
        MyDriver.openNewTab(link);
        return link;
    }

    @Override
    protected Object getLawyer(WebElement lawyer) throws Exception {
        String name = extractor.extractLawyerText(lawyer, new By[]{By.className("equipo-nombre")}, "NAME", LawyerExceptions::nameException);

        String link = this.openNewTab(lawyer);

        WebElement container = driver.findElement(By.className("equipo-lay"));
        String text = extractor.extractLawyerText(container, new By[]{By.xpath("./div[2]")}, "CONTENT", (e) -> null);
        String[] socials = super.getSocialsFromText(text);

        return Map.of(
                "link", link,
                "name", name,
                "role", "Partner",
                "firm", this.name,
                "country", "Mexico",
                "practice_area", "Labor Law",
                "email", socials[0].isEmpty() ? "contacto@dlvmr.com.mx" : socials[0],
                "phone", socials[1].isEmpty() ? "525552020777" : socials[1]
        );
    }
}
