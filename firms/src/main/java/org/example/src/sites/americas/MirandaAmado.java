package org.example.src.sites.americas;

import org.example.exceptions.LawyerExceptions;
import org.example.src.entities.BaseSites.ByNewPage;
import org.example.src.entities.MyDriver;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;

import java.util.List;
import java.util.Map;

public class MirandaAmado extends ByNewPage {

    public MirandaAmado() {
        super(
                "Miranda & Amado",
                "https://www.mafirma.pe/es/resultado",
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
        String[] validRoles = {"socio", "socia", "consejero", "consejera"};

        try {
            List<WebElement> lawyers = MyDriver.wait.findElements(By.cssSelector("div.col-lg-3.col-6"));
            return this.siteUtl.filterLawyersInPage(lawyers, new By[]{By.tagName("h3")}, true, validRoles);

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
        String name = extractor.extractLawyerAttribute(lawyer, new By[]{By.tagName("h2")}, "NAME", "textContent", LawyerExceptions::nameException);
        String role = extractor.extractLawyerAttribute(lawyer, new By[]{By.tagName("h3")}, "ROLE", "textContent", LawyerExceptions::roleException);
        String pa = extractor.extractLawyerAttribute(lawyer, new By[]{By.tagName("h4")}, "PRACTICE AREA", "textContent", LawyerExceptions::practiceAreaException);

        String link = this.openNewTab(lawyer);

        WebElement container = MyDriver.wait.findElement(By.cssSelector("div.col-lg-4.offset-lg-1"));
        String[] socials = super.getSocials(container.findElements(By.tagName("a")), false);

        return Map.of(
                "link", link,
                "name", name,
                "role", role,
                "firm", this.name,
                "country", "Peru",
                "practice_area", pa,
                "email", socials[0],
                "phone", socials[1].isEmpty() ? "5116104747" : socials[1]
        );
    }
}
