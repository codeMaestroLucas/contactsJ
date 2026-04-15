package org.example.src.sites.byNewPage;

import org.example.exceptions.LawyerExceptions;
import org.example.src.entities.BaseSites.ByNewPage;
import org.example.src.entities.MyDriver;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;

import java.util.List;
import java.util.Map;

public class DuarteGarcia extends ByNewPage {

    public DuarteGarcia() {
        super(
                "Duarte Garcia, Serra Netto e Terra",
                "https://www.duartegarcia.com.br/en/equipe/index?termo=socio",
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
            return MyDriver.wait.findElements(By.cssSelector("li[data-filtro*='socio']"));
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
        String name = extractor.extractLawyerText(lawyer, new By[]{By.tagName("a")}, "NAME", LawyerExceptions::nameException);
        String pa = lawyer.getAttribute("data-filtro");

        String link = this.openNewTab(lawyer);

        WebElement container = driver.findElement(By.cssSelector("div.membro-pad.nopt"));

        String[] socials = super.getSocials(container.findElements(By.tagName("p")), true);
        if (socials[0].isEmpty()) socials = super.getSocialsFromText(container.getText());
        return Map.of(
                "link", link,
                "name", name,
                "role", "Partner",
                "firm", this.name,
                "country", "Brazil",
                "practice_area", pa,
                "email", socials[0],
                "phone", socials[1].isEmpty() ? "551142006600" : socials[1]
        );
    }
}
