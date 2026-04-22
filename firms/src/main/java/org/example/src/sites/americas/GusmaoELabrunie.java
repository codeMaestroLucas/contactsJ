package org.example.src.sites.americas;

import org.example.exceptions.LawyerExceptions;
import org.example.src.entities.BaseSites.ByNewPage;
import org.example.src.entities.MyDriver;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import java.util.List;
import java.util.Map;

public class GusmaoELabrunie extends ByNewPage {

    public GusmaoELabrunie() {
        super(
                "Gusmão & Labrunie",
                "https://www.glpi.com.br/equipe",
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
        return MyDriver.wait.findElements(By.cssSelector("div.results-socios.container div.item-advogado"));
    }

    @Override
    public String openNewTab(WebElement lawyer) throws LawyerExceptions {
        String link = extractor.extractLawyerAttribute(lawyer, new By[]{By.className("nome-advogado")}, "LINK", "href", LawyerExceptions::linkException);
        MyDriver.openNewTab(link);
        return link;
    }

    @Override
    public Object getLawyer(WebElement lawyer) throws Exception {
        String name = extractor.extractLawyerAttribute(lawyer, new By[]{By.className("nome-advogado")}, "NAME", "textContent", LawyerExceptions::nameException);

        String link = this.openNewTab(lawyer);
        WebElement container = MyDriver.wait.findElement(By.className("primary-infos"));
        String[] socials = super.getSocials(container.findElements(By.tagName("a")), false);
        String phone = extractor.extractLawyerAttribute(container, new By[]{By.className("email-tel"), By.tagName("span")}, "PHONE", "textContent", LawyerExceptions::socialsException);

        return Map.of(
                "link", link,
                "name", name,
                "role", "Partner",
                "firm", this.name,
                "country", "Brazil",
                "practice_area", "",
                "email", socials[0],
                "phone", phone.isEmpty() ? "551121494500" : phone
        );
    }
}
