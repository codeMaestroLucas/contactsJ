package org.example.src.sites.to_test.americas;

import org.example.exceptions.LawyerExceptions;
import org.example.src.entities.BaseSites.ByNewPage;
import org.example.src.entities.MyDriver;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;

import java.util.List;
import java.util.Map;

public class LarrainYAsociados extends ByNewPage {

    public LarrainYAsociados() {
        super(
                "Larrain y Asociados",
                "https://larrain.cl/todos/",
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
        return MyDriver.wait.findElements(By.cssSelector("li.portfolio-item"));
    }

    @Override
    public String openNewTab(WebElement lawyer) throws LawyerExceptions {
        String url = extractor.extractLawyerAttribute(lawyer, new By[]{By.cssSelector("h3 a")}, "LINK", "href", LawyerExceptions::linkException);
        MyDriver.openNewTab(url);
        return url;
    }

    @Override
    protected Object getLawyer(WebElement lawyer) throws Exception {
        String name = extractor.extractLawyerText(lawyer, new By[]{By.tagName("h3")}, "NAME", LawyerExceptions::nameException);

        String link = this.openNewTab(lawyer);
        WebElement container = MyDriver.wait.findElement(By.className("mcb-wrap-inner"));

        String roleRaw = extractor.extractLawyerText(container, new By[]{By.xpath(".//p[contains(text(), 'socio') or contains(text(), 'asociado')]")}, "ROLE", LawyerExceptions::roleException);
        String role = roleRaw.isEmpty() ? "Lawyer" : roleRaw;

        String[] socials = this.getSocials(container.findElements(By.cssSelector("a[href^='mailto'], a[href^='tel']")), false);
        String practiceArea = extractor.extractLawyerText(container, new By[]{By.xpath(".//b[contains(text(), 'Áreas de Práctica')]/following::p")}, "PRACTICE AREA", LawyerExceptions::practiceAreaException);

        return Map.of(
                "link", link,
                "name", name,
                "role", role,
                "firm", this.name,
                "country", "Chile",
                "practice_area", practiceArea,
                "email", socials[0],
                "phone", socials[1].isEmpty() ? "56224635800" : socials[1]
        );
    }
}
