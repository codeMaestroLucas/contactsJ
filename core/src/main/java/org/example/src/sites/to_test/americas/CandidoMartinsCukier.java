package org.example.src.sites.to_test.americas;

import org.example.exceptions.LawyerExceptions;
import org.example.src.entities.BaseSites.ByNewPage;
import org.example.src.entities.MyDriver;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;

import java.util.List;
import java.util.Map;

public class CandidoMartinsCukier extends ByNewPage {

    public CandidoMartinsCukier() {
        super(
                "Candido Martins Cukier",
                "https://candidomartinscukier.com.br/en/team/",
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
        return MyDriver.wait.findElements(By.cssSelector("div.jet-listing-grid__item"));
    }

    @Override
    public String openNewTab(WebElement lawyer) throws LawyerExceptions {
        String url = extractor.extractLawyerAttribute(lawyer, new By[]{By.cssSelector("a.jet-engine-listing-overlay-link")}, "LINK", "href", LawyerExceptions::linkException);
        MyDriver.openNewTab(url);
        return url;
    }

    @Override
    protected Object getLawyer(WebElement lawyer) throws Exception {
        String name = extractor.extractLawyerText(lawyer, new By[]{By.tagName("h2")}, "NAME", LawyerExceptions::nameException);

        String link = this.openNewTab(lawyer);
        WebElement container = MyDriver.wait.findElement(By.cssSelector("div.elementor-element-aa09387"));

        String roleRaw = extractor.extractLawyerText(container, new By[]{By.cssSelector("div.elementor-element-6551efd")}, "ROLE", LawyerExceptions::roleException);
        String role = roleRaw.split("is a")[1].split("at")[0].trim();

        String[] socials = this.getSocials(container.findElements(By.cssSelector("a.jet-listing-dynamic-link__link")), true);

        return Map.of(
                "link", link,
                "name", name,
                "role", role,
                "firm", this.name,
                "country", "Brazil",
                "practice_area", "",
                "email", socials[1],
                "phone", socials[0].isEmpty() ? "551130541700" : socials[0]
        );
    }
}
