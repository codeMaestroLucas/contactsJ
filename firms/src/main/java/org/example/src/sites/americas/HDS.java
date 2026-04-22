package org.example.src.sites.americas;

import org.example.exceptions.LawyerExceptions;
import org.example.src.entities.BaseSites.ByNewPage;
import org.example.src.entities.MyDriver;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import java.util.List;
import java.util.Map;

public class HDS extends ByNewPage {

    public HDS() {
        super(
                "HDS",
                "https://www.hds.com.ar/en/busqueda-alfa/",
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
        List<WebElement> lawyers = MyDriver.wait.findElements(By.cssSelector("div.col-sm-3"));
        return this.siteUtl.filterLawyersInPage(lawyers, new By[]{By.className("SubGrilla")}, true);
    }

    @Override
    public String openNewTab(WebElement lawyer) throws LawyerExceptions {
        String link = extractor.extractLawyerAttribute(lawyer, new By[]{By.tagName("a")}, "LINK", "href", LawyerExceptions::linkException);
        MyDriver.openNewTab(link);
        return link;
    }

    @Override
    public Object getLawyer(WebElement lawyer) throws Exception {
        String[] parts = extractor.extractLawyerText(lawyer, new By[]{By.className("TituloGrilla")}, "NAME", LawyerExceptions::nameException).split(",");

        String name = parts[parts.length - 1] + " " + parts[0];
        String role = extractor.extractLawyerText(lawyer, new By[]{By.className("SubGrilla")}, "ROLE", LawyerExceptions::roleException);

        String link = this.openNewTab(lawyer);
        WebElement container = driver.findElement(By.xpath("//*[@id=\"FondoProfesionales\"]/div[2]/div"));
        String[] socials = super.getSocials(container.findElements(By.tagName("a")), false);

        return Map.of(
                "link", link,
                "name", name,
                "role", role,
                "firm", this.name,
                "country", "Argentina",
                "practice_area", extractor.extractLawyerText(container, new By[]{By.id("Areas")}, "PRACTICE AREA", LawyerExceptions::practiceAreaException),
                "email", socials[0],
                "phone", "541148711550"
        );
    }
}
